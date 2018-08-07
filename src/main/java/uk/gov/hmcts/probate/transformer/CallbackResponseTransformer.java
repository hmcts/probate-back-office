package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;
import static uk.gov.hmcts.probate.transformer.NameParser.FIRST_NAMES;
import static uk.gov.hmcts.probate.transformer.NameParser.SURNAME;

@Component
@RequiredArgsConstructor
public class CallbackResponseTransformer {

    static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    static final String PAYMENT_REFERENCE_FEE_PREFIX = "Fee account PBA-";
    static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = SOLICITOR;
    private static final String DEFAULT_REGISTRY_LOCATION = "Birmingham";

    @Autowired
    private NameParser nameParser = new NameParser();

    private final AdditionalExecutorsListFilter additionalExecutorsListFilter;

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest, Optional<String> newState) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .state(newState.orElse(null))
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse addCcdState(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse addDocumentReceivedNotification(CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        ResponseCaseData responseCaseData = getResponseCaseData(caseDetails, false)
                .boEmailDocsReceivedNotificationRequested(caseDetails.getData().getBoEmailDocsReceivedNotification())
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse grantIssued(CallbackRequest callbackRequest, Document document) {
        if (DIGITAL_GRANT_DRAFT.equals(document.getDocumentType()) || DIGITAL_GRANT.equals(document.getDocumentType())) {
            callbackRequest.getCaseDetails().getData().getProbateDocumentsGenerated()
                    .add(new CollectionMember<>(null, document));
        }

        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        responseCaseDataBuilder.boEmailGrantIssuedNotificationRequested(
                callbackRequest.getCaseDetails().getData().getBoEmailGrantIssuedNotification());
        responseCaseDataBuilder.solsSOTNeedToUpdate(null);

        return transform(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, FeeServiceResponse feeServiceResponse) {
        String feeForNonUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForNonUkCopies());
        String feeForUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForUkCopies());
        String applicationFee = transformMoneyGBPToString(feeServiceResponse.getApplicationFee());
        String totalFee = transformMoneyGBPToString(feeServiceResponse.getTotal());

        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
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

        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        responseCaseDataBuilder.solsSOTNeedToUpdate(null);

        if (LEGAL_STATEMENT.equals(document.getDocumentType())) {
            responseCaseDataBuilder.solsLegalStatementDocument(document.getDocumentLink());
        }

        return transform(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .build();

        return transform(responseCaseData);
    }

    public CallbackResponse transformCase(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), true)
                .build();

        return transform(responseCaseData);
    }

    private CallbackResponse transform(ResponseCaseData responseCaseData) {
        return CallbackResponse.builder().data(responseCaseData).build();
    }

    private ResponseCaseDataBuilder getResponseCaseData(CaseDetails caseDetails, boolean transform) {
        CaseData caseData = caseDetails.getData();

        ResponseCaseDataBuilder builder = ResponseCaseData.builder()
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

        if (transform) {
            builder
                    .solsExecutorAliasFirstNames(transformPrimaryApplicantFirstName(caseData.getSolsExecutorAliasNames()))
                    .solsExecutorAliasSurnames(transformPrimaryApplicantSurname(caseData.getSolsExecutorAliasNames()))

                    .additionalExecutorsApplying(transformApplyingExecLists(caseData))
                    .additionalExecutorsNotApplying(transformNotApplyingExecLists(caseData))

                    .solsDeceasedAliasNamesList(caseData.getSolsDeceasedAliasNamesList())
                    .boDeceasedAliasNamesList(transformDeceasedAliasNameLists(caseData.getSolsDeceasedAliasNamesList()));
        } else {
            builder
                    .solsExecutorAliasFirstNames(caseData.getSolsExecutorAliasFirstNames())
                    .solsExecutorAliasSurnames(caseData.getSolsExecutorAliasSurnames())

                    .additionalExecutorsApplying(caseData.getAdditionalExecutorsApplying())
                    .additionalExecutorsNotApplying(caseData.getAdditionalExecutorsNotApplying())

                    .solsDeceasedAliasNamesList(caseData.getSolsDeceasedAliasNamesList())
                    .boDeceasedAliasNamesList(caseData.getBoDeceasedAliasNamesList());

        }

        return builder;
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

    private List<CollectionMember<AdditionalExecutorApplying>> transformApplyingExecLists(CaseData caseData) {
        List<CollectionMember<AdditionalExecutor>> applyingList = additionalExecutorsListFilter.filter(
                caseData.getSolsAdditionalExecutorList(),
                caseData, "Yes");

        return applyingList.stream()
                .map(this::mapToAdditionalExecutorsApplying)
                .collect(Collectors.toList());
    }

    private CollectionMember<AdditionalExecutorApplying>
        mapToAdditionalExecutorsApplying(CollectionMember<AdditionalExecutor> applyingList) {
        Map<String, String> namesMap = nameParser.parse(applyingList.getValue().getAdditionalExecAliasNameOnWill());
        AdditionalExecutorApplying additionalExecutorApplying = AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(applyingList.getValue().getAdditionalExecForenames())
                .applyingExecutorSurname(applyingList.getValue().getAdditionalExecLastname())
                .applyingExecutorPhoneNumber(null)
                .applyingExecutorEmail(null)
                .applyingExecutorAddress(applyingList.getValue().getAdditionalExecAddress())
                .aliasName(ProbateAliasName.builder()
                        .lastName(namesMap.get(SURNAME))
                        .forenames(namesMap.get(FIRST_NAMES))
                        .appearOnGrant("Yes")
                        .build())
                .build();

        return new CollectionMember<>(null, additionalExecutorApplying);
    }

    private List<CollectionMember<AdditionalExecutorNotApplying>> transformNotApplyingExecLists(CaseData caseData) {
        List<CollectionMember<AdditionalExecutor>> notApplyingList = additionalExecutorsListFilter.filter(
                caseData.getSolsAdditionalExecutorList(),
                caseData,  "No");


        return notApplyingList.stream()
                .map(this::mapToAdditionalExecutorNotApplying)
                .collect(Collectors.toList());
    }

    private CollectionMember<AdditionalExecutorNotApplying>
        mapToAdditionalExecutorNotApplying(CollectionMember<AdditionalExecutor> notApplyingList) {
        Map<String, String> namesMap = nameParser.parse(notApplyingList.getValue().getAdditionalExecAliasNameOnWill());
        AdditionalExecutorNotApplying additionalExecutorNotApplying = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorFirstName(notApplyingList.getValue().getAdditionalExecForenames())
                .notApplyingExecutorSurname(notApplyingList.getValue().getAdditionalExecLastname())
                .notApplyingExecutorNameOnWill(notApplyingList.getValue().getAdditionalExecAliasNameOnWill())
                .notApplyingExecutorNameDifferenceComment(null)
                .notApplyingExecutorReason(notApplyingList.getValue().getAdditionalExecReasonNotApplying())
                .notApplyingExecutorNotified(null)
                .notApplyingExecAddress(notApplyingList.getValue().getAdditionalExecAddress())
                .aliasName(ProbateAliasName.builder()
                        .lastName(namesMap.get(SURNAME))
                        .forenames(namesMap.get(FIRST_NAMES))
                        .appearOnGrant("Yes")
                        .build())
                .build();

        return new CollectionMember<>(null, additionalExecutorNotApplying);
    }

    private List<CollectionMember<ProbateAliasName>> transformDeceasedAliasNameLists(List<CollectionMember<AliasName>> aliasNamesList) {
        if (aliasNamesList == null) {
            return Collections.emptyList();
        }
        return aliasNamesList.stream()
                .map(this::parseDeceasedAliasList)
                .collect(Collectors.toList());
    }

    private CollectionMember<ProbateAliasName> parseDeceasedAliasList(CollectionMember<AliasName> aliasNames) {
        Map<String, String> namesMap = nameParser.parse(aliasNames.getValue().getSolsAliasname());
        ProbateAliasName aliasName = ProbateAliasName.builder()
                        .lastName(namesMap.get(SURNAME))
                        .forenames(namesMap.get(FIRST_NAMES))
                        .appearOnGrant("Yes")
                        .build();

        return new CollectionMember<>(null, aliasName);
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
