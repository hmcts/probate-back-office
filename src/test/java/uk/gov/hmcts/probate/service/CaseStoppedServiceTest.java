package uk.gov.hmcts.probate.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CaseStoppedServiceTest {

    @InjectMocks
    private CaseStoppedService caseStoppedService;

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    private CaseDetails caseDetails;

    @BeforeEach
    public void setUpTest() {
        caseDetails = new CaseDetails(CaseData.builder()
            .caseType("gop")
            .applicationType(ApplicationType.PERSONAL)
            .build(),
            LAST_MODIFIED, CASE_ID);
    }

    @Test
    void shouldIncrementGrantDelayedNotificationDateByStoppedPeriodWhenCaseResolved() {

        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantAwaitingDocumentationNotificationDate(LocalDate.now().plusWeeks(3));
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);

        assertEquals(LocalDate.now().plusWeeks(8), caseDetails.getData().getGrantDelayedNotificationDate());
        assertEquals(LocalDate.now().plusWeeks(5), caseDetails.getData()
            .getGrantAwaitingDocumentationNotificationDate());
    }

    @Test
    void shouldNotIncrementGrantDelayedNotificationDateWhenNoticationSent() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.YES);
        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);
        assertEquals(LocalDate.now().plusWeeks(6), caseDetails.getData().getGrantDelayedNotificationDate());
    }

    @Test
    void shouldNotIncrementGrantDelayedNotificationDateWhenGrantNotificationDateIsNull() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.NO);
        caseDetails.getData().setGrantDelayedNotificationDate(null);
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);

        assertEquals(null, caseDetails.getData().getGrantDelayedNotificationDate());
    }

    @Test
    void shouldNotIncrementGrantDelayedNotificationDateWhenGrantStoppedDateIsNull() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.NO);
        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(null);

        caseStoppedService.caseResolved(caseDetails);

        assertEquals(LocalDate.now().plusWeeks(6), caseDetails.getData().getGrantDelayedNotificationDate());
    }

    @Test
    void shouldNotIncrementGrantAwaitingDocsNotificationDateWhenNoDocDateIsSet() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.NO);
        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(null);

        caseStoppedService.caseResolved(caseDetails);

        assertEquals(LocalDate.now().plusWeeks(6), caseDetails.getData().getGrantDelayedNotificationDate());
        assertEquals(null, caseDetails.getData().getGrantAwaitingDocumentationNotificationDate());
    }

    @Test
    void shouldSetEvidenceHandledNo() {
        caseDetails.getData().setEvidenceHandled(Constants.YES);
        caseStoppedService.setEvidenceHandledNo(caseDetails);
        assertEquals(Constants.NO, caseDetails.getData().getEvidenceHandled());

        caseDetails.getData().setEvidenceHandled(null);
        caseStoppedService.setEvidenceHandledNo(caseDetails);
        assertEquals(Constants.NO, caseDetails.getData().getEvidenceHandled());
    }

    @Test
    void shouldSetFirstStopReminderSentDateNull() {
        caseDetails.getData().setGrantStoppedDate(null);
        caseDetails.getData().setDocumentUploadedAfterCaseStopped(Constants.YES);
        caseDetails.getData().setFirstStopReminderSentDate(LocalDate.now());
        caseStoppedService.caseStopped(caseDetails);
        assertEquals(Constants.NO, caseDetails.getData().getDocumentUploadedAfterCaseStopped());
        assertEquals(LocalDate.now(), caseDetails.getData().getGrantStoppedDate());
        assertNull(caseDetails.getData().getFirstStopReminderSentDate());

    }
}
