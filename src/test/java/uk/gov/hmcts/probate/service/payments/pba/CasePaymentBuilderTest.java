package uk.gov.hmcts.probate.service.payments.pba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.service.payments.CasePaymentBuilder;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CasePaymentBuilderTest {
    @InjectMocks
    private CasePaymentBuilder casePaymentBuilder;
    @Mock
    private CasePayment casePayment;
    private static final String KEY_PAYMENT_STATUS = "status";
    private static final String KEY_PAYMENT_TRANSACTION_ID = "transactionId";
    private static final String KEY_PAYMENT_SITE_ID = "siteId";
    private static final String KEY_PAYMENT_REFERENCE = "reference";
    private static final String KEY_PAYMENT_METHOD = "method";
    private static final String KEY_PAYMENT_DATE = "date";
    private static final String KEY_PAYMENT_AMOUNT = "amount";
    private static final String KEY_COLLECTION_VALUE = "value";
    private Map<String, Object> caseDataMap;
    private List<Map> paymentsList;
    private Map paymentMaps;
    private Map paymentMap;
    private CasePayment payment;
    private uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        caseDataMap = new HashMap<String, Object>();
        paymentsList = new ArrayList<Map>();
        paymentMaps = new HashMap();
        paymentMap = new HashMap();
        caseDetails = Mockito.mock(uk.gov.hmcts.reform.ccd.client.model.CaseDetails.class);
    }

    @Test
    void shouldBuildWithPayment() {
        paymentMap.put(KEY_PAYMENT_STATUS, "Success");
        paymentMap.put(KEY_PAYMENT_TRANSACTION_ID, "TransactionId-123");
        paymentMap.put(KEY_PAYMENT_REFERENCE, "Reference-123");
        paymentMap.put(KEY_PAYMENT_METHOD, "online");
        paymentMap.put(KEY_PAYMENT_DATE, "2022-11-02");
        paymentMap.put(KEY_PAYMENT_AMOUNT, "100");
        paymentMap.put(KEY_PAYMENT_SITE_ID, "SiteId-123");
        paymentMaps.put(KEY_COLLECTION_VALUE, paymentMap);
        paymentMaps.put("id", casePayment);
        paymentsList.add(paymentMaps);
        caseDataMap.put("payments", paymentsList);

        when(caseDetails.getData()).thenReturn(caseDataMap);

        List<CollectionMember<CasePayment>> payments = casePaymentBuilder.buildCurrentPayments(caseDetails);
        for (CollectionMember<CasePayment> paymentCollectionMember : payments) {
            payment = paymentCollectionMember.getValue();
        }

        assertEquals("Reference-123", payment.getReference());
        assertEquals("online", payment.getMethod());
    }

    @Test
    void shouldThrowExceptionBuildPayment() {
        paymentMap.put(KEY_PAYMENT_STATUS, "Success");
        paymentMap.put(KEY_PAYMENT_TRANSACTION_ID, "TransactionId-123");
        paymentMap.put(KEY_PAYMENT_REFERENCE, "Reference-123");
        paymentMap.put(KEY_PAYMENT_METHOD, "online");
        paymentMap.put(KEY_PAYMENT_DATE, "02/11-2022");
        paymentMap.put(KEY_PAYMENT_SITE_ID, "SiteId-123");
        paymentMaps.put(KEY_COLLECTION_VALUE, paymentMap);
        paymentMaps.put("id", casePayment);
        paymentsList.add(paymentMaps);
        caseDataMap.put("payments", paymentsList);

        when(caseDetails.getData()).thenReturn(caseDataMap);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            casePaymentBuilder.buildCurrentPayments(caseDetails);
        });

        assertEquals("Unparseable date: \"02/11-2022\"",
                exception.getMessage());
    }

    @Test
    void shouldBuildWithNoPayment() {
        caseDataMap.put("payments", paymentsList);
        when(caseDetails.getData()).thenReturn(caseDataMap);

        List<CollectionMember<CasePayment>> payments = casePaymentBuilder.buildCurrentPayments(caseDetails);

        assertEquals(0, payments.size());
    }
}
