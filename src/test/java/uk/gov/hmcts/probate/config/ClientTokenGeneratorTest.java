package uk.gov.hmcts.probate.config;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.ClientException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientTokenGeneratorTest {

    private HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null);
    private StringEntity entity = new StringEntity("{\"code\":\"321\",\"access_token\":\"123\"}");

    @InjectMocks
    private ClientTokenGenerator clientTokenGenerator = new ClientTokenGenerator();

    @Mock
    private HttpClient client;

    public ClientTokenGeneratorTest() throws UnsupportedEncodingException {
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        response.setEntity(entity);
        when(client.execute(any())).thenReturn(response);
    }

    @Test
    public void testClientTokenIsSuccessfullyReturned() {
        assertThat(clientTokenGenerator.generateClientToken(), is("123"));
    }

    @Test
    public void testClientExceptionIsThrownOnFailedPost() throws IOException {
        when(client.execute(any())).thenThrow(IOException.class);
        Assertions.assertThatThrownBy(() -> clientTokenGenerator.generateClientToken())
                .isInstanceOf(ClientException.class);
    }
}
