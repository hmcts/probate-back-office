package uk.gov.hmcts.probate.service.evidencemanagement.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdamHttpHeaderFactoryTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private IdamHttpHeaderFactory underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMultiPartHttpHeader() {
        HttpHeaders httpHeaders = underTest.getMultiPartHttpHeader();

        assertTrue(httpHeaders.containsKey("ServiceAuthorization"));
        assertTrue(httpHeaders.containsKey("user-id"));
        assertEquals(MediaType.MULTIPART_FORM_DATA, httpHeaders.getContentType());
    }

    @Test
    void getApplicationJsonHttpHeader() {
        HttpHeaders httpHeaders = underTest.getApplicationJsonHttpHeader();

        assertTrue(httpHeaders.containsKey("ServiceAuthorization"));
        assertTrue(httpHeaders.containsKey("user-id"));
        assertEquals(MediaType.APPLICATION_JSON, httpHeaders.getContentType());
    }

    @Test
    void getAuthorizationHeaders() {
        HttpHeaders httpHeaders = underTest.getAuthorizationHeaders();

        assertTrue(httpHeaders.containsKey("Authorization"));
        assertEquals(MediaType.APPLICATION_JSON, httpHeaders.getContentType());
    }

    @Test
    public void getAuthorizationHeadersForDocuments() {
        HttpHeaders httpHeaders = underTest.getAuthorizationHeadersForDownloadDocuments();

        assertTrue(httpHeaders.containsKey("Authorization"));
        assertTrue(httpHeaders.containsKey("user-roles"));
        assertEquals(IdamHttpHeaderFactory.CASEWORKER_PROBATE_SCHEDULER, httpHeaders.get("user-roles").get(0));
    }
}
