package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.service.fee.FeeService;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SolicitorPBAPaymentDefaulterTest {
    @InjectMocks
    private SolicitorPBAPaymentDefaulter solicitorPBAPaymentDefaulter;

    @Mock
    private FeeService feeServiceMock;

    @Mock
    private CaseData caseDataMock;
    @Mock
    private FeesResponse feesResponseMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSkipPBAPageForNoTotalFee() {
        when(caseDataMock.getIhtNetValue()).thenReturn(BigDecimal.ZERO);
        when(feeServiceMock.getAllFeesData(any(), any(), any())).thenReturn(feesResponseMock);
        when(caseDataMock.getSolsSolicitorAppReference()).thenReturn("SolAppRef");
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.ZERO);
        ResponseCaseData.ResponseCaseDataBuilder builder = ResponseCaseData.builder();

        solicitorPBAPaymentDefaulter.defaultPageFlowForPayments(caseDataMock, builder);
        assertEquals("No", builder.build().getSolsNeedsPBAPayment());
    }

    @Test
    public void shouldUsePBAPageForTotalFee() {
        when(caseDataMock.getIhtNetValue()).thenReturn(BigDecimal.valueOf(500100));
        when(feeServiceMock.getAllFeesData(any(), any(), any())).thenReturn(feesResponseMock);
        when(caseDataMock.getSolsSolicitorAppReference()).thenReturn("SolAppRef");
        when(feesResponseMock.getTotalAmount()).thenReturn(BigDecimal.valueOf(5001));
        ResponseCaseData.ResponseCaseDataBuilder builder = ResponseCaseData.builder();

        solicitorPBAPaymentDefaulter.defaultPageFlowForPayments(caseDataMock, builder);
        assertEquals("Yes", builder.build().getSolsNeedsPBAPayment());
    }

}