package uk.gov.hmcts.probate.service.evidencemanagement.header;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
class IdamHttpHeaderFactory implements HttpHeadersFactory {

    private final HttpServletRequest httpServletRequest;
    private static final String SERVICE_AUTH = "ServiceAuthorization";
    private static final String AUTHORISATION = "Authorization";
    private static final String USER_ID = "user-id";

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
    public HttpHeaders getHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORISATION, httpServletRequest.getHeader(SERVICE_AUTH));
        return headers;
    }
}
