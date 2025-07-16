package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.caseaccess.Organisation;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.OriginalDocuments;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.fee.FeesResponse;
import uk.gov.hmcts.probate.model.payments.pba.OrganisationEntityResponse;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.service.ExecutorsApplyingNotificationService;
import uk.gov.hmcts.probate.service.ccd.AuditEventService;
import uk.gov.hmcts.probate.service.organisations.OrganisationsRetrievalService;
import uk.gov.hmcts.probate.service.solicitorexecutor.FormattingService;
import uk.gov.hmcts.probate.service.tasklist.TaskListUpdateService;
import uk.gov.hmcts.probate.transformer.assembly.AssembleLetterTransformer;
import uk.gov.hmcts.probate.transformer.reset.ResetResponseCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;
import uk.gov.hmcts.reform.probate.model.cases.CitizenResponse;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.idam.UserInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationState.BO_CASE_STOPPED;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.DATE_OF_DEATH_TYPE_DEFAULT;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.LATEST_SCHEMA_VERSION;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.CHANNEL_CHOICE_DIGITAL;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.ASSEMBLED_LETTER;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_STOPPED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.EDGE_CASE;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.reform.probate.model.cases.ApplicationType.SOLICITORS;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.INTESTACY;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackResponseTransformer {

    public static final String ANSWER_YES = "Yes";
    public static final String ANSWER_NO = "No";
    public static final String QA_CASE_STATE = "BOCaseQA";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String OTHER = "other";
    public static final String EXCEPTION_RECORD_CASE_TYPE_ID = "GrantOfRepresentation";
    public static final String EXCEPTION_RECORD_EVENT_ID = "createCaseFromBulkScan";
    public static final RegistryLocation EXCEPTION_RECORD_REGISTRY_LOCATION = RegistryLocation.CTSC;
    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final String CASE_TYPE_DEFAULT = GRANT_OF_PROBATE_NAME;
    private static final DocumentType[] LEGAL_STATEMENTS = {LEGAL_STATEMENT_PROBATE, LEGAL_STATEMENT_INTESTACY,
        LEGAL_STATEMENT_ADMON, LEGAL_STATEMENT_PROBATE_TRUST_CORPS};
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = SOLICITOR;
    private static final String DEFAULT_REGISTRY_LOCATION = CTSC;
    private static final String CASE_MATCHING_ISSUE_GRANT = "BOCaseMatchingIssueGrant";
    private static final String CASE_PRINTED = "CasePrinted";
    private static final String READY_FOR_ISSUE = "BOReadyToIssue";
    private static final String DEFAULT_DATE_OF_DEATHTYPE = "diedOn";
    private static final String POLICY_ROLE_APPLICANT_SOLICITOR = "[APPLICANTSOLICITOR]";
    private static final String IHT400 = "IHT400";
    private static final List<String> EXCLUDED_EVENT_LIST = Arrays.asList("boHistoryCorrection",
            "boCorrection");
    private static final List<String> ROLLBACK_STATE_LIST = List.of("Pending", "CasePaymentFailed", "SolAdmonCreated",
            "SolAppCreatedDeceasedDtls", "SolAppCreatedSolicitorDtls", "SolAppUpdated", "SolProbateCreated",
            "SolIntestacyCreated", "Deleted", "Stopped");
    private final DocumentTransformer documentTransformer;
    private final AssembleLetterTransformer assembleLetterTransformer;
    private final ExecutorsApplyingNotificationService executorsApplyingNotificationService;
    private final ReprintTransformer reprintTransformer;
    private final SolicitorLegalStatementNextStepsTransformer solicitorLegalStatementNextStepsDefaulter;
    private final ExecutorsTransformer solicitorExecutorTransformer;
    private final ResetResponseCaseDataTransformer resetResponseCaseDataTransformer;
    private final TaskListUpdateService taskListUpdateService;
    private final CaseDataTransformer caseDataTransformer;
    private final OrganisationsRetrievalService organisationsRetrievalService;
    private final SolicitorPaymentReferenceDefaulter solicitorPaymentReferenceDefaulter;
    private final IhtEstateDefaulter ihtEstateDefaulter;
    private final Iht400421Defaulter iht400421Defaulter;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    private final AuditEventService auditEventService;
    private final SecurityUtils securityUtils;

    @Value("${make_dormant.add_time_minutes}")
    private int makeDormantAddTimeMinutes;

    public static final DateTimeFormatter DORMANT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public CallbackResponse createSolsCase(CallbackRequest callbackRequest, String authToken) {

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(),
            callbackRequest.getEventId(), Optional.empty(), true);
        responseCaseDataBuilder.applicantOrganisationPolicy(buildOrganisationPolicy(
            callbackRequest.getCaseDetails(), authToken));
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse updateTaskList(CallbackRequest callbackRequest, Optional<UserInfo> caseworkerInfo) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        true);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse defaultDateOfDeathType(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> builder = ResponseCaseData.builder().dateOfDeathType(DEFAULT_DATE_OF_DEATHTYPE);
        return transformResponse(builder.build());
    }

    public CallbackResponse setupOriginalDocumentsForRemoval(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(),
                callbackRequest.getEventId(), Optional.empty(),true);
        OriginalDocuments originalDocuments = OriginalDocuments.builder()
                .originalDocsGenerated(caseData.getProbateDocumentsGenerated())
                .originalDocsScanned(caseData.getScannedDocuments())
                .originalDocsUploaded(caseData.getBoDocumentsUploaded())
                .build();
        responseCaseDataBuilder.originalDocuments(originalDocuments);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse defaultIhtEstateFromDateOfDeath(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?,?> responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(),
                callbackRequest.getEventId(), Optional.empty(),true);
        ihtEstateDefaulter.defaultPageFlowIhtSwitchDate(callbackRequest.getCaseDetails().getData(),
            responseCaseDataBuilder);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse defaultIht400421DatePageFlow(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?,?> responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(),
                callbackRequest.getEventId(), Optional.empty(),true);
        iht400421Defaulter.defaultPageFlowForIht400421(callbackRequest.getCaseDetails().getData(),
            responseCaseDataBuilder);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest,
                                                                Optional<String> newState,
                                                                Optional<UserInfo> caseworkerInfo) {
        final CaseDetails cd = callbackRequest.getCaseDetails();
        // set here to ensure tasklist html is correctly generated
        cd.setState(newState.orElse(null));

        ResponseCaseData responseCaseData =
                getResponseCaseData(cd,
                        callbackRequest.getEventId(),
                        newState.isPresent() ? caseworkerInfo : Optional.empty(),
                        false)
                // set here again to make life easier mocking
                .state(newState.orElse(null))
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse grantRaised(CallbackRequest callbackRequest, List<Document> documents, String letterId,
                                        Optional<UserInfo> caseworkerInfo) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, true));

        ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);

        if (documentTransformer.hasDocumentWithType(documents, GRANT_RAISED) && letterId != null) {
            CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, GRANT_RAISED.getTemplateName());
            appendToBulkPrintCollection(bulkPrint, caseData);

            responseCaseDataBuilder
                    .bulkPrintId(caseData.getBulkPrintId())
                    .build();
        }
        if (caseData.getApplicationSubmittedDate() == null) {
            responseCaseDataBuilder.applicationSubmittedDate(dateTimeFormatter.format(LocalDate.now()));
        }

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse caseStopped(CallbackRequest callbackRequest, List<Document> documents, String letterId,
                                        Optional<UserInfo> caseworkerInfo) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, true));

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);

        if (documentTransformer.hasDocumentWithType(documents, CAVEAT_STOPPED) && letterId != null) {
            CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, CAVEAT_STOPPED.getTemplateName());
            appendToBulkPrintCollection(bulkPrint, caseData);

            responseCaseDataBuilder
                    .bulkPrintId(caseData.getBulkPrintId())
                    .boCaveatStopSendToBulkPrintRequested(caseData.getBoCaveatStopSendToBulkPrint())
                    .build();
        }

        return transformResponse(responseCaseDataBuilder.build());
    }


    public CallbackResponse defaultRedeclarationSOTValues(CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        List<CollectionMember<ExecutorsApplyingNotification>> exec =
                executorsApplyingNotificationService.createExecutorList(caseDetails.getData());
        ResponseCaseData responseCaseData =
            getResponseCaseData(caseDetails, callbackRequest.getEventId(), Optional.empty(),false)
                .executorsApplyingNotifications(exec)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse defaultRequestInformationValues(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        Optional.empty(),
                        false);
        resetRequestInformationFields(responseCaseDataBuilder);
        defaultInformationRequestSwitch(callbackRequest, responseCaseDataBuilder);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addInformationRequestDocuments(CallbackRequest callbackRequest, List<Document> documents,
                                                          Optional<UserInfo> caseworkerInfo) {
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        caseworkerInfo,
                        false);
        responseCaseDataBuilder.evidenceHandled(YES);
        responseCaseDataBuilder.evidenceHandledDate(dateTimeFormatter.format(LocalDate.now()));
        final CaseData caseData = callbackRequest.getCaseDetails().getData();
        if (isHubResponseRequired(caseData)) {
            responseCaseDataBuilder.citizenResponseCheckbox(null)
                    .expectedResponseDate(null)
                    .documentUploadIssue(null);
        }
        if (documentTransformer.hasDocumentWithType(documents, SENT_EMAIL)) {
            responseCaseDataBuilder.boEmailRequestInfoNotificationRequested(
                    callbackRequest.getCaseDetails().getData().getBoEmailRequestInfoNotification());
        }

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCitizenHubResponse(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        Optional.empty(),
                        false);
        final CaseData caseData = callbackRequest.getCaseDetails().getData();

        if (YES.equalsIgnoreCase(caseData.getDocumentUploadIssue())
                && !YES.equalsIgnoreCase(caseData.getIsSaveAndClose())) {
            responseCaseDataBuilder.evidenceHandled(YES);
            responseCaseDataBuilder.evidenceHandledDate(dateTimeFormatter.format(LocalDate.now()));

            if (nothingSubmitted(caseData)) {
                resetRequestInformationFields(responseCaseDataBuilder);
            }
        }

        if (isHubResponseRequired(caseData) && YES.equalsIgnoreCase(caseData.getCitizenResponseCheckbox())) {
            resetRequestInformationFields(responseCaseDataBuilder);

            responseCaseDataBuilder
                    .citizenResponse(null)
                    .citizenDocumentsUploaded(null)
                    .isSaveAndClose(null)
                    .citizenResponses(getCitizenResponsesList(caseData))
                    .boDocumentsUploaded(addCitizenUploadDocument(caseData));

            if (!YES.equalsIgnoreCase(caseData.getDocumentUploadIssue())) {
                responseCaseDataBuilder.evidenceHandled(NO);
            }
        }
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addDocumentPreview(CallbackRequest callbackRequest, Document document) {

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        Optional.empty(),
                        false);
        responseCaseDataBuilder.emailPreview(document.getDocumentLink());

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addDocuments(CallbackRequest callbackRequest, List<Document> documents,
                                         String letterId, String pdfSize, Optional<UserInfo> caseworkerInfo) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        caseData.setAuthenticatedDate(LocalDate.now());

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);

        if (documents.isEmpty()) {
            responseCaseDataBuilder.boEmailDocsReceivedNotificationRequested(
                    callbackRequest.getCaseDetails().getData().getBoEmailDocsReceivedNotification());

        }
        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, AD_COLLIGENDA_BONA_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_AD_COLLIGENDA_BONA_GRANT)) {

            String grantIssuedDate = dateTimeFormatter.format(LocalDate.now());
            responseCaseDataBuilder
                    .boEmailGrantIssuedNotificationRequested(
                            callbackRequest.getCaseDetails().getData().getBoEmailGrantIssuedNotification())
                    .boSendToBulkPrintRequested(
                            callbackRequest.getCaseDetails().getData().getBoSendToBulkPrint())
                    .bulkPrintSendLetterId(letterId)
                    .bulkPrintPdfSize(String.valueOf(pdfSize))
                    .grantIssuedDate(grantIssuedDate);

            responseCaseDataBuilder.evidenceHandled(YES);
            responseCaseDataBuilder.evidenceHandledDate(dateTimeFormatter.format(LocalDate.now()));

        } else if (documentTransformer.hasDocumentWithType(documents, EDGE_CASE)) {
            String grantIssuedDate = dateTimeFormatter.format(LocalDate.now());

            responseCaseDataBuilder.grantIssuedDate(grantIssuedDate);
        }
        if (documentTransformer.hasDocumentWithType(documents, SENT_EMAIL)) {
            responseCaseDataBuilder.boEmailDocsReceivedNotificationRequested(
                    callbackRequest.getCaseDetails().getData().getBoEmailDocsReceivedNotification());
        }

        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, AD_COLLIGENDA_BONA_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE)) {
            if (letterId != null) {
                var documentTypes = new DocumentType[] {
                    DIGITAL_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
                    AD_COLLIGENDA_BONA_GRANT_REISSUE, WELSH_DIGITAL_GRANT_REISSUE, WELSH_ADMON_WILL_GRANT_REISSUE,
                    WELSH_INTESTACY_GRANT_REISSUE, WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE
                };
                String templateName = getTemplateName(documents, documentTypes);
                CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, templateName);
                appendToBulkPrintCollection(bulkPrint, caseData);
                responseCaseDataBuilder
                        .bulkPrintId(caseData.getBulkPrintId());
            }
            String grantReissuedDate = dateTimeFormatter.format(LocalDate.now());
            responseCaseDataBuilder
                    .latestGrantReissueDate(grantReissuedDate)
                    .boEmailGrantReissuedNotificationRequested(
                            callbackRequest.getCaseDetails().getData().getBoEmailGrantReissuedNotification())
                    .boGrantReissueSendToBulkPrintRequested(
                            callbackRequest.getCaseDetails().getData().getBoGrantReissueSendToBulkPrint());
        }

        responseCaseDataBuilder
                .solsSOTNeedToUpdate(null);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addNocDocuments(CallbackRequest callbackRequest, List<Document> documents,
                                            Optional<UserInfo> caseworkerInfo) {
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addBulkPrintInformationForReprint(CallbackRequest callbackRequest, Document document,
                                                              String letterId, String pdfSize,
                                                              Optional<UserInfo> caseworkerInfo) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);

        List<Document> documents = Arrays.asList(document);
        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, AD_COLLIGENDA_BONA_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_AD_COLLIGENDA_BONA_GRANT)) {

            responseCaseDataBuilder
                    .bulkPrintSendLetterId(letterId)
                    .bulkPrintPdfSize(String.valueOf(pdfSize));
        }
        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, AD_COLLIGENDA_BONA_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, STATEMENT_OF_TRUTH)
                || documentTransformer.hasDocumentWithType(documents, WELSH_STATEMENT_OF_TRUTH)
                || documentTransformer.hasDocumentWithType(documents, DocumentType.OTHER)) {
            if (letterId != null) {
                var documentTypes = new DocumentType[] {
                    DIGITAL_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
                    AD_COLLIGENDA_BONA_GRANT_REISSUE, WELSH_DIGITAL_GRANT_REISSUE, WELSH_ADMON_WILL_GRANT_REISSUE,
                    WELSH_INTESTACY_GRANT_REISSUE, WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE,
                    STATEMENT_OF_TRUTH, WELSH_STATEMENT_OF_TRUTH, DocumentType.OTHER
                };
                String templateName = getTemplateName(documents, documentTypes);
                CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, templateName);
                appendToBulkPrintCollection(bulkPrint, caseData);
                responseCaseDataBuilder
                        .bulkPrintId(caseData.getBulkPrintId());
            }
        }

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addSOTDocument(CallbackRequest callbackRequest, Document document,
                                           Optional<UserInfo> caseworkerInfo) {
        documentTransformer.addDocument(callbackRequest, document, false);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addMatches(CallbackRequest callbackRequest, List<CaseMatch> newMatches,
                                       Optional<UserInfo> caseworkerInfo) {
        List<CollectionMember<CaseMatch>> storedMatches = callbackRequest.getCaseDetails().getData().getCaseMatches();

        // Removing case matches that have been already added
        storedMatches.stream()
                .map(CollectionMember::getValue).forEach(newMatches::remove);

        storedMatches.addAll(newMatches.stream().map(CollectionMember::new).collect(Collectors.toList()));

        storedMatches.sort(Comparator.comparingInt(m -> ofNullable(m.getValue().getValid()).orElse("").length()));

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);
        if (!storedMatches.isEmpty()) {
            responseCaseDataBuilder.matches("Possible case matches");
        } else {
            responseCaseDataBuilder.matches("No matches found");
        }

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse resolveStop(CallbackRequest callbackRequest, Optional<UserInfo> caseworkerInfo) {
        setState(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);
        switch (callbackRequest.getCaseDetails().getData().getResolveStopState()) {
            case CASE_MATCHING_ISSUE_GRANT:
                responseCaseDataBuilder.state(CASE_MATCHING_ISSUE_GRANT);
                break;
            case QA_CASE_STATE:
                responseCaseDataBuilder.state(QA_CASE_STATE);
                break;
            case READY_FOR_ISSUE:
                responseCaseDataBuilder.state(READY_FOR_ISSUE);
                break;
            default:
                responseCaseDataBuilder.state(CASE_PRINTED);
                break;
        }
        return transformResponse(responseCaseDataBuilder.build());
    }

    private void setState(CallbackRequest callbackRequest) {
        CaseDetails details = callbackRequest.getCaseDetails();
        switch (details.getData().getResolveStopState()) {
            case CASE_MATCHING_ISSUE_GRANT:
                details.setState(CASE_MATCHING_ISSUE_GRANT);
                break;
            case QA_CASE_STATE:
                details.setState(QA_CASE_STATE);
                break;
            case READY_FOR_ISSUE:
                details.setState(READY_FOR_ISSUE);
                break;
            default:
                details.setState(CASE_PRINTED);
                break;
        }
    }

    public CallbackResponse resolveCaseWorkerEscalationState(CallbackRequest callbackRequest,
                                                             Optional<UserInfo> caseworkerInfo) {
        return transformWithConditionalStateChange(callbackRequest,
                ofNullable(callbackRequest.getCaseDetails().getData().getResolveCaseWorkerEscalationState()),
                caseworkerInfo);
    }


    public CallbackResponse transferToState(CallbackRequest callbackRequest, Optional<UserInfo> caseworkerInfo) {
        return transformWithConditionalStateChange(callbackRequest, Optional.of(callbackRequest.getCaseDetails()
                .getData().getTransferToState()), caseworkerInfo);
    }

    public CallbackResponse transferCaveatStopState(
            final CallbackRequest callbackRequest,
            final Optional<UserInfo> caseworkerInfo) {
        return transformWithConditionalStateChange(
                callbackRequest,
                Optional.of(callbackRequest.getCaseDetails().getData().getResolveCaveatStopState()),
                caseworkerInfo);
    }

    public CallbackResponse rollback(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(),false);
        SecurityDTO securityDTO = securityUtils.getSecurityDTO();
        auditEventService.getLatestAuditEventByState(
                        callbackRequest.getCaseDetails().getId().toString(), ROLLBACK_STATE_LIST,
                        securityDTO.getAuthorisation(), securityDTO.getServiceAuthorisation())
                .ifPresent(auditEvent -> {
                    log.info("Audit event found: Case ID = {}, Event State = {}",
                            callbackRequest.getCaseDetails().getId(), auditEvent.getStateId());
                    responseCaseDataBuilder.state(auditEvent.getStateId());
                });
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse changeDob(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(),false);
        responseCaseDataBuilder.deceasedDateOfBirth(callbackRequest.getCaseDetails().getData().getDeceasedDob());
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse superUserMakeCaseDormant(CallbackRequest callbackRequest,
                                                     Optional<UserInfo> caseworkerInfo) {
        LocalDateTime dormantDateTime = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(makeDormantAddTimeMinutes);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        false);
        responseCaseDataBuilder.moveToDormantDateTime(dormantDateTime.format(DORMANT_DATE_FORMAT));
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformUniqueProbateCode(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(),false);
        showAssetsValuePageFlow(callbackRequest.getCaseDetails().getData(), responseCaseDataBuilder);
        responseCaseDataBuilder.uniqueProbateCodeId(callbackRequest.getCaseDetails()
                .getData().getUniqueProbateCodeId() != null ? callbackRequest.getCaseDetails()
                        .getData().getUniqueProbateCodeId().replaceAll("\\s+", "") : null);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformValuesPage(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(),false);
        showAssetsValuePageFlow(callbackRequest.getCaseDetails().getData(), responseCaseDataBuilder);
        return transformResponse(responseCaseDataBuilder.build());
    }

    private BigDecimal getNetValueLabel(CaseData caseData) {
        boolean isOnOrAfterSwitchDate = dateOfDeathIsOnOrAfterSwitchDate(caseData.getDeceasedDateOfDeath());
        if (ApplicationType.SOLICITOR.equals(caseData.getApplicationType())
                && CHANNEL_CHOICE_DIGITAL.equals(caseData.getChannelChoice())
                && (!isOnOrAfterSwitchDate && IHT400.equals(caseData.getIhtFormId()))
                || (isOnOrAfterSwitchDate && IHT400.equals(caseData.getIhtFormEstate()))
                && caseData.getIhtFormNetValue() != null) {
            return caseData.getIhtFormNetValue();
        } else {
            return caseData.getIhtNetValue();
        }
    }

    private void showAssetsValuePageFlow(CaseData caseData, ResponseCaseDataBuilder<?,?> responseCaseDataBuilder) {
        boolean isOnOrAfterSwitchDate = dateOfDeathIsOnOrAfterSwitchDate(caseData.getDeceasedDateOfDeath());
        boolean isIhtFormCompleted = YES.equals(caseData.getIhtFormEstateValuesCompleted());
        boolean isIht400FormAfter = IHT400.equals(caseData.getIhtFormEstate());
        boolean isIht400FormBefore = IHT400.equals(caseData.getIhtFormId());
        boolean isHmrcLetterId = YES.equals(caseData.getHmrcLetterId());

        boolean shouldSwitch =
                (isOnOrAfterSwitchDate && isIhtFormCompleted && isIht400FormAfter && isHmrcLetterId)
                        || (isOnOrAfterSwitchDate && isIhtFormCompleted && caseData.getIhtFormEstate() != null
                        && !isIht400FormAfter)
                        || (!isOnOrAfterSwitchDate && isIht400FormBefore && isHmrcLetterId)
                        || (!isOnOrAfterSwitchDate && caseData.getIhtFormId() != null && !isIht400FormBefore)
                        || (isOnOrAfterSwitchDate && NO.equals(caseData.getIhtFormEstateValuesCompleted())
                        && (YES.equals(caseData.getDeceasedHadLateSpouseOrCivilPartner())
                        || NO.equals(caseData.getDeceasedHadLateSpouseOrCivilPartner())));

        boolean shouldNetValueSwitch = (isOnOrAfterSwitchDate && isIhtFormCompleted
                && caseData.getIhtFormEstate() != null && !isIht400FormAfter)
                        || (!isOnOrAfterSwitchDate && caseData.getIhtFormId() != null && !isIht400FormBefore)
                        || (isOnOrAfterSwitchDate && NO.equals(caseData.getIhtFormEstateValuesCompleted())
                        && (YES.equals(caseData.getDeceasedHadLateSpouseOrCivilPartner())
                        || NO.equals(caseData.getDeceasedHadLateSpouseOrCivilPartner())));

        responseCaseDataBuilder.iht400Switch(shouldSwitch ? YES : NO)
                .ihtNetValueSwitch(shouldNetValueSwitch ? YES : NO);
    }

    private boolean dateOfDeathIsOnOrAfterSwitchDate(LocalDate dateOfDeath) {
        return exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(dateOfDeath);
    }

    public CallbackResponse transformForSolicitorComplete(CallbackRequest callbackRequest, FeesResponse feesResponse,
                                                          Document sentEmail, Document coversheet, String userId) {
        final var feeForNonUkCopies = transformMoneyGBPToString(feesResponse.getOverseasCopiesFeeResponse()
            .getFeeAmount());
        final var feeForUkCopies = transformMoneyGBPToString(feesResponse.getUkCopiesFeeResponse().getFeeAmount());
        final var applicationFee = transformMoneyGBPToString(feesResponse.getApplicationFeeResponse().getFeeAmount());
        final var totalFee = transformMoneyGBPToString(feesResponse.getTotalAmount());

        final var applicationSubmittedDate = dateTimeFormatter.format(LocalDate.now());
        final var schemaVersion = getSchemaVersion(callbackRequest.getCaseDetails().getData());
        caseDataTransformer
                .transformForSolicitorApplicationCompletion(callbackRequest, feesResponse.getTotalAmount());
        caseDataTransformer.transformCaseDataForEvidenceHandled(callbackRequest);
        if (sentEmail != null) {
            documentTransformer.addDocument(callbackRequest, sentEmail, false);
        }

        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(),
                callbackRequest.getEventId(), Optional.empty(),false)
            // Applications are always new schema but when application becomes a case we retain a mix of schemas for
            // in-flight submitted cases, and bulk scan
            .schemaVersion(schemaVersion)
            .feeForNonUkCopies(feeForNonUkCopies)
            .feeForUkCopies(feeForUkCopies)
            .applicationFee(applicationFee)
            .totalFee(totalFee)
            .applicationSubmittedDate(applicationSubmittedDate)
            .boDocumentsUploaded(addLegalStatementDocument(callbackRequest))
            .applicationSubmittedBy(userId)
            .solsCoversheetDocument(coversheet == null ? null : coversheet.getDocumentLink())
            .build();


        return transformResponse(responseCaseData);
    }

    private List<CollectionMember<UploadDocument>> addLegalStatementDocument(CallbackRequest callbackRequest) {
        List<CollectionMember<UploadDocument>> currentUploads = callbackRequest.getCaseDetails().getData()
            .getBoDocumentsUploaded();
        if (currentUploads == null) {
            currentUploads = new ArrayList<CollectionMember<UploadDocument>>();
        }
        DocumentLink uploadedLegalStatement = callbackRequest.getCaseDetails().getData()
            .getSolsLegalStatementUpload();
        if (uploadedLegalStatement != null) {
            UploadDocument uploadDocument = UploadDocument.builder()
                .documentLink(uploadedLegalStatement)
                .documentType(DocumentType.UPLOADED_LEGAL_STATEMENT)
                .build();
            currentUploads.add(new CollectionMember<UploadDocument>(uploadDocument));
        }

        return currentUploads;
    }

    public CallbackResponse transformForDeceasedDetails(CallbackRequest callbackRequest, Optional<String> newState) {
        CallbackResponse response = transformWithConditionalStateChange(callbackRequest, newState, Optional.empty());

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(),false);

        solicitorExecutorTransformer.mapSolicitorExecutorFieldsToExecutorNamesLists(
                callbackRequest.getCaseDetails().getData(), responseCaseDataBuilder);

        final ResponseCaseData tempNamesResponse = responseCaseDataBuilder.build();
        final ResponseCaseData responseData = response.getData();
        responseData.setSolsIdentifiedApplyingExecs(tempNamesResponse.getSolsIdentifiedApplyingExecs());
        responseData.setSolsIdentifiedNotApplyingExecs(tempNamesResponse.getSolsIdentifiedNotApplyingExecs());
        responseData.setSolsIdentifiedApplyingExecsCcdCopy(tempNamesResponse.getSolsIdentifiedApplyingExecsCcdCopy());
        responseData.setSolsIdentifiedNotApplyingExecsCcdCopy(tempNamesResponse
                .getSolsIdentifiedNotApplyingExecsCcdCopy());

        return response;
    }

    public CallbackResponse transformForSolicitorExecutorNames(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(),false);

        solicitorExecutorTransformer.mapSolicitorExecutorFieldsToExecutorNamesLists(
                callbackRequest.getCaseDetails().getData(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, Document document, String caseType) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(),false);
        responseCaseDataBuilder.solsSOTNeedToUpdate(null);

        if (Arrays.asList(LEGAL_STATEMENTS).contains(document.getDocumentType())) {
            responseCaseDataBuilder.solsLegalStatementDocument(document.getDocumentLink());
            responseCaseDataBuilder.caseType(caseType);
        }
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, Optional<UserInfo> caseworkerInfo) {
        ResponseCaseData responseCaseData = getResponseCaseData(
                callbackRequest.getCaseDetails(),
                callbackRequest.getEventId(),
                callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                false
        ).build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCase(CallbackRequest callbackRequest, Optional<UserInfo> caseworkerInfo) {

        boolean transform = doTransform(callbackRequest);

        ResponseCaseData responseCaseData = getResponseCaseData(
            callbackRequest.getCaseDetails(),
            callbackRequest.getEventId(),
            callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
            transform
        ).build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCaseWithRegistrarDirection(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(),
                callbackRequest.getEventId(), Optional.empty(),false)
                .registrarDirectionToAdd(RegistrarDirection.builder()
                        .build())
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCaseForAttachScannedDocs(CallbackRequest callbackRequest, Document document,
                                                              Optional<UserInfo> caseworkerInfo) {
        boolean transform = doTransform(callbackRequest);
        if (document != null) {
            documentTransformer.addDocument(callbackRequest, document, false);
        }
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        transform);
        responseCaseDataBuilder.probateNotificationsGenerated(
                callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated());
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForLetter(CallbackRequest callbackRequest, Optional<UserInfo> caseworkerInfo) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        callbackRequest.isStateChanged() ? caseworkerInfo : Optional.empty(),
                        doTransform);
        assembleLetterTransformer
                .setupAllLetterParagraphDetails(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForLetter(CallbackRequest callbackRequest, List<Document> documents,
                                                   String letterId, Optional<UserInfo> caseworkerInfo) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        boolean doTransform = doTransform(callbackRequest);
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(),
                        callbackRequest.getEventId(),
                        caseworkerInfo,
                        doTransform);

        if (letterId != null) {
            CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, ASSEMBLED_LETTER.getTemplateName());
            appendToBulkPrintCollection(bulkPrint, callbackRequest.getCaseDetails().getData());
            responseCaseDataBuilder
                    .bulkPrintId(caseData.getBulkPrintId())
                    .boAssembleLetterSendToBulkPrintRequested(caseData.getBoCaveatStopSendToBulkPrint())
                    .build();
        }

        responseCaseDataBuilder
                .previewLink(null)
                .paragraphDetails(new ArrayList<>())
                .build();

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForLetterPreview(CallbackRequest callbackRequest, Document letterPreview) {
        boolean doTransform = doTransform(callbackRequest);

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(), doTransform);
        responseCaseDataBuilder.previewLink(letterPreview.getDocumentLink());

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForReprint(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(), doTransform);
        reprintTransformer.transformReprintDocuments(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForSolicitorLegalStatementRegeneration(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        Optional.empty(), doTransform);
        solicitorLegalStatementNextStepsDefaulter
                .transformLegalStatmentAmendStates(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        transformCaseForSolicitorConfirmText(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public void transformCaseForSolicitorConfirmText(CaseDetails caseDetails, ResponseCaseDataBuilder<?, ?> builder) {
        List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs =
                solicitorExecutorTransformer.createCaseworkerApplyingList(caseDetails.getData());

        var primaryApplicantIsApplying = caseDetails.getData().isPrimaryApplicantApplying();
        var believePlural = "s";
        if (listOfApplyingExecs != null
            && ((primaryApplicantIsApplying && listOfApplyingExecs.size() > 0) || listOfApplyingExecs.size() > 1)) {
            believePlural = "";
        }

        var professionalName = caseDetails.getData().getSolsSOTName();
        var confirmSOT = "";

        if (caseDetails.getData().getSolsWillType() != null
                && caseDetails.getData().getSolsWillType().matches("WillLeft")) {

            confirmSOT = "By signing the statement of truth by ticking the boxes below, I, " + professionalName
                    + " confirm the following:\n\n"
                    + "I, " + professionalName + ", have provided a copy of this application to the executor"
                    + returnPlural(listOfApplyingExecs, primaryApplicantIsApplying) + " named below.\n\n"
                    + "I, " + professionalName + ", have informed the executor"
                    + returnPlural(listOfApplyingExecs, primaryApplicantIsApplying)
                    + " that in signing the statement of truth I am confirming that the executor"
                    + returnPlural(listOfApplyingExecs, primaryApplicantIsApplying)
                    + " believe"  + believePlural + " the facts set out in this legal statement are true.\n\n"
                    + "I, " + professionalName + ", have informed the executor"
                    + returnPlural(listOfApplyingExecs, primaryApplicantIsApplying)
                    + " of the consequences if it should subsequently appear that the executor"
                    + returnPlural(listOfApplyingExecs, primaryApplicantIsApplying)
                    + " did not have an honest belief in the facts set out in the legal statement.\n\n"
                    + "I, " + professionalName + ", have been authorised by the executor"
                    + returnPlural(listOfApplyingExecs, primaryApplicantIsApplying)
                    + " to sign the statement of truth.\n\n"
                    + "I, " + professionalName + ", understand that proceedings for contempt of court may be brought "
                    + "against anyone who makes, or causes to be made, a false statement in a document verified by a "
                    + "statement of truth without an honest belief in its truth.\n";
        } else {

            confirmSOT = "By signing the statement of truth by ticking the boxes below, I, " + professionalName
                    + " confirm the following:\n\n"
                    + "I, " + professionalName + ", have provided a copy of this application to the applicant"
                    + " named below.\n\n"
                    + "I, " + professionalName + ", have informed the applicant"
                    + " that in signing the statement of truth I am confirming that the applicant"
                    + " believes the facts set out in this legal statement are true.\n\n"
                    + "I, " + professionalName + ", have informed the applicant"
                    + " of the consequences if it should subsequently appear that the applicant"
                    + " did not have an honest belief in the facts set out in the legal statement.\n\n"
                    + "I, " + professionalName + ", have been authorised by the applicant"
                    + " to sign the statement of truth.\n\n"
                    + "I, " + professionalName + ", understand that proceedings for contempt of court may be brought "
                    + "against anyone who makes, or causes to be made, a false statement in a document verified by a "
                    + "statement of truth without an honest belief in its truth.\n";
        }

        String executorNames = setExecutorNames(caseDetails.getData(), listOfApplyingExecs, professionalName);

        builder.solsReviewSOTConfirm(confirmSOT);
        builder.solsReviewSOTConfirmCheckbox1Names(executorNames);
        builder.solsReviewSOTConfirmCheckbox2Names(executorNames);
    }

    private String returnPlural(List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs,
                                Boolean primaryApplicantIsApplying) {
        var plural = "";
        if (listOfApplyingExecs != null
            && ((primaryApplicantIsApplying && listOfApplyingExecs.size() > 0) || listOfApplyingExecs.size() > 1)) {
            plural = "s";
        }
        return plural;
    }

    public String setExecutorNames(CaseData caseData,
                                List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs,
                                String professionalName) {
        String executorNames = "";
        Boolean primaryApplicantIsApplying = caseData.isPrimaryApplicantApplying();
        if (caseData.getSolsWillType() != null
            && caseData.getSolsWillType().matches("WillLeft")) {
            executorNames = "The executor" + returnPlural(listOfApplyingExecs,
                primaryApplicantIsApplying) + " ";

            if (caseData.getSolsSolicitorIsApplying().matches(YES)) {
                executorNames = listOfApplyingExecs.isEmpty() ? executorNames + professionalName + ": " :
                    executorNames + FormattingService.createExecsApplyingNames(listOfApplyingExecs) + ": ";
            } else {
                // If only primary applicant as executor then they must be applying otherwise they get hard stopped
                // so no need to check if primary applicant is applying
                if (listOfApplyingExecs.isEmpty()) {
                    executorNames = executorNames + caseData.getPrimaryApplicantForenames()
                        + " " + caseData.getPrimaryApplicantSurname() + ": ";

                // If more than one executor, check if primary applicant is applying to show on sot else show list
                // of applying executors
                } else {
                    executorNames = primaryApplicantIsApplying ? executorNames
                        + caseData.getPrimaryApplicantForenames() + " " + caseData.getPrimaryApplicantSurname()
                        + ", " + FormattingService.createExecsApplyingNames(listOfApplyingExecs) + ": " :
                        executorNames + FormattingService.createExecsApplyingNames(listOfApplyingExecs) + ": ";
                }
            }
        } else {
            executorNames = "The applicant " + caseData.getPrimaryApplicantForenames()
                + " " + caseData.getPrimaryApplicantSurname() + ": ";
        }
        return executorNames;
    }

    public CallbackResponse transformCaseForSolicitorPayment(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(),
                callbackRequest.getEventId(), Optional.empty(), doTransform);
        solicitorPaymentReferenceDefaulter.defaultSolicitorReference(callbackRequest.getCaseDetails().getData(),
                responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    private boolean doTransform(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        return caseData.getApplicationType() == ApplicationType.SOLICITOR
                && caseData.getRecordId() == null
                && !caseData.getPaperForm().equalsIgnoreCase(ANSWER_YES);

    }

    public CallbackResponse paperForm(CallbackRequest callbackRequest, Document document,
                                      Optional<UserInfo> caseworkerInfo) {

        final CaseData cd = callbackRequest.getCaseDetails().getData();
        if (SOLICITOR.equals(cd.getApplicationType())
                // We have currently applied this change to both paperform Yes and paperform No
                // && NO.equals(cd.getPaperForm())
                && GRANT_OF_PROBATE_NAME.equals(cd.getCaseType())) {
            caseDataTransformer.transformForSolicitorApplicationCompletion(callbackRequest);
        }
        if (document != null) {
            documentTransformer.addDocument(callbackRequest, document, false);
        }
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), callbackRequest.getEventId(),
                        caseworkerInfo,false);
        getCaseCreatorResponseCaseBuilder(callbackRequest.getCaseDetails().getData(), responseCaseDataBuilder);
        responseCaseDataBuilder.probateNotificationsGenerated(
                callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated());

        final String ccdVersion = getSchemaVersion(callbackRequest.getCaseDetails().getData());

        return transformResponse(responseCaseDataBuilder
                .schemaVersion(ccdVersion)
                .build()
        );
    }

    private String getSchemaVersion(CaseData cd) {
        final var paperForm = cd.getPaperForm();
        final var applicationType = cd.getApplicationType();

        // not applicable to intestacy or admon will yet
        return (GRANT_TYPE_PROBATE.equals(cd.getSolsWillType()) || GRANT_OF_PROBATE_NAME.equals(cd.getCaseType()))
                && (paperForm == null || paperForm.equals(NO))
                && (applicationType == null || SOLICITOR.equals(applicationType)) ? LATEST_SCHEMA_VERSION : null;
    }

    private CallbackResponse transformResponse(ResponseCaseData responseCaseData) {
        return CallbackResponse.builder().data(responseCaseData).build();
    }

    ResponseCaseDataBuilder<?, ?> getResponseCaseData(CaseDetails caseDetails, String eventId,
                                                      Optional<UserInfo> caseworkerInfo, boolean transform) {
        CaseData caseData = caseDetails.getData();

        ResponseCaseDataBuilder<?, ?> builder = ResponseCaseData.builder()
            .schemaVersion(caseData.getSchemaVersion())
            .state(caseDetails.getState())
            .applicationType(ofNullable(caseData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
            .registryLocation(ofNullable(caseData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))
            .deceasedForenames(caseData.getDeceasedForenames())
            .deceasedSurname(caseData.getDeceasedSurname())
            .deceasedDateOfBirth(ofNullable(caseData.getDeceasedDateOfBirth())
                .map(dateTimeFormatter::format).orElse(null))
            .deceasedDateOfDeath(ofNullable(caseData.getDeceasedDateOfDeath())
                .map(dateTimeFormatter::format).orElse(null))
            .willExists(caseData.getWillExists())
            .willAccessOriginal((caseData.getWillAccessOriginal()))
            .willAccessNotarial((caseData.getWillAccessNotarial()))
            .willHasCodicils(caseData.getWillHasCodicils())
            .willNumberOfCodicils(caseData.getWillNumberOfCodicils())
            .ihtFormId(caseData.getIhtFormId())
            .primaryApplicantForenames(caseData.getPrimaryApplicantForenames())
            .primaryApplicantSurname(caseData.getPrimaryApplicantSurname())
            .primaryApplicantEmailAddress(caseData.getPrimaryApplicantEmailAddress())
            .primaryApplicantIsApplying(caseData.getPrimaryApplicantIsApplying())
            .solsPrimaryExecutorNotApplyingReason(caseData.getSolsPrimaryExecutorNotApplyingReason())
            .primaryApplicantHasAlias(getPrimaryApplicantHasAlias(caseData))
            .otherExecutorExists(getOtherExecutorExists(caseData))
            .primaryApplicantSameWillName(caseData.getPrimaryApplicantSameWillName())
            .primaryApplicantAliasReason(caseData.getPrimaryApplicantAliasReason())
            .primaryApplicantOtherReason(caseData.getPrimaryApplicantOtherReason())
            .deceasedAddress(caseData.getDeceasedAddress())
            .deceasedAnyOtherNames(caseData.getDeceasedAnyOtherNames())
            .primaryApplicantAddress(caseData.getPrimaryApplicantAddress())
            .primaryApplicantNotRequiredToSendDocuments(caseData.getPrimaryApplicantNotRequiredToSendDocuments())
            .solsAdditionalInfo(caseData.getSolsAdditionalInfo())
            .caseMatches(caseData.getCaseMatches())

            .solsSOTNeedToUpdate(caseData.getSolsSOTNeedToUpdate())
            .solsLegalStatementUpload(caseData.getSolsLegalStatementUpload())

            .ihtGrossValue(caseData.getIhtGrossValue())
            .ihtNetValue(getNetValueLabel(caseData))
            .ihtFormNetValue(caseData.getIhtFormNetValue())
            .deceasedDomicileInEngWales(caseData.getDeceasedDomicileInEngWales())
            .ihtFormEstateValuesCompleted(caseData.getIhtFormEstateValuesCompleted())
            .ihtFormEstate(caseData.getIhtFormEstate())
            .ihtEstateGrossValue(caseData.getIhtEstateGrossValue())
            .ihtEstateGrossValueField(caseData.getIhtEstateGrossValueField())
            .ihtEstateNetValue(caseData.getIhtEstateNetValue())
            .ihtEstateNetValueField(caseData.getIhtEstateNetValueField())
            .ihtEstateNetQualifyingValue(caseData.getIhtEstateNetQualifyingValue())
            .ihtEstateNetQualifyingValueField(caseData.getIhtEstateNetQualifyingValueField())
            .deceasedHadLateSpouseOrCivilPartner(caseData.getDeceasedHadLateSpouseOrCivilPartner())
            .ihtUnusedAllowanceClaimed(caseData.getIhtUnusedAllowanceClaimed())

            .solsPaymentMethods(caseData.getSolsPaymentMethods())
            .solsFeeAccountNumber(caseData.getSolsFeeAccountNumber())
            .solsPBANumber(caseData.getSolsPBANumber())
            .solsPBAPaymentReference(caseData.getSolsPBAPaymentReference())

            .extraCopiesOfGrant(transformToString(caseData.getExtraCopiesOfGrant()))
            .outsideUKGrantCopies(transformToString(caseData.getOutsideUKGrantCopies()))
            .feeForNonUkCopies(transformToString(caseData.getFeeForNonUkCopies()))
            .feeForUkCopies(transformToString(caseData.getFeeForUkCopies()))
            .applicationFee(transformToString(caseData.getApplicationFee()))
            .totalFee(transformToString(caseData.getTotalFee()))

            .solsLegalStatementDocument(caseData.getSolsLegalStatementDocument())
            .solsCoversheetDocument(caseData.getSolsCoversheetDocument())
            .casePrinted(caseData.getCasePrinted())
            .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotificationRequested())
            .boEmailGrantIssuedNotificationRequested(caseData.getBoEmailGrantIssuedNotificationRequested())
            .boEmailDocsReceivedNotification(caseData.getBoEmailDocsReceivedNotification())
            .boEmailGrantIssuedNotification(caseData.getBoEmailGrantIssuedNotification())

            .boCaseStopReasonList(caseData.getBoCaseStopReasonList())
            .boStopDetails(caseData.getBoStopDetails())

            .boDeceasedTitle(caseData.getBoDeceasedTitle())
            .boDeceasedHonours(caseData.getBoDeceasedHonours())

            .ihtFormCompletedOnline(caseData.getIhtFormCompletedOnline())

            .boWillMessage(caseData.getBoWillMessage())
            .boExecutorLimitation(caseData.getBoExecutorLimitation())
            .boAdminClauseLimitation(caseData.getBoAdminClauseLimitation())
            .boLimitationText(caseData.getBoLimitationText())
            .probateDocumentsGenerated(caseData.getProbateDocumentsGenerated())
            .probateNotificationsGenerated(caseData.getProbateNotificationsGenerated())
            .boDocumentsUploaded(caseData.getBoDocumentsUploaded())

            .primaryApplicantPhoneNumber(caseData.getPrimaryApplicantPhoneNumber())
            .declaration(caseData.getDeclaration())
            .legalStatement(caseData.getLegalStatement())
            .deceasedMarriedAfterWillOrCodicilDate(caseData.getDeceasedMarriedAfterWillOrCodicilDate())

            .boExaminationChecklistQ1(caseData.getBoExaminationChecklistQ1())
            .boExaminationChecklistQ2(caseData.getBoExaminationChecklistQ2())
            .boExaminationChecklistRequestQA(caseData.getBoExaminationChecklistRequestQA())

            .payments(caseData.getPayments())
            .deceasedMarriedAfterWillOrCodicilDate(caseData.getDeceasedMarriedAfterWillOrCodicilDate())
            .applicationSubmittedDate(caseData.getApplicationSubmittedDate())

            .scannedDocuments(caseData.getScannedDocuments())
            .evidenceHandled(caseData.getEvidenceHandled())

            .paperForm(caseData.getPaperForm())
            .channelChoice(caseData.getChannelChoice())
            .languagePreferenceWelsh(caseData.getLanguagePreferenceWelsh())
            .caseType(caseData.getCaseType())
            .solsSolicitorIsExec(caseData.getSolsSolicitorIsExec())
            .solsSolicitorIsApplying(caseData.getSolsSolicitorIsApplying())
            .solsSolicitorNotApplyingReason(caseData.getSolsSolicitorNotApplyingReason())
            .solsWillType(caseData.getSolsWillType())
            .solsApplicantRelationshipToDeceased(caseData.getSolsApplicantRelationshipToDeceased())
            .solsSpouseOrCivilRenouncing(caseData.getSolsSpouseOrCivilRenouncing())
            .solsAdoptedEnglandOrWales(caseData.getSolsAdoptedEnglandOrWales())
            .solsMinorityInterest(caseData.getSolsMinorityInterest())
            .solsApplicantSiblings(caseData.getSolsApplicantSiblings())
            .solsDiedOrNotApplying(caseData.getSolsDiedOrNotApplying())
            .solsEntitledMinority(caseData.getSolsEntitledMinority())
            .solsLifeInterest(caseData.getSolsLifeInterest())
            .isSolThePrimaryApplicant(caseData.getIsSolThePrimaryApplicant())
            .solsResiduary(caseData.getSolsResiduary())
            .solsResiduaryType(caseData.getSolsResiduaryType())

            .boCaveatStopNotificationRequested(caseData.getBoCaveatStopNotificationRequested())
            .boCaveatStopNotification(caseData.getBoCaveatStopNotification())

            .boCaseStopCaveatId(caseData.getBoCaseStopCaveatId())

            .boCaveatStopEmailNotificationRequested(caseData.getBoCaveatStopEmailNotificationRequested())
            .boCaveatStopEmailNotification(caseData.getValueForCaveatStopEmailNotification())
            .boCaveatStopSendToBulkPrintRequested(caseData.getBoCaveatStopSendToBulkPrintRequested())
            .boCaveatStopSendToBulkPrint(caseData.getBoCaveatStopSendToBulkPrint())
            .boEmailGrantReissuedNotification(caseData.getBoEmailGrantReissuedNotification())
            .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotificationRequested())
            .boGrantReissueSendToBulkPrint(caseData.getBoGrantReissueSendToBulkPrint())
            .boGrantReissueSendToBulkPrintRequested(caseData.getBoGrantReissueSendToBulkPrintRequested())
            .boAssembleLetterSendToBulkPrint(caseData.getBoAssembleLetterSendToBulkPrint())
            .boAssembleLetterSendToBulkPrintRequested(caseData.getBoAssembleLetterSendToBulkPrintRequested())

            .recordId(caseData.getRecordId())
            .legacyType(caseData.getLegacyType())
            .legacyCaseViewUrl(caseData.getLegacyCaseViewUrl())
            .grantIssuedDate(caseData.getGrantIssuedDate())
            .dateOfDeathType(caseData.getDateOfDeathType())
            .orderNeeded(caseData.getOrderNeeded())
            .reissueReason(caseData.getReissueReason())
            .reissueDate(caseData.getReissueDate())
            .reissueReasonNotation(caseData.getReissueReasonNotation())
            .latestGrantReissueDate(caseData.getLatestGrantReissueDate())
            .bulkPrintId(caseData.getBulkPrintId())

            .deceasedDivorcedInEnglandOrWales(caseData.getDeceasedDivorcedInEnglandOrWales())
            .primaryApplicantAdoptionInEnglandOrWales(caseData.getPrimaryApplicantAdoptionInEnglandOrWales())
            .deceasedSpouseNotApplyingReason(caseData.getDeceasedSpouseNotApplyingReason())
            .deceasedOtherChildren(caseData.getDeceasedOtherChildren())
            .allDeceasedChildrenOverEighteen(caseData.getAllDeceasedChildrenOverEighteen())
            .anyDeceasedChildrenDieBeforeDeceased(caseData.getAnyDeceasedChildrenDieBeforeDeceased())
            .anyDeceasedGrandChildrenUnderEighteen(caseData.getAnyDeceasedGrandChildrenUnderEighteen())
            .deceasedAnyChildren(caseData.getDeceasedAnyChildren())
            .deceasedHasAssetsOutsideUK(caseData.getDeceasedHasAssetsOutsideUK())
            .statementOfTruthDocument(caseData.getStatementOfTruthDocument())
            .amendedLegalStatement(caseData.getAmendedLegalStatement())
            .boStopDetailsDeclarationParagraph(caseData.getBoStopDetailsDeclarationParagraph())
            .executorsApplyingNotifications(caseData.getExecutorsApplyingNotifications())
            .boEmailRequestInfoNotification(caseData.getBoEmailRequestInfoNotification())
            .boEmailRequestInfoNotificationRequested(caseData.getBoEmailRequestInfoNotificationRequested())
            .boRequestInfoSendToBulkPrint(caseData.getBoRequestInfoSendToBulkPrint())
            .boRequestInfoSendToBulkPrintRequested(caseData.getBoRequestInfoSendToBulkPrintRequested())
            .probateSotDocumentsGenerated(caseData.getProbateSotDocumentsGenerated())
            .bulkScanCaseReference(caseData.getBulkScanCaseReference())
            .grantDelayedNotificationIdentified(caseData.getGrantDelayedNotificationIdentified())
            .grantDelayedNotificationDate(ofNullable(caseData.getGrantDelayedNotificationDate())
                .map(dateTimeFormatter::format).orElse(null))
            .grantStoppedDate(ofNullable(caseData.getGrantStoppedDate())
                .map(dateTimeFormatter::format).orElse(null))
            .grantDelayedNotificationSent(caseData.getGrantDelayedNotificationSent())
            .grantAwaitingDocumentationNotificationDate(
                ofNullable(caseData.getGrantAwaitingDocumentationNotificationDate())
                    .map(dateTimeFormatter::format).orElse(null))
            .grantAwaitingDocumentatioNotificationSent(caseData.getGrantAwaitingDocumentatioNotificationSent())
            .pcqId(caseData.getPcqId())
            .reprintDocument(caseData.getReprintDocument())
            .reprintNumberOfCopies(caseData.getReprintNumberOfCopies())
            .solsAmendLegalStatmentSelect(caseData.getSolsAmendLegalStatmentSelect())
            .declarationCheckbox(caseData.getDeclarationCheckbox())
            .ihtGrossValueField(caseData.getIhtGrossValueField())
            .ihtNetValueField(caseData.getIhtNetValueField())
            .numberOfExecutors(caseData.getNumberOfExecutors())
            .numberOfApplicants(caseData.getNumberOfApplicants())
            .legalDeclarationJson(caseData.getLegalDeclarationJson())
            .checkAnswersSummaryJson(caseData.getCheckAnswersSummaryJson())
            .registryAddress(caseData.getRegistryAddress())
            .registryEmailAddress(caseData.getRegistryEmailAddress())
            .registrySequenceNumber(caseData.getRegistrySequenceNumber())
            .solsForenames(caseData.getSolsForenames())
            .solsSurname(caseData.getSolsSurname())
            .solsSolicitorWillSignSOT(caseData.getSolsSolicitorWillSignSOT())
            .dispenseWithNotice(caseData.getDispenseWithNotice())
            .dispenseWithNoticeLeaveGiven(caseData.getDispenseWithNoticeLeaveGiven())
            .dispenseWithNoticeLeaveGivenDate(caseData.getDispenseWithNoticeLeaveGivenDate())
            .dispenseWithNoticeOverview(caseData.getDispenseWithNoticeOverview())
            .dispenseWithNoticeSupportingDocs(caseData.getDispenseWithNoticeSupportingDocs())
            .titleAndClearingType(caseData.getTitleAndClearingType())
            .trustCorpName(caseData.getTrustCorpName())
            .trustCorpAddress(caseData.getTrustCorpAddress())
            .lodgementAddress(caseData.getLodgementAddress())
            .lodgementDate(ofNullable(caseData.getLodgementDate())
                .map(dateTimeFormatter::format).orElse(null))
            .nameOfFirmNamedInWill(caseData.getNameOfFirmNamedInWill())
            .addressOfFirmNamedInWill(caseData.getAddressOfFirmNamedInWill())
            .nameOfSucceededFirm(caseData.getNameOfSucceededFirm())
            .addressOfSucceededFirm(caseData.getAddressOfSucceededFirm())
            .anyOtherApplyingPartners(caseData.getAnyOtherApplyingPartners())
            .anyOtherApplyingPartnersTrustCorp(caseData.getAnyOtherApplyingPartnersTrustCorp())
            .otherPartnersApplyingAsExecutors(caseData.getOtherPartnersApplyingAsExecutors())
            .whoSharesInCompanyProfits(caseData.getWhoSharesInCompanyProfits())
            .taskList(caseData.getTaskList())
            .registrarEscalateReason(caseData.getRegistrarEscalateReason())
            .escalatedDate(ofNullable(caseData.getEscalatedDate())
                .map(dateTimeFormatter::format).orElse(null))
            .caseWorkerEscalationDate(ofNullable(caseData.getCaseWorkerEscalationDate())
                .map(dateTimeFormatter::format).orElse(null))
            .resolveCaseWorkerEscalationDate(ofNullable(caseData.getResolveCaseWorkerEscalationDate())
                .map(dateTimeFormatter::format).orElse(null))
            .authenticatedDate(ofNullable(caseData.getAuthenticatedDate())
                .map(dateTimeFormatter::format).orElse(null))
            .deceasedDiedEngOrWales(caseData.getDeceasedDiedEngOrWales())
            .deceasedDeathCertificate(caseData.getDeceasedDeathCertificate())
            .deceasedForeignDeathCertInEnglish(caseData.getDeceasedForeignDeathCertInEnglish())
            .deceasedForeignDeathCertTranslation(caseData.getDeceasedForeignDeathCertTranslation())
            .morePartnersHoldingPowerReserved(caseData.getMorePartnersHoldingPowerReserved())
            .probatePractitionersPositionInTrust(caseData.getProbatePractitionersPositionInTrust())
            .iht217(caseData.getIht217())
            .originalWillSignedDate(caseData.getOriginalWillSignedDate())
            .noOriginalWillAccessReason(caseData.getNoOriginalWillAccessReason())
            .codicilAddedDateList(caseData.getCodicilAddedDateList())
            .furtherEvidenceForApplication(caseData.getFurtherEvidenceForApplication())
            .caseHandedOffToLegacySite(caseData.getCaseHandedOffToLegacySite())
            .deathRecords(caseData.getDeathRecords())
            .willHasVisibleDamage(caseData.getWillHasVisibleDamage())
            .willDamage(caseData.getWillDamage())
            .willDamageReasonKnown(caseData.getWillDamageReasonKnown())
            .willDamageReasonDescription(caseData.getWillDamageReasonDescription())
            .willDamageCulpritKnown(caseData.getWillDamageCulpritKnown())
            .willDamageCulpritName(caseData.getWillDamageCulpritName())
            .willDamageDateKnown(caseData.getWillDamageDateKnown())
            .willDamageDate(caseData.getWillDamageDate())
            .codicilsHasVisibleDamage(caseData.getCodicilsHasVisibleDamage())
            .codicilsDamage(caseData.getCodicilsDamage())
            .codicilsDamageReasonKnown(caseData.getCodicilsDamageReasonKnown())
            .codicilsDamageReasonDescription(caseData.getCodicilsDamageReasonDescription())
            .codicilsDamageCulpritKnown(caseData.getCodicilsDamageCulpritKnown())
            .codicilsDamageCulpritName(caseData.getCodicilsDamageCulpritName())
            .codicilsDamageDateKnown(caseData.getCodicilsDamageDateKnown())
            .codicilsDamageDate(caseData.getCodicilsDamageDate())
            .deceasedWrittenWishes(caseData.getDeceasedWrittenWishes())
            .applicantOrganisationPolicy(caseData.getApplicantOrganisationPolicy())
            .moveToDormantDateTime(caseData.getMoveToDormantDateTime())
            .lastEvidenceAddedDate(caseData.getLastEvidenceAddedDate())
            .registrarDirections(getNullForEmptyRegistrarDirections(caseData.getRegistrarDirections()))
            .removedRepresentative(caseData.getRemovedRepresentative())
            .changeOrganisationRequestField(caseData.getChangeOrganisationRequestField())
            .changeOfRepresentatives(getNullForEmptyRepresentatives(caseData.getChangeOfRepresentatives()))
            .documentUploadedAfterCaseStopped(caseData.getDocumentUploadedAfterCaseStopped())
            .documentsReceivedNotificationSent(caseData.getDocumentsReceivedNotificationSent())
            .serviceRequestReference(caseData.getServiceRequestReference())
            .paymentTaken(caseData.getPaymentTaken())
            .hmrcLetterId(caseData.getHmrcLetterId())
            .uniqueProbateCodeId(caseData.getUniqueProbateCodeId())
            .boHandoffReasonList(getHandoffReasonList(caseData))
            .lastModifiedDateForDormant(getLastModifiedDate(eventId, caseData.getLastModifiedDateForDormant()))
            .applicationSubmittedBy(caseData.getApplicationSubmittedBy())
            .modifiedOCRFieldList(caseData.getModifiedOCRFieldList())
            .autoCaseWarnings(caseData.getAutoCaseWarnings())
            .lastModifiedCaseworkerForenames(caseData.getLastModifiedCaseworkerForenames())
            .lastModifiedCaseworkerSurname(caseData.getLastModifiedCaseworkerSurname())
            .informationNeeded(caseData.getInformationNeeded())
            .informationNeededByPost(caseData.getInformationNeededByPost())
            .citizenResponse(caseData.getCitizenResponse())
            .documentUploadIssue(caseData.getDocumentUploadIssue())
            .citizenResponseCheckbox(caseData.getCitizenResponseCheckbox())
            .expectedResponseDate(caseData.getExpectedResponseDate())
            .citizenResponses(caseData.getCitizenResponses())
            .citizenDocumentsUploaded(caseData.getCitizenDocumentsUploaded())
            .isSaveAndClose(caseData.getIsSaveAndClose())
            .executorsNamed(caseData.getExecutorsNamed())
            .ttl(caseData.getTtl())
            .firstStopReminderSentDate(caseData.getFirstStopReminderSentDate())
            .evidenceHandledDate(caseData.getEvidenceHandledDate());

        handleDeceasedAliases(
                builder,
                caseData,
                caseDetails.getId());

        if (transform) {
            updateCaseBuilderForTransformCase(caseData, builder);
        } else {
            updateCaseBuilder(caseData, builder);
        }
        updateCaseBuilderForCaseworkerNames(builder, caseworkerInfo);

        builder = getCaseCreatorResponseCaseBuilder(caseData, builder);

        builder = taskListUpdateService.generateTaskList(caseDetails, builder);


        return builder;
    }

    void handleDeceasedAliases(
            final ResponseCaseDataBuilder<?,?> builder,
            final CaseData caseData,
            final Long caseRef) {
        // Question this asks is "Is the name on the will the same?" Not "Are there other names on the will?" as the
        // name of the variable in the CaseData object suggests.
        final String decNameOnWillSame = caseData.getDeceasedAnyOtherNameOnWill();
        final var decAliases = caseData.getDeceasedAliasNameList();
        final var solsDecAliases = caseData.getSolsDeceasedAliasNamesList();

        {
            final boolean hasAlternateNameOnWill = decNameOnWillSame != null && NO.equals(decNameOnWillSame);
            final boolean hasDecAliases = decAliases != null && !decAliases.isEmpty();
            final boolean hasSolsDecAliases = solsDecAliases != null && !solsDecAliases.isEmpty();

            if ((hasAlternateNameOnWill || hasDecAliases) && hasSolsDecAliases) {
                // This is one of the contributing causes for DTSPB-4388
                log.info("For case {} found both non-sols and sols aliases: hasAltNameOnWill: {}, hasDecAliases: {},"
                                + " hasSolsDecAliases: {}",
                        caseRef,
                        hasAlternateNameOnWill,
                        hasDecAliases,
                        hasSolsDecAliases);
            }
        }

        List<CollectionMember<AliasName>> newSolsDecAliases = new ArrayList<>();

        if (solsDecAliases != null) {
            newSolsDecAliases.addAll(solsDecAliases);
        }

        newSolsDecAliases.addAll(convertDecAliasesSolsDecAliasList(decAliases));

        {
            final String decAliasFNOnWill = caseData.getDeceasedAliasFirstNameOnWill();
            final String decAliasLNOnWill = caseData.getDeceasedAliasLastNameOnWill();

            newSolsDecAliases.addAll(convertAliasOnWillToSolsDecAliasList(
                    caseRef,
                    decNameOnWillSame,
                    decAliasFNOnWill,
                    decAliasLNOnWill));
        }

        Set<String> seenAliasNames = new HashSet<>();

        builder.solsDeceasedAliasNamesList(newSolsDecAliases.stream()
                .filter(a -> seenAliasNames.add(a.getValue().getSolsAliasname()))
                .toList());
    }

    List<CollectionMember<AliasName>> convertAliasOnWillToSolsDecAliasList(
            final Long caseRef,
            final String differentNameOnWill,
            final String foreNames,
            final String lastName) {
        if (differentNameOnWill != null && NO.equals(differentNameOnWill)) {
            if (foreNames != null && lastName != null) {
                final String aliasValue = new StringBuilder()
                        .append(foreNames)
                        .append(" ")
                        .append(lastName)
                        .toString();

                final AliasName alias = AliasName.builder()
                        .solsAliasname(aliasValue)
                        .build();

                final CollectionMember<AliasName> listMember = new CollectionMember<>(alias);
                return List.of(listMember);
            } else {
                log.info("For case {}, foreNames == null: {}, lastName == null: {},"
                                + " so alias is not being added to solsDecAlias list",
                        caseRef,
                        foreNames == null,
                        lastName == null);
            }
        }
        return List.of();
    }

    List<CollectionMember<AliasName>> convertDecAliasesSolsDecAliasList(
            final List<CollectionMember<ProbateAliasName>> decAliases) {
        if (decAliases == null || decAliases.isEmpty()) {
            return List.of();
        }

        final Function<CollectionMember<ProbateAliasName>, ProbateAliasName> unwrap = c -> c.getValue();

        final Function<ProbateAliasName, AliasName> convert = p -> {
            final String aliasValue = new StringBuilder()
                    .append(p.getForenames())
                    .append(" ")
                    .append(p.getLastName())
                    .toString();

            return AliasName.builder()
                    .solsAliasname(aliasValue)
                    .build();
        };

        final Function<AliasName, CollectionMember<AliasName>> wrap = a -> new CollectionMember<>(a);

        Set<String> seenAliasNames = new HashSet<>();

        return decAliases.stream()
                .map(unwrap)
                .map(convert)
                .map(wrap)
                .filter(cm -> seenAliasNames.add(cm.getValue().getSolsAliasname()))
                .toList();
    }

    OrganisationPolicy buildOrganisationPolicy(CaseDetails caseDetails, String authToken) {
        CaseData caseData = caseDetails.getData();
        OrganisationEntityResponse organisationEntityResponse = null;
        if (null != authToken) {
            organisationEntityResponse = organisationsRetrievalService.getOrganisationEntity(
                    caseDetails.getId().toString(), authToken);
        }
        if (null != organisationEntityResponse && null != caseData.getApplicantOrganisationPolicy()) {
            return OrganisationPolicy.builder()
            .organisation(Organisation.builder()
                .organisationID(organisationEntityResponse.getOrganisationIdentifier())
                .organisationName(organisationEntityResponse.getName())
                .build())
            .orgPolicyReference(caseData.getApplicantOrganisationPolicy().getOrgPolicyReference())
            .orgPolicyCaseAssignedRole(caseData.getApplicantOrganisationPolicy().getOrgPolicyCaseAssignedRole())
            .build();
        }
        return null;
    }

    private boolean isPaperForm(CaseData caseData) {
        return ANSWER_YES.equals(caseData.getPaperForm());
    }

    private boolean willExists(CaseData caseData) {
        if (isIntestacy(caseData)) {
            return false;
        }
        return !(GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType()));
    }

    private boolean isIntestacy(CaseData caseData) {
        return INTESTACY.getName().equals(caseData.getCaseType()) || GRANT_TYPE_INTESTACY
                .equals(caseData.getSolsWillType());
    }

    private boolean isSolsEmailSet(CaseData caseData) {
        return SOLICITOR.equals(caseData.getApplicationType()) && StringUtils
                .isNotBlank(caseData.getSolsSolicitorEmail());
    }

    private boolean isPAEmailSet(CaseData caseData) {
        return PERSONAL.equals(caseData.getApplicationType()) && StringUtils
                .isNotBlank(caseData.getPrimaryApplicantEmailAddress());
    }

    private boolean isCodicil(CaseData caseData) {
        return YES.equals(caseData.getWillHasCodicils());
    }

    private boolean didDeceasedDieEngOrWales(CaseData caseData) {
        return YES.equals(caseData.getDeceasedDiedEngOrWales());
    }

    private boolean isForeignDeathCerticateInEnglish(CaseData caseData) {
        return YES.equals(caseData.getDeceasedForeignDeathCertInEnglish());
    }

    private boolean isHubResponseRequired(CaseData caseData) {
        return (PERSONAL.equals(caseData.getApplicationType())
            && CHANNEL_CHOICE_DIGITAL.equals(caseData.getChannelChoice())
            && YES.equals(caseData.getInformationNeeded())
            && NO.equals(caseData.getInformationNeededByPost()));
    }

    private boolean nothingSubmitted(CaseData caseData) {
        return (caseData.getCitizenResponse() == null || caseData.getCitizenResponse().isEmpty())
                && (caseData.getCitizenDocumentsUploaded() == null || caseData.getCitizenDocumentsUploaded().isEmpty());
    }

    private ResponseCaseDataBuilder<?, ?> getCaseCreatorResponseCaseBuilder(CaseData caseData,
                                                                            ResponseCaseDataBuilder<?, ?> builder) {

        builder
                .schemaVersion(caseData.getSchemaVersion())
                .primaryApplicantSecondPhoneNumber(caseData.getPrimaryApplicantSecondPhoneNumber())
                .primaryApplicantRelationshipToDeceased(caseData.getPrimaryApplicantRelationshipToDeceased())
                .paRelationshipToDeceasedOther(caseData.getPaRelationshipToDeceasedOther())
                .deceasedMaritalStatus(caseData.getDeceasedMaritalStatus())
                .willDatedBeforeApril(caseData.getWillDatedBeforeApril())
                .deceasedEnterMarriageOrCP(caseData.getDeceasedEnterMarriageOrCP())
                .dateOfMarriageOrCP(caseData.getDateOfMarriageOrCP())
                .dateOfDivorcedCPJudicially(caseData.getDateOfDivorcedCPJudicially())
                .willsOutsideOfUK(caseData.getWillsOutsideOfUK())
                .courtOfDecree(caseData.getCourtOfDecree())
                .willGiftUnderEighteen(caseData.getWillGiftUnderEighteen())
                .applyingAsAnAttorney(caseData.getApplyingAsAnAttorney())
                .attorneyOnBehalfOfNameAndAddress(caseData.getAttorneyOnBehalfOfNameAndAddress())
                .mentalCapacity(caseData.getMentalCapacity())
                .courtOfProtection(caseData.getCourtOfProtection())
                .epaOrLpa(caseData.getEpaOrLpa())
                .epaRegistered(caseData.getEpaRegistered())
                .domicilityCountry(caseData.getDomicilityCountry())
                .ukEstate(caseData.getUkEstate())
                .immovableEstate(caseData.getImmovableEstate())
                .domicilityIHTCert(caseData.getDomicilityIHTCert())
                .applicationGrounds(caseData.getApplicationGrounds())
                .willDispose(caseData.getWillDispose())
                .englishWill(caseData.getEnglishWill())
                .appointExec(caseData.getAppointExec())
                .appointExecByDuties(caseData.getAppointExecByDuties())
                .appointExecNo(caseData.getAppointExecNo())
                .entitledToApply(caseData.getEntitledToApply())
                .entitledToApplyOther(caseData.getEntitledToApplyOther())
                .notifiedApplicants(caseData.getNotifiedApplicants())
                .foreignAsset(caseData.getForeignAsset())
                .foreignAssetEstateValue(caseData.getForeignAssetEstateValue())
                .adopted(caseData.getAdopted())
                .adoptiveRelatives(caseData.getAdoptiveRelatives())
                .spouseOrPartner(caseData.getSpouseOrPartner())
                .childrenSurvived(caseData.getChildrenSurvived())
                .childrenOverEighteenSurvived(caseData.getChildrenOverEighteenSurvived())
                .childrenUnderEighteenSurvived(caseData.getChildrenUnderEighteenSurvived())
                .childrenDied(caseData.getChildrenDied())
                .childrenDiedOverEighteen(caseData.getChildrenDiedOverEighteen())
                .childrenDiedUnderEighteen(caseData.getChildrenDiedUnderEighteen())
                .grandChildrenSurvived(caseData.getGrandChildrenSurvived())
                .grandChildrenSurvivedOverEighteen(caseData.getGrandChildrenSurvivedOverEighteen())
                .grandChildrenSurvivedUnderEighteen(caseData.getGrandChildrenSurvivedUnderEighteen())
                .parentsExistSurvived(caseData.getParentsExistSurvived())
                .parentsExistOverEighteenSurvived(caseData.getParentsExistOverEighteenSurvived())
                .parentsExistUnderEighteenSurvived(caseData.getParentsExistUnderEighteenSurvived())
                .wholeBloodSiblingsSurvived(caseData.getWholeBloodSiblingsSurvived())
                .wholeBloodSiblingsSurvivedOverEighteen(caseData.getWholeBloodSiblingsSurvivedOverEighteen())
                .wholeBloodSiblingsSurvivedUnderEighteen(caseData.getWholeBloodSiblingsSurvivedUnderEighteen())
                .wholeBloodSiblingsDied(caseData.getWholeBloodSiblingsDied())
                .wholeBloodSiblingsDiedOverEighteen(caseData.getWholeBloodSiblingsDiedOverEighteen())
                .wholeBloodSiblingsDiedUnderEighteen(caseData.getWholeBloodSiblingsDiedUnderEighteen())
                .wholeBloodNeicesAndNephews(caseData.getWholeBloodNeicesAndNephews())
                .wholeBloodNeicesAndNephewsOverEighteen(caseData.getWholeBloodNeicesAndNephewsOverEighteen())
                .wholeBloodNeicesAndNephewsUnderEighteen(caseData.getWholeBloodNeicesAndNephewsUnderEighteen())
                .halfBloodSiblingsSurvived(caseData.getHalfBloodSiblingsSurvived())
                .halfBloodSiblingsSurvivedOverEighteen(caseData.getHalfBloodSiblingsSurvivedOverEighteen())
                .halfBloodSiblingsSurvivedUnderEighteen(caseData.getHalfBloodSiblingsSurvivedUnderEighteen())
                .halfBloodSiblingsDied(caseData.getHalfBloodSiblingsDied())
                .halfBloodSiblingsDiedOverEighteen(caseData.getHalfBloodSiblingsDiedOverEighteen())
                .halfBloodSiblingsDiedUnderEighteen(caseData.getHalfBloodSiblingsDiedUnderEighteen())
                .halfBloodNeicesAndNephews(caseData.getHalfBloodNeicesAndNephews())
                .halfBloodNeicesAndNephewsOverEighteen(caseData.getHalfBloodNeicesAndNephewsOverEighteen())
                .halfBloodNeicesAndNephewsUnderEighteen(caseData.getHalfBloodNeicesAndNephewsUnderEighteen())
                .grandparentsDied(caseData.getGrandparentsDied())
                .grandparentsDiedOverEighteen(caseData.getGrandparentsDiedOverEighteen())
                .grandparentsDiedUnderEighteen(caseData.getGrandparentsDiedUnderEighteen())
                .wholeBloodUnclesAndAuntsSurvived(caseData.getWholeBloodUnclesAndAuntsSurvived())
                .wholeBloodUnclesAndAuntsSurvivedOverEighteen(caseData
                        .getWholeBloodUnclesAndAuntsSurvivedOverEighteen())
                .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(caseData
                        .getWholeBloodUnclesAndAuntsSurvivedUnderEighteen())
                .wholeBloodUnclesAndAuntsDied(caseData.getWholeBloodUnclesAndAuntsDied())
                .wholeBloodUnclesAndAuntsDiedOverEighteen(caseData.getWholeBloodUnclesAndAuntsDiedOverEighteen())
                .wholeBloodUnclesAndAuntsDiedUnderEighteen(caseData.getWholeBloodUnclesAndAuntsDiedUnderEighteen())
                .wholeBloodCousinsSurvived(caseData.getWholeBloodCousinsSurvived())
                .wholeBloodCousinsSurvivedOverEighteen(caseData.getWholeBloodCousinsSurvivedOverEighteen())
                .wholeBloodCousinsSurvivedUnderEighteen(caseData.getWholeBloodCousinsSurvivedUnderEighteen())
                .halfBloodUnclesAndAuntsSurvived(caseData.getHalfBloodUnclesAndAuntsSurvived())
                .halfBloodUnclesAndAuntsSurvivedOverEighteen(caseData.getHalfBloodUnclesAndAuntsSurvivedOverEighteen())
                .halfBloodUnclesAndAuntsSurvivedUnderEighteen(caseData
                        .getHalfBloodUnclesAndAuntsSurvivedUnderEighteen())
                .halfBloodUnclesAndAuntsDied(caseData.getHalfBloodUnclesAndAuntsDied())
                .halfBloodUnclesAndAuntsDiedOverEighteen(caseData.getHalfBloodUnclesAndAuntsDiedOverEighteen())
                .halfBloodUnclesAndAuntsDiedUnderEighteen(caseData.getHalfBloodUnclesAndAuntsDiedUnderEighteen())
                .halfBloodCousinsSurvived(caseData.getHalfBloodCousinsSurvived())
                .halfBloodCousinsSurvivedOverEighteen(caseData.getHalfBloodCousinsSurvivedOverEighteen())
                .halfBloodCousinsSurvivedUnderEighteen(caseData.getHalfBloodCousinsSurvivedUnderEighteen())
                .applicationFeePaperForm(caseData.getApplicationFeePaperForm())
                .feeForCopiesPaperForm(caseData.getFeeForCopiesPaperForm())
                .totalFeePaperForm(caseData.getTotalFeePaperForm())
                .paperPaymentMethod(caseData.getPaperPaymentMethod())
                .paymentReferenceNumberPaperform(caseData.getPaymentReferenceNumberPaperform())
                .boSendToBulkPrint(caseData.getBoSendToBulkPrint())
                .boSendToBulkPrintRequested(caseData.getBoSendToBulkPrintRequested())
                .languagePreferenceWelsh(caseData.getLanguagePreferenceWelsh())
                .bulkPrintPdfSize(caseData.getBulkPrintPdfSize())
                .bulkPrintSendLetterId(caseData.getBulkPrintSendLetterId())
                .grantDelayedNotificationIdentified(caseData.getGrantDelayedNotificationIdentified())
                .grantDelayedNotificationDate(ofNullable(caseData.getGrantDelayedNotificationDate())
                        .map(dateTimeFormatter::format).orElse(null))
                .grantStoppedDate(ofNullable(caseData.getGrantStoppedDate())
                        .map(dateTimeFormatter::format).orElse(null))
                .grantDelayedNotificationSent(caseData.getGrantDelayedNotificationSent())
                .grantAwaitingDocumentationNotificationDate(
                        ofNullable(caseData.getGrantAwaitingDocumentationNotificationDate())
                                .map(dateTimeFormatter::format).orElse(null))
                .grantAwaitingDocumentatioNotificationSent(caseData.getGrantAwaitingDocumentatioNotificationSent())
                .reprintDocument(caseData.getReprintDocument())
                .reprintNumberOfCopies(caseData.getReprintNumberOfCopies())
                .solsAmendLegalStatmentSelect(caseData.getSolsAmendLegalStatmentSelect())
                .bulkScanEnvelopes(caseData.getBulkScanEnvelopes())
                .solsAdditionalExecutorList(caseData.getSolsAdditionalExecutorList())
                .additionalExecutorsTrustCorpList(caseData.getAdditionalExecutorsTrustCorpList())
                .otherPartnersApplyingAsExecutors(caseData.getOtherPartnersApplyingAsExecutors())
                .dispenseWithNoticeOtherExecsList(caseData.getDispenseWithNoticeOtherExecsList())
                .additionalExecutorsApplying(caseData.getAdditionalExecutorsApplying())
                .additionalExecutorsNotApplying(caseData.getAdditionalExecutorsNotApplying());

        if (YES.equals(caseData.getDeceasedDomicileInEngWales())) {
            builder
                    .domicilityCountry(null);
        }

        if (!GRANT_TYPE_PROBATE.equals(caseData.getSolsWillType())) {
            builder
                    .willDispose(null)
                    .englishWill(null)
                    .appointExec(null)
                    .appointExecByDuties(null)
                    .appointExecNo(null);
        } else if (YES.equals(caseData.getEnglishWill())) {
            builder
                    .appointExecByDuties(null);
        } else if (NO.equals(caseData.getEnglishWill())) {
            builder
                    .appointExec(null)
                    .appointExecNo(null);
        }

        return builder;
    }

    private void updateCaseBuilder(CaseData caseData, ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .primaryApplicantAlias(caseData.getPrimaryApplicantAlias());

        if (caseData.getIhtFormCompletedOnline() != null) {
            if (caseData.getIhtFormCompletedOnline().equalsIgnoreCase(ANSWER_YES)) {
                builder
                        .ihtReferenceNumber(caseData.getIhtReferenceNumber());
            } else {
                builder
                        .ihtReferenceNumber(null);
            }
        }

        if (caseData.getApplicationType() != PERSONAL) {
            builder
                    .solsSOTForenames(caseData.getSolsSOTForenames())
                    .solsSOTSurname(caseData.getSolsSOTSurname())
                    .solsSOTJobTitle(caseData.getSolsSOTJobTitle())
                    .solsSolicitorAppReference(caseData.getSolsSolicitorAppReference())
                    .solsSolicitorFirmName(caseData.getSolsSolicitorFirmName())
                    .solsSolicitorEmail(caseData.getSolsSolicitorEmail())
                    .solsSolicitorPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
                    .solsSolicitorAddress(caseData.getSolsSolicitorAddress());

            if (caseData.getSolsSOTForenames() != null && caseData.getSolsSOTSurname() != null) {
                builder
                        .solsSOTName(getSolsSOTName(caseData.getSolsSOTForenames(), caseData.getSolsSOTSurname()));
            }

        }

        if (!isPaperForm(caseData)) {
            builder
                    .paperForm(ANSWER_NO);
        }

        if (willExists(caseData)) {
            builder
                    .willExists(ANSWER_YES);
        } else {
            builder
                    .willExists(ANSWER_NO);
        }

        if (isIntestacy(caseData)) {
            builder
                    .primaryApplicantIsApplying(ANSWER_YES);
        }

        if (SOLICITOR.equals(caseData.getApplicationType())) {
            String answer = isSolsEmailSet(caseData) ? ANSWER_YES : ANSWER_NO;
            builder
                    .boEmailDocsReceivedNotification(answer)
                    .boEmailRequestInfoNotification(answer)
                    .boEmailGrantIssuedNotification(answer)
                    .boEmailGrantReissuedNotification(answer);
        }

        if (PERSONAL.equals(caseData.getApplicationType())) {
            String answer = isPAEmailSet(caseData) ? ANSWER_YES : ANSWER_NO;
            builder
                    .boEmailDocsReceivedNotification(answer)
                    .boEmailRequestInfoNotification(answer)
                    .boEmailGrantIssuedNotification(answer)
                    .boEmailGrantReissuedNotification(answer);
        }

        if (!isCodicil(caseData)) {
            builder
                    .willNumberOfCodicils(null)
                    .codicilAddedDateList(null);
        }

        if (!didDeceasedDieEngOrWales(caseData)) {
            builder.deceasedDeathCertificate(null);
        } else {
            builder.deceasedForeignDeathCertInEnglish(null);
            builder.deceasedForeignDeathCertTranslation(null);
        }

        if (isForeignDeathCerticateInEnglish(caseData)) {
            builder.deceasedForeignDeathCertTranslation(null);
        }

        if (caseData.getCaseType() == null) {
            builder
                    .caseType(CASE_TYPE_DEFAULT);
        }

        if (caseData.getDateOfDeathType() == null) {
            builder
                    .dateOfDeathType(DATE_OF_DEATH_TYPE_DEFAULT);
        }

        if (!YES.equals(caseData.getOtherExecutorExists())) {
            builder
                    .solsAdditionalExecutorList(null);
        }

        if (caseData.getPrimaryApplicantAliasReason() != null) {
            if (caseData.getPrimaryApplicantAliasReason().equalsIgnoreCase(OTHER)) {
                builder
                        .primaryApplicantOtherReason(caseData.getPrimaryApplicantOtherReason());
            } else {
                builder
                        .primaryApplicantOtherReason(null);
            }
        }

        solicitorExecutorTransformer.setFieldsIfSolicitorIsNotNamedInWillAsAnExecutor(caseData);
        resetResponseCaseDataTransformer.resetTitleAndClearingFields(caseData, builder);

        builder.solsExecutorAliasNames(caseData.getSolsExecutorAliasNames());
    }

    private void updateCaseBuilderForTransformCase(CaseData caseData, ResponseCaseDataBuilder<?, ?> builder) {
        builder
                .ihtReferenceNumber(caseData.getIhtReferenceNumber())
                .primaryApplicantAlias(caseData.getPrimaryApplicantAlias())
                .solsExecutorAliasNames(caseData.getSolsExecutorAliasNames());

        if (caseData.getApplicationType() != PERSONAL) {
            builder
                    .solsSOTForenames(caseData.getSolsSOTForenames())
                    .solsSOTSurname(caseData.getSolsSOTSurname())
                    .solsSOTJobTitle(caseData.getSolsSOTJobTitle())
                    .solsSolicitorAppReference(caseData.getSolsSolicitorAppReference())
                    .solsSolicitorFirmName(caseData.getSolsSolicitorFirmName())
                    .solsSolicitorEmail(caseData.getSolsSolicitorEmail())
                    .solsSolicitorPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
                    .solsSolicitorAddress(caseData.getSolsSolicitorAddress());

            if (caseData.getSolsSOTForenames() != null && caseData.getSolsSOTSurname() != null) {
                builder
                        .solsSOTName(getSolsSOTName(caseData.getSolsSOTForenames(), caseData.getSolsSOTSurname()));

            }
        }
        if (!isPaperForm(caseData)) {
            builder
                    .paperForm(ANSWER_NO);
        }

        if (caseData.getChannelChoice() == null) {
            builder.channelChoice(CHANNEL_CHOICE_DIGITAL);
        } else {
            builder.channelChoice(caseData.getChannelChoice());
        }

        if (willExists(caseData)) {
            builder
                    .willExists(ANSWER_YES);
        } else {
            builder
                    .willExists(ANSWER_NO);
        }

        if (isIntestacy(caseData)) {
            builder
                    .primaryApplicantIsApplying(ANSWER_YES);
        }

        if (SOLICITOR.equals(caseData.getApplicationType())) {
            String answer = isSolsEmailSet(caseData) ? ANSWER_YES : ANSWER_NO;
            builder
                    .boEmailDocsReceivedNotification(answer)
                    .boEmailRequestInfoNotification(answer)
                    .boEmailGrantIssuedNotification(answer)
                    .boEmailGrantReissuedNotification(answer);
        }

        if (PERSONAL.equals(caseData.getApplicationType())) {
            String answer = isPAEmailSet(caseData) ? ANSWER_YES : ANSWER_NO;
            builder
                    .boEmailDocsReceivedNotification(answer)
                    .boEmailRequestInfoNotification(answer)
                    .boEmailGrantIssuedNotification(answer)
                    .boEmailGrantReissuedNotification(answer);
        }

        if (!isCodicil(caseData)) {
            builder
                    .willNumberOfCodicils(null)
                    .codicilAddedDateList(null);
        }

        if (!didDeceasedDieEngOrWales(caseData)) {
            builder.deceasedDeathCertificate(null);
        } else {
            builder.deceasedForeignDeathCertInEnglish(null);
            builder.deceasedForeignDeathCertTranslation(null);
        }

        if (isForeignDeathCerticateInEnglish(caseData)) {
            builder.deceasedForeignDeathCertTranslation(null);
        }

        if (caseData.getCaseType() == null) {
            builder
                    .caseType(CASE_TYPE_DEFAULT);
        }

        if (caseData.getDateOfDeathType() == null) {
            builder
                    .dateOfDeathType(DATE_OF_DEATH_TYPE_DEFAULT);
        }
    }

    private AliasName buildDeceasedAliasNameExecutor(ProbateAliasName aliasNames) {
        return AliasName.builder()
                .solsAliasname(aliasNames.getForenames() + " " + aliasNames.getLastName())
                .build();
    }

    private String getOtherExecutorExists(CaseData caseData) {
        if (PERSONAL.equals(caseData.getApplicationType())) {
            return
                caseData.getAdditionalExecutorsApplying() == null || caseData.getAdditionalExecutorsApplying().isEmpty()
                    ? ANSWER_NO : ANSWER_YES;
        } else {
            return caseData.getOtherExecutorExists();
        }
    }

    private String getPrimaryApplicantHasAlias(CaseData caseData) {
        if (caseData.getPrimaryApplicantHasAlias() == null) {
            return ANSWER_NO;
        } else {
            return caseData.getPrimaryApplicantHasAlias();
        }
    }

    private String transformMoneyGBPToString(BigDecimal bdValue) {
        return ofNullable(bdValue)
                .map(value -> bdValue.multiply(new BigDecimal(100)))
                .map(BigDecimal::intValue)
                .map(String::valueOf)
                .orElse(null);
    }

    private String transformToString(BigDecimal bdValue) {
        return ofNullable(bdValue)
                .map(BigDecimal::intValue)
                .map(String::valueOf)
                .orElse(null);
    }

    private String transformToString(Long longValue) {
        return ofNullable(longValue)
                .map(String::valueOf)
                .orElse(null);
    }

    private CollectionMember<BulkPrint> buildBulkPrint(String letterId, String templateName) {
        return new CollectionMember<>(null, BulkPrint.builder()
                .sendLetterId(letterId)
                .templateName(templateName)
                .build());
    }

    private List<CollectionMember<BulkPrint>> appendToBulkPrintCollection(
            CollectionMember<BulkPrint> bulkPrintCollectionMember, CaseData caseData) {
        if (caseData.getBulkPrintId() == null) {
            caseData.setBulkPrintId(Arrays.asList(
                    bulkPrintCollectionMember));

        } else {
            caseData.getBulkPrintId().add(bulkPrintCollectionMember);
        }
        return caseData.getBulkPrintId();
    }

    private String getTemplateName(List<Document> documents, DocumentType[] documentTypes) {
        String templateName = null;

        for (DocumentType documentType : documentTypes) {
            for (int i = 0; i < documents.size(); i++) {
                if (documents.get(i).getDocumentType().getTemplateName().equals(documentType.getTemplateName())) {
                    templateName = documentType.getTemplateName();
                    break;
                }
            }
        }
        return templateName;
    }

    private String getSolsSOTName(String firstNames, String surname) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstNames);
        sb.append(" " + surname);
        return sb.toString();
    }

    public CaseCreationDetails bulkScanGrantOfRepresentationCaseTransform(
            GrantOfRepresentationData grantOfRepresentationData) {

        if (grantOfRepresentationData.getApplicationType() == null) {
            grantOfRepresentationData
                    .setApplicationType(uk.gov.hmcts.reform.probate.model.cases.ApplicationType.PERSONAL);
        }

        if (grantOfRepresentationData.getRegistryLocation() == null) {
            grantOfRepresentationData.setRegistryLocation(EXCEPTION_RECORD_REGISTRY_LOCATION);
        }

        if (grantOfRepresentationData.getPaperForm() == null) {
            grantOfRepresentationData.setPaperForm(true);
        }

        if (grantOfRepresentationData.getEvidenceHandled() == null) {
            grantOfRepresentationData.setEvidenceHandled(false);
        }

        if (SOLICITORS.equals(grantOfRepresentationData.getApplicationType())) {
            if (TRUE == grantOfRepresentationData.getLanguagePreferenceWelsh()) {
                grantOfRepresentationData.setRegistryLocation(RegistryLocation.CARDIFF);
            }

            grantOfRepresentationData.setApplicantOrganisationPolicy(uk.gov.hmcts.reform.probate.model.cases
                .OrganisationPolicy.builder()
                .organisation(uk.gov.hmcts.reform.probate.model.cases.Organisation.builder()
                    .organisationID(null)
                    .organisationName(null)
                    .build())
                .orgPolicyReference(null)
                .orgPolicyCaseAssignedRole(POLICY_ROLE_APPLICANT_SOLICITOR)
                .build());
        }

        return CaseCreationDetails.builder().<ResponseCaveatData>
                eventId(EXCEPTION_RECORD_EVENT_ID).caseData(grantOfRepresentationData)
                .caseTypeId(EXCEPTION_RECORD_CASE_TYPE_ID).build();
    }

    private List<CollectionMember<RegistrarDirection>> getNullForEmptyRegistrarDirections(
            List<CollectionMember<RegistrarDirection>> collectionMembers) {
        if (collectionMembers == null || collectionMembers.isEmpty()) {
            return null;
        }
        return collectionMembers;
    }

    private List<CollectionMember<ChangeOfRepresentative>> getNullForEmptyRepresentatives(
            List<CollectionMember<ChangeOfRepresentative>> collectionMembers) {
        if (collectionMembers == null || collectionMembers.isEmpty()) {
            return null;
        }
        return collectionMembers;
    }

    private List<CollectionMember<HandoffReason>> getHandoffReasonList(
            CaseData caseData) {
        List<CollectionMember<HandoffReason>> collectionMembers = caseData.getBoHandoffReasonList();
        if (collectionMembers == null || collectionMembers.isEmpty()
                || NO.equals(caseData.getCaseHandedOffToLegacySite())) {
            return Collections.emptyList();
        }
        return collectionMembers;
    }

    private LocalDateTime getLastModifiedDate(String eventId, LocalDateTime lastModifiedDateForDormant) {
        boolean shouldSetDate = EXCLUDED_EVENT_LIST.stream().noneMatch(s -> s.equals(eventId));
        if (shouldSetDate) {
            return LocalDateTime.now(ZoneOffset.UTC);
        }
        return lastModifiedDateForDormant;
    }

    private List<CollectionMember<AliasName>> getSolsDeceasedAliasNamesList(CaseData caseData) {

        List<CollectionMember<AliasName>> deceasedAliasNames = new ArrayList<>();
        if (caseData.getDeceasedAliasFirstNameOnWill() != null && caseData.getDeceasedAliasLastNameOnWill() != null) {
            deceasedAliasNames.add(new CollectionMember<>(null, AliasName.builder()
                    .solsAliasname(caseData.getDeceasedAliasFirstNameOnWill() + " "
                            + caseData.getDeceasedAliasLastNameOnWill()).build()));
        }
        if (caseData.getDeceasedAliasNameList() != null) {
            deceasedAliasNames.addAll(caseData.getDeceasedAliasNameList()
                    .stream()
                    .map(CollectionMember::getValue)
                    .map(this::buildDeceasedAliasNameExecutor)
                    .map(alias -> new CollectionMember<>(null, alias))
                    .toList());
        }
        if (caseData.getSolsDeceasedAliasNamesList() != null) {
            deceasedAliasNames.addAll(caseData.getSolsDeceasedAliasNamesList());
        }
        Set<String> seenAliasNames = new HashSet<>();
        return deceasedAliasNames.stream()
                .filter(aliasMember -> seenAliasNames.add(aliasMember.getValue().getSolsAliasname()))
                .collect(Collectors.toList());
    }

    private void updateCaseBuilderForCaseworkerNames(ResponseCaseDataBuilder<?, ?> builder,
                                                     Optional<UserInfo> caseworkerInfo) {
        caseworkerInfo.ifPresent(u -> {
            builder.lastModifiedCaseworkerForenames(u.getGivenName());
            builder.lastModifiedCaseworkerSurname(u.getFamilyName());
        });
    }

    private List<CollectionMember<CitizenResponse>> getCitizenResponsesList(CaseData caseData) {
        if (caseData.getCitizenResponses() == null) {
            caseData.setCitizenResponses(Arrays.asList(
                    buildCitizenResponse(caseData.getCitizenResponse())));
        } else {
            caseData.getCitizenResponses().add(buildCitizenResponse(caseData.getCitizenResponse()));
        }
        return caseData.getCitizenResponses();
    }

    private CollectionMember<CitizenResponse> buildCitizenResponse(String response) {
        return new CollectionMember<>(null, CitizenResponse.builder()
                .response(response)
                .submittedDate(LocalDateTime.now())
                .build());
    }

    private List<CollectionMember<UploadDocument>> addCitizenUploadDocument(CaseData caseData) {
        List<CollectionMember<UploadDocument>> currentUploads = caseData.getBoDocumentsUploaded();
        if (currentUploads == null) {
            currentUploads = new ArrayList<>();
        }
        List<CollectionMember<UploadDocument>> uploadedDocs = caseData.getCitizenDocumentsUploaded();
        if (uploadedDocs != null) {
            currentUploads.addAll(uploadedDocs);
        }
        return currentUploads;
    }

    private void resetRequestInformationFields(ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {
        responseCaseDataBuilder
                .informationNeeded(null)
                .informationNeededByPost(null)
                .boStopDetails(null)
                .boStopDetailsDeclarationParagraph(null);
    }

    public void defaultInformationRequestSwitch(CallbackRequest callbackRequest,
                                                ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {
        final var caseDetails = callbackRequest.getCaseDetails();
        final var caseData = caseDetails.getData();
        if (BO_CASE_STOPPED.getId().equalsIgnoreCase(caseDetails.getState())
            && CHANNEL_CHOICE_DIGITAL.equalsIgnoreCase(caseData.getChannelChoice())
            && PERSONAL.equals(caseData.getApplicationType())) {
            responseCaseDataBuilder.informationNeededByPostSwitch(YES);
        } else {
            responseCaseDataBuilder.informationNeededByPostSwitch(NO);
        }
    }
}
