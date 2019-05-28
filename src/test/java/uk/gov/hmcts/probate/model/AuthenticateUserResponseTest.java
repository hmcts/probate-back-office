package uk.gov.hmcts.probate.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuthenticateUserResponseTest {
    AuthenticateUserResponse authenticateUserResponse;

    @Before
    public void setup() {
        authenticateUserResponse = new AuthenticateUserResponse("123");
    }

    @Test
    public void testGetCode() {
        assertEquals("123", authenticateUserResponse.getCode());
    }
}