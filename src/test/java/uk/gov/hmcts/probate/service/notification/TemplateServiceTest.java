package uk.gov.hmcts.probate.service.notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.LanguagePreference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_CAVEAT;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;
import static uk.gov.hmcts.probate.model.State.CAVEAT_EXTEND;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED_SOLS;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.probate.model.State.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.State.GRANT_REISSUED;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @MockBean
    private AppInsights appInsights;

    @Test
    public void getDocumentsReceivedPA() {

        String response = templateService.getTemplateId(DOCUMENTS_RECEIVED, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-document-received", response);

        String responseWelsh = templateService.getTemplateId(DOCUMENTS_RECEIVED, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-document-received-welsh", responseWelsh);
    }

    @Test
    public void getApplicationReceivedPA() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-application-received", response);

        String responseWelsh = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-application-received-welsh", responseWelsh);
    }


    @Test
    public void getDocumentsReceivedSols() {

        String response = templateService.getTemplateId(DOCUMENTS_RECEIVED, SOLICITOR, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("sol-document-received", response);

        String responseWelsh = templateService.getTemplateId(DOCUMENTS_RECEIVED, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sol-document-received-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedPA() {

        String response = templateService.getTemplateId(CASE_STOPPED, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-case-stopped", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-case-stopped-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedSols() {

        String response = templateService.getTemplateId(CASE_STOPPED, SOLICITOR, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("sol-case-stopped", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sol-case-stopped-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedCaveatPA() {

        String response = templateService.getTemplateId(CASE_STOPPED_CAVEAT, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-case-stopped-caveat", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_CAVEAT, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-case-stopped-caveat-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedCaveatSols() {

        String response = templateService.getTemplateId(CASE_STOPPED_CAVEAT, SOLICITOR, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("sol-case-stopped-caveat", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_CAVEAT, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sol-case-stopped-caveat-welsh", responseWelsh);
    }

    @Test
    public void getGrantIssuedPA() {

        String response = templateService.getTemplateId(GRANT_ISSUED, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-grant-issued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_ISSUED, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-grant-issued-welsh", responseWelsh);
    }

    @Test
    public void getGrantIssuedSols() {

        String response = templateService.getTemplateId(GRANT_ISSUED, SOLICITOR, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("sol-grant-issued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_ISSUED, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sol-grant-issued-welsh", responseWelsh);
    }

    @Test
    public void getGrantReissuedPA() {

        String response = templateService.getTemplateId(GRANT_REISSUED, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-grant-reissued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_REISSUED, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-grant-reissued-welsh", responseWelsh);
    }

    @Test
    public void getGrantReissuedSols() {

        String response = templateService.getTemplateId(GRANT_REISSUED, SOLICITOR, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("sol-grant-reissued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_REISSUED, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sol-grant-reissued-welsh", responseWelsh);
    }

    @Test
    public void getGeneralCaveatMessagePA() {

        String response = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-general-caveat-message", response);

        String responseWelsh = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-general-caveat-message-welsh", responseWelsh);
    }

    @Test
    public void getGeneralCaveatMessageSols() {

        String response = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, SOLICITOR, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("sol-general-caveat-message", response);

        String responseWelsh = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sol-general-caveat-message-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedRequestForInfoPA() {

        String response = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC,LanguagePreference.ENGLISH);
        assertEquals("pa-request-information", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC,LanguagePreference.WELSH);
        assertEquals("pa-request-information-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedRequestForInfoSols() {

        String response = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, SOLICITOR, CTSC,LanguagePreference.ENGLISH);
        assertEquals("sols-request-information", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, SOLICITOR, CTSC,LanguagePreference.WELSH);
        assertEquals("sols-request-information-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedPA() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, PERSONAL, "oxford",
                LanguagePreference.ENGLISH);
        assertEquals("pa-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, PERSONAL, "oxford",
                LanguagePreference.WELSH);
        assertEquals("pa-caveat-raised-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedSols() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, SOLICITOR, "oxford"
                ,LanguagePreference.ENGLISH);
        assertEquals("sols-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, SOLICITOR, "oxford"
                ,LanguagePreference.WELSH);
        assertEquals("sols-caveat-raised-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedPersonalCTSC() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, PERSONAL, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("pa-ctsc-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-ctsc-caveat-raised-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedSolsCTSC() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, SOLICITOR, CTSC,
                LanguagePreference.ENGLISH);
        assertEquals("sols-ctsc-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sols-ctsc-caveat-raised-welsh", responseWelsh);
    }

    @Test
    public void getSolsCaveatRaised() {

        String response = templateService.getTemplateId(CAVEAT_RAISED_SOLS, SOLICITOR,
                CTSC, LanguagePreference.ENGLISH);

        assertEquals("solicitor-caveat-raised", response);
     }
     
    @Test
    public void getCaveatExtendPersonalCTSC() {

        String response = templateService.getTemplateId(CAVEAT_EXTEND, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-ctsc-caveat-extend", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_EXTEND, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-ctsc-caveat-extend-welsh", responseWelsh);
    }

    @Test
    public void shouldGetGrantRaisedTemplateForDigital() {

        String response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-grant-raised", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-grant-raised", response);
    }

    @Test
    public void shouldGetGrantRaisedTemplateForPaperFormIsNo() {

        String response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, NO);
        assertEquals("pa-grant-raised", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, NO);
        assertEquals("sol-grant-raised", response);
    }

    @Test
    public void shouldGetGrantRaisedTemplateForPaperForm() {

        String response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, YES);
        assertEquals("pa-grant-raised-paper-bulk-scan", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, YES);
        assertEquals("sol-grant-raised-paper-bulk-scan", response);
    }

    @Test
    public void getApplicationReceivedPACaseworkerOrigin() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, YES, CaseOrigin.CASEWORKER);
        assertEquals("pa-application-received-cw", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, YES, CaseOrigin.CASEWORKER);
        assertEquals("sol-application-received-cw", response);
    }

    @Test
    public void getApplicationReceivedPACaseworkerOriginPaperFormNo() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, NO, CaseOrigin.CASEWORKER);
        assertEquals("pa-application-received", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, NO, CaseOrigin.CASEWORKER);
        assertEquals("sol-application-received", response);
    }

    @Test
    public void getApplicationReceivedPACaseworkerOriginWelsh() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.WELSH, YES, CaseOrigin.CASEWORKER);
        assertEquals("pa-application-received-cw-welsh", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.WELSH, YES, CaseOrigin.CASEWORKER);
        assertEquals("sol-application-received-cw-welsh", response);
    }

    @Test
    public void getApplicationReceivedPAOtherOrigin() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, YES, CaseOrigin.CITIZEN);
        assertEquals("pa-application-received", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, YES, CaseOrigin.CITIZEN);
        assertEquals("sol-application-received", response);
    }

}
