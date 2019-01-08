package uk.gov.hmcts.probate.model.ccd.standingsearch.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(builder = StandingSearchData.StandingSearchDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StandingSearchData {

    private ApplicationType applicationType;
    private String registryLocation;

    // EVENT = createStandingSearch - deceased data

    private String deceasedForenames;

    private String deceasedSurname;

    private LocalDate deceasedDateOfDeath;

    private LocalDate deceasedDateOfBirth;

    private String deceasedAnyOtherNames;

    private List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;

    private ProbateAddress deceasedAddress;

    // EVENT = createStandingSearch - applicant data

    private String applicantForenames;

    private String applicantSurname;

    private String applicantEmailAddress;

    private ProbateAddress applicantAddress;

    // EVENT = createStandingSearch - standing search details

    private long numberOfCopies;

    private LocalDate expiryDate;

    private final List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    // EVENT = uploadDocument

    private List<CollectionMember<UploadDocument>> documentsUploaded;

    // EVENT = misc

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getApplicantFullName() {
        return String.join(" ", applicantForenames, applicantSurname);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class StandingSearchDataBuilder {
    }

}
