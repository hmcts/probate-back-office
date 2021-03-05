package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.payments.pba.PBARetrievalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SolicitorPBADefaulterTest {
    @InjectMocks
    private SolicitorPBADefaulter solicitorPBADefaulter;

    @Mock
    private PBARetrievalService pbaRetrievalService;

    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaveatData caveatDataMock;

    private static final String AUTH_TOKEN = "AUTH";

    List<String> allPBAs = Arrays.asList("PBA1111", "PBA2222", "PBA3333");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(pbaRetrievalService.getPBAs(AUTH_TOKEN)).thenReturn(allPBAs);
        when(caseDataMock.getSolsSolicitorAppReference()).thenReturn("SolAppRef");
        when(caveatDataMock.getSolsSolicitorAppReference()).thenReturn("SolAppRef");
    }

    @Test
    public void shouldReturnListOfPBAs() {
        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = ResponseCaseData.builder();

        solicitorPBADefaulter.defaultFeeAccounts(caseDataMock, responseCaseDataBuilder, AUTH_TOKEN);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(3, responseCaseData.getSolsPBANumber().getListItems().size());
        assertEquals("PBA1111", responseCaseData.getSolsPBANumber().getListItems().get(0).getCode());
        assertEquals("PBA2222", responseCaseData.getSolsPBANumber().getListItems().get(1).getCode());
        assertEquals("PBA3333", responseCaseData.getSolsPBANumber().getListItems().get(2).getCode());
        assertEquals(null, responseCaseData.getSolsPBANumber().getValue().getCode());
        assertEquals("Yes", responseCaseData.getSolsOrgHasPBAs());
        assertEquals("SolAppRef", responseCaseData.getSolsPBAPaymentReference());
    }

    @Test
    public void shouldReturnNoPBAs() {
        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = ResponseCaseData.builder();
        when(pbaRetrievalService.getPBAs(AUTH_TOKEN)).thenReturn(Collections.emptyList());

        solicitorPBADefaulter.defaultFeeAccounts(caseDataMock, responseCaseDataBuilder, AUTH_TOKEN);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(0, responseCaseData.getSolsPBANumber().getListItems().size());
        assertEquals("No", responseCaseData.getSolsOrgHasPBAs());
    }

    @Test
    public void shouldReturnListOfPBAsForCaveats() {
        ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = ResponseCaveatData.builder();

        solicitorPBADefaulter.defaultCaveatFeeAccounts(caveatDataMock, responseCaseDataBuilder, AUTH_TOKEN);

        ResponseCaveatData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(3, responseCaseData.getSolsPBANumber().getListItems().size());
        assertEquals("PBA1111", responseCaseData.getSolsPBANumber().getListItems().get(0).getCode());
        assertEquals("PBA2222", responseCaseData.getSolsPBANumber().getListItems().get(1).getCode());
        assertEquals("PBA3333", responseCaseData.getSolsPBANumber().getListItems().get(2).getCode());
        assertEquals(null, responseCaseData.getSolsPBANumber().getValue().getCode());
        assertEquals("Yes", responseCaseData.getSolsOrgHasPBAs());
        assertEquals("SolAppRef", responseCaseData.getSolsPBAPaymentReference());
    }

    @Test
    public void shouldReturnNoPBAsForCaveats() {
        ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = ResponseCaveatData.builder();
        when(pbaRetrievalService.getPBAs(AUTH_TOKEN)).thenReturn(Collections.emptyList());

        solicitorPBADefaulter.defaultCaveatFeeAccounts(caveatDataMock, responseCaseDataBuilder, AUTH_TOKEN);

        ResponseCaveatData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(0, responseCaseData.getSolsPBANumber().getListItems().size());
        assertEquals("No", responseCaseData.getSolsOrgHasPBAs());
    }
}