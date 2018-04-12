package uk.gov.hmcts.probate.transformer;

import lombok.Data;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.Solicitor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.PAYEMNT_METHOD_VALUE_FEE_ACCOUNT;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.PAYMENT_REFERENCE_CHEQUE;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.PAYMENT_REFERENCE_FEE_PREFIX;

@Data
@Component
public class CCDDataTransformer {

    public CCDData transform(CallbackRequest callbackRequest, FeeServiceResponse feeServiceResponse) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        Solicitor solicitor = Solicitor.builder()
                .firmName(caseData.getSolsSolicitorFirmName())
                .firmPostcode(caseData.getSolsSolicitorFirmPostcode())
                .fullname(caseData.getSolsSOTName())
                .jobRole(caseData.getSolsSOTJobTitle())
                .build();

        Deceased deceased = Deceased.builder()
                .firstname(caseData.getDeceasedForenames())
                .lastname(caseData.getDeceasedSurname())
                .dateOfBirth((caseData.getDeceasedDateOfBirth()))
                .dateOfDeath((caseData.getDeceasedDateOfDeath()))
                .build();

        InheritanceTax inheritanceTax = InheritanceTax.builder()
                .formName(caseData.getSolsIHTFormId())
                .netValue(caseData.getIhtNetValue())
                .grossValue(caseData.getIhtGrossValue())
                .build();

        Fee fee = Fee.builder()
                .extraCopiesOfGrant(caseData.getExtraCopiesOfGrant())
                .outsideUKGrantCopies(caseData.getOutsideUKGrantCopies())
                .paymentMethod(caseData.getSolsPaymentMethods())
                .amount(feeServiceResponse.getTotal())
                .applicationFee(feeServiceResponse.getApplicationFee())
                .paymentReferenceNumber(getPaymentReferenceNumber(caseData))
                .build();

        return CCDData.builder()
                .solicitorReference(getSolicitorAppReference(caseData.getSolsSolicitorAppReference()))
                .caseSubmissionDate(getCaseSubmissionDate(callbackRequest.getCaseDetails().getLastModified()))
                .solicitor(solicitor)
                .deceased(deceased)
                .iht(inheritanceTax)
                .fee(fee)
                .solsAdditionalInfo(caseData.getSolsAdditionalInfo())
                .executors(getAllExecutors(caseData))
                .build();
    }

    public CCDData transform(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        Solicitor solicitor = Solicitor.builder()
            .firmName(caseData.getSolsSolicitorFirmName())
            .firmPostcode(caseData.getSolsSolicitorFirmPostcode())
            .fullname(caseData.getSolsSOTName())
            .jobRole(caseData.getSolsSOTJobTitle())
            .build();

        Deceased deceased = Deceased.builder()
            .firstname(caseData.getDeceasedForenames())
            .lastname(caseData.getDeceasedSurname())
            .dateOfBirth((caseData.getDeceasedDateOfBirth()))
            .dateOfDeath((caseData.getDeceasedDateOfDeath()))
                .address(caseData.getDeceasedAddress())
            .build();

        InheritanceTax inheritanceTax = InheritanceTax.builder()
            .formName(caseData.getSolsIHTFormId())
            .netValue(caseData.getIhtNetValue())
            .grossValue(caseData.getIhtGrossValue())
            .build();

        Fee fee = Fee.builder()
            .extraCopiesOfGrant(caseData.getExtraCopiesOfGrant())
            .outsideUKGrantCopies(caseData.getOutsideUKGrantCopies())
            .paymentMethod(caseData.getSolsPaymentMethods())
            .paymentReferenceNumber(getPaymentReferenceNumber(caseData))
            .applicationFee(caseData.getApplicationFee())
            .amount(caseData.getTotalFee())
            .build();

        return CCDData.builder()
            .solicitorReference(getSolicitorAppReference(caseData.getSolsSolicitorAppReference()))
            .caseSubmissionDate(getCaseSubmissionDate(callbackRequest.getCaseDetails().getLastModified()))
            .solicitor(solicitor)
            .deceased(deceased)
            .iht(inheritanceTax)
            .fee(fee)
            .solsAdditionalInfo(caseData.getSolsAdditionalInfo())
            .executors(getAllExecutors(caseData))
            .build();
    }

    private String getPaymentReferenceNumber(CaseData caseData) {
        if (PAYEMNT_METHOD_VALUE_FEE_ACCOUNT.equals(caseData.getSolsPaymentMethods())) {
            return PAYMENT_REFERENCE_FEE_PREFIX + caseData.getSolsFeeAccountNumber();
        } else {
            return PAYMENT_REFERENCE_CHEQUE;
        }
    }

    private String getSolicitorAppReference(String solsSolicitorAppReference) {
        return solsSolicitorAppReference == null ? "" : solsSolicitorAppReference;
    }

    private List<Executor> getAllExecutors(CaseData caseData) {
        List<Executor> executors = new ArrayList<>();
        if (caseData.getSolsAdditionalExecutorList() != null) {
            executors = caseData.getSolsAdditionalExecutorList().stream()
                .map(AdditionalExecutors::getAdditionalExecutor)
                .map(executor -> Executor.builder()
                    .applying("Yes".equals(executor.getAdditionalApplying()))
                    .address(executor.getAdditionalExecAddress())
                    .reasonNotApplying(executor.getAdditionalExecReasonNotApplying())
                    .forename(executor.getAdditionalExecForenames())
                    .lastname(executor.getAdditionalExecLastname())
                    .build())
                .collect(Collectors.toList());
        }

        Executor primaryExecutor = Executor.builder()
            .applying(caseData.isPrimaryApplicantApplying())
            .address(caseData.getPrimaryApplicantAddress())
            .reasonNotApplying(caseData.getSolsPrimaryExecutorNotApplyingReason())
            .forename(caseData.getPrimaryApplicantForenames())
            .lastname(caseData.getPrimaryApplicantSurname())
            .build();

        executors.add(primaryExecutor);

        return executors;
    }

    private LocalDate getCaseSubmissionDate(String[] lastModified) {
        if (lastModified == null || lastModified.length == 0 || lastModified[0] == null
            || lastModified[1] == null || lastModified[2] == null) {
            return null;
        }
        int year = Integer.parseInt(lastModified[0]);
        int month = Integer.parseInt(lastModified[1]);
        int day = Integer.parseInt(lastModified[2]);
        return LocalDate.of(year, month, day);
    }
}
