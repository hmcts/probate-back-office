package uk.gov.hmcts.probate.model.ccd.caveat.request;

import lombok.Builder;
import lombok.Data;
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

    private final List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;

    private final ProbateAddress deceasedAddress;

    // EVENT = cavRaiseCaveat - caveator data

    private final String caveatorForenames;

    private final String caveatorSurname;

    private final String caveatorEmailAddress;

    private final ProbateAddress caveatorAddress;

    // EVENT = cavRaiseCaveat - caveat details

    private LocalDate expiryDate;

    // EVENT = cavEmailCaveator

    private final String messageContent;

    // EVENT = cavUploadDocument

    private final List<CollectionMember<UploadDocument>> documentsUploaded;

    private final List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    // EVENT = misc

    private final List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getCaveatorFullName() {
        return String.join(" ", caveatorForenames, caveatorSurname);
    }
}
