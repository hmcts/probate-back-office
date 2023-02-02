package uk.gov.hmcts.probate.service.payments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.payments.servicerequest.ServiceRequestUpdateResponseDto;
import uk.gov.hmcts.reform.probate.model.PaymentStatus;
import uk.gov.hmcts.reform.probate.model.cases.CasePayment;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static uk.gov.hmcts.reform.probate.model.PaymentStatus.FAILED;
import static uk.gov.hmcts.reform.probate.model.PaymentStatus.INITIATED;
import static uk.gov.hmcts.reform.probate.model.PaymentStatus.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasePaymentBuilder {
    final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    static final String SRP_METHOD_ACCOUNT = "payment by account";
    static final String SRP_METHOD_CARD = "card";
    static final String SRP_STATUS_PAID = "Paid";
    static final String SRP_STATUS_NOT_PAID = "Not paid";
    static final String SRP_STATUS_PARTIALLY_PAID = "Partially paid";
    static final String CASE_PAYMENT_METHOD_PBA = "pba";
    static final String CASE_PAYMENT_METHOD_CARD = "card";

    @Value("${payment.pba.siteId}")
    private String siteId;

    public List<CollectionMember<CasePayment>> addPaymentFromServiceRequestResponse(
            List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<Payment>> payments,
            ServiceRequestUpdateResponseDto response) {
        List<CollectionMember<CasePayment>> allPayments = buildCurrentPayments(payments);
        CasePayment casePayment = buildCasePayment(
                getPaymentStatusByServiceRequestStatus(response.getServiceRequestStatus()).getName(),
                response.getServiceRequestReference(),
                siteId,
                response.getServiceRequestReference(),
                getCasePaymentMethod(response),
                "" + (response.getServiceRequesAmount().longValue() * 100));
        allPayments.add(new CollectionMember(null, casePayment));

        return allPayments;
    }

    public LocalDate parseDate(String date) {
        try {
            return dateFormatter.parse(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<CollectionMember<CasePayment>> buildCurrentPayments(
            List<uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<Payment>> currentPayments) {
        if (currentPayments == null) {
            return new ArrayList<>();
        }
        List<CollectionMember<CasePayment>> allPayments = new ArrayList<>();

        for (uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<Payment> currentPayment : currentPayments) {
            Payment paymentValue = currentPayment.getValue();
            CasePayment casePayment = buildCasePayment(paymentValue.getStatus(), paymentValue.getTransactionId(),
                    paymentValue.getSiteId(), paymentValue.getReference(), paymentValue.getMethod(),
                    paymentValue.getDate(), paymentValue.getAmount());

            CollectionMember<CasePayment> collectionMember = new CollectionMember<>(currentPayment.getId(),
                    casePayment);
            allPayments.add(collectionMember);
        }

        return allPayments;
    }

    private CasePayment buildCasePayment(String paymentStatus, String transId, String siteId, String ref,
                                         String method, String amount) {

        String dateStr = dateFormatter.format(new Date());
        return buildCasePayment(paymentStatus, transId, siteId, ref, method, dateStr, amount);
    }

    private CasePayment buildCasePayment(String paymentStatus, String transId, String siteId, String ref, String method,
                                         String date, String amount) {
        return CasePayment.builder()
                .status(getPaymentStatus(paymentStatus))
                .transactionId(transId)
                .siteId(siteId)
                .reference(ref)
                .method(method)
                .date(getDate(date))
                .amount(Long.valueOf(amount))
                .build();
    }

    private PaymentStatus getPaymentStatus(String status) {
        return PaymentStatus.getPaymentStatusByName(status);
    }

    private Date getDate(String dateString) {
        try {
            log.info("dateFormatter:" + dateFormatter.toString());
            log.info("dateString:" + dateString);
            return dateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.getMessage();
            return null;
        }
    }

    private String getCasePaymentMethod(ServiceRequestUpdateResponseDto response) {
        //"cheque", online", "card", "pba"
        if (SRP_METHOD_ACCOUNT.equals(response.getServiceRequestPaymentResponseDto().getPaymentMethod())) {
            return CASE_PAYMENT_METHOD_PBA;
        } else if (SRP_METHOD_CARD.equals(response.getServiceRequestPaymentResponseDto().getPaymentMethod())) {
            return CASE_PAYMENT_METHOD_CARD;
        }

        throw new IllegalArgumentException("Invalid Service request payment method:"
                + response.getServiceRequestPaymentResponseDto().getPaymentMethod()
                + " for case:" + response.getCcdCaseNumber());
    }

    protected PaymentStatus getPaymentStatusByServiceRequestStatus(String serviceRequestStatus) {
        if (SRP_STATUS_PAID.equals(serviceRequestStatus)) {
            return SUCCESS;
        } else if (SRP_STATUS_NOT_PAID.equals(serviceRequestStatus)) {
            return FAILED;
        } else if (SRP_STATUS_PARTIALLY_PAID.equals(serviceRequestStatus)) {
            return INITIATED;
        }

        throw new IllegalArgumentException("Invalid serviceRequestStatus:" + serviceRequestStatus);
    }


}
