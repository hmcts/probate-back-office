package uk.gov.hmcts.probate.service.evidencemanagement.header;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class IdamHttpHeaderFactoryTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private IdamHttpHeaderFactory underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMultiPartHttpHeader() {
        HttpHeaders httpHeaders = underTest.getMultiPartHttpHeader();

        assertTrue(httpHeaders.containsKey("ServiceAuthorization"));
        assertTrue(httpHeaders.containsKey("user-id"));
        assertEquals(MediaType.MULTIPART_FORM_DATA, httpHeaders.getContentType());
    }

    @Test
    public void getApplicationJsonHttpHeader() {
        HttpHeaders httpHeaders = underTest.getApplicationJsonHttpHeader();

        assertTrue(httpHeaders.containsKey("ServiceAuthorization"));
        assertTrue(httpHeaders.containsKey("user-id"));
        assertEquals(MediaType.APPLICATION_JSON, httpHeaders.getContentType());
    }

    @Test
    public void getHttpHeader() {
        HttpHeaders httpHeaders = underTest.getHttpHeader();

        assertTrue(httpHeaders.containsKey("Authorization"));
    }
}
