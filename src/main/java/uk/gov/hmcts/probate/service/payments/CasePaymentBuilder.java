package uk.gov.hmcts.probate.service.payments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.probate.model.PaymentStatus;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasePaymentBuilder {
    final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final String KEY_PAYMENTS = "payments";
    private static final String KEY_COLLECTION_ID = "id";
    private static final String KEY_COLLECTION_VALUE = "value";
    private static final String KEY_PAYMENT_STATUS = "status";
    private static final String KEY_PAYMENT_TRANSACTION_ID = "transactionId";
    private static final String KEY_PAYMENT_SITE_ID = "siteId";
    private static final String KEY_PAYMENT_REFERENCE = "reference";
    private static final String KEY_PAYMENT_METHOD = "method";
    private static final String KEY_PAYMENT_DATE = "date";
    private static final String KEY_PAYMENT_AMOUNT = "amount";

    public List<CollectionMember<CasePayment>> buildCurrentPayments(CaseDetails caseDetails) {
        List<CollectionMember<CasePayment>> allPayments = new ArrayList<>();
        Map<String, Object> caseDataMap = caseDetails.getData();
        Object paymentsData = caseDataMap.get(KEY_PAYMENTS);
        if (paymentsData == null) {
            return allPayments;
        }

        List<Map> payMapList = (List<Map>) (caseDataMap.get(KEY_PAYMENTS));
        for (Map pay : payMapList) {
            Map cpHM = (Map) pay.get("value");
            CasePayment casePayment = CasePayment.builder()
                    .status(getPaymentStatus(cpHM.get(KEY_PAYMENT_STATUS).toString()))
                    .transactionId(cpHM.get(KEY_PAYMENT_TRANSACTION_ID).toString())
                    .siteId(cpHM.get(KEY_PAYMENT_SITE_ID).toString())
                    .reference(cpHM.get(KEY_PAYMENT_REFERENCE).toString())
                    .method(cpHM.get(KEY_PAYMENT_METHOD).toString())
                    .date(getDate(cpHM.get(KEY_PAYMENT_DATE).toString()))
                    .amount(Long.valueOf(cpHM.get(KEY_PAYMENT_AMOUNT).toString()))
                    .build();

            CollectionMember<CasePayment> collectionMember = new CollectionMember(pay.get(KEY_COLLECTION_ID)
                    .toString(), casePayment);
            allPayments.add(collectionMember);

        }

        return allPayments;
    }

    private PaymentStatus getPaymentStatus(String status) {
        return PaymentStatus.getPaymentStatusByName(status);
    }

    private Date getDate(String dateString) {
        try {
            return dateFormatter.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
