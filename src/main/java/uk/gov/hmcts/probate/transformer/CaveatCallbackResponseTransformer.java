package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData.ResponseCaveatDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_EXTENDED;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT_RAISED;

@Component
@RequiredArgsConstructor
public class CaveatCallbackResponseTransformer {

    private final DocumentTransformer documentTransformer;

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final ApplicationType DEFAULT_APPLICATION_TYPE = PERSONAL;
    public static final String DEFAULT_REGISTRY_LOCATION = "Leeds";

    public static final String EXCEPTION_RECORD_CASE_TYPE_ID = "Caveat";
    public static final String EXCEPTION_RECORD_EVENT_ID = "raiseCaveatFromBulkScan";
    public static final RegistryLocation EXCEPTION_RECORD_REGISTRY_LOCATION = RegistryLocation.CTSC;

    public CaveatCallbackResponse caveatRaised(CaveatCallbackRequest caveatCallbackRequest, List<Document> documents, String letterId) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();
        CaveatData caveatData = caveatDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(caveatCallbackRequest, document));
        ResponseCaveatDataBuilder responseCaveatDataBuilder = getResponseCaveatData(caveatDetails);

        if (documentTransformer.hasDocumentWithType(documents, CAVEAT_RAISED) && letterId != null) {
            CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, CAVEAT_RAISED.getTemplateName());
            caveatData.getBulkPrintId().add(bulkPrint);

            responseCaveatDataBuilder
                    .bulkPrintId(caveatData.getBulkPrintId())
                    .build();
        }

        if (caveatData.getApplicationType() != null) {
            responseCaveatDataBuilder
                    .applicationSubmittedDate(dateTimeFormatter.format(LocalDate.now()))
                    .paperForm(caveatData.getApplicationType().equals(SOLICITOR) ? NO : YES)
                    .build();
        } else {
            responseCaveatDataBuilder
                    .applicationSubmittedDate(dateTimeFormatter.format(LocalDate.now()))
                    .paperForm(YES)
                    .build();
        }

        return transformResponse(responseCaveatDataBuilder.build());
    }

    public CaveatCallbackResponse caveatExtendExpiry(CaveatCallbackRequest caveatCallbackRequest, List<Document> documents, String letterId) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();
        CaveatData caveatData = caveatDetails.getData();
        documents.forEach(document -> documentTransformer.addDocument(caveatCallbackRequest, document));
        ResponseCaveatDataBuilder responseCaveatDataBuilder = getResponseCaveatData(caveatDetails);

        if (documentTransformer.hasDocumentWithType(documents, CAVEAT_EXTENDED) && letterId != null) {
            CollectionMember<BulkPrint> bulkPrint = buildBulkPrint(letterId, CAVEAT_EXTENDED.getTemplateName());
            caveatData.getBulkPrintId().add(bulkPrint);

            responseCaveatDataBuilder
                .bulkPrintId(caveatData.getBulkPrintId())
                .build();
        }

        return transformResponse(responseCaveatDataBuilder.build());
    }

    public CaveatCallbackResponse withdrawn(final CaveatCallbackRequest caveatCallbackRequest, List<Document> documents, String letterId) {
        documents.forEach(document -> documentTransformer.addDocument(caveatCallbackRequest, document));
        return defaultCaveatValues(caveatCallbackRequest);
    }

    public CaveatCallbackResponse defaultCaveatValues(CaveatCallbackRequest caveatCallbackRequest) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        ResponseCaveatData responseCaveatData = getResponseCaveatData(caveatDetails)
                .caveatRaisedEmailNotificationRequested(caveatCallbackRequest.getCaseDetails().getData().getCaveatRaisedEmailNotification())
                .sendToBulkPrintRequested(caveatCallbackRequest.getCaseDetails().getData().getSendToBulkPrint())
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse generalMessage(CaveatCallbackRequest caveatCallbackRequest, Document document) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        caveatDetails.getData().getDocumentsGenerated().add(new CollectionMember<>(null, document));

        ResponseCaveatData responseCaveatData = getResponseCaveatData(caveatDetails)
                .messageContent("")
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse transformForSolicitor(CaveatCallbackRequest callbackRequest) {
        ResponseCaveatData responseCaveatData = getResponseCaveatData(callbackRequest.getCaseDetails())
                .applicationType(SOLICITOR)
                .paperForm(NO)
                .registryLocation(CTSC)
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse addMatches(CaveatCallbackRequest request, List<CaseMatch> newMatches) {
        List<CollectionMember<CaseMatch>> storedMatches = request.getCaseDetails().getData().getCaseMatches();

        // Removing case matches that have been already added
        storedMatches.stream()
                .map(CollectionMember::getValue).forEach(newMatches::remove);

        storedMatches.addAll(newMatches.stream().map(CollectionMember::new).collect(Collectors.toList()));

        storedMatches.sort(Comparator.comparingInt(m -> ofNullable(m.getValue().getValid()).orElse("").length()));

        ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = getResponseCaveatData(request.getCaseDetails());

        return transformResponse(responseCaseDataBuilder.build());
    }

    public CaveatCallbackResponse transformResponseWithExtendedExpiry(CaveatCallbackRequest caveatCallbackRequest) {
        ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = getResponseCaveatData(caveatCallbackRequest.getCaseDetails());

        String defaultExpiry = dateTimeFormatter.format(caveatCallbackRequest.getCaseDetails().getData().getExpiryDate().plusMonths(6));
        return transformResponse(responseCaseDataBuilder.expiryDate(defaultExpiry)
            .caveatRaisedEmailNotificationRequested(caveatCallbackRequest.getCaseDetails().getData().getCaveatRaisedEmailNotificationRequested())
            .build());
    }

    public CaveatCallbackResponse transformResponseWithNoChanges(CaveatCallbackRequest caveatCallbackRequest) {
        ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = getResponseCaveatData(caveatCallbackRequest.getCaseDetails());

        return transformResponse(responseCaseDataBuilder.build());
    }

    private CaveatCallbackResponse transformResponse(ResponseCaveatData responseCaveatData) {
        return CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
    }

    private ResponseCaveatDataBuilder getResponseCaveatData(CaveatDetails caveatDetails) {
        CaveatData caveatData = caveatDetails.getData();

        return ResponseCaveatData.builder()

                .applicationType(ofNullable(caveatData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
                .registryLocation(ofNullable(caveatData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))
                .deceasedForenames(caveatData.getDeceasedForenames())
                .deceasedSurname(caveatData.getDeceasedSurname())
                .deceasedDateOfDeath(formatDateOfDeath(caveatData.getDeceasedDateOfDeath()))
                .deceasedDateOfBirth(transformToString(caveatData.getDeceasedDateOfBirth()))
                .deceasedAnyOtherNames(caveatData.getDeceasedAnyOtherNames())
                .deceasedFullAliasNameList(caveatData.getDeceasedFullAliasNameList())
                .deceasedAddress(caveatData.getDeceasedAddress())

                .languagePreferenceWelsh(caveatData.getLanguagePreferenceWelsh())
                .solsSolicitorFirmName(caveatData.getSolsSolicitorFirmName())
                .solsSolicitorPhoneNumber(caveatData.getSolsSolicitorPhoneNumber())
                .solsSolicitorAppReference(caveatData.getSolsSolicitorAppReference())

                .solsPaymentMethods(caveatData.getSolsPaymentMethods())
                .solsFeeAccountNumber(caveatData.getSolsFeeAccountNumber())

                .caveatorForenames(caveatData.getCaveatorForenames())
                .caveatorSurname(caveatData.getCaveatorSurname())
                .caveatorEmailAddress(caveatData.getCaveatorEmailAddress())
                .caveatorAddress(caveatData.getCaveatorAddress())

                .caseMatches(caveatData.getCaseMatches())
                .applicationSubmittedDate(transformToString(caveatData.getApplicationSubmittedDate()))
                .expiryDate(transformToString(caveatData.getExpiryDate()))
                .messageContent(caveatData.getMessageContent())
                .caveatReopenReason(caveatData.getCaveatReopenReason())

                .documentsUploaded(caveatData.getDocumentsUploaded())
                .documentsGenerated(caveatData.getDocumentsGenerated())
                .scannedDocuments(caveatData.getScannedDocuments())
                .notificationsGenerated(caveatData.getNotificationsGenerated())
                .recordId(caveatData.getRecordId())
                .paperForm(caveatData.getPaperForm())
                .legacyCaseViewUrl(caveatData.getLegacyCaseViewUrl())
                .legacyType(caveatData.getLegacyType())
                .sendToBulkPrintRequested(caveatData.getSendToBulkPrintRequested())
                .caveatRaisedEmailNotificationRequested(caveatData.getCaveatRaisedEmailNotificationRequested())
                .bulkPrintId(caveatData.getBulkPrintId())
                .bulkScanCaseReference((caveatData.getBulkScanCaseReference()))
                .applicationSubmittedDate(transformToString(caveatData.getApplicationSubmittedDate()));
    }

    public CaseCreationDetails bulkScanCaveatCaseTransform(uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData caveatData) {

        if (caveatData.getApplicationType() == null) {
            caveatData.setApplicationType(uk.gov.hmcts.reform.probate.model.cases.ApplicationType.PERSONAL);
        }

        if (caveatData.getRegistryLocation() == null) {
            caveatData.setRegistryLocation(EXCEPTION_RECORD_REGISTRY_LOCATION);
        }

        if (caveatData.getPaperForm() == null) {
            caveatData.setPaperForm(true);
        }

        if (caveatData.getApplicationSubmittedDate() == null) {
            caveatData.setApplicationSubmittedDate(LocalDate.now());
        }

        if (caveatData.getCaveatorEmailAddress() == null || caveatData.getCaveatorEmailAddress().isEmpty()) {
            caveatData.setSendToBulkPrintRequested(Boolean.TRUE);
            caveatData.setCaveatRaisedEmailNotificationRequested(Boolean.FALSE);
        } else {
            caveatData.setCaveatRaisedEmailNotificationRequested(Boolean.TRUE);
            caveatData.setSendToBulkPrintRequested(Boolean.FALSE);
        }

        caveatData.setBulkScanCaseReference((caveatData.getBulkScanCaseReference()));

        return CaseCreationDetails.builder().<ResponseCaveatData>
                eventId(EXCEPTION_RECORD_EVENT_ID).caseData(caveatData).caseTypeId(EXCEPTION_RECORD_CASE_TYPE_ID).build();
    }

    private String transformToString(LocalDate dateValue) {
        return ofNullable(dateValue)
                .map(String::valueOf)
                .orElse(null);
    }

    private String formatDateOfDeath(LocalDate dod) {
        return dod != null ? dateTimeFormatter.format(dod) : null;
    }

    private CollectionMember<BulkPrint> buildBulkPrint(String letterId, String templateName) {
        return new CollectionMember<>(null, BulkPrint.builder()
                .sendLetterId(letterId)
                .templateName(templateName)
                .build());
    }
}
