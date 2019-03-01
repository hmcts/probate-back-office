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
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCases;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.net.URI;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REST_CLIENT_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseQueryService {

    private static final String DOCUMENT_TYPE = "data.probateDocumentsGenerated.value.DocumentType";
    private static final String DOCUMENT_DATE = "data.probateDocumentsGenerated.value.DocumentDateAdded";
    private static final String STATE = "state";
    private static final String STATE_MATCH = "BOGrantIssued";
    private static final String CASE_TYPE_ID = "ctid";
    private static final CaseType CASE_TYPE = CaseType.GRANT_OF_REPRESENTATION;
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;

    public List<CaseDetails> findCasesWithDatedDocument(String documentTypeGenerated, String queryDate) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(STATE, STATE_MATCH));
        query.must(matchQuery(DOCUMENT_TYPE, documentTypeGenerated));
        query.must(matchQuery(DOCUMENT_DATE, queryDate));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        return runQuery(jsonQuery);
    }

    public List<CaseDetails> findCaseStateWithinTimeFrame(String documentTypeGenerated,
                                                   String startDate, String endDate) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(STATE, STATE_MATCH));
        query.must(matchQuery(DOCUMENT_TYPE, documentTypeGenerated));
        query.must(rangeQuery(DOCUMENT_DATE).gt(startDate).lt(endDate));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        return runQuery(jsonQuery);
    }

    private List<CaseDetails> runQuery(String jsonQuery) {
        log.info("GrantMatchingService runQuery: " + jsonQuery);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(ccdDataStoreAPIConfiguration.getHost() + ccdDataStoreAPIConfiguration.getCaseMatchingPath())
                .queryParam(CASE_TYPE_ID, CASE_TYPE.getCode())
                .build().encode().toUri();

        HttpEntity<String> entity = new HttpEntity<>(jsonQuery, headers.getAuthorizationHeaders());

        ReturnedCases returnedCases;
        try {
            returnedCases = restTemplate.postForObject(uri, entity, ReturnedCases.class);
        } catch (HttpClientErrorException e) {
            appInsights.trackEvent(REST_CLIENT_EXCEPTION, e.getMessage());
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        }

        appInsights.trackEvent(REQUEST_SENT, uri.toString());

        return returnedCases.getCases();
    }
}
