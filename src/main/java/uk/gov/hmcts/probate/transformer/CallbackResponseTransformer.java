package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@Component
public class CallbackResponseTransformer {

    static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    static final String PAYMENT_REFERENCE_FEE_PREFIX = "Fee account PBA-";
    static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = SOLICITOR;
    private static final String DEFAULT_REGISTRY_LOCATION = "Birmingham";

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest, Optional<String> newState) {
        ResponseCaseData responseCaseData = this.getResponseCaseData(callbackRequest.getCaseDetails())
                .state(newState.orElse(null))
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse addCcdState(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = this.getResponseCaseData(callbackRequest.getCaseDetails())
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse addDocumentReceivedNotification(CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseDetails)
                .boEmailDocsReceivedNotificationRequested(caseDetails.getData().getBoEmailDocsReceivedNotification())
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse addGrandIssuedNotification(CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseDetails)
                .boEmailGrantIssuedNotificationRequested(caseDetails.getData().getBoEmailGrantIssuedNotification())
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, FeeServiceResponse feeServiceResponse) {
        String feeForNonUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForNonUkCopies());
        String feeForUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForUkCopies());
        String applicationFee = transformMoneyGBPToString(feeServiceResponse.getApplicationFee());
        String totalFee = transformMoneyGBPToString(feeServiceResponse.getTotal());

        ResponseCaseData responseCaseData = this.getResponseCaseData(callbackRequest.getCaseDetails())
                .feeForNonUkCopies(feeForNonUkCopies)
                .feeForUkCopies(feeForUkCopies)
                .applicationFee(applicationFee)
                .totalFee(totalFee)
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, Document document) {
        if (DIGITAL_GRANT_DRAFT.equals(document.getDocumentType()) || DIGITAL_GRANT.equals(document.getDocumentType())) {
            callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                    .add(new CollectionMember<>(null, document));
        }

        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails());
        responseCaseDataBuilder.solsSOTNeedToUpdate(null);

        if (LEGAL_STATEMENT.equals(document.getDocumentType())) {
            responseCaseDataBuilder.solsLegalStatementDocument(document.getDocumentLink());
        }

        return transform(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails())
                .build();

        return transform(responseCaseData);
    }

    private CallbackResponse transform(ResponseCaseData responseCaseData) {
        return CallbackResponse.builder().data(responseCaseData).build();
    }

    private ResponseCaseDataBuilder getResponseCaseData(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();

        return ResponseCaseData.builder()
                .applicationType(Optional.ofNullable(caseData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
                .registryLocation(Optional.ofNullable(caseData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))
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

                .ihtGrossValue(caseData.getIhtGrossValue())
                .ihtNetValue(caseData.getIhtNetValue())
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
                .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotificationRequested())
                .boEmailGrantIssuedNotificationRequested(caseData.getBoEmailGrantIssuedNotificationRequested())
                .boEmailDocsReceivedNotification(caseData.getBoEmailDocsReceivedNotification())
                .boEmailGrantIssuedNotification(caseData.getBoEmailGrantIssuedNotification())

                .boCaseStopReasonList(caseData.getBoCaseStopReasonList())

                .boDeceasedTitle(caseData.getBoDeceasedTitle())
                .boDeceasedHonours(caseData.getBoDeceasedHonours())

                .ccdState(caseDetails.getState())
                .ihtReferenceNumber(caseData.getIhtReferenceNumber())
                .ihtFormCompletedOnline(caseData.getIhtFormCompletedOnline())

                .boWillMessage(caseData.getBoWillMessage())
                .boExecutorLimitation(caseData.getBoExecutorLimitation())
                .boAdminClauseLimitation(caseData.getBoAdminClauseLimitation())
                .boLimitationText(caseData.getBoLimitationText())
                .probateDocumentsGenerated(caseData.getProbateDocumentsGenerated());
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
    
}
