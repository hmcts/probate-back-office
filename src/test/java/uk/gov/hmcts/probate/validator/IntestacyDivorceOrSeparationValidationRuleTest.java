package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.validator.IntestacyDivorceOrSeparationValidationRule.DIVORCED_OUTSIDE_ENGLAND_OR_WALES;
import static uk.gov.hmcts.probate.validator.IntestacyDivorceOrSeparationValidationRule.SEPARATED_OUTSIDE_ENGLAND_OR_WALES;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.DIVORCED_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.MaritalStatus.Constants.JUDICIALLY_SEPARATED_VALUE;

@ExtendWith(MockitoExtension.class)
class IntestacyDivorceOrSeparationValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private Deceased deceasedMock;

    private IntestacyDivorceOrSeparationValidationRule underTest;

    @BeforeEach
     void setUp() {
        // The validator requests a language-specific message key (including Welsh variants).
        // Preserve the business error code irrespective of the translated message key.
        lenient().when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), anyString()))
                .thenAnswer(invocation -> {
                    String messageKey = invocation.getArgument(1);
                    return FieldErrorResponse.builder()
                            .code(messageKey.replaceFirst("Welsh$", ""))
                            .build();
                });
        underTest = new IntestacyDivorceOrSeparationValidationRule(businessValidationMessageService);
        when(ccdDataMock.getDeceased()).thenReturn(deceasedMock);

    }

    @Test
    void testValidateWithSuccessWhenDeceasedIsNull() {
        when(ccdDataMock.getDeceased()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldValidateSuccessIfApplicantDivorcedInEnglandOrWales() {
        when(deceasedMock.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(deceasedMock.getDeceasedDivorcedInEnglandOrWales()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfApplicantDivorcedOutsideEnglandOrWales() {
        when(deceasedMock.getDeceasedMaritalStatus()).thenReturn(DIVORCED_VALUE);
        when(deceasedMock.getDeceasedDivorcedInEnglandOrWales()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(DIVORCED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfApplicantSeparatedInEnglandOrWales() {
        when(deceasedMock.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(deceasedMock.getDeceasedDivorcedInEnglandOrWales()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfApplicantSeparatedOutsideEnglandOrWales() {
        when(deceasedMock.getDeceasedMaritalStatus()).thenReturn(JUDICIALLY_SEPARATED_VALUE);
        when(deceasedMock.getDeceasedDivorcedInEnglandOrWales()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(SEPARATED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }
}
