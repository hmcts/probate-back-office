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

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.template.PDFServiceTemplate.LEGAL_STATEMENT;

@Component
public class CallbackResponseTransformer {

    public static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    public static final String PAYMENT_REFERENCE_FEE_PREFIX = "Fee account PBA-";
    public static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String APPLICATION_TYPE_SOLS = "Solicitor";
    private static final String REGISTRY_LOCATION_BIRMINGHAM = "Birmingham";

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest, Optional<String> newState) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
                .state(newState.orElse(null))
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
        responseCaseData.solsSOTNeedToUpdate(null);
        if (LEGAL_STATEMENT.equals(pdfServiceTemplate)) {
            responseCaseData.solsLegalStatementDocument(ccdDocument);
        }

        return this.transform(responseCaseData.build());
    }

    private CallbackResponse transform(ResponseCaseData responseCaseData) {
        return CallbackResponse.builder().data(responseCaseData).build();
    }

    private ResponseCaseDataBuilder getResponseCaseData(CaseData caseData) {

        return ResponseCaseData.builder()
                .applicationType(APPLICATION_TYPE_SOLS)
                .registryLocation(REGISTRY_LOCATION_BIRMINGHAM)
                .solsSolicitorFirmName(caseData.getSolsSolicitorFirmName())
                .solsSolicitorFirmPostcode(caseData.getSolsSolicitorFirmPostcode())
                .solsSolicitorEmail(caseData.getSolsSolicitorEmail())
                .solsSolicitorPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
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
        if (PAYMENT_METHOD_VALUE_FEE_ACCOUNT.equals(caseData.getSolsPaymentMethods())) {
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
