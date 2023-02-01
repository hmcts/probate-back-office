package uk.gov.hmcts.probate.service.payments.pba;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.payments.CasePaymentBuilder;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CaseDetails caseDetails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        caseDataMap = new HashMap<String, Object>();
        paymentsList = new ArrayList<Map>();
        paymentMaps = new HashMap();
        paymentMap = new HashMap();
        caseDetails = Mockito.mock(CaseDetails.class);
    }

}
