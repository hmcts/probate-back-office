package uk.gov.hmcts.probate.service.payments.pba;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestPaymentResponseDto;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.probate.service.payments.CasePaymentBuilder;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CasePaymentBuilderTest {
    @InjectMocks
    private CasePaymentBuilder casePaymentBuilder;
    @Mock
    private CasePayment casePayment;
    private List<CollectionMember<Payment>> paymentsList;
    private Map paymentMaps;
    private Map paymentMap;
    private CasePayment payment;
    private CaseDetails caseDetails;
    @Mock
    private ServiceRequestUpdateResponseDto serviceRequestUpdateResponseDtoMock;
    @Mock
    private ServiceRequestPaymentResponseDto serviceRequestPaymentResponseDtoMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        paymentsList = new ArrayList();
        paymentMaps = new HashMap();
        paymentMap = new HashMap();
        caseDetails = Mockito.mock(CaseDetails.class);
        ReflectionTestUtils.setField(casePaymentBuilder, "siteId", "siteId");
    }

    @Test
    public void shouldGetAllPayments() {
        Payment payment1 = buildPayment(0);
        paymentsList.add(new CollectionMember<>(null, payment1));
        when(serviceRequestUpdateResponseDtoMock.getServiceRequestPaymentResponseDto())
                .thenReturn(serviceRequestPaymentResponseDtoMock);
        when(serviceRequestUpdateResponseDtoMock.getServiceRequestStatus()).thenReturn("Paid");
        when(serviceRequestUpdateResponseDtoMock.getServiceRequesAmount()).thenReturn(BigDecimal.valueOf(999));
        when(serviceRequestUpdateResponseDtoMock.getServiceRequestReference()).thenReturn("newServiceRequestRef");
        when(serviceRequestPaymentResponseDtoMock.getPaymentMethod()).thenReturn("payment by account");

        List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<CasePayment>> allCasePayments =
                casePaymentBuilder.addPaymentFromServiceRequestResponse(paymentsList,
                        serviceRequestUpdateResponseDtoMock);

        assertEquals(2, allCasePayments.size());
        assertPayment(allCasePayments.get(0).getValue(),
                LocalDate.parse("2001-12-31").toDateTimeAtStartOfDay().toDate(),
                100, "method", "ref0", "trans0", "Success");

        assertPayment(allCasePayments.get(1).getValue(),
                LocalDate.now().toDateTimeAtStartOfDay().toDate(),
                99900, "pba", "newServiceRequestRef", "newServiceRequestRef", "Success");
    }

    private void assertPayment(CasePayment casePayment, Date date, int amount, String method, String ref,
                               String trans, String status) {
        assertEquals(date, casePayment.getDate());
        assertEquals(amount, casePayment.getAmount());
        assertEquals(method, casePayment.getMethod());
        assertEquals(ref, casePayment.getReference());
        assertEquals("siteId", casePayment.getSiteId());
        assertEquals(trans, casePayment.getTransactionId());
        assertEquals(status, casePayment.getStatus().getName());
    }

    private Payment buildPayment(int version) {
        return Payment.builder()
                .date("2001-12-31")
                .amount("100")
                .method("method")
                .reference("ref" + version)
                .siteId("siteId")
                .status("Success")
                .transactionId("trans" + version)
                .build();

    }
}
