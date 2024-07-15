package uk.gov.hmcts.probate.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ccd.raw.AuditEvent;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
class MigrationIssueDormantCaseServiceTest {

    @InjectMocks
    private MigrationIssueDormantCaseService migrationIssueDormantCaseService;

    @Mock
    private CoreCaseDataService coreCaseDataService;
    @Mock
    private CcdClientApi ccdClientApi;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private  CoreCaseDataApi coreCaseDataApi;
    @Mock
    private AuditEventService auditEventService;
    private List<String> caseReferenceList;
    private final String caseReference = "1234567890123456";
    private static final String EVENT = "CasePrinted";
    private static final LocalDateTime dormancyDate = LocalDateTime.now().minusMonths(6L);
    private SecurityDTO securityDTO;
    private AuditEvent mockedEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(migrationIssueDormantCaseService, "makeDormantAddTimeMinutes", 5);
        caseReferenceList = new ArrayList<>();
        caseReferenceList.add("1234567890123456");
        securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getUserBySchedulerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        mockedEvent = AuditEvent.builder().id(EVENT).userId("123")
                .createdDate(LocalDateTime.now().minusMonths(7L)).build();
    }

    @Test
    void shouldMakeCaseDormantWhenEventDateIsGreaterThanDormancyDate() {
        when(ccdClientApi.findCaseById(caseReference, securityDTO)).thenReturn(Optional.of(CaseDetails.builder()
                .id(1234567890123456L).build()));
        when(auditEventService.getLatestAuditEventByName(anyString(), anyList(), anyString(), anyString()))
                .thenReturn(Optional.of(mockedEvent));
        migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList, dormancyDate);
        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                 any(), any(), any(), any());
    }

    @Test
    void shouldNotMakeCaseDormantWhenEventDateIsLessThanDormancyDate() {
        when(ccdClientApi.findCaseById(caseReference, securityDTO)).thenReturn(Optional.of(CaseDetails.builder()
                .id(1234567890123456L).build()));
        mockedEvent = AuditEvent.builder().id(EVENT).userId("123")
                .createdDate(LocalDateTime.now()).build();
        when(auditEventService.getLatestAuditEventByName(anyString(), anyList(), anyString(), anyString()))
                .thenReturn(Optional.of(mockedEvent));
        migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList, dormancyDate);
        verify(ccdClientApi, never()).updateCaseAsCaseworker(any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    void shouldNotUpdateCaseWhenNoCasesInMakeDormant() {
        when(ccdClientApi.findCaseById(caseReference, securityDTO)).thenReturn(Optional.empty());
        migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList, dormancyDate);
        verify(ccdClientApi, never()).updateCaseAsCaseworker(any(), any(), any(), any(),
                any(), any(), any(), any());
        assertEquals(Optional.empty(), coreCaseDataService.findCaseById(caseReference, securityDTO));
    }

    @Test
    void shouldNotUpdateCaseWhenNoEventIsFound() {
        when(ccdClientApi.findCaseById(caseReference, securityDTO)).thenReturn(Optional.of(CaseDetails.builder()
                .id(1234567890123456L).build()));
        when(auditEventService.getLatestAuditEventByName(anyString(), anyList(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList, dormancyDate));
        Assert.assertEquals("Could not find any event other than [boHistoryCorrection, boCorrection] "
                + "event in audit", exception.getMessage());
        verify(auditEventService, Mockito.times(1)).getLatestAuditEventByName(anyString(),
                anyList(), anyString(), anyString());
        verify(ccdClientApi, never()).updateCaseAsCaseworker(any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    void shouldThrowExceptionForMakeDormantCases() {
        when(ccdClientApi.findCaseById(caseReference, securityDTO)).thenReturn(Optional.of(CaseDetails.builder()
                .id(1234567890123456L).build()));
        mockedEvent = AuditEvent.builder().id(EVENT).userId("123")
                .createdDate(LocalDateTime.now().minusMonths(6L)).build();
        when(auditEventService.getLatestAuditEventByName(anyString(), anyList(), anyString(), anyString()))
                .thenReturn(Optional.of(mockedEvent));
        when(coreCaseDataService.findCaseById(caseReference, securityDTO)).thenReturn(Optional.of(CaseDetails.builder()
                .id(1234567890123456L).build()));
        doThrow(new NullPointerException()).when(ccdClientApi)
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                        any(), any(), any(), any());
        migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList, dormancyDate);
        verifyNoInteractions(coreCaseDataApi);
    }
}
