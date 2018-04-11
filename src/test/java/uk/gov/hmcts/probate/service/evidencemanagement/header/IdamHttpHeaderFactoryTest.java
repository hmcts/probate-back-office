package uk.gov.hmcts.probate.service.evidencemanagement.header;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
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
        assertTrue(httpHeaders.containsKey("Authorization"));
        assertEquals(MediaType.MULTIPART_FORM_DATA, httpHeaders.getContentType());
    }

    @Test
    public void getHttpHeader() {
        HttpHeaders httpHeaders = underTest.getHttpHeader();

        assertTrue(httpHeaders.containsKey("Authorization"));
    }
}
