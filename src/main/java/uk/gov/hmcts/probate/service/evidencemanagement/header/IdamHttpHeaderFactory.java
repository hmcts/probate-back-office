package uk.gov.hmcts.probate.service.evidencemanagement.header;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
class IdamHttpHeaderFactory implements HttpHeadersFactory {

    public static final String CASEWORKER_PROBATE_SCHEDULER = "caseworker-probate-scheduler";
    private final HttpServletRequest httpServletRequest;
    private static final String USER_ID = "user-id";
    private static final String USER_ROLES = "user-roles";
    private static final String SERVICE_AUTH = "ServiceAuthorization";
    private static final String AUTHORIZATION = "Authorization";

    @Override
    public HttpHeaders getMultiPartHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTH, httpServletRequest.getHeader(SERVICE_AUTH));
        headers.add(USER_ID, httpServletRequest.getHeader(USER_ID));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    @Override
    public HttpHeaders getApplicationJsonHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTH, httpServletRequest.getHeader(SERVICE_AUTH));
        headers.add(USER_ID, httpServletRequest.getHeader(USER_ID));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    @Override
    public HttpHeaders getAuthorizationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTH, httpServletRequest.getHeader(SERVICE_AUTH));
        headers.add(AUTHORIZATION, httpServletRequest.getHeader(AUTHORIZATION));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public HttpHeaders getAuthorizationHeadersForDownloadDocuments() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTH, httpServletRequest.getHeader(SERVICE_AUTH));
        headers.add(AUTHORIZATION, httpServletRequest.getHeader(AUTHORIZATION));
        headers.add(USER_ROLES, CASEWORKER_PROBATE_SCHEDULER);
        return headers;
    }
}
