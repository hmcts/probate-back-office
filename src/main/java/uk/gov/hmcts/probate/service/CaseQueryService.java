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
import java.time.LocalDate;
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
    @Value("${data-extract.block.size}")
    protected int dataExtractBlockSize;
    @Value("${data-extract.block.numDaysInclusive}")
    protected int numDaysBlock;
    @Value("${data-extract.smee-and-ford.size}")
    protected int dataExtractSmeeAndFordSize;

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
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final IdamAuthenticateUserService idamAuthenticateUserService;
    private final FileSystemResourceService fileSystemResourceService;

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "Entity should be non null in CaseQueryService");
        return result;
    }

    public List<ReturnedCaseDetails> findCasesWithDatedDocument(String queryDate) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(STATE, STATE_MATCH));
        query.must(matchQuery(GRANT_ISSUED_DATE, queryDate));

        String jsonQuery = new SearchSourceBuilder().query(query).size(10000).toString();

        return runQuery(jsonQuery);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeExela(String startDate, String endDate) {
        return findCaseStateWithinDateRange(dataExtractBlockSize, GRANT_RANGE_QUERY_EXELA, startDate, endDate);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeHMRC(String startDate, String endDate) {
        return findCaseStateWithinDateRange(dataExtractBlockSize, GRANT_RANGE_QUERY_HMRC, startDate, endDate);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeSmeeAndFord(String startDate, String endDate) {
        return findCaseStateWithinDateRange(dataExtractSmeeAndFordSize, GRANT_RANGE_QUERY_HMRC, startDate, endDate);
    }

    private List<ReturnedCaseDetails> findCaseStateWithinDateRange(int size, String qry, String startDate,
                                                                  String endDate) {
        List<ReturnedCaseDetails> allCases = new ArrayList<>();
        LocalDate end = LocalDate.parse(endDate, DATE_FORMAT);
        LocalDate counter = LocalDate.parse(startDate, DATE_FORMAT);
        while (!counter.isAfter(end)) {
            String stBlock = counter.format(DATE_FORMAT);
            LocalDate endCounter = counter.plusDays(numDaysBlock);
            if (endCounter.isAfter(end)) {
                endCounter = end;
            }
            String endBlock = endCounter.format(DATE_FORMAT);
            log.info("findCaseStateWithinDateRange stBlock:" + stBlock + " endBlock:" + endBlock + " days:" 
                + (counter.datesUntil(endCounter).count() + 1));
            String jsonQuery = fileSystemResourceService.getFileFromResourceAsString(qry)
                .replace(":size", "" + size)
                .replace(":fromDate", stBlock)
                .replace(":toDate", endBlock);
            List<ReturnedCaseDetails> blockCases = runQuery(jsonQuery);
            if (blockCases.size() == size) {
                String message = "Number of cases returned during data range query at max block size for "
                    + stBlock + " to " + endBlock;
                log.info(message);
                throw new ClientDataException(message);
            }
            allCases.addAll(blockCases);

            counter = endCounter.plusDays(1);
        }

        return allCases;
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

        String jsonQuery = new SearchSourceBuilder().query(query).size(10000).toString();

        return runQuery(jsonQuery);
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

        String jsonQuery = new SearchSourceBuilder().query(query).size(10000).toString();

        return runQuery(jsonQuery);
    }

    @Nullable
    private List<ReturnedCaseDetails> runQuery(String jsonQuery) {
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
            appInsights.trackEvent(REST_CLIENT_EXCEPTION, e.getMessage());
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        } catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }

        appInsights.trackEvent(REQUEST_SENT, uri.toString());

        log.info("CaseQueryService returnedCases.size = {}", returnedCases.getCases().size());
        return returnedCases.getCases();
    }
}