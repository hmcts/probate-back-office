package uk.gov.hmcts.probate.model.ccd.willlodgement.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateExecutor;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWillLodgementData {

    private final String executorSurname;
    private final String executorForenames;
    private final String executorTitle;

    private final ApplicationType applicationType;
    private final String registryLocation;

    private final List<CollectionMember<ProbateExecutor>> additionalExecutorList;
    private final String executorEmailAddress;
    private final ProbateAddress executorAddress;

    private final String lodgementType;
    private final String lodgedDate;
    private final String willDate;
    private final String codicilDate;
    private final String numberOfCodicils;
    private final String jointWill;

    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedGender;
    private final String deceasedDateOfBirth;
    private final String deceasedDateOfDeath;
    private final String deceasedTypeOfDeath;
    private final String deceasedAnyOtherNames;
    private final List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;

    private final String withdrawalReason;
    private final List<CollectionMember<Document>> documentsGenerated;
    private final List<CollectionMember<UploadDocument>> documentsUploaded;

    private final String deceasedEmailAddress;
    private final ProbateAddress deceasedAddress;

    private final List<CollectionMember<CaseMatch>> caseMatches;

    private final String legacyId;
    private final String legacyType;
    private final String legacyCaseViewUrl;

}
