package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REST_CLIENT_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseMatchingService {

    private static final String TEMPLATE_DIRECTORY = "templates/elasticsearch/caseMatching/";
    private static final String ES_QUERY = "main_query.json";
    private static final String ES_ALIASES_SUB_QUERY = "aliases_sub_query.json";
    private static final String ES_ALIASES_TO_ALIASES_SUB_QUERY = "aliases_to_aliases_sub_query.json";
    private static final String CASE_TYPE_ID = "ctid";

    private static final String DECEASED_FORENAMES = "data.deceasedForenames";
    private static final String DECEASED_SURNAME = "data.deceasedSurname";
    private static final String DECEASED_ALIAS_NAME_LIST = "data.solsDeceasedAliasNamesList.*";
    private static final String DECEASED_DOB = "data.deceasedDateOfBirth";
    private static final String DECEASED_DOD = "data.deceasedDateOfDeath";
    private static final String IMPORTED_TO_CCD = "data.imported_to_ccd";
    private static final String IMPORTED_TO_CCD_Y = "Y";

    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final FileSystemResourceService fileSystemResourceService;
    private final CaseMatchBuilderService caseMatchBuilderService;

    public List<CaseMatch> findMatches(CaseType caseType, CaseMatchingCriteria criteria) {

        String optionalAliasesToNameQuery = criteria.getDeceasedAliases().stream()
                .map(alias -> getAliasesToNameSubQueryTemplate().replace(":deceasedAliases", alias))
                .collect(Collectors.joining());

        String optionalAliasesToAliasesQuery = criteria.getDeceasedAliases().stream()
                .map(alias -> getAliasesToAliasesSubQueryTemplate().replace(":deceasedAliases", alias))
                .collect(Collectors.joining());

        String jsonQuery = getQueryTemplate()
                .replace(":deceasedForenames", criteria.getDeceasedForenames())
                .replace(":deceasedSurname", criteria.getDeceasedSurname())
                .replace(":deceasedFullName", criteria.getDeceasedFullName())
                .replace(":deceasedDateOfBirth", criteria.getDeceasedDateOfBirth())
                .replace(":deceasedDateOfDeath", criteria.getDeceasedDateOfDeath())
                .replace(":optionalAliasesToNameQuery", optionalAliasesToNameQuery)
                .replace(":optionalAliasesToAliasesQuery", optionalAliasesToAliasesQuery);

        return runQuery(caseType, criteria, jsonQuery);
    }

    public List<CaseMatch> findCases(CaseType caseType, CaseMatchingCriteria criteria) {
        BoolQueryBuilder fuzzy = boolQuery();
        BoolQueryBuilder strict = boolQuery();
        BoolQueryBuilder filter = boolQuery();

        ofNullable(criteria.getDeceasedForenames())
                .filter(s -> !s.isEmpty())
                .ifPresent(s -> {
                    fuzzy.must(multiMatchQuery(s, DECEASED_FORENAMES).fuzziness(2).operator(AND));
                    strict.must(multiMatchQuery(s, DECEASED_FORENAMES).fuzziness(0).boost(2).operator(AND));
                });

        ofNullable(criteria.getDeceasedSurname())
                .filter(s -> !s.isEmpty())
                .ifPresent(s -> {
                    fuzzy.must(multiMatchQuery(s, DECEASED_SURNAME).fuzziness(2).operator(AND));
                    strict.must(multiMatchQuery(s, DECEASED_SURNAME).fuzziness(0).boost(2).operator(AND));
                });

        ofNullable(criteria.getDeceasedFullName())
                .filter(s -> !s.isEmpty())
                .ifPresent(s -> {
                    fuzzy.should(multiMatchQuery(s, DECEASED_ALIAS_NAME_LIST).fuzziness(2).operator(AND));
                    strict.should(multiMatchQuery(s, DECEASED_ALIAS_NAME_LIST).fuzziness(0).boost(2).operator(AND));
                });

        ofNullable(criteria.getDeceasedDateOfBirthRaw())
                .ifPresent(date -> filter.must(termQuery(DECEASED_DOB, date)));

        ofNullable(criteria.getDeceasedDateOfDeathRaw())
                .ifPresent(date -> filter.must(rangeQuery(DECEASED_DOD).gte(date.minusDays(3)).lte(date.plusDays(3))));

        filter.mustNot(matchQuery(IMPORTED_TO_CCD, IMPORTED_TO_CCD_Y));

        BoolQueryBuilder wrapper = boolQuery().should(fuzzy).should(strict).minimumShouldMatch(1).filter(filter);

        String jsonQuery = new SearchSourceBuilder().query(wrapper).size(100).toString();

        return runQuery(caseType, criteria, jsonQuery);
    }

    public List<CaseMatch> findCrossMatches(List<CaseType> caseTypes, CaseMatchingCriteria criteria) {
        return caseTypes.stream()
                .map(caseType -> findMatches(caseType, criteria))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<CaseMatch> runQuery(CaseType caseType, CaseMatchingCriteria criteria, String jsonQuery) {
        log.info("CaseMatchingService runQuery: " + jsonQuery);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(ccdDataStoreAPIConfiguration.getHost() + ccdDataStoreAPIConfiguration.getCaseMatchingPath())
                .queryParam(CASE_TYPE_ID, caseType.getCode())
                .build().encode().toUri();

        HttpEntity<String> entity = new HttpEntity<>(jsonQuery, headers.getAuthorizationHeaders());

        MatchedCases matchedCases;
        try {
            matchedCases = restTemplate.postForObject(uri, entity, MatchedCases.class);
        } catch (HttpClientErrorException e) {
            appInsights.trackEvent(REST_CLIENT_EXCEPTION, e.getMessage());
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        }

        appInsights.trackEvent(REQUEST_SENT, uri.toString());

        return matchedCases.getCases().stream()
                .filter(c -> c.getId() == null || !criteria.getId().equals(c.getId()))
                .map(c -> caseMatchBuilderService.buildCaseMatch(c, caseType))
                .collect(Collectors.toList());
    }

    private String getQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY + ES_QUERY);
    }

    private String getAliasesToNameSubQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_ALIASES_SUB_QUERY);
    }

    private String getAliasesToAliasesSubQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_ALIASES_TO_ALIASES_SUB_QUERY);
    }
}
