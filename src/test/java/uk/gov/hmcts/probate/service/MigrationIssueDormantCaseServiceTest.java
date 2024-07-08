package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
    private List<String> caseReferenceList;
    private String caseReference = "1234567890123456";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(migrationIssueDormantCaseService, "makeDormantAddTimeMinutes", 5);
        caseReferenceList = new ArrayList<>();
        caseReferenceList.add("1234567890123456");
        caseReferenceList.add("2234567890123456");
    }

    @Test
    void shouldMakeDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(coreCaseDataService.findCaseById(caseReference, securityDTO)).thenReturn(Optional.of(CaseDetails.builder()
                .id(1234567890123456L).build()));
        migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList);
        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                 any(), any(), any(), any());
    }

    @Test
    void shouldReturnWhenNoCasesInMakeDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(coreCaseDataService.findCaseById(caseReference, securityDTO)).thenReturn(Optional.empty());
        migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList);
        verify(ccdClientApi, never()).updateCaseAsCaseworker(any(), any(), any(), any(),
                any(), any(), any(), any());
        assertEquals(Optional.empty(), coreCaseDataService.findCaseById(caseReference, securityDTO));
    }

    @Test
    void shouldThrowExceptionForMakeDormantCases() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(coreCaseDataService.findCaseById(caseReference, securityDTO)).thenReturn(Optional.of(CaseDetails.builder()
                .id(1234567890123456L).build()));
        doThrow(new NullPointerException()).when(ccdClientApi)
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                        any(), any(), any(), any());
        migrationIssueDormantCaseService.makeCaseReferenceDormant(caseReferenceList);
        verifyNoInteractions(coreCaseDataApi);
    }
}
