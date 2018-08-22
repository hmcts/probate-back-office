package uk.gov.hmcts.probate.transformer;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Alias;
import uk.gov.hmcts.probate.model.ApplicationType;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT;

@Component
@RequiredArgsConstructor
public class CallbackResponseTransformer {

    static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    static final String PAYMENT_REFERENCE_FEE_PREFIX = "Fee account PBA-";
    static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to ‘HM Courts & Tribunals Service’)";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = SOLICITOR;
    private static final String DEFAULT_REGISTRY_LOCATION = "Birmingham";

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest, Optional<String> newState) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .state(newState.orElse(null))
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse addDocumentReceivedNotification(CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        ResponseCaseData responseCaseData = getResponseCaseData(caseDetails, false)
                .boEmailDocsReceivedNotificationRequested(caseDetails.getData().getBoEmailDocsReceivedNotification())
                .build();

        return transformResponse(responseCaseData);
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

        return transformResponse(responseCaseDataBuilder.build());
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

        return transformResponse(responseCaseData);
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

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCase(CallbackRequest callbackRequest) {

        boolean transform = callbackRequest.getCaseDetails().getData().getApplicationType() == ApplicationType.SOLICITOR ? true : false;

        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), transform)
                .build();

        return transformResponse(responseCaseData);
    }

    private CallbackResponse transformResponse(ResponseCaseData responseCaseData) {
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
                .solsDeceasedAliasNamesList(caseData.getSolsDeceasedAliasNamesList())

                .boCaseStopReasonList(caseData.getBoCaseStopReasonList())

                .boDeceasedTitle(caseData.getBoDeceasedTitle())
                .boDeceasedHonours(caseData.getBoDeceasedHonours())

                .ihtReferenceNumber(caseData.getIhtReferenceNumber())
                .ihtFormCompletedOnline(caseData.getIhtFormCompletedOnline())

                .boWillMessage(caseData.getBoWillMessage())
                .boExecutorLimitation(caseData.getBoExecutorLimitation())
                .boAdminClauseLimitation(caseData.getBoAdminClauseLimitation())
                .boLimitationText(caseData.getBoLimitationText())
                .probateDocumentsGenerated(caseData.getProbateDocumentsGenerated())
                .boDocumentsUploaded(caseData.getBoDocumentsUploaded());

        if (transform) {
            if (!Strings.isNullOrEmpty(caseData.getSolsExecutorAliasNames())) {
                Alias executorAlias = new Alias(caseData.getSolsExecutorAliasNames());
                builder
                        .solsExecutorAliasFirstNames(executorAlias.getFirstName())
                        .solsExecutorAliasSurnames(executorAlias.getLastName());
            }

            if (caseData.getSolsDeceasedAliasNamesList() != null) {
                List<CollectionMember<ProbateAliasName>> deceasedAliases = caseData.getSolsDeceasedAliasNamesList()
                        .stream()
                        .map(CollectionMember::getValue)
                        .map(AliasName::getSolsAliasname)
                        .map(Alias::new)
                        .map(alias -> new ProbateAliasName(alias.getFirstName(), alias.getLastName(), "YES"))
                        .map(probateAliasName -> new CollectionMember<>(null, probateAliasName))
                        .collect(Collectors.toList());

                builder.boDeceasedAliasNamesList(deceasedAliases);
            }


            if (caseData.getSolsAdditionalExecutorList() != null) {

                List<CollectionMember<AdditionalExecutorApplying>> applyingExec = caseData.getSolsAdditionalExecutorList()
                        .stream()
                        .map(CollectionMember::getValue)
                        .filter(additionalExecutor -> "YES".equalsIgnoreCase(additionalExecutor.getAdditionalApplying()))
                        .map(additionalExecutor -> AdditionalExecutorApplying.builder()
                                .applyingExecutorFirstName(additionalExecutor.getAdditionalExecForenames())
                                .applyingExecutorSurname(additionalExecutor.getAdditionalExecLastname())
                                .applyingExecutorPhoneNumber(null)
                                .applyingExecutorEmail(null)
                                .applyingExecutorAddress(additionalExecutor.getAdditionalExecAddress())
                                .aliasName(ProbateAliasName
                                        .createFromAlias(new Alias(
                                                Optional.ofNullable(additionalExecutor.getAdditionalExecAliasNameOnWill())
                                                        .orElse(""))))
                                .build())
                        .map(executor -> new CollectionMember<>(null, executor))
                        .collect(Collectors.toList());


                List<CollectionMember<AdditionalExecutorNotApplying>> notApplyingExec = caseData.getSolsAdditionalExecutorList()
                        .stream()
                        .map(CollectionMember::getValue)
                        .filter(additionalExecutor -> "NO".equalsIgnoreCase(additionalExecutor.getAdditionalApplying()))
                        .map(additionalExecutor -> AdditionalExecutorNotApplying.builder()
                                .notApplyingExecutorFirstName(additionalExecutor.getAdditionalExecForenames())
                                .notApplyingExecutorSurname(additionalExecutor.getAdditionalExecLastname())
                                .notApplyingExecutorReason(additionalExecutor.getAdditionalExecReasonNotApplying())
                                .notApplyingExecAddress(additionalExecutor.getAdditionalExecAddress())
                                .aliasName(ProbateAliasName
                                        .createFromAlias(new Alias(
                                                Optional.ofNullable(additionalExecutor.getAdditionalExecAliasNameOnWill())
                                                        .orElse(""))))
                                .build())
                        .map(executor -> new CollectionMember<>(null, executor))
                        .collect(Collectors.toList());

                builder
                        .additionalExecutorsApplying(applyingExec)
                        .additionalExecutorsNotApplying(notApplyingExec);

            }

        } else {

            builder
                    .solsExecutorAliasFirstNames(caseData.getSolsExecutorAliasFirstNames())
                    .solsExecutorAliasSurnames(caseData.getSolsExecutorAliasSurnames())
                    .boDeceasedAliasNamesList(caseData.getBoDeceasedAliasNamesList())
                    .additionalExecutorsApplying(caseData.getAdditionalExecutorsApplying())
                    .additionalExecutorsNotApplying(caseData.getAdditionalExecutorsNotApplying());
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

}
