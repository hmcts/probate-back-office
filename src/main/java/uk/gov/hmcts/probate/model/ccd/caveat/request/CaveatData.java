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

    private final ApplicationType applicationType;
    private final String registryLocation;

    // EVENT = cavRaiseCaveat - deceased data

    private final String deceasedForenames;

    private final String deceasedSurname;

    private final LocalDate deceasedDateOfDeath;

    private final LocalDate deceasedDateOfBirth;

    private final String deceasedAnyOtherNames;

    private final List<CollectionMember<CavFullAliasName>> deceasedFullAliasNameList;

    private final CavAddress deceasedAddress;

    // EVENT = cavRaiseCaveat - caveator data

    private final String caveatorForenames;

    private final String caveatorSurname;

    private final String caveatorEmailAddress;

    private final CavAddress caveatorAddress;

    // EVENT = cavRaiseCaveat - caveat details

    private LocalDate expiryDate;

    // EVENT = cavEmailCaveator

    private final String messageContent;

    // EVENT = cavUploadDocument

    private final List<CollectionMember<UploadDocument>> documentsUploaded;

    // EVENT = misc

    private final List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getCaveatorFullName() {
        return String.join(" ", caveatorForenames, caveatorSurname);
    }
}
