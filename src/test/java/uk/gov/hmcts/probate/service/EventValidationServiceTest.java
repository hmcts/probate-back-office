package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventValidationServiceTest {

    private EventValidationService eventValidationService;

    @Mock
    private CCDData ccdDataMock;
    @Mock
    private FieldErrorResponse fieldErrorResponse1Mock;
    @Mock
    private FieldErrorResponse fieldErrorResponse2Mock;

    private SimpleValidationRule validationRule;


    @Before
    public void setup() {
        eventValidationService = new EventValidationService();
        validationRule = new SimpleValidationRule();
    }

    @Test
    public void shouldGatherValidationErrors() {

        List<FieldErrorResponse> fieldErrorResponses = eventValidationService
                .validate(ccdDataMock, Arrays.asList(validationRule));

        assertEquals(2, fieldErrorResponses.size());

    }

    private class SimpleValidationRule implements ValidationRule {
        @Override
        public List<FieldErrorResponse> validate(CCDData form) {
            return Arrays.asList(fieldErrorResponse1Mock, fieldErrorResponse2Mock);
        }
    }
}