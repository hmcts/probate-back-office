package uk.gov.hmcts.probate.service.evidencemanagement.header;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Data
@Service
class IdamHttpHeaderFactory implements HttpHeadersFactory {

    private final HttpServletRequest httpServletRequest;

    @Override
    public HttpHeaders getMultiPartHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", httpServletRequest.getHeader("Authorization"));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    @Override
    public HttpHeaders getHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", httpServletRequest.getHeader("Authorization"));
        return headers;
    }
}
