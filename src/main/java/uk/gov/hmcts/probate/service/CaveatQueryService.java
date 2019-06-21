package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.request.ReturnedCaveats;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import java.net.URI;
import java.util.List;

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
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final ServiceAuthTokenGenerator serviceAuthTokenGenerator;
    private final IdamAuthenticateUserService idamAuthenticateUserService;

    public List<ReturnedCaveatDetails> findCaveatsById(CaseType caseType, String caveatId) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(REFERENCE, caveatId));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        return runQuery(caseType, jsonQuery);
    }

    public CaveatData findCaveatById(CaseType caseType, String caveatId) {
        BoolQueryBuilder query = boolQuery();

        query.must(matchQuery(REFERENCE, caveatId));

        String jsonQuery = new SearchSourceBuilder().query(query).toString();

        return runQuery(caseType, jsonQuery).get(0).getData();
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
            returnedCaveats = restTemplate.postForObject(uri, entity, ReturnedCaveats.class);
        } catch (HttpClientErrorException e) {
            appInsights.trackEvent(REST_CLIENT_EXCEPTION, e.getMessage());
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        }

        appInsights.trackEvent(REQUEST_SENT, uri.toString());

        return returnedCaveats.getCaveats();
    }
}
