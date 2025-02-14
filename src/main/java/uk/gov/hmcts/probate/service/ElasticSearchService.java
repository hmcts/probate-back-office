package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.CCDDataStoreAPIConfiguration;
import uk.gov.hmcts.probate.exception.CaseMatchingException;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {

    private static final String CASE_TYPE_ID = "ctid";

    private final CCDDataStoreAPIConfiguration ccdDataStoreAPIConfiguration;
    private final RestTemplate restTemplate;
    private final HttpHeadersFactory headers;

    public MatchedCases runQuery(CaseType caseType, String jsonQuery) {
        log.debug("ElasticSearchService runQuery: " + jsonQuery);
        URI uri = UriComponentsBuilder
            .fromHttpUrl(ccdDataStoreAPIConfiguration.getHost() + ccdDataStoreAPIConfiguration.getCaseMatchingPath())
            .queryParam(CASE_TYPE_ID, caseType.getCode())
            .build().encode().toUri();

        HttpEntity<String> entity = new HttpEntity<>(jsonQuery, headers.getAuthorizationHeaders());

        MatchedCases matchedCases;
        try {
            matchedCases = restTemplate.postForObject(uri, entity, MatchedCases.class);
        } catch (HttpClientErrorException e) {
            throw new CaseMatchingException(e.getStatusCode(), e.getMessage());
        }

        return matchedCases;
    }
}
