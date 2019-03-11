package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.springframework.util.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseSearchService {

    private static final int ES_RESULTS_LIMIT = 100;

    private static final String RECORD_ID = "data.record_id";
    private static final String DECEASED_FORENAMES = "data.deceasedForenames";
    private static final String DECEASED_SURNAME = "data.deceasedSurname";
    private static final String DECEASED_ALIAS_NAME_LIST = "data.solsDeceasedAliasNamesList.*";
    private static final String DECEASED_DOB = "data.deceasedDateOfBirth";
    private static final String DECEASED_DOD = "data.deceasedDateOfDeath";
    private static final String IMPORTED_TO_CCD = "data.imported_to_ccd";
    private static final String IMPORTED_TO_CCD_Y = "Y";

    private final ElasticSearchService elasticSearchService;
    private final CaseMatchBuilderService caseMatchBuilderService;

    public List<CaseMatch> findCases(CaseType caseType, CaseMatchingCriteria criteria) {

        String jsonQuery;

        if (isEmpty(criteria.getRecordId())) {
            jsonQuery = getSearchQuery(criteria);
        } else {
            jsonQuery = getSearchByRecordIdQuery(criteria);
        }

        MatchedCases matchedCases = elasticSearchService.runQuery(caseType, jsonQuery);

        return matchedCases.getCases().stream()
                .map(caseMatchBuilderService::buildCaseMatch)
                .collect(Collectors.toList());
    }

    private String getSearchQuery(CaseMatchingCriteria criteria) {
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

        return new SearchSourceBuilder().query(wrapper).size(ES_RESULTS_LIMIT).toString();
    }

    private String getSearchByRecordIdQuery(CaseMatchingCriteria criteria) {
        BoolQueryBuilder query = boolQuery().must(termQuery(RECORD_ID, criteria.getRecordId()));
        BoolQueryBuilder filter = boolQuery().mustNot(matchQuery(IMPORTED_TO_CCD, IMPORTED_TO_CCD_Y));

        return new SearchSourceBuilder().query(query.filter(filter)).toString();
    }
}
