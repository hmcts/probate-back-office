package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.ExecutorsApplyingNotificationService;
import uk.gov.hmcts.probate.service.solicitorexecutor.FormattingService;
import uk.gov.hmcts.probate.service.tasklist.TaskListUpdateService;
import uk.gov.hmcts.probate.transformer.assembly.AssembleLetterTransformer;
import uk.gov.hmcts.probate.transformer.reset.ResetResponseCaseDataTransformer;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.DATE_OF_DEATH_TYPE_DEFAULT;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.ASSEMBLED_LETTER;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_STOPPED;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.GRANT_RAISED;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.DocumentType.SENT_EMAIL;
import static uk.gov.hmcts.probate.model.DocumentType.SOT_INFORMATION_REQUEST;
import static uk.gov.hmcts.probate.model.DocumentType.STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_INTESTACY_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.WELSH_STATEMENT_OF_TRUTH;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.INTESTACY;

@Component
@RequiredArgsConstructor
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
    private static final String DEFAULT_IHT_FORM_ID = "IHT205";
    private static final String CASE_CREATED = "CaseCreated";
    private static final String CASE_PRINTED = "CasePrinted";
    private static final String READY_FOR_EXAMINATION = "BOReadyForExamination";
    private static final String EXAMINING = "BOExamining";

    public static final String SCHEMA_VERSION = "2.0.0"; // Is set when Solicitor completes

    private static final String SOL_AS_EXEC_ID = "solicitor";
    private final DocumentTransformer documentTransformer;
    private final AssembleLetterTransformer assembleLetterTransformer;
    private final ExecutorsApplyingNotificationService executorsApplyingNotificationService;
    private final ReprintTransformer reprintTransformer;
    private final SolicitorLegalStatementNextStepsTransformer solicitorLegalStatementNextStepsDefaulter;
    private final ExecutorsTransformer solicitorExecutorTransformer;
    private final ResetResponseCaseDataTransformer resetResponseCaseDataTransformer;
    private final TaskListUpdateService taskListUpdateService;
    private final CaseDataTransformer caseDataTransformer;

    public CallbackResponse updateTaskList(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(),
            true, false);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest,
                                                                Optional<String> newState, boolean nullify) {
        final CaseDetails cd = callbackRequest.getCaseDetails();
        // set here to ensure tasklist html is correctly generated
        cd.setState(newState.orElse(null));

        ResponseCaseData responseCaseData = getResponseCaseData(cd, false, nullify)
                // set here again to make life easier mocking
                .state(newState.orElse(null))
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse grantRaised(CallbackRequest callbackRequest, List<Document> documents, String letterId) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, true));

        ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(caseDetails, false, false);

        if (documentTransformer.hasDocumentWithType(documents, GRANT_RAISED) && letterId != null) {
            CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, GRANT_RAISED.getTemplateName());
            appendToBulkPrintCollection(bulkPrint, caseData);

            responseCaseDataBuilder
                    .bulkPrintId(caseData.getBulkPrintId())
                    .build();
        }

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse caseStopped(CallbackRequest callbackRequest, List<Document> documents, String letterId) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, true));

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);

        if (documentTransformer.hasDocumentWithType(documents, CAVEAT_STOPPED) && letterId != null) {
            CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, CAVEAT_STOPPED.getTemplateName());
            appendToBulkPrintCollection(bulkPrint, caseData);

            responseCaseDataBuilder
                    .bulkPrintId(caseData.getBulkPrintId())
                    .boCaveatStopSendToBulkPrintRequested(caseData.getBoCaveatStopSendToBulkPrint())
                    .build();
        }
        responseCaseDataBuilder
                .boCaveatStopEmailNotificationRequested(caseData.getValueForCaveatStopEmailNotification())
                .boStopDetails("")
                .build();

        return transformResponse(responseCaseDataBuilder.build());
    }


    public CallbackResponse defaultRequestInformationValues(CallbackRequest callbackRequest) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();

        List<CollectionMember<ExecutorsApplyingNotification>> exec =
                executorsApplyingNotificationService.createExecutorList(caseDetails.getData());
        ResponseCaseData responseCaseData = getResponseCaseData(caseDetails, false, false)
                .executorsApplyingNotifications(exec)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse addInformationRequestDocuments(CallbackRequest callbackRequest, List<Document> documents,
                                                           List<String> letterIds) {
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);

        if (documentTransformer.hasDocumentWithType(documents, SENT_EMAIL)) {
            responseCaseDataBuilder.boEmailRequestInfoNotificationRequested(
                    callbackRequest.getCaseDetails().getData().getBoEmailRequestInfoNotification());
        }

        if (documentTransformer.hasDocumentWithType(documents, SOT_INFORMATION_REQUEST) && !letterIds.isEmpty()) {
            letterIds.forEach(letterId -> {
                CollectionMember<BulkPrint> bulkPrint =
                        buildBulkPrint(letterId, SOT_INFORMATION_REQUEST.getTemplateName());
                appendToBulkPrintCollection(bulkPrint, callbackRequest.getCaseDetails().getData());
            });
            responseCaseDataBuilder
                    .boRequestInfoSendToBulkPrintRequested(
                            callbackRequest.getCaseDetails().getData().getBoRequestInfoSendToBulkPrint())
                    .bulkPrintId(callbackRequest.getCaseDetails().getData().getBulkPrintId());
        }

        return transformResponse(responseCaseDataBuilder.build());

    }

    public CallbackResponse addDocuments(CallbackRequest callbackRequest, List<Document> documents,
                                         String letterId, String pdfSize) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        caseData.setAuthenticatedDate(LocalDate.now());

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);

        if (documents.isEmpty()) {
            responseCaseDataBuilder.boEmailDocsReceivedNotificationRequested(
                    callbackRequest.getCaseDetails().getData().getBoEmailDocsReceivedNotification());

        }
        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT)) {

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

        }
        if (documentTransformer.hasDocumentWithType(documents, SENT_EMAIL)) {
            responseCaseDataBuilder.boEmailDocsReceivedNotificationRequested(
                    callbackRequest.getCaseDetails().getData().getBoEmailDocsReceivedNotification());
        }

        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT_REISSUE)) {
            if (letterId != null) {
                var documentTypes = new DocumentType[] {
                    DIGITAL_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
                    WELSH_DIGITAL_GRANT_REISSUE, WELSH_ADMON_WILL_GRANT_REISSUE, WELSH_INTESTACY_GRANT_REISSUE
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

    public CallbackResponse addBulkPrintInformationForReprint(CallbackRequest callbackRequest, Document document,
                                                              String letterId, String pdfSize) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);

        List<Document> documents = Arrays.asList(document);
        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT)) {

            responseCaseDataBuilder
                    .bulkPrintSendLetterId(letterId)
                    .bulkPrintPdfSize(String.valueOf(pdfSize));
        }
        if (documentTransformer.hasDocumentWithType(documents, DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_DIGITAL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_ADMON_WILL_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, WELSH_INTESTACY_GRANT_REISSUE)
                || documentTransformer.hasDocumentWithType(documents, STATEMENT_OF_TRUTH)
                || documentTransformer.hasDocumentWithType(documents, WELSH_STATEMENT_OF_TRUTH)
                || documentTransformer.hasDocumentWithType(documents, DocumentType.OTHER)) {
            if (letterId != null) {
                var documentTypes = new DocumentType[] {
                    DIGITAL_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
                    WELSH_DIGITAL_GRANT_REISSUE, WELSH_ADMON_WILL_GRANT_REISSUE, WELSH_INTESTACY_GRANT_REISSUE,
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

    public CallbackResponse addSOTDocument(CallbackRequest callbackRequest, Document document) {
        documentTransformer.addDocument(callbackRequest, document, false);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addMatches(CallbackRequest callbackRequest, List<CaseMatch> newMatches) {
        List<CollectionMember<CaseMatch>> storedMatches = callbackRequest.getCaseDetails().getData().getCaseMatches();

        // Removing case matches that have been already added
        storedMatches.stream()
                .map(CollectionMember::getValue).forEach(newMatches::remove);

        storedMatches.addAll(newMatches.stream().map(CollectionMember::new).collect(Collectors.toList()));

        storedMatches.sort(Comparator.comparingInt(m -> ofNullable(m.getValue().getValid()).orElse("").length()));

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse selectForQA(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
            getResponseCaseData(callbackRequest.getCaseDetails(), false, false);
        if (ANSWER_YES.equalsIgnoreCase(callbackRequest.getCaseDetails().getData()
                .getBoExaminationChecklistRequestQA())) {
            responseCaseDataBuilder.state(QA_CASE_STATE);
        }
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse resolveStop(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);
        switch (callbackRequest.getCaseDetails().getData().getResolveStopState()) {
            case CASE_CREATED:
                responseCaseDataBuilder.state(CASE_CREATED);
                break;
            case CASE_PRINTED:
                responseCaseDataBuilder.state(CASE_PRINTED);
                break;
            case READY_FOR_EXAMINATION:
                responseCaseDataBuilder.state(READY_FOR_EXAMINATION);
                break;
            default:
                responseCaseDataBuilder.state(EXAMINING);
                break;
        }
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformForSolicitorComplete(CallbackRequest callbackRequest,
                                                          FeeServiceResponse feeServiceResponse) {

        final var feeForNonUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForNonUkCopies());
        final var feeForUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForUkCopies());
        final var applicationFee = transformMoneyGBPToString(feeServiceResponse.getApplicationFee());
        final var totalFee = transformMoneyGBPToString(feeServiceResponse.getTotal());

        final var applicationSubmittedDate = dateTimeFormatter.format(LocalDate.now());
        final var schemaVersion = getSchemaVersion(callbackRequest.getCaseDetails().getData());

        caseDataTransformer.transformCaseDataForSolicitorApplicationCompletion(callbackRequest);

        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false, false)
                // Applications are always new schema but when application becomes a case we retain a mix of schemas for
                // in-flight submitted cases, and bulk scan
                .schemaVersion(schemaVersion)
                .feeForNonUkCopies(feeForNonUkCopies)
                .feeForUkCopies(feeForUkCopies)
                .applicationFee(applicationFee)
                .totalFee(totalFee)
                .applicationSubmittedDate(applicationSubmittedDate)
                .boDocumentsUploaded(addLegalStatementDocument(callbackRequest))
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
        CallbackResponse response = transformWithConditionalStateChange(callbackRequest, newState, true);

        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);

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

    public CallbackResponse transformForSolicitorExecutorNames(CallbackRequest callbackRequest,
                                                               boolean nullParentFields) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, nullParentFields);

        solicitorExecutorTransformer.mapSolicitorExecutorFieldsToExecutorNamesLists(
                callbackRequest.getCaseDetails().getData(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, Document document, String caseType) {
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);
        responseCaseDataBuilder.solsSOTNeedToUpdate(null);

        if (Arrays.asList(LEGAL_STATEMENTS).contains(document.getDocumentType())) {
            responseCaseDataBuilder.solsLegalStatementDocument(document.getDocumentLink());
            responseCaseDataBuilder.caseType(caseType);
        }

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false, false)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCase(CallbackRequest callbackRequest) {

        boolean transform = doTransform(callbackRequest);

        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), transform, false)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCaseForLetter(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), doTransform, false);
        assembleLetterTransformer
                .setupAllLetterParagraphDetails(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForLetter(CallbackRequest callbackRequest, List<Document> documents,
                                                   String letterId) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        boolean doTransform = doTransform(callbackRequest);
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), doTransform, false);

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
                getResponseCaseData(callbackRequest.getCaseDetails(), doTransform, false);
        responseCaseDataBuilder.previewLink(letterPreview.getDocumentLink());

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForReprint(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), doTransform, false);
        reprintTransformer.transformReprintDocuments(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForSolicitorLegalStatementRegeneration(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), doTransform, false);
        solicitorLegalStatementNextStepsDefaulter
                .transformLegalStatmentAmendStates(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        transformCaseForSolicitorConfirmText(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public void transformCaseForSolicitorConfirmText(CaseDetails caseDetails, ResponseCaseDataBuilder<?, ?> builder) {
        List<CollectionMember<AdditionalExecutorApplying>> listOfApplyingExecs =
                solicitorExecutorTransformer.createCaseworkerApplyingList(caseDetails.getData());

        var plural = "";
        var believePlural = "s";
        if (listOfApplyingExecs.size() > 1) {
            plural = "s";
            believePlural = "";
        }

        var executorNames = "";
        var professionalName = caseDetails.getData().getSolsSOTName();
        var confirmSOT = "";

        if (caseDetails.getData().getSolsWillType() != null
                && caseDetails.getData().getSolsWillType().matches("WillLeft")) {
            executorNames = "The executor" + plural + " ";

            confirmSOT = "By signing the statement of truth by ticking the boxes below, I, " + professionalName
                    + " confirm the following:\n\n"
                    + "I, " + professionalName + ", have provided a copy of this application to the executor" + plural
                    + " named below.\n\n"
                    + "I, " + professionalName + ", have informed the executor"  + plural
                    + " that in signing the statement of truth I am confirming that the executor"  + plural
                    + " believe"  + believePlural + " the facts set out in this legal statement are true.\n\n"
                    + "I, " + professionalName + ", have informed the executor"   + plural
                    + " of the consequences if it should subsequently appear that the executor"  + plural
                    + " did not have an honest belief in the facts set out in the legal statement.\n\n"
                    + "I, " + professionalName + ", have been authorised by the executor"  + plural
                    + " to sign the statement of truth.\n\n"
                    + "I, " + professionalName + ", understand that proceedings for contempt of court may be brought "
                    + "against anyone who makes, or causes to be made, a false statement in a document verified by a "
                    + "statement of truth without an honest belief in its truth.\n";

            executorNames = listOfApplyingExecs.isEmpty() ? executorNames + professionalName + ": " :
                    executorNames + FormattingService.createExecsApplyingNames(listOfApplyingExecs) + ": ";
        } else {
            executorNames = "The applicant" + plural + " ";

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

            executorNames = executorNames + caseDetails.getData().getPrimaryApplicantForenames()
                    + " " + caseDetails.getData().getPrimaryApplicantSurname();
        }

        builder.solsReviewSOTConfirm(confirmSOT);
        builder.solsReviewSOTConfirmCheckbox1Names(executorNames);
        builder.solsReviewSOTConfirmCheckbox2Names(executorNames);
    }

    private boolean doTransform(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        return caseData.getApplicationType() == ApplicationType.SOLICITOR
                && caseData.getRecordId() == null
                && !caseData.getPaperForm().equalsIgnoreCase(ANSWER_YES);

    }

    public CallbackResponse paperForm(CallbackRequest callbackRequest, Document document) {

        final CaseData cd = callbackRequest.getCaseDetails().getData();
        if (SOLICITOR.equals(cd.getApplicationType())
                // We have currently applied this change to both paperform Yes and paperform No
                // && NO.equals(cd.getPaperForm())
                && GRANT_OF_PROBATE_NAME.equals(cd.getCaseType())) {
            caseDataTransformer.transformCaseDataForSolicitorApplicationCompletion(callbackRequest);
        }
        if (document != null) {
            documentTransformer.addDocument(callbackRequest, document, false);
        }
        ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder =
                getResponseCaseData(callbackRequest.getCaseDetails(), false, false);
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
                && (applicationType == null || SOLICITOR.equals(applicationType)) ? SCHEMA_VERSION : null;
    }

    private CallbackResponse transformResponse(ResponseCaseData responseCaseData) {
        return CallbackResponse.builder().data(responseCaseData).build();
    }

    private ResponseCaseDataBuilder<?, ?> getResponseCaseData(CaseDetails caseDetails, boolean transform,
                                                              boolean nullifyParentFields) {
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
            .solsAdditionalInfo(caseData.getSolsAdditionalInfo())
            .caseMatches(caseData.getCaseMatches())

            .solsSOTNeedToUpdate(caseData.getSolsSOTNeedToUpdate())
            .solsLegalStatementUpload(caseData.getSolsLegalStatementUpload())
            
            .ihtGrossValue(caseData.getIhtGrossValue())
            .ihtNetValue(caseData.getIhtNetValue())
            .deceasedDomicileInEngWales(caseData.getDeceasedDomicileInEngWales())

            .solsPaymentMethods(caseData.getSolsPaymentMethods())
            .solsFeeAccountNumber(caseData.getSolsFeeAccountNumber())

            .extraCopiesOfGrant(transformToString(caseData.getExtraCopiesOfGrant()))
            .outsideUKGrantCopies(transformToString(caseData.getOutsideUKGrantCopies()))
            .feeForNonUkCopies(transformToString(caseData.getFeeForNonUkCopies()))
            .feeForUkCopies(transformToString(caseData.getFeeForUkCopies()))
            .applicationFee(transformToString(caseData.getApplicationFee()))
            .totalFee(transformToString(caseData.getTotalFee()))

            .solsLegalStatementDocument(caseData.getSolsLegalStatementDocument())
            .casePrinted(caseData.getCasePrinted())
            .boEmailDocsReceivedNotificationRequested(caseData.getBoEmailDocsReceivedNotificationRequested())
            .boEmailGrantIssuedNotificationRequested(caseData.getBoEmailGrantIssuedNotificationRequested())
            .boEmailDocsReceivedNotification(caseData.getBoEmailDocsReceivedNotification())
            .boEmailGrantIssuedNotification(caseData.getBoEmailGrantIssuedNotification())

            .boCaseStopReasonList(caseData.getBoCaseStopReasonList())
            .boStopDetails(caseData.getBoStopDetails())

            .boDeceasedTitle(caseData.getBoDeceasedTitle())
            .boDeceasedHonours(caseData.getBoDeceasedHonours())

            .ihtFormCompletedOnline(
                caseData.getIhtFormCompletedOnline() == null && caseData.getIhtFormId() != null ? NO :
                    caseData.getIhtFormCompletedOnline())

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
            .anyOtherApplyingPartners(nullifyParentFields ? null :
                caseData.getAnyOtherApplyingPartners())
            .anyOtherApplyingPartnersTrustCorp(nullifyParentFields ? null :
                caseData.getAnyOtherApplyingPartnersTrustCorp())
            .otherPartnersApplyingAsExecutors(caseData.getOtherPartnersApplyingAsExecutors())
            .whoSharesInCompanyProfits(caseData.getWhoSharesInCompanyProfits())
            .taskList(caseData.getTaskList())
            .escalatedDate(ofNullable(caseData.getEscalatedDate())
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
            .deathRecords(caseData.getDeathRecords());

        if (transform) {
            updateCaseBuilderForTransformCase(caseData, builder);
        } else {
            updateCaseBuilder(caseData, builder);
        }

        builder = getCaseCreatorResponseCaseBuilder(caseData, builder);

        builder = taskListUpdateService.generateTaskList(caseDetails, builder);

        return builder;
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

        if (isSolsEmailSet(caseData)) {
            builder
                    .boEmailDocsReceivedNotification(ANSWER_YES)
                    .boEmailRequestInfoNotification(ANSWER_YES)
                    .boEmailGrantIssuedNotification(ANSWER_YES)
                    .boEmailGrantReissuedNotification(ANSWER_YES);
        } else {
            builder
                    .boEmailDocsReceivedNotification(ANSWER_NO)
                    .boEmailRequestInfoNotification(ANSWER_NO)
                    .boEmailGrantIssuedNotification(ANSWER_NO)
                    .boEmailGrantReissuedNotification(ANSWER_NO);
        }

        if (isPAEmailSet(caseData)) {
            builder
                    .boEmailDocsReceivedNotification(ANSWER_YES)
                    .boEmailRequestInfoNotification(ANSWER_YES)
                    .boEmailGrantIssuedNotification(ANSWER_YES)
                    .boEmailGrantReissuedNotification(ANSWER_YES);
        } else {
            builder
                    .boEmailDocsReceivedNotification(ANSWER_NO)
                    .boEmailRequestInfoNotification(ANSWER_NO)
                    .boEmailGrantIssuedNotification(ANSWER_NO)
                    .boEmailGrantReissuedNotification(ANSWER_NO);
        }

        if (!isCodicil(caseData)) {
            builder
                    .willNumberOfCodicils(null);
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

        if (shouldNullifyAliasNamesList(caseData)) {
            builder
                .solsDeceasedAliasNamesList(null)
                .deceasedAliasNamesList(null);
        } else {
            List<CollectionMember<AliasName>> deceasedAliasNames = EMPTY_LIST;
            if (caseData.getDeceasedAliasNameList() != null) {
                deceasedAliasNames = caseData.getDeceasedAliasNameList()
                        .stream()
                        .map(CollectionMember::getValue)
                        .map(this::buildDeceasedAliasNameExecutor)
                        .map(alias -> new CollectionMember<>(null, alias))
                        .collect(Collectors.toList());
            }
            if (deceasedAliasNames.isEmpty()) {
                builder
                        .solsDeceasedAliasNamesList(caseData.getSolsDeceasedAliasNamesList());
            } else {
                builder
                        .solsDeceasedAliasNamesList(deceasedAliasNames)
                        .deceasedAliasNamesList(null);
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
                .solsExecutorAliasNames(caseData.getSolsExecutorAliasNames())
                .solsDeceasedAliasNamesList(shouldNullifyAliasNamesList(caseData) ? null
                        : caseData.getSolsDeceasedAliasNamesList());

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

        if (isSolsEmailSet(caseData)) {
            builder
                    .boEmailDocsReceivedNotification(ANSWER_YES)
                    .boEmailRequestInfoNotification(ANSWER_YES)
                    .boEmailGrantIssuedNotification(ANSWER_YES)
                    .boEmailGrantReissuedNotification(ANSWER_YES);
        } else {
            builder
                    .boEmailDocsReceivedNotification(ANSWER_NO)
                    .boEmailRequestInfoNotification(ANSWER_NO)
                    .boEmailGrantIssuedNotification(ANSWER_NO)
                    .boEmailGrantReissuedNotification(ANSWER_NO);
        }

        if (!isCodicil(caseData)) {
            builder.willNumberOfCodicils(null);
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

    private boolean shouldNullifyAliasNamesList(CaseData caseData) {
        return (caseData.getDeceasedAliasNameList() == null
                || caseData.getDeceasedAliasNameList().isEmpty())
                && !YES.equals(caseData.getDeceasedAnyOtherNames());
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

        if (grantOfRepresentationData.getApplicationSubmittedDate() == null) {
            grantOfRepresentationData.setApplicationSubmittedDate(LocalDate.now());
        }

        return CaseCreationDetails.builder().<ResponseCaveatData>
                eventId(EXCEPTION_RECORD_EVENT_ID).caseData(grantOfRepresentationData)
                .caseTypeId(EXCEPTION_RECORD_CASE_TYPE_ID).build();
    }
}
