package uk.gov.hmcts.probate.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CaseStoppedServiceTest {

    @InjectMocks
    private CaseStoppedService caseStoppedService;

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    private CaseDetails caseDetails;

    @Before
    public void setUpTest() {
        caseDetails = new CaseDetails(CaseData.builder()
            .caseType("gop")
            .applicationType(ApplicationType.PERSONAL)
            .build(),
            LAST_MODIFIED, CASE_ID);
    }

    @Test
    public void shouldIncrementGrantDelayedNotificationDateByStoppedPeriodWhenCaseResolved() {

        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);

        assertEquals(LocalDate.now().plusWeeks(8), caseDetails.getData().getGrantDelayedNotificationDate());
    }

    @Test
    public void shouldNotIncrementGrantDelayedNotificationDateWhenNoticationSent() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.YES);
        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);
        assertEquals(LocalDate.now().plusWeeks(6), caseDetails.getData().getGrantDelayedNotificationDate());
    }

    @Test
    public void shouldNotIncrementGrantDelayedNotificationDateWhenGrantNotificationDateIsNull() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.NO);
        caseDetails.getData().setGrantDelayedNotificationDate(null);
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);

        assertEquals(null, caseDetails.getData().getGrantDelayedNotificationDate());
    }

    @Test
    public void shouldNotIncrementGrantDelayedNotificationDateWhenGrantStoppedDateIsNull() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.NO);
        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(null);

        caseStoppedService.caseResolved(caseDetails);

        assertEquals(LocalDate.now().plusWeeks(6), caseDetails.getData().getGrantDelayedNotificationDate());
    }
}