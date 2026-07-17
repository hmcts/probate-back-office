package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.ccd.access.DefaultAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotSolicitorSystemupdateCitizenCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CharityCrudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCrudSolicitorCrudSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCruAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCuAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCitizenCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarCudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudSolicitorCrudSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SuperuserCudAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSolicitorCrudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCrudSystemupdateCuAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudSystemupdateCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCrudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudSolicitorCrudSystemupdateCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCruAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCrudCitizenCuAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarRAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotRAccess;
import uk.gov.hmcts.probate.ccd.access.SuperuserRAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenRAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotSchedulerSuperuserRAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerApproverCaseworkerCaaSystemupdateCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSuperuserCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorRAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSuperuserSystemupdateCudAccess;
import uk.gov.hmcts.probate.ccd.access.IssuerCuAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCuAccess;
import uk.gov.hmcts.probate.ccd.access.CaseofficerCuAccess;
import uk.gov.hmcts.probate.ccd.access.RegistrarCuAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCrudSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerCaaCudCharityRAccess;

@Jacksonized
@SuperBuilder
@Data
@Slf4j
public class CaseDataParent {

    @CCD(
            label = "Version of schema applicable for this case",
            access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class, CharityCrudAccess.class}
    )
    protected final String schemaVersion;
    @CCD(
            label = "Registry Sequence Number",
            typeOverride = FieldType.Number,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    protected final String registrySequenceNumber;
    @CCD(
            label = "Do you have a death certificate or an interim death certificate?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "deceasedDeathCertificateFixedList",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, CitizenCuAccess.class}
    )
    protected final String deceasedDeathCertificate;
    @CCD(
            label = "Did the deceased die in England or Wales?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, CitizenCuAccess.class}
    )
    protected final String deceasedDiedEngOrWales;
    @CCD(
            label = "Is the original foreign death certificate in English?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class, SolicitorCudAccess.class}
    )
    protected final String deceasedForeignDeathCertInEnglish;
    @CCD(
            label = "Is the English translation included in the foreign death certificate?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class, SolicitorCudAccess.class}
    )
    protected final String deceasedForeignDeathCertTranslation;
    @CCD(
            label = "Your first name(s)",
            hint = "Include all middle names",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, RparobotCudSolicitorCrudSystemupdateCruAccess.class, SchedulerCrudAccess.class, SuperuserCudAccess.class}
    )
    protected final String solsForenames;
    @CCD(
            label = "Your last name(s)",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, RparobotCudSolicitorCrudSystemupdateCruAccess.class, SchedulerCrudAccess.class, SuperuserCudAccess.class}
    )
    protected final String solsSurname;
    @CCD(
            label = "Are you the Probate practitioner who will sign the statement of truth?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, SchedulerSolicitorCrudAccess.class, RparobotCudAccess.class, SuperuserCudAccess.class}
    )
    protected final String solsSolicitorWillSignSOT;
    @CCD(
            label = "Is a dispense with notice required for any of the executors?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final String dispenseWithNotice;
    @CCD(
            label = "What best describes the title and clearing type?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "TitleClearingTypes",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final String titleAndClearingType;
    @CCD(
            label = "Name of trust corporation",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final String trustCorpName;
    @CCD(
            label = "Address of trust corporation",
            typeOverride = FieldType.AddressUK,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected SolsAddress trustCorpAddress;
    // Not final so field can be reset in CaseDataTransformer
    @CCD(
            label = "Add all people acting on behalf of the trust corporation",
            hint = "Maximum 4",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ExecutorActingForTrustCorp",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList;
    @CCD(
            label = "Where is the resolution lodged?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final String lodgementAddress;
    @CCD(
            label = "Lodgement date",
            hint = "For example, 12 11 2007",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final LocalDate lodgementDate;
    @CCD(
            label = "Name of firm named in will",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final String nameOfFirmNamedInWill;
    @CCD(
            label = "Address of firm named in will",
            typeOverride = FieldType.AddressUK,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudAccess.class}
    )
    protected SolsAddress addressOfFirmNamedInWill;
    @CCD(
            label = "Name of firm that has succeeded to, and carried on the practice",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final String nameOfSucceededFirm;
    @CCD(
            label = "Address of firm that has succeeded to, and carried on the practice",
            typeOverride = FieldType.AddressUK,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudAccess.class}
    )
    protected SolsAddress addressOfSucceededFirm;
    @CCD(label = "probate practitioner is primary applicant")
    protected String isSolThePrimaryApplicant;
    @CCD(
            label = "Are there any other partners who are applying to act as an executor?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final String anyOtherApplyingPartners;
    @CCD(
            label = "Is anyone else applying to act as an executor on behalf of the trust corporation?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final String anyOtherApplyingPartnersTrustCorp;
    @CCD(
            label = "Please provide any further information that may be needed on your legal statement",
            hint = "Where there is a will, this could include rule 12 (3) clearing, explanation of the plight and condition of the will, information supporting the will’s validity or information on any other documents of a testamentary nature that may exist. You may also wish to provide information to confirm the identity of an executor or beneficiary.",
            typeOverride = FieldType.TextArea,
            access = {CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess.class, RparobotCuAccess.class, SolicitorCruAccess.class, SystemupdateCuAccess.class}
    )
    protected final String furtherEvidenceForApplication;
    // Not final so field can be reset in CaseDataTransformer
    @CCD(
            label = "Add all partners that are applying to act as an executor",
            hint = "Maximum 4",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "OtherPartnerExecutorApplying",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected List<CollectionMember<AdditionalExecutorPartners>> otherPartnersApplyingAsExecutors;
    @CCD(
            label = "Is there more than one partner holding power reserved?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final String morePartnersHoldingPowerReserved;
    @CCD(
            label = "Name of the Probate practitioner's position within the trust corporation as per the resolution",
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudAccess.class}
    )
    protected final String probatePractitionersPositionInTrust;
    @CCD(
            label = "Has leave already been given to dispense with notice?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final String dispenseWithNoticeLeaveGiven;
    @CCD(
            label = "On what date was leave given to dispense with notice?",
            hint = "For example, 12 11 2007",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final LocalDate dispenseWithNoticeLeaveGivenDate;
    // Not final as field set in CaseDataTransformer
    @CCD(ignore = true)
    protected String dispenseWithNoticeLeaveGivenDateFormatted;
    // Not final as field set in CaseDataTransformer
    @CCD(ignore = true)
    protected List<CollectionMember<String>> codicilAddedFormattedDateList;
    @CCD(ignore = true)
    protected String originalWillSignedDateFormatted;
    @CCD(
            label = "Please give a brief overview of why notice should be dispensed with",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final String dispenseWithNoticeOverview;
    @CCD(
            label = "Please list any supporting documents you are providing as part of your application",
            typeOverride = FieldType.TextArea,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final String dispenseWithNoticeSupportingDocs;
    // Not final so field can be reset in CaseDataTransformer
    @CCD(
            label = "Add all executors to whom power is reserved",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "DispenseWithNoticeExecutor",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeOtherExecsList;
    @CCD(
            label = "Who shares in the profits at your company?",
            hint = "Select all that apply.",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "SharingInProfits",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    protected final List<String> whoSharesInCompanyProfits;
    @CCD(
            label = "Applying executors",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    protected final String solsIdentifiedApplyingExecs;
    @CCD(
            label = "Non-applying executors",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    protected final String solsIdentifiedNotApplyingExecs;
    @CCD(
            label = "Applying executors",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    protected final String solsIdentifiedApplyingExecsCcdCopy;
    @CCD(
            label = "Non-applying executors",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    protected final String solsIdentifiedNotApplyingExecsCcdCopy;
    @CCD(
            label = "Was an IHT217 form filled in?",
            typeOverride = FieldType.YesOrNo,
            typeParameterOverride = "iht217",
            access = {DefaultAccess.class, SolicitorCrudCitizenCuAccess.class, RparobotCudAccess.class}
    )
    protected String iht217;
    @CCD(
            label = "Tell us why you only have a copy of the will",
            hint = "Give as much detail as you can. This may help reduce any delays in processing your application.",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCudAccess.class}
    )
    protected final String noOriginalWillAccessReason;
    @CCD(
            label = "When was the original will signed and dated?",
            hint = "For example, 31 03 2020",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected final LocalDate originalWillSignedDate;
    // Not final so field can be reset in CaseDataTransformer
    @CCD(
            label = "When was the codicil added?",
            hint = "For example, 31 03 2020",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    protected List<CollectionMember<CodicilAddedDate>> codicilAddedDateList;

    @CCD(
            label = "Case authenticated date",
            access = {DefaultAccess.class, RparobotCrudAccess.class, SolicitorCruAccess.class}
    )
    @Getter
    protected LocalDate authenticatedDate;

    @CCD(ignore = true)
    protected String singularProfitSharingTextForLegalStatement;
    @CCD(ignore = true)
    protected String pluralProfitSharingTextForLegalStatement;
    @CCD(
            label = "Fee account number",
            hint = "Select the correct PBA number from the list",
            typeOverride = FieldType.DynamicList,
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerSolicitorCrudAccess.class, RparobotRAccess.class, SuperuserRAccess.class, CitizenRAccess.class}
    )
    private final DynamicList solsPBANumber;
    @CCD(
            label = "Customer reference",
            hint = "This should be your own unique reference to identify the case. It will appear on your statements if you paid by PBA.",
            max = 255,
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, RparobotSchedulerSuperuserRAccess.class, SolicitorCrudAccess.class}
    )
    private final String solsPBAPaymentReference;
    @CCD(
            label = "Does this organisation have any PBAs associated?",
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, RparobotSchedulerSuperuserRAccess.class, SolicitorCrudAccess.class}
    )
    private final String solsOrgHasPBAs;
    @CCD(
            label = "PBA payment needed",
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, RparobotSchedulerSuperuserRAccess.class, SolicitorCrudAccess.class}
    )
    private final String solsNeedsPBAPayment;

    // This has to be mutable to permit us to rollback DTSPB-5005. mutation is done by
    // clearApplicantOrganisationPolicy() which we should remove after that migration completes and make this
    // final again
    @CCD(
            label = "Applicant Solicitor Details",
            access = {CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess.class, CaseworkerApproverCaseworkerCaaSystemupdateCrudAccess.class, SchedulerSolicitorCrudAccess.class}
    )
    @Getter
    @Setter(AccessLevel.NONE)
    private OrganisationPolicy applicantOrganisationPolicy;

    @CCD(
            label = "Service request reference",
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerSuperuserCrudAccess.class, SolicitorRAccess.class, SystemupdateCrudAccess.class, CitizenRAccess.class}
    )
    private String serviceRequestReference;
    @CCD(
            label = "Payment has been taken",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "paymentTaken",
            access = {SchedulerSuperuserSystemupdateCudAccess.class, IssuerCuAccess.class}
    )
    private String paymentTaken;
    @CCD(
            label = "Application Submitted By",
            access = {SchedulerSuperuserSystemupdateCudAccess.class, CaseadminCuAccess.class, CaseofficerCuAccess.class, IssuerCuAccess.class, RegistrarCuAccess.class}
    )
    private String applicationSubmittedBy;

    @CCD(
            label = "What date should appear on the notation?",
            typeOverride = FieldType.Date,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String reissueDate;
    @CCD(
            label = "Grant issued date",
            typeOverride = FieldType.Date,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, CaseworkerCaaCudCharityRAccess.class, SystemupdateCuAccess.class}
    )
    private final String grantIssuedDate;

    @CCD(ignore = true)
    @Getter(lazy = true)
    private final String reissueDateFormatted = convertDate(reissueDate);

    @CCD(ignore = true)
    @Getter(lazy = true)
    private final String grantIssuedDateFormatted = convertDate(grantIssuedDate);

    public String convertDate(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        return convertDate(dateToConvert.toString());
    }

    public String convertDate(String dateToConvert) {
        if (dateToConvert == null || dateToConvert.equals("")) {
            return null;
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMMM yyyy");
        try {
            Date date = originalFormat.parse(dateToConvert);
            String formattedDate = targetFormat.format(date);
            int day = Integer.parseInt(formattedDate.substring(0, 2));
            switch (day) {
                case 1:
                case 21:
                case 31:
                    return day + "st " + formattedDate.substring(3);

                case 2:
                case 22:
                    return day + "nd " + formattedDate.substring(3);

                case 3:
                case 23:
                    return day + "rd " + formattedDate.substring(3);

                default:
                    return day + "th " + formattedDate.substring(3);
            }
        } catch (ParseException ex) {
            ex.getMessage();
            return null;
        }
    }

    public void clearApplicantOrganisationPolicy(final long caseReference) {
        log.info("Clearing applicantOrganisationPolicy for case {}", caseReference);
        this.applicantOrganisationPolicy = null;
    }
}
