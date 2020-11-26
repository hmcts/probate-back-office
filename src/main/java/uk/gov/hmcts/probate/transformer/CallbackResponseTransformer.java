package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.ExecutorsApplyingNotificationService;
import uk.gov.hmcts.probate.service.SolicitorExecutorService;
import uk.gov.hmcts.probate.transformer.assembly.AssembleLetterTransformer;
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

    private static final String CASE_TYPE_DEFAULT = GRANT_OF_PROBATE_NAME;
    private final DocumentTransformer documentTransformer;
    private final AssembleLetterTransformer assembleLetterTransformer;
    private final ExecutorsApplyingNotificationService executorsApplyingNotificationService;
    private final SolicitorExecutorService solicitorExecutorService;
    private final ReprintTransformer reprintTransformer;
    private final SolicitorLegalStatementNextStepsTransformer solicitorLegalStatementNextStepsDefaulter;

    private static final DocumentType[] LEGAL_STATEMENTS = {LEGAL_STATEMENT_PROBATE, LEGAL_STATEMENT_INTESTACY,
        LEGAL_STATEMENT_ADMON};
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = SOLICITOR;
    private static final String DEFAULT_REGISTRY_LOCATION = CTSC;
    private static final String DEFAULT_IHT_FORM_ID = "IHT205";
    private static final String CASE_CREATED = "CaseCreated";
    private static final String CASE_PRINTED = "CasePrinted";
    private static final String READY_FOR_EXAMINATION = "BOReadyForExamination";
    private static final String EXAMINING = "BOExamining";
    private static final String SOL_AS_EXEC_ID = "solicitor";

    public static final String ANSWER_YES = "Yes";
    public static final String ANSWER_NO = "No";
    public static final String QA_CASE_STATE = "BOCaseQA";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String OTHER = "other";

    public static final String EXCEPTION_RECORD_CASE_TYPE_ID = "GrantOfRepresentation";
    public static final String EXCEPTION_RECORD_EVENT_ID = "createCaseFromBulkScan";
    public static final RegistryLocation EXCEPTION_RECORD_REGISTRY_LOCATION = RegistryLocation.CTSC;

    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public CallbackResponse transformWithConditionalStateChange(CallbackRequest callbackRequest, Optional<String> newState) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .state(newState.orElse(null))
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse grantRaised(CallbackRequest callbackRequest, List<Document> documents, String letterId) {
        CaseDetails caseDetails = callbackRequest.getCaseDetails();
        CaseData caseData = caseDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, true));

        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(caseDetails, false);

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

        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);

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
        ResponseCaseData responseCaseData = getResponseCaseData(caseDetails, false)
                .executorsApplyingNotifications(exec)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse addInformationRequestDocuments(CallbackRequest callbackRequest, List<Document> documents,
                                                           List<String> letterIds) {
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);

        if (documentTransformer.hasDocumentWithType(documents, SENT_EMAIL)) {
            responseCaseDataBuilder.boEmailRequestInfoNotificationRequested(
                    callbackRequest.getCaseDetails().getData().getBoEmailRequestInfoNotification());
        }

        if (documentTransformer.hasDocumentWithType(documents, SOT_INFORMATION_REQUEST) && !letterIds.isEmpty()) {
            letterIds.forEach(letterId -> {
                CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, SOT_INFORMATION_REQUEST.getTemplateName());
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
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);

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
                DocumentType[] documentTypes = {DIGITAL_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
                    WELSH_DIGITAL_GRANT_REISSUE, WELSH_ADMON_WILL_GRANT_REISSUE, WELSH_INTESTACY_GRANT_REISSUE};
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
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        
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
                DocumentType[] documentTypes = {DIGITAL_GRANT_REISSUE, ADMON_WILL_GRANT_REISSUE, INTESTACY_GRANT_REISSUE,
                    WELSH_DIGITAL_GRANT_REISSUE, WELSH_ADMON_WILL_GRANT_REISSUE, WELSH_INTESTACY_GRANT_REISSUE,
                    STATEMENT_OF_TRUTH, WELSH_STATEMENT_OF_TRUTH, DocumentType.OTHER};
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
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse addMatches(CallbackRequest callbackRequest, List<CaseMatch> newMatches) {
        List<CollectionMember<CaseMatch>> storedMatches = callbackRequest.getCaseDetails().getData().getCaseMatches();

        // Removing case matches that have been already added
        storedMatches.stream()
                .map(CollectionMember::getValue).forEach(newMatches::remove);

        storedMatches.addAll(newMatches.stream().map(CollectionMember::new).collect(Collectors.toList()));

        storedMatches.sort(Comparator.comparingInt(m -> ofNullable(m.getValue().getValid()).orElse("").length()));

        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse selectForQA(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        if (callbackRequest.getCaseDetails().getData().getBoExaminationChecklistRequestQA().equalsIgnoreCase(ANSWER_YES)) {
            responseCaseDataBuilder.state(QA_CASE_STATE);
        }
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse resolveStop(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
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

    public CallbackResponse setApplicantFieldsForSolsApplyAsExec(CallbackRequest callbackRequest) {
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        responseCaseDataBuilder.solicitorIsMainApplicant(caseData.solicitorIsMainApplicant());
        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformForSolicitorComplete(CallbackRequest callbackRequest, FeeServiceResponse feeServiceResponse) {
        String feeForNonUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForNonUkCopies());
        String feeForUkCopies = transformMoneyGBPToString(feeServiceResponse.getFeeForUkCopies());
        String applicationFee = transformMoneyGBPToString(feeServiceResponse.getApplicationFee());
        String totalFee = transformMoneyGBPToString(feeServiceResponse.getTotal());

        String applicationSubmittedDate = dateTimeFormatter.format(LocalDate.now());
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .feeForNonUkCopies(feeForNonUkCopies)
                .feeForUkCopies(feeForUkCopies)
                .applicationFee(applicationFee)
                .totalFee(totalFee)
                .applicationSubmittedDate(applicationSubmittedDate)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transform(CallbackRequest callbackRequest, Document document, String caseType) {
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        responseCaseDataBuilder.solsSOTNeedToUpdate(null);

        if (Arrays.asList(LEGAL_STATEMENTS).contains(document.getDocumentType())) {
            responseCaseDataBuilder.solsLegalStatementDocument(document.getDocumentLink());
            responseCaseDataBuilder.caseType(caseType);
        }

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transform(CallbackRequest callbackRequest) {
        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), false)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCase(CallbackRequest callbackRequest) {

        boolean transform = doTransform(callbackRequest);

        ResponseCaseData responseCaseData = getResponseCaseData(callbackRequest.getCaseDetails(), transform)
                .build();

        return transformResponse(responseCaseData);
    }

    public CallbackResponse transformCaseForLetter(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), doTransform);
        assembleLetterTransformer.setupAllLetterParagraphDetails(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForLetter(CallbackRequest callbackRequest, List<Document> documents, String letterId) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        boolean doTransform = doTransform(callbackRequest);
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document, false));
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), doTransform);

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

        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), doTransform);
        responseCaseDataBuilder.previewLink(letterPreview.getDocumentLink());

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForReprint(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), doTransform);
        reprintTransformer.transformReprintDocuments(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CallbackResponse transformCaseForSolicitorLegalStatementRegeneration(CallbackRequest callbackRequest) {
        boolean doTransform = doTransform(callbackRequest);
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), doTransform);
        solicitorLegalStatementNextStepsDefaulter.transformLegalStatmentAmendStates(callbackRequest.getCaseDetails(), responseCaseDataBuilder);

        return transformResponse(responseCaseDataBuilder.build());
    }

    private boolean doTransform(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();
        return caseData.getApplicationType() == ApplicationType.SOLICITOR
                && caseData.getRecordId() == null
                && !caseData.getPaperForm().equalsIgnoreCase(ANSWER_YES);

    }

    public CallbackResponse paperForm(CallbackRequest callbackRequest, Document document) {

        if (document != null) {
            documentTransformer.addDocument(callbackRequest, document, false);
        }
        ResponseCaseDataBuilder responseCaseDataBuilder = getResponseCaseData(callbackRequest.getCaseDetails(), false);
        if (callbackRequest.getCaseDetails().getData().getIhtReferenceNumber() != null) {
            if (!callbackRequest.getCaseDetails().getData().getIhtReferenceNumber().isEmpty()) {
                responseCaseDataBuilder.ihtFormId(DEFAULT_IHT_FORM_ID);
            }
        }
        getCaseCreatorResponseCaseBuilder(callbackRequest.getCaseDetails().getData(), responseCaseDataBuilder);
        responseCaseDataBuilder.probateNotificationsGenerated(callbackRequest.getCaseDetails().getData().getProbateNotificationsGenerated());
        
        return transformResponse(responseCaseDataBuilder.build());
    }

    private CallbackResponse transformResponse(ResponseCaseData responseCaseData) {
        return CallbackResponse.builder().data(responseCaseData).build();
    }

    private ResponseCaseDataBuilder getResponseCaseData(CaseDetails caseDetails, boolean transform) {
        CaseData caseData = caseDetails.getData();

        ResponseCaseDataBuilder builder = ResponseCaseData.builder()
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
                .languagePreferenceWelsh(caseData.getLanguagePreferenceWelsh())
                .caseType(caseData.getCaseType())
                .solsSolicitorIsExec(caseData.getSolsSolicitorIsExec())
                .solsSolicitorIsMainApplicant(caseData.getSolsSolicitorIsMainApplicant())
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
                .grantAwaitingDocumentationNotificationDate(ofNullable(caseData.getGrantAwaitingDocumentationNotificationDate())
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
                .registrySequenceNumber(caseData.getRegistrySequenceNumber());

        if (transform) {
            updateCaseBuilderForTransformCase(caseData, builder);

        } else {

            updateCaseBuilder(caseData, builder);
        }

        builder = getCaseCreatorResponseCaseBuilder(caseData, builder);


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
        return INTESTACY.getName().equals(caseData.getCaseType()) || GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType());
    }

    private boolean isSolsEmailSet(CaseData caseData) {
        return SOLICITOR.equals(caseData.getApplicationType()) && StringUtils.isNotBlank(caseData.getSolsSolicitorEmail());
    }

    private boolean isPAEmailSet(CaseData caseData) {
        return PERSONAL.equals(caseData.getApplicationType()) && StringUtils.isNotBlank(caseData.getPrimaryApplicantEmailAddress());
    }

    private boolean isCodicil(CaseData caseData) {
        return YES.equals(caseData.getWillHasCodicils());
    }

    private boolean isSolicitorExecutor(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsExec());
    }

    private boolean isSolicitorMainApplicant(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsMainApplicant());
    }

    private ResponseCaseDataBuilder getCaseCreatorResponseCaseBuilder(CaseData caseData, ResponseCaseDataBuilder builder) {

        builder
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
                .wholeBloodUnclesAndAuntsSurvivedOverEighteen(caseData.getWholeBloodUnclesAndAuntsSurvivedOverEighteen())
                .wholeBloodUnclesAndAuntsSurvivedUnderEighteen(caseData.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen())
                .wholeBloodUnclesAndAuntsDied(caseData.getWholeBloodUnclesAndAuntsDied())
                .wholeBloodUnclesAndAuntsDiedOverEighteen(caseData.getWholeBloodUnclesAndAuntsDiedOverEighteen())
                .wholeBloodUnclesAndAuntsDiedUnderEighteen(caseData.getWholeBloodUnclesAndAuntsDiedUnderEighteen())
                .wholeBloodCousinsSurvived(caseData.getWholeBloodCousinsSurvived())
                .wholeBloodCousinsSurvivedOverEighteen(caseData.getWholeBloodCousinsSurvivedOverEighteen())
                .wholeBloodCousinsSurvivedUnderEighteen(caseData.getWholeBloodCousinsSurvivedUnderEighteen())
                .halfBloodUnclesAndAuntsSurvived(caseData.getHalfBloodUnclesAndAuntsSurvived())
                .halfBloodUnclesAndAuntsSurvivedOverEighteen(caseData.getHalfBloodUnclesAndAuntsSurvivedOverEighteen())
                .halfBloodUnclesAndAuntsSurvivedUnderEighteen(caseData.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen())
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
                .grantAwaitingDocumentationNotificationDate(ofNullable(caseData.getGrantAwaitingDocumentationNotificationDate())
                    .map(dateTimeFormatter::format).orElse(null))
                .grantAwaitingDocumentatioNotificationSent(caseData.getGrantAwaitingDocumentatioNotificationSent())
                .reprintDocument(caseData.getReprintDocument())
                .reprintNumberOfCopies(caseData.getReprintNumberOfCopies())
                .solsAmendLegalStatmentSelect(caseData.getSolsAmendLegalStatmentSelect());


        if (YES.equals(caseData.getSolsSolicitorIsMainApplicant())) {
            builder
                    .primaryApplicantSecondPhoneNumber(null)
                    .primaryApplicantRelationshipToDeceased(null);
        }

        if(YES.equals(caseData.getDeceasedDomicileInEngWales())) {
            builder
                    .domicilityCountry(null);
        }

        if(!GRANT_TYPE_PROBATE.equals(caseData.getSolsWillType())) {
            builder
                    .willDispose(null)
                    .englishWill(null)
                    .appointExec(null)
                    .appointExecByDuties(null)
                    .appointExecNo(null);
        } else if(YES.equals(caseData.getEnglishWill())) {
            builder
                    .appointExecByDuties(null);
        } else if(NO.equals(caseData.getEnglishWill())) {
            builder
                    .appointExec(null)
                    .appointExecNo(null);
        }

        return builder;
    }

    private void updateCaseBuilder(CaseData caseData, ResponseCaseDataBuilder builder) {
        builder
                .primaryApplicantAlias(caseData.getPrimaryApplicantAlias())
                .additionalExecutorsNotApplying(caseData.getAdditionalExecutorsNotApplying())
                .solsAdditionalExecutorList(caseData.getSolsAdditionalExecutorList());

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

        if (isSolicitorExecutor(caseData)) {
            if (isSolicitorMainApplicant(caseData)) {
                builder
                        .primaryApplicantForenames(caseData.getSolsSOTForenames())
                        .primaryApplicantSurname(caseData.getSolsSOTSurname())
                        .primaryApplicantPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
                        .primaryApplicantEmailAddress(caseData.getSolsSolicitorEmail())
                        .primaryApplicantAddress(caseData.getSolsSolicitorAddress())
                        .primaryApplicantAlias(null)
                        .primaryApplicantHasAlias(NO)
                        .primaryApplicantIsApplying(YES)
                        .solsSolicitorIsApplying(YES)
                        .solsSolicitorNotApplyingReason(null)
                        .solsPrimaryExecutorNotApplyingReason(null);
            } else if (YES.equals(caseData.getSolsSolicitorIsApplying()) || NO.equals(caseData.getSolsSolicitorIsApplying())) {
                if(getSolsSOTName(caseData.getSolsSOTForenames(), caseData.getSolsSOTSurname()).equals(caseData.getPrimaryApplicantFullName())) {
                    builder
                            .primaryApplicantForenames(null)
                            .primaryApplicantSurname(null)
                            .primaryApplicantPhoneNumber(null)
                            .primaryApplicantEmailAddress(null)
                            .primaryApplicantAddress(null)
                            .primaryApplicantAlias(null)
                            .primaryApplicantHasAlias(null)
                            .primaryApplicantIsApplying(null)
                            .solsPrimaryExecutorNotApplyingReason(null);
                } else if(YES.equals(caseData.getSolsSolicitorIsApplying())) {
                    builder
                            .solsPrimaryExecutorNotApplyingReason(null);
                }
            } else {
                builder
                        .solsPrimaryExecutorNotApplyingReason(null);
            }
        } else {
            builder
                    .solsSolicitorIsMainApplicant(null)
                    .solsSolicitorIsApplying(null)
                    .solsSolicitorNotApplyingReason(null)
                    .primaryApplicantAlias(caseData.getPrimaryApplicantAlias());
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


        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = new ArrayList<>();

        if (caseData.getAdditionalExecutorsApplying() != null) {
            execsApplying = mapApplyingAdditionalExecutors(caseData);
        }

        if (caseData.getAdditionalExecutorsNotApplying() != null) {
            execsNotApplying = caseData.getAdditionalExecutorsNotApplying();
        }

        if (YES.equals(caseData.getSolsSolicitorIsExec()) && !isSolicitorMainApplicant(caseData)) {
            if (YES.equals(caseData.getSolsSolicitorIsApplying())) {
                execsApplying = solicitorExecutorService.updateSolicitorApplyingExecutor(caseData, execsApplying);
                execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
            } else if (NO.equals(caseData.getSolsSolicitorIsApplying())) {
                execsNotApplying = solicitorExecutorService.updateSolicitorNotApplyingExecutor(caseData, execsNotApplying);
                execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
            }
        }

        if (NO.equals(caseData.getSolsSolicitorIsExec())) {
            execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
            execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
        }

        if (isSolicitorMainApplicant(caseData)) {
            execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
            execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
        }

        builder
                .additionalExecutorsApplying(execsApplying)
                .additionalExecutorsNotApplying(execsNotApplying)
                .solsAdditionalExecutorList(caseData.getSolsAdditionalExecutorList())
                .solsExecutorAliasNames(caseData.getSolsExecutorAliasNames());

        if (GRANT_TYPE_PROBATE.equals(caseData.getSolsWillType()) && caseData.getSolsFeeAccountNumber() == null) {
            List<CollectionMember<AdditionalExecutor>> solsExecutors = caseData.getSolsAdditionalExecutorList();
            solsExecutors = mapSolsAdditionalExecutors(caseData, solsExecutors);

            builder.solsAdditionalExecutorList(solsExecutors);

            if (isSolicitorExecutor(caseData) && !isSolicitorMainApplicant(caseData)) {
                builder.otherExecutorExists(YES);
            }
        }

        if (caseData.getSolsAdditionalExecutorList() != null) {
            if (!caseData.getSolsAdditionalExecutorList().isEmpty()) {
                builder
                        .additionalExecutorsApplying(EMPTY_LIST)
                        .additionalExecutorsNotApplying(EMPTY_LIST);
            }
        }
    }

    private void updateCaseBuilderForTransformCase(CaseData caseData, ResponseCaseDataBuilder builder) {
        builder
                .ihtReferenceNumber(caseData.getIhtReferenceNumber())
                .primaryApplicantAlias(caseData.getPrimaryApplicantAlias())
                .solsDeceasedAliasNamesList(caseData.getSolsDeceasedAliasNamesList());

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

        if (isSolicitorExecutor(caseData)) {
            if (isSolicitorMainApplicant(caseData)) {
                builder
                        .primaryApplicantForenames(caseData.getSolsSOTForenames())
                        .primaryApplicantSurname(caseData.getSolsSOTSurname())
                        .primaryApplicantPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
                        .primaryApplicantEmailAddress(caseData.getSolsSolicitorEmail())
                        .primaryApplicantAddress(caseData.getSolsSolicitorAddress())
                        .primaryApplicantAlias(null)
                        .primaryApplicantHasAlias(NO)
                        .primaryApplicantIsApplying(YES)
                        .solsSolicitorIsApplying(YES)
                        .solsSolicitorNotApplyingReason(null)
                        .solsPrimaryExecutorNotApplyingReason(null);
            } else if (YES.equals(caseData.getSolsSolicitorIsApplying()) || NO.equals(caseData.getSolsSolicitorIsApplying())) {
                if(getSolsSOTName(caseData.getSolsSOTForenames(), caseData.getSolsSOTSurname()).equals(caseData.getPrimaryApplicantFullName())) {
                    builder
                            .primaryApplicantForenames(null)
                            .primaryApplicantSurname(null)
                            .primaryApplicantPhoneNumber(null)
                            .primaryApplicantEmailAddress(null)
                            .primaryApplicantAddress(null)
                            .primaryApplicantAlias(null)
                            .primaryApplicantHasAlias(null)
                            .primaryApplicantIsApplying(null)
                            .solsPrimaryExecutorNotApplyingReason(null);
                } else if(YES.equals(caseData.getSolsSolicitorIsApplying())) {
                    builder
                            .solsPrimaryExecutorNotApplyingReason(null);
                }
            } else {
                builder
                        .solsPrimaryExecutorNotApplyingReason(null);
            }
        } else {
            builder
                    .solsSolicitorIsMainApplicant(null)
                    .solsSolicitorIsApplying(null)
                    .solsSolicitorNotApplyingReason(null)
                    .primaryApplicantAlias(caseData.getPrimaryApplicantAlias());
        }

        if (caseData.getCaseType() == null) {
            builder
                    .caseType(CASE_TYPE_DEFAULT);
        }

        if (caseData.getDateOfDeathType() == null) {
            builder
                    .dateOfDeathType(DATE_OF_DEATH_TYPE_DEFAULT);
        }

        if (isSolicitorMainApplicant(caseData)) {
            builder
                    .primaryApplicantAlias(null);
        } else if (caseData.getSolsExecutorAliasNames() != null) {
                builder
                        .primaryApplicantAlias(caseData.getSolsExecutorAliasNames())
                        .solsExecutorAliasNames(null);
        } else {
                builder
                        .primaryApplicantAlias(caseData.getPrimaryApplicantAlias())
                        .solsExecutorAliasNames(caseData.getSolsExecutorAliasNames());
        }

        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();
        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = new ArrayList<>();

        if (CollectionUtils.isEmpty(caseData.getSolsAdditionalExecutorList())) {
            if (YES.equals(caseData.getSolsSolicitorIsExec())) {
                if (caseData.getAdditionalExecutorsApplying() != null) {
                    execsApplying = mapApplyingAdditionalExecutors(caseData);
                }

                if (caseData.getAdditionalExecutorsNotApplying() != null) {
                    execsNotApplying = caseData.getAdditionalExecutorsNotApplying();
                }

                if (YES.equals(caseData.getSolsSolicitorIsExec()) && !isSolicitorMainApplicant(caseData)) {
                    if (YES.equals(caseData.getSolsSolicitorIsApplying())) {
                        execsApplying = solicitorExecutorService.updateSolicitorApplyingExecutor(caseData, execsApplying);
                        execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
                    } else if (NO.equals(caseData.getSolsSolicitorIsApplying())) {
                        execsNotApplying = solicitorExecutorService.updateSolicitorNotApplyingExecutor(caseData, execsNotApplying);
                        execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
                    }
                }

                if (NO.equals(caseData.getSolsSolicitorIsExec())) {
                    execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
                    execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
                }

                if (isSolicitorMainApplicant(caseData)) {
                    execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
                    execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
                }

                builder
                        .additionalExecutorsApplying(execsApplying)
                        .additionalExecutorsNotApplying(execsNotApplying);
            } else {
                builder
                        .additionalExecutorsApplying(caseData.getAdditionalExecutorsApplying())
                        .additionalExecutorsNotApplying(caseData.getAdditionalExecutorsNotApplying());
            }
        } else {
            List<CollectionMember<AdditionalExecutorApplying>> applyingExec = new ArrayList<>();
            List<CollectionMember<AdditionalExecutorNotApplying>> notApplyingExec = new ArrayList<>();

            for (CollectionMember<AdditionalExecutor> additionalExec : caseData.getSolsAdditionalExecutorList()) {
                if (ANSWER_YES.equalsIgnoreCase(additionalExec.getValue().getAdditionalApplying())) {
                    applyingExec.add( new CollectionMember<>(additionalExec.getId(), buildApplyingAdditionalExecutor(additionalExec.getValue())));
                } else if (ANSWER_NO.equalsIgnoreCase(additionalExec.getValue().getAdditionalApplying())) {
                    notApplyingExec.add( new CollectionMember<>(additionalExec.getId(), buildNotApplyingAdditionalExecutor(additionalExec.getValue())));
                }
            }

            builder
                    .additionalExecutorsApplying(applyingExec)
                    .additionalExecutorsNotApplying(notApplyingExec)
                    .solsAdditionalExecutorList(EMPTY_LIST);
        }
    }

    private AdditionalExecutorApplying buildApplyingAdditionalExecutor(AdditionalExecutor additionalExecutorApplying) {
        return AdditionalExecutorApplying.builder()
                .applyingExecutorName(additionalExecutorApplying.getAdditionalExecForenames()
                        + " " + additionalExecutorApplying.getAdditionalExecLastname())
                .applyingExecutorPhoneNumber(null)
                .applyingExecutorEmail(null)
                .applyingExecutorAddress(additionalExecutorApplying.getAdditionalExecAddress())
                .applyingExecutorOtherNames(additionalExecutorApplying.getAdditionalExecAliasNameOnWill())
                .build();
    }

    private List<CollectionMember<AdditionalExecutorApplying>> mapApplyingAdditionalExecutors(CaseData caseData) {
        if (caseData.getAdditionalExecutorsApplying() != null) {
            return caseData.getAdditionalExecutorsApplying()
                    .stream()
                    .map(this::buildApplyingAdditionalExecutors)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private CollectionMember<AdditionalExecutorApplying> buildApplyingAdditionalExecutors(CollectionMember<AdditionalExecutorApplying> additionalExecutorApplying) {
        if (additionalExecutorApplying.getValue().getApplyingExecutorName() == null) {
            AdditionalExecutorApplying newExec = additionalExecutorApplying.getValue();
            newExec = AdditionalExecutorApplying.builder()
                    .applyingExecutorName(newExec.getApplyingExecutorFirstName()
                            + " " + newExec.getApplyingExecutorLastName())
                    .applyingExecutorPhoneNumber(newExec.getApplyingExecutorPhoneNumber())
                    .applyingExecutorEmail(newExec.getApplyingExecutorEmail())
                    .applyingExecutorAddress(newExec.getApplyingExecutorAddress())
                    .applyingExecutorOtherNames(newExec.getApplyingExecutorOtherNames())
                    .applyingExecutorOtherNamesReason(newExec.getApplyingExecutorOtherNamesReason())
                    .applyingExecutorOtherReason(newExec.getApplyingExecutorOtherReason())
                    .build();

            return new CollectionMember<>(additionalExecutorApplying.getId(), newExec);
        }
        return additionalExecutorApplying;
    }

    private List<CollectionMember<AdditionalExecutor>> mapSolsAdditionalExecutors(CaseData caseData, List<CollectionMember<AdditionalExecutor>> execs) {
        List<CollectionMember<AdditionalExecutor>> updatedExecs = new ArrayList<>();

        if (execs != null && !execs.isEmpty()) {
            updatedExecs.addAll(execs);
        }

        if (updatedExecs.stream().anyMatch(exec -> SOL_AS_EXEC_ID.equalsIgnoreCase(exec.getId()))) {
            return updatedExecs;
        }

        if (YES.equals(caseData.getSolsSolicitorIsExec()) && !isSolicitorMainApplicant(caseData)) {
            if (YES.equals(caseData.getSolsSolicitorIsApplying())) {
                updatedExecs = solicitorExecutorService.addSolicitorApplyingExecutor(caseData, updatedExecs);
            } else if (NO.equals(caseData.getSolsSolicitorIsApplying())) {
                updatedExecs = solicitorExecutorService.addSolicitorNotApplyingExecutor(caseData, updatedExecs);
            }
        }

        return updatedExecs;
    }

    private AliasName buildDeceasedAliasNameExecutor(ProbateAliasName aliasNames) {
        return AliasName.builder()
                .solsAliasname(aliasNames.getForenames() + " " + aliasNames.getLastName())
                .build();
    }

    private AdditionalExecutorNotApplying buildNotApplyingAdditionalExecutor(AdditionalExecutor additionalExecutorNotApplying) {
        return AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(additionalExecutorNotApplying.getAdditionalExecForenames()
                        + " " + additionalExecutorNotApplying.getAdditionalExecLastname())
                .notApplyingExecutorReason(additionalExecutorNotApplying.getAdditionalExecReasonNotApplying())
                .notApplyingExecutorNameOnWill(additionalExecutorNotApplying.getAdditionalExecAliasNameOnWill())
                .build();
    }

    private String getOtherExecutorExists(CaseData caseData) {
        if (PERSONAL.equals(caseData.getApplicationType())) {
            return caseData.getAdditionalExecutorsApplying() == null || caseData.getAdditionalExecutorsApplying().isEmpty()
                    ? ANSWER_NO : ANSWER_YES;
        } else {
            return caseData.getOtherExecutorExists();
        }
    }

    private String getPrimaryApplicantHasAlias(CaseData caseData) {
        if (PERSONAL.equals(caseData.getApplicationType())) {
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

    public CaseCreationDetails bulkScanGrantOfRepresentationCaseTransform(GrantOfRepresentationData grantOfRepresentationData) {

        if (grantOfRepresentationData.getApplicationType() == null) {
            grantOfRepresentationData.setApplicationType(uk.gov.hmcts.reform.probate.model.cases.ApplicationType.PERSONAL);
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
                eventId(EXCEPTION_RECORD_EVENT_ID).caseData(grantOfRepresentationData).caseTypeId(EXCEPTION_RECORD_CASE_TYPE_ID).build();
    }
}
