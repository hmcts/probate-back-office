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
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    private ApplicationType applicationType;
    private String registryLocation;

    // EVENT = cavRaiseCaveat - deceased data

    private String deceasedForenames;

    private String deceasedSurname;

    private LocalDate deceasedDateOfDeath;

    private LocalDate deceasedDateOfBirth;

    private String deceasedAnyOtherNames;

    private List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;

    private ProbateAddress deceasedAddress;

    // EVENT = cavRaiseCaveat - caveator data

    private String caveatorForenames;

    private String caveatorSurname;

    @NotBlank(groups = {CaveatCreatedGroup.class}, message = "{caveatorEmailAddressIsNull}")
    private String caveatorEmailAddress;

    @NotNull(groups = {CaveatCreatedGroup.class}, message = "{caveatorAddressIsNull}")
    private ProbateAddress caveatorAddress;

    // EVENT = solicitorCreateCaveat - firm data

    @NotBlank(groups = {CaveatCreatedGroup.class}, message = "{solsSolicitorFirmNameIsNull}")
    private String solsSolicitorFirmName;

    private String solsSolicitorPhoneNumber;

    @NotBlank(groups = {CaveatCreatedGroup.class}, message = "{solsSolicitorAppReferenceIsNull}")
    private String solsSolicitorAppReference;

    // EVENT = solicitorUpdateCaveat - application details

    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{solsApplicantForenamesIsNull}")
    private String solsApplicantForenames;

    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{solsApplicantSurnameIsNull}")
    private String solsApplicantSurname;

    private String solsDeceasedDetailsSection;

    private String solsDeceasedNameSection;

    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{solsDeceasedForenamesIsNull}")
    private String solsDeceasedForenames;

    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{solsDeceasedSurnameIsNull}")
    private String solsDeceasedSurname;

    @NotNull(groups = {CaveatUpdatedGroup.class}, message = "{solsDeceasedSurnameIsNull}")
    private LocalDate solsDeceasedDateOfDeath;

    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{solsHasDateOfBirthIsNull}")
    private String solsHasDateOfBirth;

    private LocalDate solsDeceasedDateOfBirth;

    @NotBlank(groups = {CaveatUpdatedGroup.class}, message = "{solsDeceasedAnyOtherNamesIsNull}")
    private String solsDeceasedAnyOtherNames;

    private List<CollectionMember<ProbateFullAliasName>> solsDeceasedAliasNameList;

    @NotNull(groups = {CaveatUpdatedGroup.class}, message = "{solsDeceasedAddressIsNull}")
    private ProbateAddress solsDeceasedAddress;

    // EVENT = cavRaiseCaveat - caveat details

    @Getter(lazy = true)
    private final String caveatRaisedEmailNotification = getDefaultValueForEmailNotifications();

    private String caveatRaisedEmailNotificationRequested;

    @SuppressWarnings("squid:S1170")
    @Getter(lazy = true)
    private final String sendToBulkPrint = YES;

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

    private String recordId;
    private String legacyType;
    private String legacyCaseViewUrl;

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getCaveatorFullName() {
        return String.join(" ", caveatorForenames, caveatorSurname);
    }

    public String getSolsApplicantFullName() {
        return String.join(" ", solsApplicantForenames, solsApplicantSurname);
    }

    public String getDefaultValueForEmailNotifications() {
        return caveatorEmailAddress == null || caveatorEmailAddress.isEmpty() ? NO : YES;
    }

    public boolean isSendForBulkPrintingRequested() {
        return YES.equals(getSendToBulkPrintRequested());
    }

    public boolean isCaveatRaisedEmailNotificationRequested() {
        return YES.equals(getCaveatRaisedEmailNotificationRequested());
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class CaveatDataBuilder {
    }
}
