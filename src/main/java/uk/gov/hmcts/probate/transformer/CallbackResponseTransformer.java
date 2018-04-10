package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class CallbackResponseTransformer {

    public static final String PAYEMNT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    public static final String PAYMENT_REFERENCE_FEE_PREFIX = "Fee account PBA-";
    public static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest, Optional<String> newState) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
            .state(newState.orElse(null))
            .build();

        return this.transform(responseCaseData);
    }

    //TODO: Waiting for CCD to complete the task to handle markdown
    public CallbackResponse transform(CallbackRequest callbackRequest, TemplateResponse templateResponse) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
            .build();

        return this.transform(responseCaseData);
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, FeeServiceResponse feeServiceResponse) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        String feeForNonUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForNonUkCopies());
        String feeForUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForUkCopies());
        String applicationFee = transformMoneyGBPToString(feeServiceResponse.getApplicationFee());
        String totalFee = transformMoneyGBPToString(feeServiceResponse.getTotal());

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
            .feeForNonUkCopies(feeForNonUkCopies)
            .feeForUkCopies(feeForUkCopies)
            .applicationFee(applicationFee)
            .totalFee(totalFee)
            .build();

        return this.transform(responseCaseData);
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, PDFServiceTemplate pdfServiceTemplate, CCDDocument ccdDocument) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseDataBuilder responseCaseData = this.getResponseCaseData(caseData);
        switch (pdfServiceTemplate) {
            case LEGAL_STATEMENT:
                responseCaseData.solsLegalStatementDocument(ccdDocument);
                break;
            case NEXT_STEPS:
                responseCaseData.solsNextStepsDocument(ccdDocument);
                break;
            default:
                break;
        }

        return this.transform(responseCaseData.build());
    }

    private CallbackResponse transform(ResponseCaseData responseCaseData) {
        return CallbackResponse.builder().data(responseCaseData).build();
    }

    private ResponseCaseDataBuilder getResponseCaseData(CaseData caseData) {

        return ResponseCaseData.builder()
            .solsSolicitorFirmName(caseData.getSolsSolicitorFirmName())
            .solsSolicitorFirmPostcode(caseData.getSolsSolicitorFirmPostcode())
            .solsSOTName(caseData.getSolsSOTName())
            .solsSOTJobTitle(caseData.getSolsSOTJobTitle())
            .deceasedForenames(caseData.getDeceasedForenames())
            .deceasedSurname(caseData.getDeceasedSurname())
            .deceasedDateOfBirth(dateTimeFormatter.format(caseData.getDeceasedDateOfBirth()))
            .deceasedDateOfDeath(dateTimeFormatter.format(caseData.getDeceasedDateOfDeath()))
            .willExists(caseData.getWillExists())
            .willAccessOriginal((caseData.getWillAccessOriginal()))
            .willHasCodicils(caseData.getWillHasCodicils())
            .willNumberOfCodicils(caseData.getWillNumberOfCodicils())
            .solsIHTFormId(caseData.getSolsIHTFormId())
            .primaryApplicantForenames(caseData.getPrimaryApplicantForenames())
            .primaryApplicantSurname(caseData.getPrimaryApplicantSurname())
            .primaryApplicantIsApplying(caseData.getPrimaryApplicantIsApplying())
            .solsPrimaryExecutorNotApplyingReason(caseData.getSolsPrimaryExecutorNotApplyingReason())
            .primaryApplicantHasAlias(caseData.getPrimaryApplicantHasAlias())
            .otherExecutorExists(caseData.getOtherExecutorExists())
            .solsExecutorAliasNames(caseData.getSolsExecutorAliasNames())
            .solsAdditionalExecutorList(caseData.getSolsAdditionalExecutorList())
            .deceasedAddress(caseData.getDeceasedAddress())
            .deceasedAnyOtherNames(caseData.getDeceasedAnyOtherNames())
            .primaryApplicantAddress(caseData.getPrimaryApplicantAddress())
            .solsDeceasedAliasNamesList(caseData.getSolsDeceasedAliasNamesList())
            .solsSolicitorAppReference(caseData.getSolsSolicitorAppReference())
            .solsAdditionalInfo(caseData.getSolsAdditionalInfo())

            .solsSOTNeedToUpdate(caseData.getSolsSOTNeedToUpdate())

            .ihtGrossValue(this.transformToString(caseData.getIhtGrossValue()))
            .ihtNetValue(this.transformToString(caseData.getIhtNetValue()))
            .deceasedDomicileInEngWales(caseData.getDeceasedDomicileInEngWales())

            .solsPaymentMethods(caseData.getSolsPaymentMethods())
            .solsFeeAccountNumber(caseData.getSolsFeeAccountNumber())
            .solsPaymentReferenceNumber(this.getPaymentReference(caseData))

            .extraCopiesOfGrant(this.transformToString(caseData.getExtraCopiesOfGrant()))
            .outsideUKGrantCopies(this.transformToString(caseData.getOutsideUKGrantCopies()))
            .feeForNonUkCopies(transformMoneyGBPToString(caseData.getFeeForNonUkCopies()))
            .feeForUkCopies(this.transformMoneyGBPToString(caseData.getFeeForUkCopies()))
            .applicationFee(this.transformMoneyGBPToString(caseData.getApplicationFee()))
            .totalFee(this.transformMoneyGBPToString(caseData.getTotalFee()))

            .solsLegalStatementDocument(caseData.getSolsLegalStatementDocument());
    }

    private String getPaymentReference(CaseData caseData) {
        if (PAYEMNT_METHOD_VALUE_FEE_ACCOUNT.equals(caseData.getSolsPaymentMethods())) {
            return PAYMENT_REFERENCE_FEE_PREFIX + caseData.getSolsFeeAccountNumber();
        } else {
            return PAYMENT_REFERENCE_CHEQUE;
        }
    }

    private String transformMoneyGBPToString(BigDecimal bdValue) {
        return Optional.ofNullable(bdValue)
            .map(value -> bdValue.multiply(new BigDecimal(100)))
            .map(BigDecimal::intValue)
            .map(String::valueOf)
            .orElse(null);
    }

    private String transformToString(Long longValue) {
        return Optional.ofNullable(longValue)
            .map(String::valueOf)
            .orElse(null);
    }

    private String transformToString(Float value) {
        return Optional.ofNullable(value)
            .map(Float::intValue)
            .map(String::valueOf)
            .orElse(null);
    }
}
