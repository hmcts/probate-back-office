package uk.gov.hmcts.probate.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AliasNames;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.template.PDFServiceTemplate.LEGAL_STATEMENT;
import static uk.gov.hmcts.probate.transformer.NameParser.FIRST_NAMES;
import static uk.gov.hmcts.probate.transformer.NameParser.SURNAME;

@Component
public class CallbackResponseTransformer {

    static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    static final String PAYMENT_REFERENCE_FEE_PREFIX = "Fee account PBA-";
    static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = SOLICITOR;
    private static final String DEFAULT_REGISTRY_LOCATION = "Birmingham";

    @Autowired
    private NameParser nameParser = new NameParser();

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

    public CallbackResponse addDocumentReceivedNotification(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
                .ccdState(callbackRequest.getCaseDetails().getState())
                .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotification())
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse addGrandIssuedNotification(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        ResponseCaseData responseCaseData = this.getResponseCaseData(caseData)
                .ccdState(callbackRequest.getCaseDetails().getState())
                .boEmailGrantIssuedNotificationRequested(caseData.getBoEmailGrantIssuedNotification())
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
                .solsExecutorAliasFirstName(transformPrimaryApplicantFirstName(caseData.getSolsExecutorAliasNames()))
                .solsExecutorAliasSurname(transformPrimaryApplicantSurname(caseData.getSolsExecutorAliasNames()))
                .solsAdditionalExecutorList(transformAdditionalExecutorsAliasNameLists(caseData.getSolsAdditionalExecutorList()))
                .deceasedAddress(caseData.getDeceasedAddress())
                .deceasedAnyOtherNames(caseData.getDeceasedAnyOtherNames())
                .primaryApplicantAddress(caseData.getPrimaryApplicantAddress())
                .solsDeceasedAliasNamesList(transformDeceasedAliasNameLists(caseData.getSolsDeceasedAliasNamesList()))
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
                .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotificationRequested())
                .boEmailGrantIssuedNotificationRequested(caseData.getBoEmailGrantIssuedNotificationRequested())
                .boEmailDocsReceivedNotification(caseData.getBoEmailDocsReceivedNotification())
                .boEmailGrantIssuedNotification(caseData.getBoEmailGrantIssuedNotification())

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

    private List<AliasNames> transformDeceasedAliasNameLists(List<AliasNames> aliasNamesList) {
        if (aliasNamesList == null) {
            return Collections.emptyList();
        }
        return aliasNamesList.stream()
                .map(this::parseDeceasedAliasList)
                .collect(Collectors.toList());
    }

    private AliasNames parseDeceasedAliasList(AliasNames aliasNames) {
        Map<String, String> namesMap = nameParser.parse(aliasNames.getAliasName().getSolsAliasname());
        return AliasNames.builder()
                .aliasName(AliasName.builder()
                        .solsAliasFirstName(namesMap.get(FIRST_NAMES))
                        .solsAliasSurname(namesMap.get(SURNAME))
                        .build())
                .build();
    }

    private List<AdditionalExecutors> transformAdditionalExecutorsAliasNameLists(List<AdditionalExecutors> aliasNamesList) {
        if (aliasNamesList == null) {
            return Collections.emptyList();
        }
        return aliasNamesList.stream()
                .map(this::parseAdditionalExecutorsList)
                .collect(Collectors.toList());
    }

    private AdditionalExecutors parseAdditionalExecutorsList(AdditionalExecutors additionalExecutors) {
        Map<String, String> namesMap = nameParser.parse(additionalExecutors.getAdditionalExecutor().getAdditionalExecAliasNameOnWill());
        return AdditionalExecutors.builder()
                .additionalExecutor(AdditionalExecutor.builder()
                        .additionalExecAliasFirstNameOnWill(namesMap.get(FIRST_NAMES))
                        .additionalExecAliasSurNameOnWill(namesMap.get(SURNAME))
                        .build())
                .build();
    }

    private String transformPrimaryApplicantFirstName(String primaryApplicantAlias) {
        Map<String, String> namesMap = nameParser.parse(primaryApplicantAlias);
        if (namesMap.size() == 0) {
            return "";
        }
        return namesMap.get(FIRST_NAMES);
    }

    private String transformPrimaryApplicantSurname(String primaryApplicantAlias) {
        Map<String, String> namesMap = nameParser.parse(primaryApplicantAlias);
        if (namesMap.size() == 0) {
            return "";
        }
        return namesMap.get(SURNAME);
    }

}
