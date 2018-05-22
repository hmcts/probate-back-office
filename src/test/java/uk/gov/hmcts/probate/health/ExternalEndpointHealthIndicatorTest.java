package uk.gov.hmcts.probate.health;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalEndpointHealthIndicatorTest {

    private static final String URL = "http://url.com";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity responseEntity;

    private ExternalEndpointHealthIndicator externalEndpointHealthIndicator;

    @Before
    public void setUp() {
        externalEndpointHealthIndicator = new ExternalEndpointHealthIndicator(URL, restTemplate);
    }

    @Test
    public void shouldReturnStatusOfUpWhenHttpStatusIsOK() {
        when(restTemplate.getForEntity(URL + "/health", String.class)).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        Health health = externalEndpointHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.UP));
        assertThat(health.getDetails().get("url"), is(URL));
    }

    @Test
    public void shouldReturnStatusOfDownWhenHttpStatusIsNotOK() {
        when(restTemplate.getForEntity(URL + "/health", String.class)).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        when(responseEntity.getStatusCodeValue()).thenReturn(HttpStatus.NO_CONTENT.value());
        Health health = externalEndpointHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("HTTP Status code not 200"));
        assertThat(health.getDetails().get("exception"), is("HTTP Status: 204"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenResourceAccessExceptionIsThrown() {
        final String message = "EXCEPTION MESSAGE";
        when(restTemplate.getForEntity(URL + "/health", String.class)).thenThrow(new ResourceAccessException(message));

        Health health = externalEndpointHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is(message));
        assertThat(health.getDetails().get("exception"), is("ResourceAccessException"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenHttpStatusCodeExceptionIsThrown() {
        when(restTemplate.getForEntity(URL + "/health", String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        Health health = externalEndpointHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("400 BAD_REQUEST"));
        assertThat(health.getDetails().get("exception"), is("HttpStatusCodeException - HTTP Status: 400"));
    }

    @Test
    public void shouldReturnStatusOfDownWhenUnknownHttpStatusCodeExceptionIsThrown() {
        final String statusText = "status text";
        when(restTemplate.getForEntity(URL + "/health", String.class))
                .thenThrow(new UnknownHttpStatusCodeException(1000, statusText, null, null, null));

        Health health = externalEndpointHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("Unknown status code [1000] status text"));
        assertThat(health.getDetails().get("exception"), is("UnknownHttpStatusCodeException - " + statusText));
    }
}