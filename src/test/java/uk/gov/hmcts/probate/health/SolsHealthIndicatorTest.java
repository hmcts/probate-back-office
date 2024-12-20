package uk.gov.hmcts.probate.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SolsHealthIndicatorTest {

    private static final String URL = "http://url.com";
    private static final String HEALTH = "/health";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity responseEntity;

    private SolsHealthIndicator solsHealthIndicator;

    @BeforeEach
    public void setUp() {
        solsHealthIndicator = new SolsHealthIndicator(URL, restTemplate, HEALTH);
    }

    @Test
    void shouldReturnStatusOfUpWhenHttpStatusIsOK() {
        when(restTemplate.getForEntity(URL + HEALTH, String.class)).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        Health health = solsHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.UP));
        assertThat(health.getDetails().get("url"), is(URL));
    }

    @Test
    void shouldReturnStatusOfDownWhenHttpStatusIsNotOK() {
        when(restTemplate.getForEntity(URL + HEALTH, String.class)).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        when(responseEntity.getStatusCodeValue()).thenReturn(HttpStatus.NO_CONTENT.value());
        Health health = solsHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("HTTP Status code not 200"));
        assertThat(health.getDetails().get("exception"), is("HTTP Status: 204"));
    }

    @Test
    void shouldReturnStatusOfDownWhenResourceAccessExceptionIsThrown() {
        final String message = "EXCEPTION MESSAGE";
        when(restTemplate.getForEntity(URL + HEALTH, String.class)).thenThrow(new ResourceAccessException(message));

        Health health = solsHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is(message));
        assertThat(health.getDetails().get("exception"), is("ResourceAccessException"));
    }

    @Test
    void shouldReturnStatusOfDownWhenHttpStatusCodeExceptionIsThrown() {
        when(restTemplate.getForEntity(URL + HEALTH, String.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        Health health = solsHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("400 BAD_REQUEST"));
        assertThat(health.getDetails().get("exception"), is("HttpStatusCodeException - HTTP Status: 400"));
    }

    @Test
    void shouldReturnStatusOfDownWhenUnknownHttpStatusCodeExceptionIsThrown() {
        final String statusText = "status text";
        when(restTemplate.getForEntity(URL + HEALTH, String.class))
            .thenThrow(new UnknownHttpStatusCodeException(999, statusText, null, null, null));

        Health health = solsHealthIndicator.health();

        assertThat(health.getStatus(), is(Status.DOWN));
        assertThat(health.getDetails().get("url"), is(URL));
        assertThat(health.getDetails().get("message"), is("Unknown status code [999] status text"));
        assertThat(health.getDetails().get("exception"), is("UnknownHttpStatusCodeException - " + statusText));
    }
}
