package uk.gov.hmcts.probate.service.evidencemanagement.header;

import org.springframework.http.HttpHeaders;

public interface HttpHeadersFactory {

    HttpHeaders getMultiPartHttpHeader();

    HttpHeaders getApplicationJsonHttpHeader();

    HttpHeaders getAuthorizationHeaders();

    HttpHeaders getElasticSearchAuthorizationHeaders();
}
