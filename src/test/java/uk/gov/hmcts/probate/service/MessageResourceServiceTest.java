package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class MessageResourceServiceTest {

    @Mock
    private MessageSource resourceMessageSource;

    private MessageResourceService underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        underTest = new MessageResourceService(resourceMessageSource);
    }

    @Test
    public void shouldReturnMessage() {

        when(resourceMessageSource.getMessage("someMessageId", null, null)).thenReturn("someMessageValue");
        String message = underTest.getMessage("someMessageId");
        assertEquals("someMessageValue", message);
    }
}
