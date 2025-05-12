package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveats;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import jakarta.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static uk.gov.hmcts.probate.model.CaseType.CAVEAT;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaveatQueryService {

    private static final String SERVICE_AUTH = "ServiceAuthorization";
    private static final String AUTHORIZATION = "Authorization";
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

    private final RestTemplate restTemplate;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final SecurityUtils securityUtils;
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "Entity should be non null in CaveatQueryService");
        return result;
    }

    public CaveatData findCaveatById(CaseType caseType, String caveatId) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(REFERENCE, caveatId));
        query.mustNot(matchQuery(STATE, PA_APP_CREATED));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        List<ReturnedCaveatDetails> foundCaveats = runQuery(caseType, jsonQuery);
        if (foundCaveats.size() != 1) {
            String[] args = {caveatId};
            String userMessage = businessValidationMessageRetriever.getMessage(CAVEAT_NOT_FOUND_CODE, args, Locale.UK);
            String userMessageWelsh = businessValidationMessageRetriever.getMessage(CAVEAT_NOT_FOUND_CODE_WELSH, args,
                    Locale.UK);
            throw new BusinessValidationException(userMessage,
                "Could not find any caveats for the entered caveat id: " + caveatId, userMessageWelsh);
        }
        return foundCaveats.get(0).getData();
    }

    public List<ReturnedCaveatDetails> findCaveatDraftCases(String startDate,
                                                                   String endDate, CaseType caseType) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(STATE, PA_APP_CREATED));
        query.filter(rangeQuery("last_modified").gte(startDate).lte(endDate));
        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        return runQuery(caseType, jsonQuery);
    }

    private List<ReturnedCaveatDetails> runQuery(CaseType caseType, String jsonQuery) {
        log.debug("CaveatMatchingService runQuery: " + jsonQuery);
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
            tokenHeaders.add(SERVICE_AUTH, "Bearer " + serviceAuthTokenGenerator.generate());
            tokenHeaders.add(AUTHORIZATION, securityUtils.getCaseworkerToken());
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
        } finally {
            entity = new HttpEntity<>(jsonQuery, tokenHeaders);
            log.info("Data search - caveat cases: " + entity);
        }

        ReturnedCaveats returnedCaveats;
        try {
            returnedCaveats = nonNull(restTemplate.postForObject(uri, entity, ReturnedCaveats.class));
        } catch (HttpClientErrorException e) {
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        } catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }

        return returnedCaveats.getCaveats();
    }

    public List<ReturnedCaveatDetails> findCaveatExpiredCases(String expiryDate) {
        BoolQueryBuilder query = boolQuery()
                .must(matchQuery(DATA_EXPIRY_DATE, expiryDate))
                .should(matchQuery(STATE, CAVEAT_NOT_MATCHED))
                .should(matchQuery(STATE, AWAITING_CAVEAT_RESOLUTION))
                .should(matchQuery(STATE, WARNING_VALIDATION))
                .should(matchQuery(STATE, AWAITING_WARNING_RESPONSE))
                .minimumShouldMatch(1);
        String jsonQuery = new SearchSourceBuilder().query(query).toString();
        return runQuery(CAVEAT, jsonQuery);
    }
}
