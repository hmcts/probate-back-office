package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.PAYMENT_METHOD_VALUE_FEE_ACCOUNT;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.PAYMENT_REFERENCE_CHEQUE;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.PAYMENT_REFERENCE_FEE_PREFIX;

@Slf4j
@Component
public class CCDDataTransformer {

    public CCDData transform(CallbackRequest callbackRequest) {

        return buildCCDData(callbackRequest);
    }

    private CCDData buildCCDData(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        return CCDData.builder()
                .solicitorReference(getSolicitorAppReference(caseData.getSolsSolicitorAppReference()))
                .caseSubmissionDate(getCaseSubmissionDate(callbackRequest.getCaseDetails().getLastModified()))
                .solicitor(buildSolicitorDetails(caseData))
                .deceased(buildDeceasedDetails(caseData))
                .iht(buildInheritanceTaxDetails(caseData))
                .fee(buildFeeDetails(caseData))
                .solsAdditionalInfo(caseData.getSolsAdditionalInfo())
                .executors(getAllExecutors(caseData))
                .build();
    }

    private Solicitor buildSolicitorDetails(CaseData caseData) {
        return Solicitor.builder()
                .firmName(caseData.getSolsSolicitorFirmName())
                .firmPostcode(caseData.getSolsSolicitorFirmPostcode())
                .fullname(caseData.getSolsSOTName())
                .jobRole(caseData.getSolsSOTJobTitle())
                .build();
    }

    private Deceased buildDeceasedDetails(CaseData caseData) {
        return Deceased.builder()
                .firstname(caseData.getDeceasedForenames())
                .lastname(caseData.getDeceasedSurname())
                .dateOfBirth((caseData.getDeceasedDateOfBirth()))
                .dateOfDeath((caseData.getDeceasedDateOfDeath()))
                .address(caseData.getDeceasedAddress())
                .build();
    }

    private InheritanceTax buildInheritanceTaxDetails(CaseData caseData) {
        return InheritanceTax.builder()
                .formName(caseData.getSolsIHTFormId())
                .netValue(caseData.getIhtNetValue())
                .grossValue(caseData.getIhtGrossValue())
                .build();
    }

    private Fee buildFeeDetails(CaseData caseData) {
        return Fee.builder()
                .extraCopiesOfGrant(caseData.getExtraCopiesOfGrant())
                .outsideUKGrantCopies(caseData.getOutsideUKGrantCopies())
                .paymentMethod(caseData.getSolsPaymentMethods())
                .paymentReferenceNumber(getPaymentReferenceNumber(caseData))
                .applicationFee(caseData.getApplicationFee())
                .amount(caseData.getTotalFee())
                .feeForUkCopies(caseData.getFeeForUkCopies())
                .feeForNonUkCopies(caseData.getFeeForNonUkCopies())
                .build();
    }

    private String getPaymentReferenceNumber(CaseData caseData) {
        if (PAYMENT_METHOD_VALUE_FEE_ACCOUNT.equals(caseData.getSolsPaymentMethods())) {
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
                            .applying(YES.equals(executor.getAdditionalApplying()))
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
        try {
            return LocalDate.of(parseInt(lastModified[0]), parseInt(lastModified[1]), parseInt(lastModified[2]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | DateTimeException | NullPointerException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }
}
