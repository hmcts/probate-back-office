package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

public class EmailAddressNotificationDefaultValidationRuleTest {

    @InjectMocks
    private EmailAddressNotificationDefaultValidationRule emailAddressNotificationDefaultValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private FieldErrorResponse emailAddressError;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldErrorOnNoEmailForPersonal() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(PERSONAL)).build();

        assertForError(ccdData, "emailNotProvidedPA");
    }

    @Test
    public void shouldErrorOnEmptyEmailForPersonal() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(PERSONAL)).build();
        assertForError(ccdData, "emailNotProvidedPA");
    }

    @Test
    public void shouldErrorOnInvalidEmailForPersonal() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(PERSONAL))
            .primaryApplicantEmailAddress("rubbish")
            .build();
        assertForError(ccdData, "emailNotProvidedPA");
    }

    @Test
    public void shouldPassOnValidEmailForPersonal() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(PERSONAL))
            .primaryApplicantEmailAddress("a@b.com")
            .build();
        assertForPass(ccdData);

    }

    @Test
    public void shouldErrorOnNoEmailForSolicitor() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(SOLICITOR)).build();

        assertForError(ccdData, "emailNotProvidedSOLS");
    }

    @Test
    public void shouldErrorOnEmptyEmailForSolicitor() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(SOLICITOR)).build();
        assertForError(ccdData, "emailNotProvidedSOLS");
    }

    @Test
    public void shouldErrorOnInvalidEmailForSolicitor() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(SOLICITOR))
            .primaryApplicantEmailAddress("rubbish")
            .build();
        assertForError(ccdData, "emailNotProvidedSOLS");
    }

    @Test
    public void shouldPassOnValidEmailForSolicitor() {
        CCDData ccdData = CCDData.builder().applicationType(String.valueOf(SOLICITOR))
            .primaryApplicantEmailAddress("a@b.com")
            .build();
        assertForPass(ccdData);
    }

    private void assertForPass(CCDData ccdData) {
        List<FieldErrorResponse> errors = emailAddressNotificationDefaultValidationRule.validate(ccdData);
        assertEquals(0, errors.size());
        verify(businessValidationMessageService, never()).generateError(anyString(), anyString());
    }
    
    private void assertForError(CCDData ccdData, String emailMessage) {
        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq(emailMessage)))
            .thenReturn(emailAddressError);

        List<FieldErrorResponse> errors = emailAddressNotificationDefaultValidationRule.validate(ccdData);
        assertEquals(1, errors.size());
        assertTrue(errors.contains(emailAddressError));
    }

}