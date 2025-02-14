package uk.gov.hmcts.probate.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class DisposalCCDServiceTest {

    @Mock
    private CcdClientApi ccdClientApi;

    @Mock
    private CaseData caseDataMock;

    @InjectMocks
    private DisposalCCDService disposalCCDService;

    private static final String CASE_ID = "1234567890123456";
    private static final EventId EVENT_ID = EventId.DISPOSE_CASE;
    private static final CcdCaseType CASE_TYPE_GOP = CcdCaseType.GRANT_OF_REPRESENTATION;
    private static final CcdCaseType CASE_TYPE_CAVEAT = CcdCaseType.CAVEAT;
    private static final String DESCRIPTION = DisposalCCDService.DISPOSE_DRAFT_DESCRIPTION;
    private static final String SUMMARY = DisposalCCDService.DISPOSE_DRAFT_SUMMARY;

    private CaseDetails caseDetails;
    private SecurityDTO securityDTO;

    @BeforeEach
    void setUp() {
        caseDetails = mock(CaseDetails.class);
        securityDTO = mock(SecurityDTO.class);

        when(caseDetails.getLastModified()).thenReturn(LocalDateTime.of(2018, 10, 11, 13, 14, 20));
    }

    @Test
    void shouldDisposeGOPCaseSuccessfully() {
        disposalCCDService.disposeGOPCase(caseDetails, CASE_ID, securityDTO);

        verify(ccdClientApi, times(1)).updateCaseAsCaseworker(
                eq(CASE_TYPE_GOP), eq(CASE_ID),
                any(), any(),
                eq(EVENT_ID), eq(securityDTO),
                eq(DESCRIPTION), eq(SUMMARY)
        );

        verifyNoMoreInteractions(ccdClientApi);
    }

    @Test
    void shouldDisposeCaveatCaseSuccessfully() {
        disposalCCDService.disposeCaveatCase(caseDetails, CASE_ID, securityDTO);

        verify(ccdClientApi, times(1)).updateCaseAsCaseworker(
                eq(CASE_TYPE_CAVEAT), eq(CASE_ID),
                any(), any(),
                eq(EVENT_ID), eq(securityDTO),
                eq(DESCRIPTION), eq(SUMMARY)
        );

        verifyNoMoreInteractions(ccdClientApi);
    }
}
