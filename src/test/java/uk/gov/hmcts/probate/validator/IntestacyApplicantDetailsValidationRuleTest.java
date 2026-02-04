package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Applicant;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.validator.IntestacyApplicantDetailsValidationRule.ADOPTED_OUTSIDE_ENGLAND_OR_WALES;
import static uk.gov.hmcts.probate.validator.IntestacyApplicantDetailsValidationRule.ADOPTED_OUT;

@ExtendWith(SpringExtension.class)
class IntestacyApplicantDetailsValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private Applicant applicantMock;

    private IntestacyApplicantDetailsValidationRule underTest;

    @BeforeEach
    public void setUp() {
        underTest = new IntestacyApplicantDetailsValidationRule(businessValidationMessageService);
        when(ccdDataMock.getApplicant()).thenReturn(applicantMock);

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUTSIDE_ENGLAND_OR_WALES))
                .thenReturn(FieldErrorResponse.builder().code(ADOPTED_OUTSIDE_ENGLAND_OR_WALES).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUT))
                .thenReturn(FieldErrorResponse.builder().code(ADOPTED_OUT).build());
    }

    @Test
    void testValidateWithSuccessWhenApplicantIsNull() {
        when(ccdDataMock.getApplicant()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldValidateSuccessIfApplicantAdoptedInEnglandOrWales() {
        when(applicantMock.getPrimaryApplicantAdoptionInEnglandOrWales()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfApplicantAdoptedOutsideEnglandOrWales() {
        when(applicantMock.getPrimaryApplicantAdoptionInEnglandOrWales()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfApplicantIsNotAdoptedOut() {
        when(applicantMock.getPrimaryApplicantAdoptedOut()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfApplicantIsAdoptedOut() {
        when(applicantMock.getPrimaryApplicantAdoptedOut()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUT, validationErrors.getFirst().getCode());
    }
}
