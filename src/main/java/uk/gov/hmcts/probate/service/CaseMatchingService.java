package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.CCDGatewayConfiguration;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REST_CLIENT_EXCEPTION;

@Service
@RequiredArgsConstructor
public class CaseMatchingService {

    private static final String TEMPLATE_DIRECTORY = "templates/elasticsearch/";
    private static final String ES_QUERY = "case_matching_query.json";
    private static final String CASE_TYPE_ID = "ctid";

    private final CCDGatewayConfiguration ccdGatewayConfiguration;
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final FileSystemResourceService fileSystemResourceService;

    public List<CaseMatch> findMatches(CaseType caseType, CaseMatchingCriteria criteria) {

        String jsonQuery = getQueryTemplate()
                .replace(":deceasedForenames", criteria.getDeceasedForenames())
                .replace(":deceasedSurname", criteria.getDeceasedSurname())
                .replace(":deceasedAliasForenames", criteria.getDeceasedForenames())
                .replace(":deceasedAliasSurname", criteria.getDeceasedSurname())
                .replace(":solsDeceasedAliases", criteria.getDeceasedAliases())
                .replace(":deceasedDateOfBirth", criteria.getDeceasedDateOfBirth())
                .replace(":deceasedDateOfDeath", criteria.getDeceasedDateOfDeath());

        URI uri = UriComponentsBuilder
                .fromHttpUrl(ccdGatewayConfiguration.getHost() + ccdGatewayConfiguration.getCaseMatchingPath())
                .queryParam(CASE_TYPE_ID, caseType.getCode())
                .build().encode().toUri();

        HttpEntity<String> entity = new HttpEntity<>(jsonQuery, headers.getAuthorizationHeaders());

        MatchedCases matchedCases;
        try {
            matchedCases = restTemplate.postForObject(uri, entity, MatchedCases.class);
        } catch (RestClientException e) {
            appInsights.trackEvent(REST_CLIENT_EXCEPTION, e.getMessage());
            return new ArrayList<>();
        }

        appInsights.trackEvent(REQUEST_SENT, uri.toString());

        return matchedCases.getCases().stream()
                .filter(c -> !criteria.getId().equals(c.getId()))
                .map(c -> CaseMatch.buildCaseMatch(c, caseType))
                .collect(Collectors.toList());
    }

    public List<CaseMatch> findCrossMatches(List<CaseType> caseTypes, CaseMatchingCriteria criteria) {
        return caseTypes.stream()
                .map(caseType -> findMatches(caseType, criteria))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String getQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY + ES_QUERY);
    }
}
