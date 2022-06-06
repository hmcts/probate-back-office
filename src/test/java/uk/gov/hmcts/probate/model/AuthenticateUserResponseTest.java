package uk.gov.hmcts.probate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class AuthenticateUserResponseTest {
    AuthenticateUserResponse authenticateUserResponse;

    @BeforeEach
    public void setup() {
        authenticateUserResponse = new AuthenticateUserResponse("123");
    }

    @Test
    public void testGetCode() {
        assertEquals("123", authenticateUserResponse.getCode());
    }
}
