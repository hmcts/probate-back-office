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
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCases;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import jakarta.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseQueryService {
    private static final String GRANT_ISSUED_DATE = "data.grantIssuedDate";
    private static final String STATE = "state";
    private static final String[] STATE_MATCH = {"BOGrantIssued", "BOPostGrantIssued"};
    private static final String SERVICE_AUTH = "ServiceAuthorization";
    private static final String AUTHORIZATION = "Authorization";
    private static final String CASE_TYPE_ID = "ctid";
    private static final CaseType CASE_TYPE_GOP = CaseType.GRANT_OF_REPRESENTATION;
    private static final CaseType CASE_TYPE_CAVEAT = CaseType.CAVEAT;
    private static final String[] STATES_MATCH_GRANT_DELAYED =
        {"BOCaseMatchingExamining", "BOReadyToIssue", "BOCaseQA", "BOCaseMatchingIssueGrant"};
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
    private static final String DORMANT_QUERY = "templates/elasticsearch/caseMatching/"
            + "dormant_date_range_query.json";
    private static final String REACTIVATE_DORMANT_QUERY = "templates/elasticsearch/caseMatching/"
            + "reactivate_dormant_date_range_query.json";
    private static final String DISPOSE_GOP_QUERY = "templates/elasticsearch/caseMatching/"
            + "dispose_gop_date_range_query.json";
    private static final String DISPOSE_CAVEAT_QUERY = "templates/elasticsearch/caseMatching/"
            + "dispose_caveat_date_range_query.json";
    private static final String DISPOSE_GOP_DELETED_QUERY = "templates/elasticsearch/caseMatching/"
            + "dispose_gop_deleted_query.json";
    private static final String SORT_COLUMN = "id";
    private static final String QUERY_NAME_MAKE_DORMANT = "MakeDormant";
    private static final String QUERY_NAME_REACTIVATE_DORMANT = "ReactivateDormant";
    private static final String QUERY_NAME_EXELA = "Exela";
    private static final String QUERY_NAME_HMRC = "HMRC";
    private static final String QUERY_NAME_SMEEFORD = "SMEEFORD";
    private static final String QUERY_NAME_DISPOSE_DRAFT_GOP = "DisposeDraftGOP";
    private static final String QUERY_NAME_DISPOSE_DELETED_GOP = "DisposeDeletedGOP";
    private static final String QUERY_NAME_DISPOSE_DRAFT_CAVEAT = "DisposeDraftCaveat";
    private static final String QUERY_NAME_DISPOSE_REMINDER = "DisposalReminder";
    private final RestTemplate restTemplate;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final SecurityUtils securityUtils;
    private final FileSystemResourceService fileSystemResourceService;
    @Value("${data-extract.pagination.size}")
    protected int dataExtractPaginationSize;

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "Entity should be non null in CaseQueryService");
        return result;
    }

    public List<ReturnedCaseDetails> findGrantIssuedCasesWithGrantIssuedDate(String invokedFrom, String queryDate) {
        BoolQueryBuilder query = boolQuery();
        BoolQueryBuilder stateChecks = boolQuery();

        for (String stateToMatch : Arrays.asList(STATE_MATCH)) {
            stateChecks.should(new MatchQueryBuilder(STATE, stateToMatch));
        }
        stateChecks.minimumShouldMatch(1);

        query.must(stateChecks);
        query.must(matchQuery(GRANT_ISSUED_DATE, queryDate));
        String jsonQuery = new SearchSourceBuilder().query(query)
                .size(dataExtractPaginationSize)
                .from(0)
                .sort(SORT_COLUMN)
                .toString();

        return runQueryWithPagination(invokedFrom + " findGrantIssuedCasesWithGrantIssuedDate", jsonQuery,
                queryDate, queryDate);
    }

    public List<ReturnedCaseDetails> findAllCasesWithGrantIssuedDate(String invokedFrom, String queryDate) {
        BoolQueryBuilder query = boolQuery();
        query.must(matchQuery(GRANT_ISSUED_DATE, queryDate));
        String jsonQuery = new SearchSourceBuilder().query(query)
                .size(dataExtractPaginationSize)
                .from(0)
                .sort(SORT_COLUMN)
                .toString();

        return runQueryWithPagination(invokedFrom + " findAllCasesWithGrantIssuedDate", jsonQuery, queryDate,
                queryDate);
    }

    public List<ReturnedCaseDetails> findCaseToBeMadeDormant(String dormancyStartDate,String endDate) {
        //When a new state is being added ,it should be added in the elastic search query DORMANT_QUERY
        return findCaseStateWithinDateRange(QUERY_NAME_MAKE_DORMANT, DORMANT_QUERY, dormancyStartDate, endDate);
    }

    public List<ReturnedCaseDetails> findCaseToBeReactivatedFromDormant(String date) {
        return findCaseStateWithinDateRange(QUERY_NAME_REACTIVATE_DORMANT, REACTIVATE_DORMANT_QUERY, date, date);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeExela(String startDate, String endDate) {
        return findCaseStateWithinDateRange(QUERY_NAME_EXELA, GRANT_RANGE_QUERY_EXELA, startDate, endDate);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeHMRC(String startDate, String endDate) {
        return findCaseStateWithinDateRange(QUERY_NAME_HMRC, GRANT_RANGE_QUERY_HMRC, startDate, endDate);
    }

    public List<ReturnedCaseDetails> findCaseStateWithinDateRangeSmeeAndFord(String startDate, String endDate) {
        return findCaseStateWithinDateRange(QUERY_NAME_SMEEFORD, GRANT_RANGE_QUERY_SMEEFORD, startDate, endDate);
    }

    private List<ReturnedCaseDetails> findCaseStateWithinDateRange(String qryFrom, String qry, String startDate,
                                                                   String endDate) {
        return findCaseStateWithinDateRange(qryFrom, qry, startDate, endDate, CASE_TYPE_GOP);
    }

    private List<ReturnedCaseDetails> findCaseStateWithinDateRange(String qryFrom, String qry, String startDate,
                                                                   String endDate, CaseType caseType) {
        String jsonQuery = fileSystemResourceService.getFileFromResourceAsString(qry)
                .replace(":from,", "0,")
                .replace(":size", "" + dataExtractPaginationSize)
                .replace(":fromDate", startDate)
                .replace(":toDate", endDate);

        return runQueryWithPagination(qryFrom + " findCaseStateWithinDateRange",
                jsonQuery, startDate, endDate, caseType);
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

        String jsonQuery = new SearchSourceBuilder().query(query)
                .size(dataExtractPaginationSize)
                .from(0)
                .sort(SORT_COLUMN)
                .toString();

        return runQueryWithPagination("findCasesForGrantDelayed", jsonQuery, queryDate, queryDate);
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

        String jsonQuery = new SearchSourceBuilder().query(query)
                .size(dataExtractPaginationSize)
                .from(0)
                .sort(SORT_COLUMN)
                .toString();

        return runQueryWithPagination("findCasesForGrantAwaitingDocumentation", jsonQuery, queryDate,
                queryDate);
    }

    private List<ReturnedCaseDetails> runQueryWithPagination(String queryName, String jsonQuery,
                                                             String queryDateStart, String queryDateEnd) {
        return runQueryWithPagination(queryName, jsonQuery, queryDateStart, queryDateEnd, CASE_TYPE_GOP);
    }

    private List<ReturnedCaseDetails> runQueryWithPagination(String queryName, String jsonQuery,
                                                             String queryDateStart, String queryDateEnd,
                                                             CaseType caseType) {

        List<ReturnedCaseDetails> allResults = new ArrayList<>();
        List<ReturnedCaseDetails> pagedResults = new ArrayList<>();
        int index = 0;
        int pageStart = 0;
        int total = 10000000;
        String paginatedQry = jsonQuery;
        while (index < total) {
            log.info("Querying for {} from date:{} to date:{}, from index:{} to index:{}", queryName, queryDateStart,
                    queryDateEnd, pageStart, (pageStart + dataExtractPaginationSize));
            ReturnedCases cases = runQuery(paginatedQry, caseType, queryName);
            total = cases.getTotal();
            pagedResults = cases.getCases();
            if (!CollectionUtils.isEmpty(pagedResults)) {
                log.info("index: {}, first|last case ref: {}|{}", index, pagedResults.get(0).getId(),
                        pagedResults.get(pagedResults.size() - 1).getId());
            } else {
                log.info("index: {}, first|last case ref: {}|{}", index, "Not found", "Not found");
            }
            allResults.addAll(pagedResults);
            index = index + pagedResults.size();
            pageStart = pageStart + dataExtractPaginationSize;
            paginatedQry = updatePageStartOnQry(paginatedQry, pageStart);
        }

        return allResults;
    }

    @Nullable
    private ReturnedCases runQuery(String jsonQuery, CaseType caseType, String queryName) {
        log.debug("CaseQueryService runQuery: {} for caseType: {} queryName: {}",
                jsonQuery, caseType.getCode(), queryName);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(ccdDataStoreAPIConfiguration.getHost()
                        + ccdDataStoreAPIConfiguration.getCaseMatchingPath())
                .queryParam(CASE_TYPE_ID, caseType.getCode())
                .build().encode().toUri();

        HttpHeaders tokenHeaders = null;
        HttpEntity<String> entity;
        try {
            tokenHeaders = headers.getAuthorizationHeaders();

        } catch (Exception e) {
            tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
            tokenHeaders.add(SERVICE_AUTH, "Bearer " + serviceAuthTokenGenerator.generate());
            tokenHeaders.add(AUTHORIZATION, getAuthToken(queryName));
        } finally {
            entity = new HttpEntity<>(jsonQuery, tokenHeaders);
        }

        ReturnedCases returnedCases;
        try {
            log.info("Posting object for CaseQueryService...jsonQry: {}", jsonQuery);
            returnedCases = nonNull(restTemplate.postForObject(uri, entity, ReturnedCases.class));
            log.info("...Posted object for CaseQueryService");
        } catch (HttpClientErrorException e) {
            log.error("CaseMatchingException on CaseQueryService, message=" + e.getMessage());
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        } catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }

        log.info("CaseQueryService returnedCases.size = {}", returnedCases.getCases().size());
        return returnedCases;
    }

    private String updatePageStartOnQry(String paginatedQry, int pageStart) {
        return paginatedQry.replaceFirst("\"from\": *\\d*,", "\"from\":" + pageStart + ",");
    }

    public List<ReturnedCaseDetails> findInactiveCaseForDisposalReminder(String fromDate, String toDate) {
        return findCaseStateWithinDateRange(QUERY_NAME_DISPOSE_REMINDER, DISPOSE_GOP_QUERY, fromDate, toDate);
    }

    public List<ReturnedCaseDetails> findInactiveGOPCaseForDisposal(String fromDate, String toDate) {
        return findCaseStateWithinDateRange(QUERY_NAME_DISPOSE_DRAFT_GOP, DISPOSE_GOP_QUERY, fromDate, toDate);
    }

    public List<ReturnedCaseDetails> findDeletedGOPCaseForDisposal(String fromDate, String toDate) {
        return findCaseStateWithinDateRange(QUERY_NAME_DISPOSE_DELETED_GOP, DISPOSE_GOP_DELETED_QUERY,
                fromDate, toDate);
    }

    public List<ReturnedCaseDetails> findInactiveCaveatCaseForDisposal(String fromDate, String toDate) {
        return findCaseStateWithinDateRange(QUERY_NAME_DISPOSE_DRAFT_CAVEAT, DISPOSE_CAVEAT_QUERY,
                fromDate, toDate, CASE_TYPE_CAVEAT);
    }

    private String getAuthToken(String queryName) {
        if (queryName.startsWith(QUERY_NAME_DISPOSE_REMINDER)
                || queryName.startsWith(QUERY_NAME_DISPOSE_DRAFT_GOP)
                || queryName.startsWith(QUERY_NAME_DISPOSE_DELETED_GOP)
                || queryName.startsWith(QUERY_NAME_DISPOSE_DRAFT_CAVEAT)) {
            log.info("securityUtils.getSchedulerToken()");
            return securityUtils.getSchedulerToken();
        }
        log.info("securityUtils.getCaseworkerToken()");
        return securityUtils.getCaseworkerToken();
    }

}
