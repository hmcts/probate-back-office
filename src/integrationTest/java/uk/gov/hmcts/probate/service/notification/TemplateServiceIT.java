package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.CaseOrigin;
import uk.gov.hmcts.probate.model.LanguagePreference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_BULKSCAN;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_PAPERFORM;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_DIGITAL;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED;
import static uk.gov.hmcts.probate.model.State.APPLICATION_RECEIVED_NO_DOCS;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TemplateServiceIT {

    @Autowired
    private TemplateService templateService;

    @Test
    void getDocumentsReceivedPA() {

        String response = templateService.getTemplateId(DOCUMENTS_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-document-received", response);

        String responseWelsh = templateService.getTemplateId(DOCUMENTS_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-document-received-welsh", responseWelsh);
    }

    @Test
    void getApplicationReceivedPA() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-application-received", response);

        String responseWelsh = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-application-received-welsh", responseWelsh);
    }

    @Test
    void getApplicationReceivedPANoDocsRequired() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED_NO_DOCS, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-application-received-no-docs", response);

        String responseWelsh = templateService.getTemplateId(APPLICATION_RECEIVED_NO_DOCS, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-application-received-no-docs-welsh", responseWelsh);
    }


    @Test
    void getDocumentsReceivedSols() {

        String response = templateService.getTemplateId(DOCUMENTS_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-document-received", response);

        String responseWelsh = templateService.getTemplateId(DOCUMENTS_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.WELSH);
        assertEquals("sol-document-received-welsh", responseWelsh);
    }

    @Test
    void getCaseStoppedPA() {

        String response = templateService.getTemplateId(CASE_STOPPED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-case-stopped", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-case-stopped-welsh", responseWelsh);
    }

    @Test
    void getCaseStoppedSols() {

        String response = templateService.getTemplateId(CASE_STOPPED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-case-stopped", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED, SOLICITOR, CTSC,
            LanguagePreference.WELSH);
        assertEquals("sol-case-stopped-welsh", responseWelsh);
    }

    @Test
    void getCaseStoppedCaveatPA() {

        String response = templateService.getTemplateId(CASE_STOPPED_CAVEAT, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-case-stopped-caveat", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_CAVEAT, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-case-stopped-caveat-welsh", responseWelsh);
    }

    @Test
    void getCaseStoppedCaveatSols() {

        String response = templateService.getTemplateId(CASE_STOPPED_CAVEAT, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-case-stopped-caveat", response);

        String responseWelsh = templateService.getTemplateId(CASE_STOPPED_CAVEAT, SOLICITOR, CTSC,
            LanguagePreference.WELSH);
        assertEquals("sol-case-stopped-caveat-welsh", responseWelsh);
    }

    @Test
    void getGrantIssuedPA() {

        String response = templateService.getTemplateId(GRANT_ISSUED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-grant-issued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_ISSUED, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-grant-issued-welsh", responseWelsh);
    }

    @Test
    void getGrantIssuedSols() {

        String response = templateService.getTemplateId(GRANT_ISSUED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-grant-issued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_ISSUED, SOLICITOR, CTSC,
            LanguagePreference.WELSH);
        assertEquals("sol-grant-issued-welsh", responseWelsh);
    }

    @Test
    void getGrantReissuedPA() {

        String response = templateService.getTemplateId(GRANT_REISSUED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-grant-reissued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_REISSUED, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-grant-reissued-welsh", responseWelsh);
    }

    @Test
    void getGrantReissuedSols() {

        String response = templateService.getTemplateId(GRANT_REISSUED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-grant-reissued", response);

        String responseWelsh = templateService.getTemplateId(GRANT_REISSUED, SOLICITOR, CTSC,
            LanguagePreference.WELSH);
        assertEquals("sol-grant-reissued-welsh", responseWelsh);
    }

    @Test
    void getGeneralCaveatMessagePA() {

        String response = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-general-caveat-message", response);

        String responseWelsh = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-general-caveat-message-welsh", responseWelsh);
    }

    @Test
    void getGeneralCaveatMessageSols() {

        String response = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-general-caveat-message", response);

        String responseWelsh = templateService.getTemplateId(GENERAL_CAVEAT_MESSAGE, SOLICITOR, CTSC,
            LanguagePreference.WELSH);
        assertEquals("sol-general-caveat-message-welsh", responseWelsh);
    }

    @Test
    void getCaseStoppedRequestForInfoPA() {

        String response =
            templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC, LanguagePreference.ENGLISH,
                null, CHANNEL_CHOICE_DIGITAL, NO);
        assertEquals("pa-request-information", response);

        String responseWelsh =
            templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC, LanguagePreference.WELSH,
                null, CHANNEL_CHOICE_DIGITAL, NO);
        assertEquals("pa-request-information-welsh", responseWelsh);

        String byPostResponse =
                templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC,
                        LanguagePreference.ENGLISH,null, CHANNEL_CHOICE_DIGITAL, YES);
        assertEquals("pa-request-information-by-post", byPostResponse);

        String byPostResponseWelsh =
                templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC,
                        LanguagePreference.WELSH,null, CHANNEL_CHOICE_DIGITAL, YES);
        assertEquals("pa-request-information-by-post-welsh", byPostResponseWelsh);
    }

    @Test
    void getCaseStoppedRequestForInfoPaperPA() {
        String response =
                templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC,
                        LanguagePreference.ENGLISH, null, CHANNEL_CHOICE_BULKSCAN, null);
        assertEquals("pa-request-information-by-post", response);

        String responseWelsh =
                templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, PERSONAL, CTSC,
                        LanguagePreference.WELSH, null, CHANNEL_CHOICE_PAPERFORM, NO);
        assertEquals("pa-request-information-by-post-welsh", responseWelsh);
    }

    @Test
    void getCaseStoppedRequestForInfoPaperSols() {
        String response =
                templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, SOLICITOR, CTSC,
                        LanguagePreference.ENGLISH,null, CHANNEL_CHOICE_BULKSCAN, null);
        assertEquals("sols-request-information", response);

        String responseWelsh =
                templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, SOLICITOR, CTSC,
                        LanguagePreference.WELSH,null, CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("sols-request-information-welsh", responseWelsh);
    }

    @Test
    void getCaseStoppedRequestForInfoSols() {

        String response = templateService
            .getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, SOLICITOR, CTSC, LanguagePreference.ENGLISH,
                    null, CHANNEL_CHOICE_DIGITAL, null);
        assertEquals("sols-request-information", response);

        String responseWelsh =
            templateService.getTemplateId(CASE_STOPPED_REQUEST_INFORMATION, SOLICITOR, CTSC, LanguagePreference.WELSH,
                    null, CHANNEL_CHOICE_DIGITAL, null);
        assertEquals("sols-request-information-welsh", responseWelsh);
    }

    @Test
    void getCaveatRaisedPA() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, PERSONAL, "oxford",
            LanguagePreference.ENGLISH);
        assertEquals("pa-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, PERSONAL, "oxford",
            LanguagePreference.WELSH);
        assertEquals("pa-caveat-raised-welsh", responseWelsh);
    }

    @Test
    void getCaveatRaisedSols() {

        String response = templateService.getTemplateId(CAVEAT_RAISED, SOLICITOR, "oxford",
            LanguagePreference.ENGLISH);
        assertEquals("sols-caveat-raised", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_RAISED, SOLICITOR, "oxford",
            LanguagePreference.WELSH);
        assertEquals("sols-caveat-raised-welsh", responseWelsh);
    }

    @Test
    void getSolsCaveatRaised() {

        String response = templateService.getTemplateId(CAVEAT_RAISED_SOLS, SOLICITOR,
            CTSC, LanguagePreference.ENGLISH);

        assertEquals("solicitor-caveat-raised", response);
    }

    @Test
    void getCaveatExtendPersonalCTSC() {

        String response = templateService.getTemplateId(CAVEAT_EXTEND, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-ctsc-caveat-extend", response);

        String responseWelsh = templateService.getTemplateId(CAVEAT_EXTEND, PERSONAL, CTSC,
            LanguagePreference.WELSH);
        assertEquals("pa-ctsc-caveat-extend-welsh", responseWelsh);
    }

    @Test
    void shouldGetGrantRaisedTemplateForDigital() {

        String response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, null, CHANNEL_CHOICE_DIGITAL, null);
        assertEquals("pa-grant-raised", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, null, CHANNEL_CHOICE_DIGITAL, null);
        assertEquals("sol-grant-raised", response);

        response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
                LanguagePreference.WELSH, null, CHANNEL_CHOICE_DIGITAL, null);
        assertEquals("pa-grant-raised-welsh", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
                LanguagePreference.WELSH, null, CHANNEL_CHOICE_DIGITAL, null);
        assertEquals("sol-grant-raised-welsh", response);
    }

    @Test
    void shouldGetGrantRaisedTemplateForPaperFormIsNo() {

        String response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("pa-grant-raised", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH);
        assertEquals("sol-grant-raised", response);

        response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
                LanguagePreference.WELSH);
        assertEquals("pa-grant-raised-welsh", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
                LanguagePreference.WELSH);
        assertEquals("sol-grant-raised-welsh", response);
    }

    @Test
    void shouldGetGrantRaisedTemplateForPaperForm() {

        String response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, null, CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("pa-grant-raised-paper-bulk-scan", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, null, CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("sol-grant-raised-paper-bulk-scan", response);

        response = templateService.getTemplateId(GRANT_RAISED, PERSONAL, CTSC,
                LanguagePreference.WELSH, null, CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("pa-grant-raised-paper-bulk-scan-welsh", response);

        response = templateService.getTemplateId(GRANT_RAISED, SOLICITOR, CTSC,
                LanguagePreference.WELSH, null, CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("sol-grant-raised-paper-bulk-scan-welsh", response);
    }

    @Test
    void getApplicationReceivedPACaseworkerOrigin() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, CaseOrigin.CASEWORKER, CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("pa-application-received-cw", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, CaseOrigin.CASEWORKER,CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("sol-application-received-cw", response);
    }

    @Test
    void getApplicationReceivedPACaseworkerOriginPaperFormNo() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, CaseOrigin.CASEWORKER,null, null);
        assertEquals("pa-application-received", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, CaseOrigin.CASEWORKER,null, null);
        assertEquals("sol-application-received", response);
    }

    @Test
    void getApplicationReceivedPACaseworkerOriginWelsh() {
        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.WELSH, CaseOrigin.CASEWORKER,CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("pa-application-received-cw-welsh", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.WELSH, CaseOrigin.CASEWORKER,CHANNEL_CHOICE_PAPERFORM, null);
        assertEquals("sol-application-received-cw-welsh", response);
    }

    @Test
    void getApplicationReceivedPAOtherOrigin() {

        String response = templateService.getTemplateId(APPLICATION_RECEIVED, PERSONAL, CTSC,
            LanguagePreference.ENGLISH, CaseOrigin.CITIZEN,null, null);
        assertEquals("pa-application-received", response);

        response = templateService.getTemplateId(APPLICATION_RECEIVED, SOLICITOR, CTSC,
            LanguagePreference.ENGLISH, CaseOrigin.CITIZEN,null, null);
        assertEquals("sol-application-received", response);
    }

    @Test
    void getFirstStopReminder() {
        assertAll(
            () -> {
                String response = templateService.getStopReminderTemplateId(PERSONAL, LanguagePreference.ENGLISH,
                        CHANNEL_CHOICE_DIGITAL, NO, true);
                assertEquals("pa-first-stop-reminder-for-hub", response);
            },
            () -> {
                String response = templateService.getStopReminderTemplateId(PERSONAL, LanguagePreference.ENGLISH,
                        CHANNEL_CHOICE_DIGITAL, YES, true);
                assertEquals("pa-first-stop-reminder", response);
            },
            () -> {
                String response = templateService.getStopReminderTemplateId(SOLICITOR, LanguagePreference.ENGLISH,
                        CHANNEL_CHOICE_DIGITAL, null, true);
                assertEquals("sol-first-stop-reminder", response);
            });
    }

    @Test
    void getFirstStopReminderWelsh() {
        assertAll(
            () -> {
                String response = templateService.getStopReminderTemplateId(PERSONAL, LanguagePreference.WELSH,
                        CHANNEL_CHOICE_DIGITAL, NO, true);
                assertEquals("pa-first-stop-reminder-for-hub-welsh", response);
            },
            () -> {
                String response = templateService.getStopReminderTemplateId(PERSONAL, LanguagePreference.WELSH,
                        CHANNEL_CHOICE_DIGITAL, YES, true);
                assertEquals("pa-first-stop-reminder-welsh", response);
            },
            () -> {
                String response = templateService.getStopReminderTemplateId(SOLICITOR, LanguagePreference.WELSH,
                        CHANNEL_CHOICE_DIGITAL, null, true);
                assertEquals("sol-first-stop-reminder-welsh", response);
            }
        );
    }

    @Test
    void getDormantWarning() {
        assertAll(
            () -> {
                String response = templateService.getDormantWarningTemplateId(PERSONAL, LanguagePreference.ENGLISH);
                assertEquals("pa-dormant-warning", response);
            },
            () -> {
                String response = templateService.getDormantWarningTemplateId(PERSONAL, LanguagePreference.ENGLISH);
                assertEquals("pa-dormant-warning", response);
            },
            () -> {
                String response = templateService.getDormantWarningTemplateId(SOLICITOR, LanguagePreference.ENGLISH);
                assertEquals("sol-dormant-warning", response);
            });
    }

    @Test
    void getDormantWarningWelsh() {
        assertAll(
            () -> {
                String response = templateService.getDormantWarningTemplateId(PERSONAL, LanguagePreference.WELSH);
                assertEquals("pa-dormant-warning-welsh", response);
            },
            () -> {
                String response = templateService.getDormantWarningTemplateId(PERSONAL, LanguagePreference.WELSH);
                assertEquals("pa-dormant-warning-welsh", response);
            },
            () -> {
                String response = templateService.getDormantWarningTemplateId(SOLICITOR, LanguagePreference.WELSH);
                assertEquals("sol-dormant-warning-welsh", response);
            }
        );
    }
}
