package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

class EmailAddressNotifyApplicantValidationRuleTest {

    @InjectMocks
    private EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private CCDData ccdData;
    private FieldErrorResponse fieldErrorResponsePrimary;
    private FieldErrorResponse fieldErrorResponseSolicitor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fieldErrorResponsePrimary = FieldErrorResponse.builder()
            .message("primary missing")
            .build();
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, "notifyApplicantNoEmailPA"))
            .thenReturn(fieldErrorResponsePrimary);

        fieldErrorResponseSolicitor = FieldErrorResponse.builder()
            .message("solicitor missing")
            .build();
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, "notifyApplicantNoEmailSOLS"))
            .thenReturn(fieldErrorResponseSolicitor);
    }

    @Test
    void shouldPassPersonalWithEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.PERSONAL.name())
            .primaryApplicantEmailAddress("primary@probate-test.com")
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.isEmpty());
        verify(businessValidationMessageService, times(0)).generateError(any(String.class), any(String.class));
    }

    @Test
    void shouldPassSolicitorWithEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.SOLICITOR.name())
            .solsSolicitorEmail("solicitor@probate-test.com")
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.isEmpty());
        verify(businessValidationMessageService, times(0)).generateError(any(String.class), any(String.class));
    }

    @Test
    void shouldFailPersonalWithNoEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.PERSONAL.name())
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.size() == 1);

    }

    @Test
    void shouldFailSolicitorWithNoEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.SOLICITOR.name())
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.size() == 1);
        assertEquals(validationErrors.get(0).getMessage(), "solicitor missing");
    }

}
