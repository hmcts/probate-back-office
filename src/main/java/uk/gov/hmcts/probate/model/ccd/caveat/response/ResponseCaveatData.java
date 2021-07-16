package uk.gov.hmcts.probate.model.ccd.caveat.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.BulkScanEnvelope;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCaveatData {

    private final ApplicationType applicationType;
    private final String registryLocation;

    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedAnyOtherNames;
    private final List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;
    private final ProbateAddress deceasedAddress;

    private final String solsSolicitorFirmName;
    private final String solsSolicitorPhoneNumber;
    private final String solsSolicitorAppReference;

    private final String solsPaymentMethods;
    private final String solsFeeAccountNumber;
    private final DynamicList solsPBANumber;
    private final String solsPBAPaymentReference;
    private final String solsOrgHasPBAs;

    private final String caveatorForenames;
    private final String caveatorSurname;
    private final String caveatorEmailAddress;
    private final ProbateAddress caveatorAddress;

    private final List<CollectionMember<CaseMatch>> caseMatches;
    private final String applicationSubmittedDate;
    private final String expiryDate;
    private final String messageContent;
    private final String automatedProcess;
    private final String caveatReopenReason;

    private final String caveatRaisedEmailNotificationRequested;
    private final String caveatRaisedEmailNotification;

    private final String sendToBulkPrintRequested;
    private final String sendToBulkPrint;

    private final List<CollectionMember<UploadDocument>> documentsUploaded;
    private final List<CollectionMember<Document>> documentsGenerated;
    private final List<CollectionMember<ScannedDocument>> scannedDocuments;
    private final List<CollectionMember<Document>> notificationsGenerated;
    private final List<CollectionMember<BulkPrint>> bulkPrintId;

    private String bulkScanCaseReference;

    private String recordId;
    private String legacyType;
    private String legacyCaseViewUrl;
    private String paperForm;

    private String languagePreferenceWelsh;
    
    private String autoClosedExpiry;
    private String pcqId;

    private final List<CollectionMember<BulkScanEnvelope>> bulkScanEnvelopes;

    private final List<CollectionMember<Payment>> payments;

}
