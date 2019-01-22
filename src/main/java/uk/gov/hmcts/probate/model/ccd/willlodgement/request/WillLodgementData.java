package uk.gov.hmcts.probate.model.ccd.willlodgement.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;
import uk.gov.hmcts.probate.model.ccd.ProbateExecutor;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Builder
@Data
public class WillLodgementData {

    private final ApplicationType applicationType;
    private final String registryLocation;

    // EVENT = createWillLodgment - general details

    private final String lodgementType;

    private final LocalDate lodgedDate;

    private final LocalDate willDate;

    private final LocalDate codicilDate;

    private final long numberOfCodicils;

    private final String jointWill;

    // EVENT = createStandingSearch - deceased data

    private final String deceasedForenames;

    private final String deceasedSurname;

    private final String deceasedGender;

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

    private final List<CollectionMember<ProbateExecutor>> additionalExecutorList;

    // EVENT = misc

    private final String withdrawalReason;

    private final List<CollectionMember<Document>> documentsGenerated = new ArrayList<>();

    private final List<CollectionMember<UploadDocument>> documentsUploaded;

    private final List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

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

    private String convertDate(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMMM yyyy");
        try {
            Date date = originalFormat.parse(dateToConvert.toString());
            String formattedDate = targetFormat.format(date);
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
}
