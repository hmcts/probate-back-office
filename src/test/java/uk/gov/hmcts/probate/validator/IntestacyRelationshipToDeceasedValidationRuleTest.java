package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.ExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.ApplicantFamilyDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.model.Constants.CHILD;
import static uk.gov.hmcts.probate.model.Constants.SPOUSE;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_SPOUSE;
import static uk.gov.hmcts.probate.validator.IntestacyRelationshipToDeceasedValidationRule.INVALID_RELATIONSHIP_TO_DECEASED;

@ExtendWith(SpringExtension.class)
class IntestacyRelationshipToDeceasedValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdDataMock;

    @Mock
    private List<ExecutorApplying> executorMock;

    private IntestacyRelationshipToDeceasedValidationRule underTest;

    @BeforeEach
    public void setUp() {
        underTest = new IntestacyRelationshipToDeceasedValidationRule(businessValidationMessageService);
        when(ccdDataMock.getCaseworkerExecutorsList()).thenReturn(executorMock);
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, INVALID_RELATIONSHIP_TO_DECEASED))
                .thenReturn(FieldErrorResponse.builder().code(INVALID_RELATIONSHIP_TO_DECEASED).build());
    }

    @Test
    void shouldReturnNoErrorsWhenExecutorsListIsNull() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SOLICITOR_SPOUSE);
        when(ccdDataMock.getPrimaryApplicantRelationshipToDeceased()).thenReturn(SPOUSE);
        when(ccdDataMock.getCaseworkerExecutorsList()).thenReturn(null);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldReturnNoErrorsWhenCoApplicantDetailsIsNull() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SOLICITOR_SPOUSE);
        when(ccdDataMock.getPrimaryApplicantRelationshipToDeceased()).thenReturn(SPOUSE);
        List<ExecutorApplying> executor = List.of(ExecutorApplying.builder()
                .applicantFamilyDetails(null)
                .build());
        when(ccdDataMock.getCaseworkerExecutorsList()).thenReturn(executor);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        verify(businessValidationMessageService, never()).generateError(any(String.class), any(String.class));
        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldReturnNoErrorsWhenApplicantIsSpouseButCoApplicantIsNotSpouse() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SOLICITOR_SPOUSE);
        when(ccdDataMock.getPrimaryApplicantRelationshipToDeceased()).thenReturn(null);
        List<ExecutorApplying> executor = List.of(ExecutorApplying.builder()
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased(CHILD)
                        .build())
                .build());
        when(ccdDataMock.getCaseworkerExecutorsList()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);
        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void shouldReturnErrorWhenApplicantAndCoApplicantAreBothSpouse() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(SOLICITOR_SPOUSE);
        when(ccdDataMock.getPrimaryApplicantRelationshipToDeceased()).thenReturn(null);
        List<ExecutorApplying> executor = List.of(ExecutorApplying.builder()
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased(SPOUSE)
                        .build())
                .build());
        when(ccdDataMock.getCaseworkerExecutorsList()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);
        assertEquals(INVALID_RELATIONSHIP_TO_DECEASED, validationErrors.getFirst().getCode());
    }

    @Test
    void shouldreturnErrorWhenPersonalApplicantAndCoApplicantAreBothSpouse() {
        when(ccdDataMock.getSolsApplicantRelationshipToDeceased()).thenReturn(null);
        when(ccdDataMock.getPrimaryApplicantRelationshipToDeceased()).thenReturn(SPOUSE);
        List<ExecutorApplying> executor = List.of(ExecutorApplying.builder()
                .applicantFamilyDetails(ApplicantFamilyDetails.builder()
                        .relationshipToDeceased(SPOUSE)
                        .build())
                .build());
        when(ccdDataMock.getCaseworkerExecutorsList()).thenReturn(executor);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);
        assertEquals(INVALID_RELATIONSHIP_TO_DECEASED, validationErrors.getFirst().getCode());
    }
}
