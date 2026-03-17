package uk.gov.hmcts.probate.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
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
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.exception.ClientDataException;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveats;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.probate.model.cases.CaseState;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static uk.gov.hmcts.probate.model.CaseType.CAVEAT;

import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;
import static uk.gov.hmcts.probate.model.ccd.EventId.CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaveatQueryService {

    private static final String SERVICE_AUTH = "ServiceAuthorization";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String CASE_TYPE_ID = "ctid";
    private static final String REFERENCE = "reference";
    private static final String PA_APP_CREATED = "PAAppCreated";
    private static final String STATE = "state";
    private static final String CAVEAT_NOT_FOUND_CODE = "caveatNotFound";
    private static final String CAVEAT_NOT_FOUND_CODE_WELSH = "caveatNotFoundWelsh";
    private static final String DATA_EXPIRY_DATE = "data.expiryDate";
    private static final String CAVEAT_NOT_MATCHED = "CaveatNotMatched";
    private static final String AWAITING_CAVEAT_RESOLUTION = "AwaitingCaveatResolution";
    private static final String WARNING_VALIDATION = "WarningValidation";
    private static final String AWAITING_WARNING_RESPONSE = "AwaitingWarningResponse";
    private static final String EVENT_DESCRIPTOR_CAVEAT_EXPIRED = "Caveat Auto Expired";
    private static final String[] EXPIRABLE_STATES = {
        CAVEAT_NOT_MATCHED,
        AWAITING_CAVEAT_RESOLUTION,
        WARNING_VALIDATION,
        AWAITING_WARNING_RESPONSE
    };

    private final RestTemplate restTemplate;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final SecurityUtils securityUtils;
    private final CcdClientApi ccdClientApi;
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    @Value("${data-extract.pagination.size}")
    protected int dataExtractPaginationSize;
    private static final String SORT_COLUMN = "id";

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "Entity should be non null in CaveatQueryService");
        return result;
    }

    public CaveatData findCaveatById(CaseType caseType, String caveatId) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(REFERENCE, caveatId));
        query.mustNot(matchQuery(STATE, PA_APP_CREATED));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        ReturnedCaveats foundCaveats = runQuery(caseType, jsonQuery);

        if (foundCaveats.getCaveats().size() != 1) {
            String[] args = {caveatId};
            String userMessage = businessValidationMessageRetriever.getMessage(CAVEAT_NOT_FOUND_CODE, args, Locale.UK);
            String userMessageWelsh = businessValidationMessageRetriever.getMessage(CAVEAT_NOT_FOUND_CODE_WELSH, args,
                    Locale.UK);
            throw new BusinessValidationException(userMessage,
                "Could not find any caveats for the entered caveat id: " + caveatId, userMessageWelsh);
        }
        return foundCaveats.getCaveats().getFirst().getData();
    }


    private ReturnedCaveats runQuery(CaseType caseType, String jsonQuery) {
        log.debug("CaveatQueryService runQuery: " + jsonQuery);
        URI uri = UriComponentsBuilder
            .fromHttpUrl(ccdDataStoreAPIConfiguration.getHost() + ccdDataStoreAPIConfiguration.getCaseMatchingPath())
            .queryParam(CASE_TYPE_ID, caseType.getCode())
            .build().encode().toUri();

        HttpHeaders tokenHeaders = null;
        HttpEntity<String> entity;
        try {
            tokenHeaders = headers.getAuthorizationHeaders();
        } catch (Exception e) {
            tokenHeaders = new HttpHeaders();
            tokenHeaders.add(SERVICE_AUTH, BEARER + serviceAuthTokenGenerator.generate());
            tokenHeaders.add(AUTHORIZATION, securityUtils.getCaseworkerToken());
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
        } finally {
            entity = new HttpEntity<>(jsonQuery, tokenHeaders);
            log.info("Data search - caveat cases: " + entity.getBody());
        }

        ReturnedCaveats returnedCaveats;
        try {
            returnedCaveats = nonNull(restTemplate.postForObject(uri, entity, ReturnedCaveats.class));
        } catch (HttpClientErrorException e) {
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        } catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }

        return returnedCaveats;
    }

    public void findAndExpireCaveatExpiredCases(String expiryDate) {
        securityUtils.setSecurityContextUserAsScheduler();

        log.info("Search for expired Caveats for expiryDate: {}", expiryDate);
        BoolQueryBuilder query = buildExpiryQuery(expiryDate);
        List<String> failedCases = new ArrayList<>();
        processExpiredCaveats(query, failedCases);

        if (!failedCases.isEmpty()) {
            log.error("Caveat autoExpire failed for cases: {}", failedCases);
        }
    }

    private BoolQueryBuilder buildExpiryQuery(String expiryDate) {
        return boolQuery()
                .filter(rangeQuery(DATA_EXPIRY_DATE).lte(expiryDate))
                .filter(termsQuery(STATE, EXPIRABLE_STATES))
                .minimumShouldMatch(1);
    }

    private void processExpiredCaveats(BoolQueryBuilder query, List<String> failedCases) {
        Long[] searchAfterValues = null;
        List<ReturnedCaveatDetails> pageResults;
        do {
            pageResults = fetchPage(query, searchAfterValues);
            log.info("Processing {} caveats in current page", pageResults.size());
            processPage(pageResults, failedCases);
            searchAfterValues = getNextSearchAfter(pageResults);
        } while (hasMorePages(pageResults));
    }

    private Long[] getNextSearchAfter(List<ReturnedCaveatDetails> pageResults) {
        if (pageResults.isEmpty()) {
            return null;
        }
        ReturnedCaveatDetails last = pageResults.get(pageResults.size() - 1);
        return new Long[]{last.getId()};
    }

    private boolean hasMorePages(List<ReturnedCaveatDetails> pageResults) {
        return pageResults.size() == dataExtractPaginationSize;
    }

    private void processPage(List<ReturnedCaveatDetails> pageResults, List<String> failedCases) {
        SecurityDTO securityDto = securityUtils.getSecurityDTO();

        for (ReturnedCaveatDetails caveat : pageResults) {
            expireCaveat(caveat, securityDto, failedCases);
        }
    }

    private void expireCaveat(ReturnedCaveatDetails caveat, SecurityDTO securityDto, List<String> failedCases) {
        EventId eventId = getEventIdForCaveatToExpireGivenPreconditionState(caveat.getState());
        uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData caseData =
                uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData.builder()
                        .autoClosedExpiry(Boolean.TRUE)
                        .build();
        updateCaseAsCaseworker(
                String.valueOf(caveat.getId()),
                caseData,
                caveat.getLastModified(),
                eventId,
                securityDto,
                failedCases
        );
    }

    private List<ReturnedCaveatDetails> fetchPage(BoolQueryBuilder query, Long[] searchAfterValues) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(query)
                .sort(SORT_COLUMN, SortOrder.ASC)
                .size(dataExtractPaginationSize);

        if (searchAfterValues != null) {
            sourceBuilder.searchAfter(searchAfterValues);
        }
        String jsonQuery = sourceBuilder.toString();
        return runQuery(CAVEAT, jsonQuery).getCaveats();
    }

    private EventId getEventIdForCaveatToExpireGivenPreconditionState(CaseState caveatState) {
        return switch (caveatState) {
            case CAVEAT_NOT_MATCHED -> CAVEAT_EXPIRED_FOR_CAVEAT_NOT_MATCHED;
            case CAVEAT_AWAITING_RESOLUTION -> CAVEAT_EXPIRED_FOR_AWAITING_RESOLUTION;
            case CAVEAT_AWAITING_WARNING_RESPONSE -> CAVEAT_EXPIRED_FOR_AWAITING_WARNING_RESPONSE;
            case CAVEAT_WARNING_VALIDATION -> CAVEAT_EXPIRED_FOR_WARNNG_VALIDATION;
            default -> throw new IllegalStateException("Unexpected state for Caveat Auto Expiry: " + caveatState);
        };
    }

    private void updateCaseAsCaseworker(String caseId, Object caseData, LocalDateTime lastModified,
                                        EventId eventIdToStart, SecurityDTO securityDto, List<String> failedCases) {
        try {
            ccdClientApi.updateCaseAsCaseworker(CcdCaseType.CAVEAT, caseId, lastModified, caseData, eventIdToStart,
                            securityDto, EVENT_DESCRIPTOR_CAVEAT_EXPIRED, EVENT_DESCRIPTOR_CAVEAT_EXPIRED);
            log.info("Caveat autoExpired: {}", caseId);
        } catch (RuntimeException e) {
            log.info("Caveat autoExpire failure for case: {}, due to {}", caseId, e.getMessage());
            failedCases.add(caseId);
        }
    }
}
