package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;


class CheckIntestacyMaritalStatusRuleTest {

    @InjectMocks
    private CheckIntestacyMaritalStatusRule underTest;

    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;

    private FieldErrorResponse errorEnglish;
    private FieldErrorResponse errorWelsh;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        errorEnglish = FieldErrorResponse.builder().message("errorNotPossible").build();
        errorWelsh = FieldErrorResponse.builder().message("errorNotPossibleWelsh").build();

        when(businessValidationMessageServiceMock.generateError(eq(BUSINESS_ERROR),
                eq("errorNotPossible"))).thenReturn(errorEnglish);
        when(businessValidationMessageServiceMock.generateError(eq(BUSINESS_ERROR),
                eq("errorNotPossibleWelsh"))).thenReturn(errorWelsh);

    }

    @Test
    void shouldReturnErrors_whenApplicantIsSpouseAndDeceasedDivorced() {
        CCDData ccdData = CCDData.builder()
                .solsApplicantRelationshipToDeceased("SpouseOrCivil")
                .deceasedMaritalStatus("divorcedCivilPartnership")
                .build();
        List<FieldErrorResponse> errors = underTest.validate(ccdData);

        assertEquals(2, errors.size());
        assertEquals("errorNotPossible", errors.get(0).getMessage());
        assertEquals("errorNotPossibleWelsh", errors.get(1).getMessage());
    }

    @Test
    void shouldReturnEmptyList_whenApplicantIsSpouseAndDeceasedMarried() {
        CCDData ccdData = CCDData.builder()
                .solsApplicantRelationshipToDeceased("SpouseOrCivil")
                .deceasedMaritalStatus("marriedCivilPartnership")
                .build();
        List<FieldErrorResponse> errors = underTest.validate(ccdData);

        assertTrue(errors.isEmpty());
        verifyNoInteractions(businessValidationMessageServiceMock);
    }

}

