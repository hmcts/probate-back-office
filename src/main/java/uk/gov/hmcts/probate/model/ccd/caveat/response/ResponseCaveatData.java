package uk.gov.hmcts.probate.model.ccd.caveat.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.BulkScanEnvelope;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.OriginalDocuments;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.TTL;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.List;

@Jacksonized
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCaveatData {

    private final String state;
    private final ApplicationType applicationType;
    private final String registryLocation;

    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedAnyOtherNames;
    private final List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;
    private final ProbateAddress deceasedAddress;

    private final String solsSolicitorRepresentativeName;
    private final String solsSolicitorFirmName;
    private final String solsSolicitorPhoneNumber;
    private final String solsSolicitorAppReference;
    private final String dxNumber;
    private final String practitionerAcceptsServiceByEmail;

    private final String solsPaymentMethods;
    private final String solsFeeAccountNumber;
    private final DynamicList solsPBANumber;
    private final String solsPBAPaymentReference;
    private final String solsOrgHasPBAs;

    private final String caveatorForenames;
    private final String caveatorSurname;
    private final String caveatorEmailAddress;
    private final ProbateAddress caveatorAddress;
    private final String caveatorPhoneNumber;
    private final String probateFee;
    private final String probateFeeNotIncludedReason;
    private final String helpWithFeesReference;
    private final String probateFeeNotIncludedExplanation;
    private final String probateFeeAccountNumber;
    private final String probateFeeAccountReference;
    private final String bilingualCorrespondenceRequested;

    private final List<CollectionMember<CaseMatch>> caseMatches;
    private final String applicationSubmittedDate;
    private final String expiryDate;
    private final String messageContent;
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
    private final OrganisationPolicy applicantOrganisationPolicy;

    private final List<CollectionMember<RegistrarDirection>> registrarDirections;
    private final RegistrarDirection registrarDirectionToAdd;

    //transient in-event vars
    private final OriginalDocuments originalDocuments;

    private final String serviceRequestReference;
    private final String paymentTaken;
    private final String applicationSubmittedBy;

    private final List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives;
    private final ChangeOfRepresentative changeOfRepresentative;
    private final RemovedRepresentative removedRepresentative;
    private final ChangeOrganisationRequest changeOrganisationRequestField;
    private String matches;
    private final List<String> paymentConfirmCheckbox;
    private final TTL ttl;

    private final List<CollectionMember<ModifiedOCRField>> modifiedOCRFieldList;
    private final List<CollectionMember<String>> autoCaseWarnings;
}
