package uk.gov.hmcts.probate.controller.exception;

import org.springframework.web.client.HttpClientErrorException;

public class PDFClientException extends RuntimeException {

    private final HttpClientErrorException httpClientErrorException;

    public PDFClientException(HttpClientErrorException httpClientErrorException) {
        this.httpClientErrorException = httpClientErrorException;
    }

    public HttpClientErrorException getHttpClientErrorException() {
        return httpClientErrorException;
    }
}
