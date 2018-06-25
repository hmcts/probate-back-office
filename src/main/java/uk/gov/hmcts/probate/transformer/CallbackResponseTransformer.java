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

    static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    static final String PAYMENT_REFERENCE_FEE_PREFIX = "Fee account PBA-";
    static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String APPLICATION_TYPE_SOLS = "Solicitor";
    private static final String REGISTRY_LOCATION_BIRMINGHAM = "Birmingham";

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest, Optional<String> newState) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
                .state(newState.orElse(null))
                .ccdState(callbackRequest.getCaseDetails().getState())
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse addCcdState(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
                .ccdState(callbackRequest.getCaseDetails().getState())
                .build();

        return transform(responseCaseData);
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
                .ccdState(callbackRequest.getCaseDetails().getState())
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, PDFServiceTemplate pdfServiceTemplate, CCDDocument ccdDocument) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseDataBuilder responseCaseData = this.getResponseCaseData(caseData);
        responseCaseData.solsSOTNeedToUpdate(null);
        responseCaseData.ccdState(callbackRequest.getCaseDetails().getState());
        if (LEGAL_STATEMENT.equals(pdfServiceTemplate)) {
            responseCaseData.solsLegalStatementDocument(ccdDocument);
        }

        return transform(responseCaseData.build());
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
                .primaryApplicantEmailAddress(caseData.getPrimaryApplicantEmailAddress())
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

                .ihtGrossValue(transformToString(caseData.getIhtGrossValue()))
                .ihtNetValue(transformToString(caseData.getIhtNetValue()))
                .deceasedDomicileInEngWales(caseData.getDeceasedDomicileInEngWales())

                .solsPaymentMethods(caseData.getSolsPaymentMethods())
                .solsFeeAccountNumber(caseData.getSolsFeeAccountNumber())
                .solsPaymentReferenceNumber(getPaymentReference(caseData))

                .extraCopiesOfGrant(transformToString(caseData.getExtraCopiesOfGrant()))
                .outsideUKGrantCopies(transformToString(caseData.getOutsideUKGrantCopies()))
                .feeForNonUkCopies(transformMoneyGBPToString(caseData.getFeeForNonUkCopies()))
                .feeForUkCopies(transformMoneyGBPToString(caseData.getFeeForUkCopies()))
                .applicationFee(transformMoneyGBPToString(caseData.getApplicationFee()))
                .totalFee(transformMoneyGBPToString(caseData.getTotalFee()))

                .solsLegalStatementDocument(caseData.getSolsLegalStatementDocument())
                .casePrinted(caseData.getCasePrinted())
                .boEmailDocsReceivedNotification(caseData.getBoEmailDocsReceivedNotificationOrDefault())
                .boEmailGrantIssuedNotification(caseData.getBoEmailGrantIssuedNotificationOrDefault())

                .boCaseStopReasonList(caseData.getBoCaseStopReasonList());
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
