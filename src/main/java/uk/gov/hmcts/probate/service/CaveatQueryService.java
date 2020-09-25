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
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveats;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REST_CLIENT_EXCEPTION;

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

    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final AuthTokenGenerator serviceAuthTokenGenerator;
    private final IdamAuthenticateUserService idamAuthenticateUserService;
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public CaveatData findCaveatById(CaseType caseType, String caveatId) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(REFERENCE, caveatId));
        query.mustNot(matchQuery(STATE, PA_APP_CREATED));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        List<ReturnedCaveatDetails> foundCaveats = runQuery(caseType, jsonQuery);
        if (foundCaveats.size() != 1) {
            String[] args = {caveatId};
            String userMessage = businessValidationMessageRetriever.getMessage(CAVEAT_NOT_FOUND_CODE, args, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "Could not find any caveats for the entered caveat id: " + caveatId);
        }
        return foundCaveats.get(0).getData();
    }
    
    private List<ReturnedCaveatDetails> runQuery(CaseType caseType, String jsonQuery) {
        log.info("CaveatMatchingService runQuery: " + jsonQuery);
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
            tokenHeaders.add(AUTHORIZATION, idamAuthenticateUserService.getIdamOauth2Token());
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
        } finally {
            entity = new HttpEntity<>(jsonQuery, tokenHeaders);
            log.info("Data search - caveat cases: " + entity);
        }

        ReturnedCaveats returnedCaveats;
        try {
            returnedCaveats = nonNull(restTemplate.postForObject(uri, entity, ReturnedCaveats.class));
        } catch (HttpClientErrorException e) {
            appInsights.trackEvent(REST_CLIENT_EXCEPTION, e.getMessage());
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        }catch (IllegalStateException e) {
            throw new ClientDataException(e.getMessage());
        }

        appInsights.trackEvent(REQUEST_SENT, uri.toString());
        return returnedCaveats.getCaveats();
    }

    private static <T> T nonNull(@Nullable T result) {
        Assert.state(result != null, "Entity should be non null in CaveatQueryService");
        return result;
    }
}
