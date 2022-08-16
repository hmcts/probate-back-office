package uk.gov.hmcts.probate.service;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class DormantCaseServiceTest {

    @InjectMocks
    private DormantCaseService dormantCaseService;

    @Mock
    private CaseQueryService caseQueryService;
    @Mock
    private CcdClientApi ccdClientApi;
    @Mock
    private SecurityUtils securityUtils;

    private static final String[] LAST_MODIFIED = {"2022", "1", "1", "0", "0", "0", "0"};
    private List<ReturnedCaseDetails> caseList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(dormantCaseService, "makeDormantAddTimeMinutes", 5);
        CaseData caseData = CaseData.builder()
                .deceasedSurname("Smith")
                .build();
        caseList = new ImmutableList.Builder<ReturnedCaseDetails>().add(new ReturnedCaseDetails(caseData,
                LAST_MODIFIED, 1L)).build();
    }

    @Test
    void shouldMakeDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caseQueryService.findCaseToBeMadeDormant("2022-01-01")).thenReturn(caseList);
        dormantCaseService.makeCasesDormant("2022-01-01");
        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(),
                 any(), any(), any(), any());
        assertEquals(1, caseQueryService.findCaseToBeMadeDormant("2022-01-01").size());
    }

    @Test
    void shouldReturnWhenNoCasesInMakeDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        List<ReturnedCaseDetails> results = new ArrayList<>();
        when(caseQueryService.findCaseToBeMadeDormant("2022-01-01")).thenReturn(results);
        dormantCaseService.makeCasesDormant("2022-01-01");
        assertEquals(0, caseQueryService.findCaseToBeMadeDormant("2022-01-01").size());
    }

    @Test
    void shouldReactivateDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caseQueryService.findCaseToBeReactivatedFromDormant("2022-01-01")).thenReturn(caseList);
        dormantCaseService.reactivateDormantCases("2022-01-01");
        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(),
                 any(), any(), any(), any());
        assertEquals(1, caseQueryService.findCaseToBeReactivatedFromDormant("2022-01-01").size());
    }

    @Test
    void shouldReturnWhenNoCasesInReactivateDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        List<ReturnedCaseDetails> results = new ArrayList<>();
        when(caseQueryService.findCaseToBeReactivatedFromDormant("2022-01-01")).thenReturn(results);
        dormantCaseService.reactivateDormantCases("2022-01-01");
        assertEquals(0, caseQueryService.findCaseToBeReactivatedFromDormant("2022-01-01").size());
    }
}
