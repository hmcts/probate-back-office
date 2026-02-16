package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Applicant;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static uk.gov.hmcts.probate.model.Constants.CHILD;
import static uk.gov.hmcts.probate.model.Constants.GRAND_CHILD;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.SIBLING;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.validator.IntestacyApplicantDetailsValidationRule.ADOPTED_OUTSIDE_ENGLAND_OR_WALES;
import static uk.gov.hmcts.probate.validator.IntestacyApplicantDetailsValidationRule.ADOPTED_OUT;
import static uk.gov.hmcts.probate.validator.IntestacyApplicantDetailsValidationRule.SIBLING_NOT_DIED;
import static uk.gov.hmcts.probate.validator.IntestacyApplicantDetailsValidationRule.DECEASED_CHILD_DEAD;



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

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, DECEASED_CHILD_DEAD))
                .thenReturn(FieldErrorResponse.builder().code(DECEASED_CHILD_DEAD).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUTSIDE_ENGLAND_OR_WALES))
                .thenReturn(FieldErrorResponse.builder().code(ADOPTED_OUTSIDE_ENGLAND_OR_WALES).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUT))
                .thenReturn(FieldErrorResponse.builder().code(ADOPTED_OUT).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, SIBLING_NOT_DIED))
                .thenReturn(FieldErrorResponse.builder().code(SIBLING_NOT_DIED).build());
    }

    private static Stream<String> relationship() {
        return Stream.of(CHILD, GRAND_CHILD, SIBLING);
    }

    @Test
    void testValidateWithSuccessWhenApplicantIsNull() {
        when(ccdDataMock.getApplicant()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldValidateFailureIfDeceasedChildDead() {
        when(applicantMock.getChildAlive()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(DECEASED_CHILD_DEAD, validationErrors.getFirst().getCode());
    }


    @Test
    void shouldValidateSuccessIfApplicantParentAdoptedInEnglandOrWales() {
        when(applicantMock.getPrimaryApplicantParentAdoptionInEnglandOrWales()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfApplicantParentAdoptedOutsideEnglandOrWales() {
        when(applicantMock.getPrimaryApplicantParentAdoptionInEnglandOrWales()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateSuccessIfApplicantAdoptedInEnglandOrWales(final String relationship) {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(relationship);
        when(applicantMock.getPrimaryApplicantAdoptionInEnglandOrWales()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateFailureIfApplicantAdoptedOutsideEnglandOrWales(final String relationship) {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(relationship);
        when(applicantMock.getPrimaryApplicantAdoptionInEnglandOrWales()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfApplicantParentIsNotAdoptedOut() {
        when(applicantMock.getPrimaryApplicantParentAdoptedOut()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfApplicantParentIsAdoptedOut() {
        when(applicantMock.getPrimaryApplicantParentAdoptedOut()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUT, validationErrors.getFirst().getCode());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateSuccessIfApplicantIsNotAdoptedOut(final String relationship) {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(relationship);
        when(applicantMock.getPrimaryApplicantAdoptedOut()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("relationship")
    void shouldValidateFailureIfApplicantIsAdoptedOut(final String relationship) {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(relationship);
        when(applicantMock.getPrimaryApplicantAdoptedOut()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUT, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfWholeSiblingIsDied() {
        when(applicantMock.getAnyLivingWholeBloodSiblings()).thenReturn(YES);
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SIBLING);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfWholeSiblingIsNotDied() {
        when(applicantMock.getAnyLivingWholeBloodSiblings()).thenReturn(NO);
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SIBLING);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(SIBLING_NOT_DIED, validationErrors.getFirst().getCode());
    }
}
