package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCases;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.annotation.Nullable;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REST_CLIENT_EXCEPTION;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseQueryService {
    private static final String GRANT_ISSUED_DATE = "data.grantIssuedDate";
    private static final String STATE = "state";
    private static final String STATE_MATCH = "BOGrantIssued";
    private static final String SERVICE_AUTH = "ServiceAuthorization";
    private static final String AUTHORIZATION = "Authorization";
    private static final String CASE_TYPE_ID = "ctid";
    private static final CaseType CASE_TYPE = CaseType.GRANT_OF_REPRESENTATION;
    private static final String[] STATES_MATCH_GRANT_DELAYED =
        {"BOReadyForExamination", "BOCaseMatchingExamining", "BOExamining",
            "BOReadyToIssue", "BOCaseQA", "BOCaseMatchingIssueGrant"};
    private static final String[] STATES_MATCH_GRANT_AWAITING_DOCUMENTATION = {"CasePrinted"};
    private static final String KEY_GRANT_DELAYED_NOTIFICATION_DATE = "data.grantDelayedNotificationDate";
    private static final String KEY_GRANT_DELAYED_NOTIFICATION_SENT = "data.grantDelayedNotificationSent";
    private static final String KEY_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_DATE =
        "data.grantAwaitingDocumentationNotificationDate";
    private static final String KEY_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT =
        "data.grantAwaitingDocumentatioNotificationSent";
    private static final String KEY_EVIDENCE_HANDLED = "data.evidenceHandled";
    private static final String KEY_PAPER_FORM = "data.paperForm";
    private static final String GRANT_RANGE_QUERY_EXELA = "templates/elasticsearch/caseMatching/"
        + "grants_issued_date_range_query_exela.json";
    private static final String GRANT_RANGE_QUERY_HMRC = "templates/elasticsearch/caseMatching/"
        + "grants_issued_date_range_query_hmrc.json";
    private static final String GRANT_RANGE_QUERY_SMEEFORD = "templates/elasticsearch/caseMatching/"
        + "grants_issued_date_range_query_smeeford.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final IdamAuthenticateUserService idamAuthenticateUserService;
    private final FileSystemResourceService fileSystemResourceService;
    @Value("${data-extract.pagination.size}")
    protected int dataExtractPaginationSize;

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "Entity should be non null in CaseQueryService");
        return result;
    }

    public List<ReturnedCaseDetails> findGrantIssuedCasesWithGrantIssuedDate(String invokedFrom, String queryDate) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(STATE, STATE_MATCH));
        query.must(matchQuery(GRANT_ISSUED_DATE, queryDate));
        String jsonQuery = new SearchSourceBuilder().query(query)
                .size(dataExtractPaginationSize)
                .from(0)
                .toString();

        return runQueryWithPagination(invokedFrom + " findGrantIssuedCasesWithGrantIssuedDate", jsonQuery,
                queryDate, null);
    }

    public List<ReturnedCaseDetails> findAllCasesWithGrantIssuedDate(String invokedFrom, String queryDate) {
        BoolQueryBuilder query = boolQuery();
        query.must(matchQuery(GRANT_ISSUED_DATE, queryDate));
        String jsonQuery = new SearchSourceBuilder().query(query)
                .size(dataExtractPaginationSize)
                .from(0)
                .toString();

        return runQueryWithPagination(invokedFrom + " findAllCasesWithGrantIssuedDate", jsonQuery, queryDate, null);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeExela(String startDate, String endDate) {
        return findCaseStateWithinDateRange("Excela", GRANT_RANGE_QUERY_EXELA, startDate, endDate);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeHMRC(String startDate, String endDate) {
        return findCaseStateWithinDateRange("HMRC", GRANT_RANGE_QUERY_HMRC, startDate, endDate);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeSmeeAndFord(String startDate, String endDate) {
        return findCaseStateWithinDateRange("SMEEFORD", GRANT_RANGE_QUERY_SMEEFORD, startDate, endDate);
    }

    private List<ReturnedCaseDetails> findCaseStateWithinDateRange(String qryFrom, String qry, String startDate,
                                                                   String endDate) {
        String jsonQuery = fileSystemResourceService.getFileFromResourceAsString(qry)
                .replace(":from,", "0,")
                .replace(":size", "" + dataExtractPaginationSize)
                .replace(":fromDate", startDate)
                .replace(":toDate", endDate);

        return runQueryWithPagination(qryFrom + " findCaseStateWithinDateRange", jsonQuery, startDate, endDate);
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
        query.mustNot(existsQuery(KEY_GRANT_DELAYED_NOTIFICATION_SENT));

        String jsonQuery = new SearchSourceBuilder().query(query).size(dataExtractPaginationSize).from(0).toString();

        return runQueryWithPagination("findCasesForGrantDelayed", jsonQuery, queryDate, null);
    }

    public List<ReturnedCaseDetails> findCasesForGrantAwaitingDocumentation(String queryDate) {
        BoolQueryBuilder query = boolQuery();
        BoolQueryBuilder awaitingDocsStateChecks = boolQuery();

        for (String stateToMatch : Arrays.asList(STATES_MATCH_GRANT_AWAITING_DOCUMENTATION)) {
            awaitingDocsStateChecks.should(new MatchQueryBuilder(STATE, stateToMatch));
        }
        awaitingDocsStateChecks.minimumShouldMatch(1);
        query.must(awaitingDocsStateChecks);
        query.must(matchQuery(KEY_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_DATE, queryDate));
        query.must(matchQuery(KEY_PAPER_FORM, NO));
        query.mustNot(existsQuery(KEY_GRANT_AWAITING_DOCUMENTATION_NOTIFICATION_SENT));
        query.mustNot(existsQuery(KEY_EVIDENCE_HANDLED));

        String jsonQuery = new SearchSourceBuilder().query(query).size(dataExtractPaginationSize).from(0).toString();

        return runQueryWithPagination("findCasesForGrantAwaitingDocumentation", jsonQuery, queryDate,
                null);
    }

    private List<ReturnedCaseDetails> runQueryWithPagination(String queryName, String jsonQuery,
                                                             String queryDateStart, String queryDateEnd) {

        List<ReturnedCaseDetails> allResults = new ArrayList<>();
        List<ReturnedCaseDetails> pagedResults = new ArrayList<>();
        int index = 0;
        int pageStart = 0;
        int total = 10000000;
        String paginatedQry = jsonQuery;
        while (index < total) {

            ReturnedCases cases = runQuery(paginatedQry);
            total = cases.getTotal();
            pagedResults = cases.getCases();
            log.info("{} for date:{} to date:{}, from:{} to:{}", queryName, queryDateStart, queryDateEnd, pageStart,
                    (pageStart + dataExtractPaginationSize));
            allResults.addAll(pagedResults);
            index = index + pagedResults.size();
            pageStart = pageStart + dataExtractPaginationSize;
            paginatedQry = updatePageStartOnQry(paginatedQry, pageStart);
        }

        return allResults;
    }

    @Nullable
    private ReturnedCases runQuery(String jsonQuery) {
        log.info("CaseQueryService runQuery: " + jsonQuery);
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
            log.info("DONE idamAuthenticateUserService.getIdamOauth2Token()");
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
        } finally {
            entity = new HttpEntity<>(jsonQuery, tokenHeaders);
        }

        ReturnedCases returnedCases;
        try {
            log.info("Posting object for CaseQueryService...");
            returnedCases = nonNull(restTemplate.postForObject(uri, entity, ReturnedCases.class));
            log.info("...Posted object for CaseQueryService");
        } catch (HttpClientErrorException e) {
            log.error("CaseMatchingException on CaseQueryService, message=" + e.getMessage());
            appInsights.trackEvent(REST_CLIENT_EXCEPTION.toString(), appInsights.trackingMap("exception",
                e.getMessage()));
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        } catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }

        appInsights.trackEvent(REQUEST_SENT.toString(), appInsights.trackingMap("url", uri.toString()));

        log.info("CaseQueryService returnedCases.size = {}", returnedCases.getCases().size());
        return returnedCases;
    }

    private String updatePageStartOnQry(String paginatedQry, int pageStart) {
        int fromIndex = paginatedQry.indexOf("from");
        int fromColIndex = paginatedQry.indexOf(":", fromIndex);
        int fromComIndex = paginatedQry.indexOf(",", fromIndex);
        String start = paginatedQry.substring(0, fromColIndex + 1);
        String end = paginatedQry.substring(fromComIndex + 1);
        String all = start + pageStart + "," + end;
        return all;
    }

}
