package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.probate.controller.validation.CaveatCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.CaveatUpdatedGroup;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.LanguagePreference;
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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@JsonDeserialize(builder = CaveatData.CaveatDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CaveatData {

    @SuppressWarnings("squid:S1170")
    @Getter(lazy = true)
    private final String sendToBulkPrint = YES;
    private ApplicationType applicationType;

    // EVENT = cavRaiseCaveat - deceased data
    private String registryLocation;
    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{deceasedForenamesIsNull}")
    private String deceasedForenames;
    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{deceasedSurnameIsNull}")
    private String deceasedSurname;
    @NotNull(groups = {CaveatUpdatedGroup.class}, message = "{deceasedDateOfDeathIsNull}")
    private LocalDate deceasedDateOfDeath;
    private LocalDate deceasedDateOfBirth;
    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{deceasedFullAliasNameListIsNull}")
    private String deceasedAnyOtherNames;
    private List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;

    // EVENT = cavRaiseCaveat - caveator data
    @NotNull(groups = {CaveatUpdatedGroup.class}, message = "{deceasedAddressIsNull}")
    private ProbateAddress deceasedAddress;
    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{caveatorForenamesIsNull}")
    private String caveatorForenames;
    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{caveatorSurnameIsNull}")
    private String caveatorSurname;
    @NotBlank(groups = {CaveatCreatedGroup.class}, message = "{caveatorEmailAddressIsNull}")
    private String caveatorEmailAddress;

    // EVENT = solicitorCreateCaveat - firm data
    // EVENT = cavRaiseCaveat - caveat details
    //both these used in multiple sceanrios - CaveatRaised, CaveatExtend etc. Ignore the naming here
    @Getter(lazy = true)
    private final String caveatRaisedEmailNotification = getDefaultValueForEmailNotifications();
    @NotNull(groups = {CaveatCreatedGroup.class}, message = "{caveatorAddressIsNull}")
    private ProbateAddress caveatorAddress;

    @Getter
    private String caveatorPhoneNumber;
    @Getter
    private String probateFee;
    @Getter
    private String probateFeeNotIncludedReason;
    @Getter
    private String helpWithFeesReference;
    @Getter
    private String probateFeeNotIncludedExplanation;
    @Getter
    private String probateFeeAccountNumber;
    @Getter
    private String probateFeeAccountReference;
    @Getter
    private String bilingualCorrespondenceRequested;
    @Getter
    private String solsSolicitorRepresentativeName;
    @NotBlank(groups = {CaveatCreatedGroup.class}, message = "{solsSolicitorFirmNameIsNull}")
    private String solsSolicitorFirmName;
    @Getter
    private String dxNumber;
    @Getter
    private String practitionerAcceptsServiceByEmail;
    // EVENT = solicitorUpdateCaveat - application details
    private String solsSolicitorPhoneNumber;
    @NotBlank(groups = {CaveatCreatedGroup.class}, message = "{solsSolicitorAppReferenceIsNull}")
    private String solsSolicitorAppReference;
    private String solsDeceasedNameSection;
    // EVENT = cavConfirmation - confirmation details
    private String solsFeeAccountNumber;
    private String solsPaymentMethods;
    private DynamicList solsPBANumber;
    private String solsPBAPaymentReference;
    private String solsOrgHasPBAs;

    private String caveatRaisedEmailNotificationRequested;
    private String sendToBulkPrintRequested;

    private LocalDate applicationSubmittedDate;

    private LocalDate expiryDate;

    // EVENT = cavEmailCaveator

    private String messageContent;

    // EVENT = cavUploadDocument

    private List<CollectionMember<UploadDocument>> documentsUploaded;

    private String paperForm;

    @Builder.Default
    private List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    @Builder.Default
    private List<CollectionMember<Document>> notificationsGenerated = new ArrayList<>();

    @Builder.Default
    private List<CollectionMember<BulkPrint>> bulkPrintId = new ArrayList<>();

    // EVENT = misc

    private String caveatReopenReason;

    @Builder.Default
    private List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();

    @Builder.Default
    private List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>();

    private String bulkScanCaseReference;

    private String recordId;
    private String legacyType;
    private String legacyCaseViewUrl;

    private String languagePreferenceWelsh;

    private String autoClosedExpiry;
    private String pcqId;
    private OrganisationPolicy applicantOrganisationPolicy;
    private String serviceRequestReference;
    private String paymentTaken;
    private String applicationSubmittedBy;

    private TTL ttl;

    @Builder.Default
    private List<CollectionMember<BulkScanEnvelope>> bulkScanEnvelopes = new ArrayList<>();

    private List<CollectionMember<Payment>> payments;

    @Builder.Default
    private final List<CollectionMember<RegistrarDirection>> registrarDirections = new ArrayList<>();
    private RegistrarDirection registrarDirectionToAdd;

    //transient in-event vars
    private OriginalDocuments originalDocuments;

    @Builder.Default
    private final List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives = new ArrayList<>();
    private ChangeOfRepresentative changeOfRepresentative;
    private RemovedRepresentative removedRepresentative;
    private ChangeOrganisationRequest changeOrganisationRequestField;
    private List<String> paymentConfirmCheckbox;

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getCaveatorFullName() {
        return String.join(" ", caveatorForenames, caveatorSurname);
    }

    public String getDefaultValueForEmailNotifications() {
        return caveatorEmailAddress == null || caveatorEmailAddress.isEmpty() ? NO : YES;
    }

    public boolean isSendForBulkPrintingRequested() {
        return YES.equals(getSendToBulkPrintRequested());
    }

    public boolean isCaveatEmailNotificationRequested() {
        return YES.equals(getCaveatRaisedEmailNotificationRequested());
    }

    public LanguagePreference getLanguagePreference() {
        return getLanguagePreferenceWelsh() != null && YES.equals(getLanguagePreferenceWelsh())
            ? LanguagePreference.WELSH : LanguagePreference.ENGLISH;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class CaveatDataBuilder {
    }

}
