package uk.gov.hmcts.probate.service.notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.SendEmailResponse;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_CAVEAT;
import static uk.gov.hmcts.probate.model.State.CASE_STOPPED_REQUEST_INFORMATION;
import static uk.gov.hmcts.probate.model.State.CAVEAT_RAISED;
import static uk.gov.hmcts.probate.model.State.DOCUMENTS_RECEIVED;
import static uk.gov.hmcts.probate.model.State.GENERAL_CAVEAT_MESSAGE;
import static uk.gov.hmcts.probate.model.State.GRANT_ISSUED;
import static uk.gov.hmcts.probate.model.State.GRANT_REISSUED;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockBean
    private CaveatQueryService caveatQueryServiceMock;

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private SendEmailResponse sendEmailResponse;


    @Test
    public void getDocumentsReceivedPA() {

        String response = templateService.getTemplateId(DOCUMENTS_RECEIVED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("pa-document-received", response);

        String responseWelsh = templateService.getTemplateId(DOCUMENTS_RECEIVED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("pa-document-received-welsh", responseWelsh);
    }

    @Test
    public void getDocumentsReceivedSols() {

        String response = templateService.getTemplateId(DOCUMENTS_RECEIVED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("sol-document-received", response);

        String responseWelsh = templateService.getTemplateId(DOCUMENTS_RECEIVED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("sol-document-received-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedPA() {

        String response = templateService.getTemplateId(CASE_STOPPED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("pa-case-stopped", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("pa-case-stopped-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedSols() {

        String response = templateService.getTemplateId(CASE_STOPPED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("sol-case-stopped", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("sol-case-stopped-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedCaveatPA() {

        String response = templateService.getTemplateId(CASE_STOPPED_CAVEAT, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("pa-case-stopped-caveat", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_CAVEAT, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("pa-case-stopped-caveat-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedCaveatSols() {

        String response = templateService.getTemplateId(CASE_STOPPED_CAVEAT, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("sol-case-stopped-caveat", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_CAVEAT, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("sol-case-stopped-caveat-welsh", responseWelsh);
    }

    @Test
    public void getGrantIssuedPA() {

        String response = templateService.getTemplateId(GRANT_ISSUED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("pa-grant-issued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_ISSUED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("pa-grant-issued-welsh", responseWelsh);
    }

    @Test
    public void getGrantIssuedSols() {

        String response = templateService.getTemplateId(GRANT_ISSUED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("sol-grant-issued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_ISSUED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("sol-grant-issued-welsh", responseWelsh);
    }

    @Test
    public void getGrantReissuedPA() {

        String response = templateService.getTemplateId(GRANT_REISSUED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("pa-grant-reissued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_REISSUED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("pa-grant-reissued-welsh", responseWelsh);
    }

    @Test
    public void getGrantReissuedSols() {

        String response = templateService.getTemplateId(GRANT_REISSUED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("sol-grant-reissued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_REISSUED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("sol-grant-reissued-welsh", responseWelsh);
    }

    @Test
    public void getGeneralCaveatMessagePA() {

        String response = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("pa-general-caveat-message", response);

        String responseWelsh = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("pa-general-caveat-message-welsh", responseWelsh);
    }

    @Test
    public void getGeneralCaveatMessageSols() {

        String response = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("sol-general-caveat-message", response);

        String responseWelsh = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("sol-general-caveat-message-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedRequestForInfoPA() {

        String response = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, ApplicationType.PERSONAL, "CTSC",LanguagePreference.ENGLISH);
        assertEquals("pa-request-information", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, ApplicationType.PERSONAL, "CTSC",LanguagePreference.WELSH);
        assertEquals("pa-request-information-welsh", responseWelsh);
    }

    @Test
    public void getCaseStoppedRequestForInfoSols() {

        String response = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, ApplicationType.SOLICITOR, "CTSC",LanguagePreference.ENGLISH);
        assertEquals("sols-request-information", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, ApplicationType.SOLICITOR, "CTSC",LanguagePreference.WELSH);
        assertEquals("sols-request-information-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedPA() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.PERSONAL, "oxford",
                LanguagePreference.ENGLISH);
        assertEquals("pa-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.PERSONAL, "oxford",
                LanguagePreference.WELSH);
        assertEquals("pa-caveat-raised-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedSols() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.SOLICITOR, "oxford"
                ,LanguagePreference.ENGLISH);
        assertEquals("sols-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.SOLICITOR, "oxford"
                ,LanguagePreference.WELSH);
        assertEquals("sols-caveat-raised-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedPersonalCTSC() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("pa-ctsc-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.PERSONAL, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("pa-ctsc-caveat-raised-welsh", responseWelsh);
    }

    @Test
    public void getCaveatRaisedSolsCTSC() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.ENGLISH);
        assertEquals("sols-ctsc-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, ApplicationType.SOLICITOR, "CTSC",
                LanguagePreference.WELSH);
        assertEquals("sols-ctsc-caveat-raised-welsh", responseWelsh);
    }
}
