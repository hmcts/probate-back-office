package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
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

public class EmailAddressNotifyApplicantValidationRuleTest {

    @InjectMocks
    private EmailAddressNotifyApplicantValidationRule emailAddressNotifyApplicantValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private CCDData ccdData;
    private FieldErrorResponse fieldErrorResponsePrimary;
    private FieldErrorResponse fieldErrorResponseSolicitor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fieldErrorResponsePrimary = FieldErrorResponse.builder()
            .message("primary missing")
            .build();
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, "notifyApplicantNoEmailPA")).thenReturn(fieldErrorResponsePrimary);

        fieldErrorResponseSolicitor = FieldErrorResponse.builder()
            .message("solicitor missing")
            .build();
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, "notifyApplicantNoEmailSOLS")).thenReturn(fieldErrorResponseSolicitor);
    }

    @Test
    public void shouldPassPersonalWithEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.PERSONAL.name())
            .primaryApplicantEmailAddress("primary@email.com")
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.isEmpty());
        verify(businessValidationMessageService, times(0)).generateError(any(String.class), any(String.class));
    }

    @Test
    public void shouldPassSolicitorWithEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.SOLICITOR.name())
            .solsSolicitorEmail("solicitor@email.com")
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.isEmpty());
        verify(businessValidationMessageService, times(0)).generateError(any(String.class), any(String.class));
    }

    @Test
    public void shouldFailPersonalWithNoEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.PERSONAL.name())
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.size() == 1);

    }

    @Test
    public void shouldFailSolicitorWithNoEmail() {
        ccdData = CCDData.builder()
            .applicationType(ApplicationType.SOLICITOR.name())
            .build();
        List<FieldErrorResponse> validationErrors = emailAddressNotifyApplicantValidationRule.validate(ccdData);

        assertTrue(validationErrors.size() == 1);
        assertEquals(validationErrors.get(0).getMessage(), "solicitor missing");
    }

}