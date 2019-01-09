package uk.gov.hmcts.probate.model.ccd.standingsearch.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStandingSearchData {

    private final ApplicationType applicationType;
    private final String registryLocation;

    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedAnyOtherNames;
    private final List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;
    private final ProbateAddress deceasedAddress;

    private final String applicantForenames;
    private final String applicantSurname;
    private final String applicantEmailAddress;
    private final ProbateAddress applicantAddress;

    private final List<CollectionMember<CaseMatch>> caseMatches;

    private final String numberOfCopies;
    private final String expiryDate;

    private final List<CollectionMember<UploadDocument>> documentsUploaded;
}
