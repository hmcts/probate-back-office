package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.CCDData;
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
import static uk.gov.hmcts.probate.model.Constants.PARENT;
import static uk.gov.hmcts.probate.model.Constants.SIBLING;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.validator.IntestacyDeceasedDetailsValidationRule.DECEASED_ADOPTED_OUT;
import static uk.gov.hmcts.probate.validator.IntestacyDeceasedDetailsValidationRule.ADOPTED_OUTSIDE_ENGLAND_OR_WALES;
import static uk.gov.hmcts.probate.validator.IntestacyDeceasedDetailsValidationRule.LIVING_DESCENDANTS;
import static uk.gov.hmcts.probate.validator.IntestacyDeceasedDetailsValidationRule.LIVING_PARENTS;

@ExtendWith(SpringExtension.class)
class IntestacyDeceasedDetailsValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private Deceased deceasedMock;

    private IntestacyDeceasedDetailsValidationRule underTest;

    @BeforeEach
    public void setUp() {
        underTest = new IntestacyDeceasedDetailsValidationRule(businessValidationMessageService);
        when(ccdDataMock.getDeceased()).thenReturn(deceasedMock);
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SIBLING);

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, ADOPTED_OUTSIDE_ENGLAND_OR_WALES))
                .thenReturn(FieldErrorResponse.builder().code(ADOPTED_OUTSIDE_ENGLAND_OR_WALES).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, DECEASED_ADOPTED_OUT))
                .thenReturn(FieldErrorResponse.builder().code(DECEASED_ADOPTED_OUT).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, LIVING_DESCENDANTS))
                .thenReturn(FieldErrorResponse.builder().code(LIVING_DESCENDANTS).build());

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, LIVING_PARENTS))
                .thenReturn(FieldErrorResponse.builder().code(LIVING_PARENTS).build());
    }

    @Test
    void testValidateWithSuccessWhenDeceasedIsNull() {
        when(ccdDataMock.getDeceased()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldValidateSuccessIfDeceasedAdoptedInEnglandOrWalesAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAdoptionInEnglandOrWales()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfDeceasedAdoptedOutsideEnglandOrWalesAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAdoptionInEnglandOrWales()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateFailureIfDeceasedAdoptedOutsideEnglandOrWalesAndApplicantIsParent() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(PARENT);
        when(deceasedMock.getDeceasedAdoptionInEnglandOrWales()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(ADOPTED_OUTSIDE_ENGLAND_OR_WALES, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfDeceasedIsNotAdoptedOutAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAdoptedOut()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfDeceasedIsAdoptedOutAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAdoptedOut()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(DECEASED_ADOPTED_OUT, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateFailureIfDeceasedIsAdoptedOutAndApplicantIsParent() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(PARENT);
        when(deceasedMock.getDeceasedAdoptedOut()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(DECEASED_ADOPTED_OUT, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfDeceasedHasNoDescendantsAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAnyLivingDescendants()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfDeceasedHasDescendantsAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAnyLivingDescendants()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(LIVING_DESCENDANTS, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateSuccessIfDeceasedHasNoParentsAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAnyLivingParents()).thenReturn(NO);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldValidateFailureIfDeceasedHasParentsAndApplicantIsSibling() {
        when(deceasedMock.getDeceasedAnyLivingParents()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(LIVING_PARENTS, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldValidateFailureIfDeceasedHasParentsAndApplicantIsParent() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(PARENT);
        when(deceasedMock.getDeceasedAnyLivingParents()).thenReturn(YES);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(LIVING_PARENTS, validationErrors.getFirst().getCode());
    }
}
