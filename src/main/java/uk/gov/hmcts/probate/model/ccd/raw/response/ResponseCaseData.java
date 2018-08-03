package uk.gov.hmcts.probate.model.ccd.raw.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.AliasNames;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReasons;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCaseData {

    private final String state;
    private final ApplicationType applicationType;
    private final String registryLocation;

    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String solsSolicitorFirmName;
    private final String solsSolicitorFirmPostcode;
    private final String solsSolicitorEmail;
    private final String solsSolicitorPhoneNumber;
    private final String solsSOTName;
    private final String solsSOTJobTitle;
    private final String solsSolicitorAppReference;
    private final String solsIHTFormId;
    private final String willExists;
    private final String willAccessOriginal;
    private final String willHasCodicils;
    private final String willNumberOfCodicils;
    private final String ihtNetValue;
    private final String ihtGrossValue;
    private final String deceasedDomicileInEngWales;
    private final String extraCopiesOfGrant;
    private final String outsideUKGrantCopies;
    private final String applicationFee;
    private final String feeForUkCopies;
    private final String feeForNonUkCopies;
    private final String totalFee;
    private final String solsPaymentMethods;
    private final String solsFeeAccountNumber;
    private final String solsPaymentReferenceNumber;
    private final CCDDocument solsLegalStatementDocument;
    private final String solsSOTNeedToUpdate;
    private final CCDDocument solsNextStepsDocument;
    private final String solsAdditionalInfo;
    private final String primaryApplicantForenames;
    private final String primaryApplicantSurname;
    private final String primaryApplicantEmailAddress;
    private final String primaryApplicantHasAlias;
    private final String primaryApplicantIsApplying;
    private final String solsPrimaryExecutorNotApplyingReason;
    private final String otherExecutorExists;
    private final String solsExecutorAliasNames;
    private final List<AdditionalExecutors> solsAdditionalExecutorList;
    private final SolsAddress deceasedAddress;
    private final String deceasedAnyOtherNames;
    private final SolsAddress primaryApplicantAddress;
    private final List<AliasNames> solsDeceasedAliasNamesList;
    private final String ccdState;
    private final String casePrinted;
    private final String boEmailDocsReceivedNotificationRequested;
    private final String boEmailGrantIssuedNotificationRequested;
    private final String boEmailDocsReceivedNotification;
    private final String boEmailGrantIssuedNotification;
    private final List<StopReasons> boCaseStopReasonList;
}
