package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventValidationServiceTest {

    @InjectMocks
    private EventValidationService eventValidationService;

    @Mock
    private CCDData ccdDataMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaveatDetails caveatDetailsMock;
    @Mock
    private CaveatData caveatDataMock;
    @Mock
    private PaymentResponse paymentResponseMock;

    private SimpleValidationRule validationRule;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        validationRule = new SimpleValidationRule();
    }


    @Test
    void shouldGatherValidationErrors() {

        List<FieldErrorResponse> fieldErrorResponses = eventValidationService
            .validate(ccdDataMock, Collections.singletonList(validationRule));

        assertEquals(2, fieldErrorResponses.size());

    }

    private class SimpleValidationRule implements ValidationRule {
        private FieldErrorResponse fieldErrorResponse1Mock;

        private FieldErrorResponse fieldErrorResponse2Mock;

        @Override
        public List<FieldErrorResponse> validate(CCDData form) {
            return Arrays.asList(fieldErrorResponse1Mock, fieldErrorResponse2Mock);
        }
    }
}
