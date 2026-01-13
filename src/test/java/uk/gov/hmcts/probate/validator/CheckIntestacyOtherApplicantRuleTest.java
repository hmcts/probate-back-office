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


class CheckIntestacyOtherApplicantRuleTest {

    @InjectMocks
    private CheckIntestacyOtherApplicantRule underTest;

    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;

    private FieldErrorResponse errorEnglish;
    private FieldErrorResponse errorWelsh;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        errorEnglish = FieldErrorResponse.builder().message("errorCannotProceed").build();
        errorWelsh = FieldErrorResponse.builder().message("errorCannotProceedWelsh").build();

        when(businessValidationMessageServiceMock.generateError(eq(BUSINESS_ERROR),
                eq("errorCannotProceed"))).thenReturn(errorEnglish);
        when(businessValidationMessageServiceMock.generateError(eq(BUSINESS_ERROR),
                eq("errorCannotProceedWelsh"))).thenReturn(errorWelsh);

    }

    @Test
    void shouldReturnErrorWhenSpouseApplicationWithOtherApplicant() {
        CCDData ccdData = CCDData.builder()
                .solsApplicantRelationshipToDeceased("SpouseOrCivil")
                .otherExecutorExists("Yes")
                .build();
        List<FieldErrorResponse> errors = underTest.validate(ccdData);

        assertEquals(2, errors.size());
        assertEquals("errorCannotProceed", errors.get(0).getMessage());
        assertEquals("errorCannotProceedWelsh", errors.get(1).getMessage());
    }

    @Test
    void shouldNoErrorWhenSpouseApplicationWithoutOtherApplicant() {
        CCDData ccdData = CCDData.builder()
                .solsApplicantRelationshipToDeceased("SpouseOrCivil")
                .otherExecutorExists("No")
                .build();
        List<FieldErrorResponse> errors = underTest.validate(ccdData);

        assertTrue(errors.isEmpty());
        verifyNoInteractions(businessValidationMessageServiceMock);
    }

}

