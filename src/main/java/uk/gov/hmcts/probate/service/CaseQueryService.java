package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCases;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.net.URI;
import java.util.Arrays;
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

    private static final String DOCUMENT_DATE = "data.grantIssuedDate";
    private static final String STATE = "state";
    private static final String STATE_MATCH = "BOGrantIssued";
    private static final String SERVICE_AUTH = "ServiceAuthorization";
    private static final String AUTHORIZATION = "Authorization";
    private static final String CASE_TYPE_ID = "ctid";
    private static final CaseType CASE_TYPE = CaseType.GRANT_OF_REPRESENTATION;
    private static final String[] STATES_MATCH_GRANT_DELAYED = {"BOReadyForExamination", "BOCaseMatchingExamining", "BOExamining",
        "BOReadyToIssue", "BOCaseQA", "BOCaseMatchingIssueGrant"};
    private static final String KEY_GRANT_DELAYED_NOTIFICATION_DATE = "created_date";  //TODO change to "data.grantDelayedNotificationDate";
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final IdamAuthenticateUserService idamAuthenticateUserService;

    public List<ReturnedCaseDetails> findCasesWithDatedDocument(String queryDate) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(STATE, STATE_MATCH));
        query.must(matchQuery(DOCUMENT_DATE, queryDate));

        //String jsonQuery = new SearchSourceBuilder().query(query).size(10000).toString();
        //TODO change to above
        String jsonQuery = new SearchSourceBuilder().query(query).size(1).toString();

        return runQuery(jsonQuery);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinTimeFrame(String startDate, String endDate) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(STATE, STATE_MATCH));
        query.must(rangeQuery(DOCUMENT_DATE).gte(startDate).lte(endDate));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        return runQuery(jsonQuery);
    }

    public List<ReturnedCaseDetails> findCasesForGrantDelayed(String queryDate) {
        
        BoolQueryBuilder query = boolQuery();
        BoolQueryBuilder oredStateChecks = boolQuery();

        for (String stateToMatch : Arrays.asList(STATES_MATCH_GRANT_DELAYED)) {
            oredStateChecks.should(new MatchQueryBuilder(STATE, stateToMatch));
        }
        oredStateChecks.minimumShouldMatch(1);
        
        query.must(oredStateChecks);
        query.must(matchQuery(KEY_GRANT_DELAYED_NOTIFICATION_DATE, queryDate));

        String jsonQuery = new SearchSourceBuilder().query(query).size(10000).toString();

        return runQuery(jsonQuery);
    }

    private List<ReturnedCaseDetails> runQuery(String jsonQuery) {
        log.info("GrantMatchingService runQuery: " + jsonQuery);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(ccdDataStoreAPIConfiguration.getHost() + ccdDataStoreAPIConfiguration.getCaseMatchingPath())
                .queryParam(CASE_TYPE_ID, CASE_TYPE.getCode())
                .build().encode().toUri();

        HttpHeaders tokenHeaders = null;
        HttpEntity<String> entity;
        try {
            tokenHeaders = headers.getAuthorizationHeaders();
        } catch (Exception e) {
            tokenHeaders = new HttpHeaders();
            tokenHeaders.add(SERVICE_AUTH, "Bearer " + serviceAuthTokenGenerator.generate());
            tokenHeaders.add(AUTHORIZATION, idamAuthenticateUserService.getIdamOauth2Token());
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
        } finally {
            entity = new HttpEntity<>(jsonQuery, tokenHeaders);
            log.info("Data extract Elastic search entity: " + entity);
        }

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