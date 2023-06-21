package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

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
import uk.gov.hmcts.probate.model.ccd.ProbateExecutor;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.OriginalDocuments;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@JsonDeserialize(builder = WillLodgementData.WillLodgementDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WillLodgementData {

    private ApplicationType applicationType;
    private String registryLocation;

    // EVENT = createWillLodgment - general details

    private String lodgementType;

    private LocalDate lodgedDate;

    private LocalDate willDate;

    private LocalDate codicilDate;

    private long numberOfCodicils;

    private String jointWill;

    // EVENT = createStandingSearch - deceased data

    private String deceasedForenames;

    private String deceasedSurname;

    private String deceasedGender;

    private LocalDate deceasedDateOfBirth;

    private LocalDate deceasedDateOfDeath;

    private String deceasedTypeOfDeath;

    private String deceasedAnyOtherNames;

    private List<CollectionMember<ProbateFullAliasName>> deceasedFullAliasNameList;

    private ProbateAddress deceasedAddress;

    private String deceasedEmailAddress;

    // EVENT = createStandingSearch - executor data

    private String executorTitle;

    private String executorForenames;

    private String executorSurname;

    private ProbateAddress executorAddress;

    private String executorEmailAddress;

    private List<CollectionMember<ProbateExecutor>> additionalExecutorList;

    // EVENT = misc

    private String withdrawalReason;

    @Builder.Default
    private List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();

    private List<CollectionMember<UploadDocument>> documentsUploaded;

    @Builder.Default
    private List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getExecutorFullName() {
        return String.join(" ", executorForenames, executorSurname);
    }

    private final LocalDate currentDate = LocalDate.now();

    private final String currentDateFormatted = convertDate(currentDate);

    @Getter(lazy = true)
    private final String willDateFormatted = convertDate(willDate);

    private String recordId;
    private String legacyType;
    private String legacyCaseViewUrl;

    //transient in-event vars
    private OriginalDocuments originalDocuments;

    private String convertDate(LocalDate date) {
        DateFormat orgFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat newFormat = new SimpleDateFormat("dd MMMMM yyyy");
        if (date == null) {
            return null;
        }

        try {
            Date dateConverted = orgFormat.parse(date.toString());
            String formattedDate = newFormat.format(dateConverted);
            int day = Integer.parseInt(formattedDate.substring(0, 2));
            switch (day) {
                case 3:
                case 23:
                    return day + "rd " + formattedDate.substring(3);
                case 2:
                case 22:
                    return day + "nd " + formattedDate.substring(3);
                case 1:
                case 21:
                case 31:
                    return day + "st " + formattedDate.substring(3);
                default:
                    return day + "th " + formattedDate.substring(3);
            }
        } catch (ParseException ex) {
            ex.getMessage();
            return null;
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class WillLodgementDataBuilder {
    }
}
