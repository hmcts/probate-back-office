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
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.hmcts.probate.validator.NocEmailAddressNotifyValidationRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;

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
    @Mock
    private NocEmailAddressNotifyValidationRule nocEmailAddressValidationRuleMock;

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

    @Test
    void shouldGatherNocValidationErrors() {

        List<FieldErrorResponse> errors = Arrays.asList(FieldErrorResponse.builder().build(),
                FieldErrorResponse.builder().build());
        caseDataMock = CaseData.builder()
                .applicationType(SOLICITOR)
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com").build())
                .build();
        when(nocEmailAddressValidationRuleMock.validate(caseDataMock.getApplicationType(),
                caseDataMock.getRemovedRepresentative().getSolicitorEmail())).thenReturn(errors);
        CallbackResponse fieldErrorResponses = eventValidationService
                .validateNocEmail(caseDataMock, nocEmailAddressValidationRuleMock);

        assertEquals(2, fieldErrorResponses.getErrors().size());

    }

    @Test
    void shouldGatherCaveatNocValidationErrors() {

        List<FieldErrorResponse> errors = Arrays.asList(FieldErrorResponse.builder().build(),
                FieldErrorResponse.builder().build());
        caveatDataMock = CaveatData.builder()
                .applicationType(SOLICITOR)
                .removedRepresentative(RemovedRepresentative.builder()
                        .solicitorEmail("solicitor@gmail.com").build())
                .build();
        when(nocEmailAddressValidationRuleMock.validate(caveatDataMock.getApplicationType(),
                caveatDataMock.getRemovedRepresentative().getSolicitorEmail())).thenReturn(errors);
        CaveatCallbackResponse fieldErrorResponses = eventValidationService
                .validateCaveatNocEmail(caveatDataMock, nocEmailAddressValidationRuleMock);

        assertEquals(2, fieldErrorResponses.getErrors().size());

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
