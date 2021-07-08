package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import uk.gov.hmcts.probate.model.payments.CreditAccountPayment;
import uk.gov.hmcts.probate.model.payments.PaymentFee;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;

public abstract class BasePBAPaymentTest {
    static final String AUTH_TOKEN = "Bearer someAuthorizationToken";


    DslPart buildPBAPaymentResponseDsl(String status, String paymentStatus, String errorCode,
                                       String errorMessage) {
        return getDslPart(status, paymentStatus, errorCode, errorMessage);
    }

    DslPart getDslPart(String status, String paymentStatus, String errorCode, String errorMessage) {
        return newJsonBody((o) -> {
            o.stringType("reference", "reference")
                .stringType("status", status)
                .minArrayLike("status_histories", 1, 1,
                    (sh) -> {
                        sh.stringMatcher("date_updated",
                            "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}\\+\\d{4})$",
                            "2020-10-06T18:54:48.785+0000")
                            .stringMatcher("date_created",
                                "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}\\+\\d{4})$",
                                "2020-10-06T18:54:48.785+0000")
                            .stringValue("status", paymentStatus);
                        if (errorCode != null) {
                            sh.stringValue("error_code", errorCode);
                            sh.stringType("error_message",
                                errorMessage);
                        }
                    });
        }).build();
    }

    CreditAccountPayment getPaymentRequest(BigDecimal amount) {
        PaymentFee feeRequest = PaymentFee.builder()
            .code("test")
            .version("v1")
            .calculatedAmount(amount)
            .volume(BigDecimal.ONE)
            .build();
        CreditAccountPayment expectedRequest = CreditAccountPayment.builder()
            .service("PROBATE")
            .currency("GBP")
            .amount(amount)
            .ccdCaseNumber("1500111222333")
            .siteId("ABA6")
            .accountNumber("PBA0082126")
            .organisationName("Solicitor Firm Name")
            .customerReference("SolicitorCutomerRef")
            .description("Probate Solicitor payment")
            .fees(Collections.singletonList(feeRequest))
            .build();


        return expectedRequest;
    }

    protected Map<String, String> getHeadersMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    protected Map<String, Object> getPaymentMap(String balance) {
        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("accountNumber", "PBA0082126");
        paymentMap.put("availableBalance", balance);
        paymentMap.put("accountName", "test.account.name");
        return paymentMap;
    }

}
