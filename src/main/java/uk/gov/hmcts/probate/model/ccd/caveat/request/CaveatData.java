package uk.gov.hmcts.probate.model.ccd.caveat.request;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.CavFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class CaveatData {

    // EVENT = cavRaiseCaveat - deceased data

    private final String cavDeceasedForenames;

    private final String cavDeceasedSurname;

    private final LocalDate cavDeceasedDateOfDeath;

    private final String cavDeceasedAnyOtherNames;

    private final List<CollectionMember<CavAliasName>> cavDeceasedAliasNameList;

    private final List<CollectionMember<CavFullAliasName>> cavDeceasedFullAliasNameList;

    private final CavAddress cavDeceasedAddress;

    // EVENT = cavRaiseCaveat - caveator data

    private final String cavCaveatorForenames;

    private final String cavCaveatorSurname;

    private final String cavCaveatorEmailAddress;

    private final CavAddress cavCaveatorAddress;

    // EVENT = cavUploadDocument

    private final List<CollectionMember<UploadDocument>> cavDocumentsUploaded;
}
