package uk.gov.hmcts.probate.model.ccd.caveat.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.CavFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCaveatData {

    private final ApplicationType cavApplicationType;
    private final String cavRegistryLocation;

    private final String cavDeceasedForenames;
    private final String cavDeceasedSurname;
    private final String cavDeceasedDateOfDeath;
    private final String cavDeceasedDateOfBirth;
    private final String cavDeceasedAnyOtherNames;
    private final List<CollectionMember<CavFullAliasName>> cavDeceasedFullAliasNameList;
    private final CavAddress cavDeceasedAddress;

    private final String cavCaveatorForenames;
    private final String cavCaveatorSurname;
    private final String cavCaveatorEmailAddress;
    private final CavAddress cavCaveatorAddress;

    private final String cavExpiryDate;
    private final String cavMessageContent;

    private final List<CollectionMember<UploadDocument>> cavDocumentsUploaded;
    private final List<CollectionMember<Document>> cavDocumentsGenerated;
}
