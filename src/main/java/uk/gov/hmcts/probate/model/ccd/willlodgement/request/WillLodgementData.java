package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
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
public class WillLodgementData {

    private final ApplicationType applicationType;
    private final String registryLocation;

    // EVENT = createWillLodgment - general details

    // ... lodgementType

    private final LocalDate lodgedDate;

    private final LocalDate willDate;

    private final LocalDate codicilDate;

    private final long numberOfCodicils;

    private final String jointWill;

    // EVENT = createStandingSearch - deceased data

    private final String deceasedForenames;

    private final String deceasedSurname;

    // ... deceasedGender

    private final LocalDate deceasedDateOfBirth;

    private final LocalDate deceasedDateOfDeath;

    private final String deceasedTypeOfDeath;

    private final String deceasedAnyOtherNames;

    private final List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;

    private final ProbateAddress deceasedAddress;

    private final String deceasedEmailAddress;

    // EVENT = createStandingSearch - executor data

    private final String executorTitle;

    private final String executorForenames;

    private final String executorSurname;

    private final ProbateAddress executorAddress;

    private final String executorEmailAddress;

    // ... additionalExecutorList

    // EVENT = misc

    // ... wlWithdrawReason

    private final List<CollectionMember<Document>> wlDocumentsGenerated = new ArrayList<>();

    private final List<CollectionMember<UploadDocument>> documentsUploaded;

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getExecutorFullName() {
        return String.join(" ", executorForenames, executorSurname);
    }
}
