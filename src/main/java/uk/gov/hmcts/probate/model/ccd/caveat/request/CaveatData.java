package uk.gov.hmcts.probate.model.ccd.caveat.request;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.CavFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class CaveatData {

    private final ApplicationType cavApplicationType;
    private final String cavRegistryLocation;

    // EVENT = cavRaiseCaveat - deceased data

    private final String cavDeceasedForenames;

    private final String cavDeceasedSurname;

    private final LocalDate cavDeceasedDateOfDeath;

    private final LocalDate cavDeceasedDateOfBirth;

    private final String cavDeceasedAnyOtherNames;

    private final List<CollectionMember<CavFullAliasName>> cavDeceasedFullAliasNameList;

    private final CavAddress cavDeceasedAddress;

    // EVENT = cavRaiseCaveat - caveator data

    private final String cavCaveatorForenames;

    private final String cavCaveatorSurname;

    private final String cavCaveatorEmailAddress;

    private final CavAddress cavCaveatorAddress;

    // EVENT = cavRaiseCaveat - caveat details

    private LocalDate cavExpiryDate;

    // EVENT = cavEmailCaveator

    private final String cavMessageContent;

    // EVENT = cavUploadDocument

    private final List<CollectionMember<UploadDocument>> cavDocumentsUploaded;

    // EVENT = misc

    private final List<CollectionMember<Document>> cavDocumentsGenerated = new ArrayList<>();

    public String getDeceasedFullName() {
        return String.join(" ", cavDeceasedForenames, cavDeceasedSurname);
    }

    public String getCaveatorFullName() {
        return String.join(" ", cavCaveatorForenames, cavCaveatorSurname);
    }
}
