package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CCDData implements Serializable {

    private final Long caseId;
    private final String solicitorReference;
    private final Solicitor solicitor;
    private final Deceased deceased;
    private final InheritanceTax iht;
    private final Fee fee;
    private final String solsAdditionalInfo;
    private final LocalDate caseSubmissionDate;
    private final String applicationSubmissionDate;
    private final List<Executor> executors;
    private final String boExaminationChecklistQ1;
    private final String boExaminationChecklistQ2;
    private final String applicationType;
    private final String solsSolicitorIsExec;
    private final String solsSolicitorIsApplying;
    private final String solsSolicitorNotApplyingReason;
    private final String solsWillType;
    private final String primaryApplicantEmailAddress;
    private final String solsSolicitorEmail;
    private final String sendLetterId;
    private final String willHasCodicils;
    private final String iht217;
    private final boolean hasUploadedLegalStatement;
    private final LocalDate originalWillSignedDate;
    private final List<CodicilAddedDate> codicilAddedDateList;
    private final LocalDate deceasedDateOfDeath;
    private final String deceasedMaritalStatus;
    private final DocumentLink solsCoversheetDocument;
    private final String solsApplicantRelationshipToDeceased;
    private final String solsApplicantSiblings;
    private final String solsSpouseOrCivilRenouncing;
    private final String titleAndClearingType;
    private final String dispenseWithNotice;
    private final String dispenseWithNoticeSupportingDocs;
    private final String englishWill;
    private final String channelChoice;
    private final String otherExecutorExists;
}
