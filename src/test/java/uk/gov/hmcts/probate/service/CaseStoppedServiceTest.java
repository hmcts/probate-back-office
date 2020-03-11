package uk.gov.hmcts.probate.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseStoppedServiceTest {

    @InjectMocks
    private CaseStoppedService caseStoppedService;

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CcdClientApi ccdClientApi;

    private static final Long ID = 1L;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @Captor
    private ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataArgumentCaptor;
    private CaseDetails caseDetails;

    @Before
    public void setUpTest() {
        SecurityDTO securityDTO = SecurityDTO.builder().authorisation("authorisation").userId("userId").serviceAuthorisation("serviceAuthorisation").build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        caseDetails = new CaseDetails(CaseData.builder()
                .caseType("gop")
                .applicationType(ApplicationType.PERSONAL)
                .build(),
                LAST_MODIFIED, CASE_ID);
    }

    @Test
    public void shouldSetGrantStoppedDateToNow() {

        caseStoppedService.caseStopped(caseDetails);

        verify(ccdClientApi).updateCaseAsCaseworker(any(CcdCaseType.class), anyString(), grantOfRepresentationDataArgumentCaptor.capture(), any(EventId.class), any(SecurityDTO.class));

        GrantOfRepresentationData gop = grantOfRepresentationDataArgumentCaptor.getValue();

        assertEquals(LocalDate.now(), gop.getGrantStoppedDate());

    }

    @Test
    public void shouldSetGrantStoppedDateIfAlreadySet() {
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseStopped(caseDetails);

        verify(ccdClientApi).updateCaseAsCaseworker(any(CcdCaseType.class), anyString(), grantOfRepresentationDataArgumentCaptor.capture(), any(EventId.class), any(SecurityDTO.class));

        GrantOfRepresentationData gop = grantOfRepresentationDataArgumentCaptor.getValue();

        assertEquals(LocalDate.now(), gop.getGrantStoppedDate());
    }


    @Test
    public void shouldIncrementGrantDelayedNotificationDateByStoppedPeriodWhenCaseResolved() {

        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);

        verify(ccdClientApi).updateCaseAsCaseworker(any(CcdCaseType.class), anyString(), grantOfRepresentationDataArgumentCaptor.capture(), any(EventId.class), any(SecurityDTO.class));

        GrantOfRepresentationData gop = grantOfRepresentationDataArgumentCaptor.getValue();

        assertEquals(LocalDate.now().plusWeeks(8), gop.getGrantDelayedNotificationDate());
    }

    @Test
    public void shouldNotIncrementGrantDelayedNotificationDateWhenNoticationSent() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.YES);
        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);

        verifyNoMoreInteractions(ccdClientApi);
    }

    @Test
    public void shouldNotIncrementGrantDelayedNotificationDateWhenGrantNotificationDateIsNull() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.NO);
        caseDetails.getData().setGrantDelayedNotificationDate(null);
        caseDetails.getData().setGrantStoppedDate(LocalDate.now().minusWeeks(2));

        caseStoppedService.caseResolved(caseDetails);

        verifyNoMoreInteractions(ccdClientApi);
    }

    @Test
    public void shouldNotIncrementGrantDelayedNotificationDateWhenGrantStoppedDateIsNull() {

        caseDetails.getData().setGrantDelayedNotificationSent(Constants.NO);
        caseDetails.getData().setGrantDelayedNotificationDate(LocalDate.now().plusWeeks(6));
        caseDetails.getData().setGrantStoppedDate(null);

        caseStoppedService.caseResolved(caseDetails);

        verifyNoMoreInteractions(ccdClientApi);
    }
}