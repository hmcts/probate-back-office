package uk.gov.hmcts.probate.model.ccd.caveat.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

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

    private String caveatorEmailAddress;

    private ProbateAddress caveatorAddress;

    // EVENT = cavRaiseCaveat - caveat details

    private String bulkPrintSendLetterId;

    @Getter(lazy = true)
    private final String caveatRaisedEmailNotification = getDefaultValueForEmailNotifications();

    private String caveatRaisedEmailNotificationRequested;

    @Getter(lazy = true)
    private final String sendToBulkPrint = YES;

    private String sendToBulkPrintRequested;

    private LocalDate expiryDate;

    // EVENT = cavEmailCaveator

    private String messageContent;

    // EVENT = cavUploadDocument

    private List<CollectionMember<UploadDocument>> documentsUploaded;

    @Builder.Default
    private List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    @Builder.Default
    private List<CollectionMember<Document>> notificationsGenerated = new ArrayList<>();

    // EVENT = misc

    private String caveatReopenReason;

    @Builder.Default
    private List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();

    private String recordId;
    private String legacyType;
    private String legacyCaseViewUrl;

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getCaveatorFullName() {
        return String.join(" ", caveatorForenames, caveatorSurname);
    }

    public String getDefaultValueForEmailNotifications() {
        return caveatorEmailAddress == null || caveatorEmailAddress.isEmpty() ? NO : YES;
    }

    public boolean isSendForBulkPrintingRequested() { return YES.equals(getSendToBulkPrintRequested()); }

    public boolean isCaveatRaisedEmailNotificationRequested() { return YES.equals(getCaveatRaisedEmailNotificationRequested()); }


    @JsonPOJOBuilder(withPrefix = "")
    public static final class CaveatDataBuilder {
    }
}
