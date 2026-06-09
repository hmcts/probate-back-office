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
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
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
    @Mock
    private  CoreCaseDataApi coreCaseDataApi;

    private static final String[] LAST_MODIFIED = {"2022", "1", "1", "0", "0", "0", "0"};
    private List<ReturnedCaseDetails> caseList;
    private List<ReturnedCaseDetails> caseList1;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(dormantCaseService, "makeDormantAddTimeMinutes", 5);
        CaseData caseData = CaseData.builder()
                .deceasedSurname("Smith")
                .build();
        CaseData migratedCaseData = CaseData.builder()
                .deceasedSurname("Smith")
                .lastModifiedDateForDormant(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        caseList = new ImmutableList.Builder<ReturnedCaseDetails>()
                .add(new ReturnedCaseDetails(caseData, LocalDateTime.now(ZoneOffset.UTC), 1L))
                .add(new ReturnedCaseDetails(migratedCaseData,
                        LocalDateTime.now(ZoneOffset.UTC).minusMonths(1), 1L))
                .build();
        CaseData caseData1 = CaseData.builder()
                .deceasedSurname("Doe")
                .lastModifiedDateForDormant(LocalDateTime.now(ZoneOffset.UTC))
                .moveToDormantDateTime(DATE_FORMAT.format(LocalDateTime.now(ZoneOffset.UTC)
                .minusMonths(1L)))
                .build();
        caseList1 = new ImmutableList.Builder<ReturnedCaseDetails>().add(new ReturnedCaseDetails(caseData1,
                LocalDateTime.now(ZoneOffset.UTC), 1L)).build();
    }

    @Test
    void shouldMakeDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caseQueryService.findCaseToBeMadeDormant("2022-01-01", "2022-01-10")).thenReturn(caseList);
        dormantCaseService.makeCasesDormant("2022-01-01", "2022-01-10");
        verify(ccdClientApi, times(2))
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                 any(), any(), any(), any());
        assertEquals(2, caseQueryService.findCaseToBeMadeDormant("2022-01-01", "2022-01-10").size());
    }

    @Test
    void shouldReturnWhenNoCasesInMakeDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        List<ReturnedCaseDetails> results = new ArrayList<>();
        when(caseQueryService.findCaseToBeMadeDormant("2022-01-01", "2022-01-10")).thenReturn(results);
        dormantCaseService.makeCasesDormant("2022-01-01", "2022-01-10");
        assertEquals(0, caseQueryService.findCaseToBeMadeDormant("2022-01-01", "2022-01-10").size());
    }

    @Test
    void shouldReactivateDormant() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caseQueryService.findCaseToBeReactivatedFromDormant("2022-01-01")).thenReturn(caseList1);
        dormantCaseService.reactivateDormantCases("2022-01-01");
        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(), any(),
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

    @Test
    void shouldThrowExceptionForMakeDormantCases() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caseQueryService.findCaseToBeMadeDormant("2022-01-01", "2022-01-10")).thenReturn(caseList);
        doThrow(new NullPointerException()).when(ccdClientApi)
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                        any(), any(), any(), any());
        dormantCaseService.makeCasesDormant("2022-01-01", "2022-01-10");
        verify(ccdClientApi, times(2))
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                        any(), any(), any(), any());
        verifyNoInteractions(coreCaseDataApi);
    }

    @Test
    void shouldThrowForReactivateDormantCases() {
        SecurityDTO securityDTO = SecurityDTO.builder()
                .authorisation("AUTH")
                .serviceAuthorisation("S2S")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(caseQueryService.findCaseToBeReactivatedFromDormant("2022-01-01")).thenReturn(caseList1);
        doThrow(new NullPointerException()).when(ccdClientApi)
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                        any(), any(), any(), any());
        dormantCaseService.reactivateDormantCases("2022-01-01");
        verify(ccdClientApi, times(1))
                .updateCaseAsCaseworker(any(), any(), any(), any(),
                        any(), any(), any(), any());
        verifyNoInteractions(coreCaseDataApi);
    }
}
