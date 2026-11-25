package uk.gov.hmcts.probate.service.evidencemanagement.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

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

        assertTrue(httpHeaders.containsHeader("ServiceAuthorization"));
        assertTrue(httpHeaders.containsHeader("user-id"));
        assertEquals(MediaType.MULTIPART_FORM_DATA, httpHeaders.getContentType());
    }

    @Test
    void getApplicationJsonHttpHeader() {
        HttpHeaders httpHeaders = underTest.getApplicationJsonHttpHeader();

        assertTrue(httpHeaders.containsHeader("ServiceAuthorization"));
        assertTrue(httpHeaders.containsHeader("user-id"));
        assertEquals(MediaType.APPLICATION_JSON, httpHeaders.getContentType());
    }

    @Test
    void getAuthorizationHeaders() {
        HttpHeaders httpHeaders = underTest.getAuthorizationHeaders();

        assertTrue(httpHeaders.containsHeader("Authorization"));
        assertEquals(MediaType.APPLICATION_JSON, httpHeaders.getContentType());
    }

    @Test
    void getAuthorizationHeadersForDocuments() {
        HttpHeaders httpHeaders = underTest.getAuthorizationHeadersForDownloadDocuments();

        assertTrue(httpHeaders.containsHeader("Authorization"));
        assertTrue(httpHeaders.containsHeader("user-roles"));
        assertEquals(IdamHttpHeaderFactory.CASEWORKER_PROBATE_SCHEDULER,
                Objects.requireNonNull(httpHeaders.get("user-roles")).getFirst());
    }
}
