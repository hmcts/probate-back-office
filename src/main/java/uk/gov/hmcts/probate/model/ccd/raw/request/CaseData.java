package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.controller.validation.AmendCaseDetailsGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationAdmonGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationIntestacyGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationProbateGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationReviewedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.controller.validation.NextStepsConfirmationGroup;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.RegistrarEscalateReason;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.Reissue;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdoptedRelative;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AttorneyApplyingOnBehalfOf;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.BulkScanEnvelope;
import uk.gov.hmcts.probate.model.ccd.raw.Categories;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOrganisationRequest;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.Declaration;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.LegalStatement;
import uk.gov.hmcts.probate.model.ccd.raw.OriginalDocuments;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.RegistrarDirection;
import uk.gov.hmcts.probate.model.ccd.raw.RemovedRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.TTL;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.reform.probate.model.cases.CombinedName;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Damage;
import uk.gov.hmcts.reform.probate.model.cases.HandoffReason;
import uk.gov.hmcts.reform.probate.model.cases.CitizenResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.ANSWER_NO;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCudAccess;
import uk.gov.hmcts.probate.ccd.access.DefaultAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCrudCitizenCuAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerApproverCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerCaaCudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudSolicitorCrudSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudSystemupdateCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarSuperuserCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerCaaCudCharityRAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerWaTaskConfigurationCruAccess;
import uk.gov.hmcts.probate.ccd.access.CharityRAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCrudCitizenCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCrudSystemupdateCuAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCrudSolicitorCrudSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCruAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCuAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCrudSystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCitizenCuAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarRAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerRAccess;
import uk.gov.hmcts.probate.ccd.access.SuperuserCrudAccess;
import uk.gov.hmcts.probate.ccd.access.IssuerCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCudAccess;
import uk.gov.hmcts.probate.ccd.access.SuperuserCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarCudAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSuperuserCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCuCitizenCudAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerApproverCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerCaaCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorRAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCruCitizenCuAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminRPlus8RolesHgcfgrAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerCudAccess;
import uk.gov.hmcts.probate.ccd.access.PcqextractorRAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerCruAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminRPlus8RolesVxljipAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCaseofficerIssuerSchedulerCrudAccess;
import uk.gov.hmcts.probate.ccd.access.RegistrarCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCPlus7RolesXmptngAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCrPlus7RolesHiilikAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCrPlus7RolesCkpmppAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCuAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotSystemupdateCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorSystemupdateCitizenCudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotSolicitorSystemupdateCitizenCrudAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotCuAccess;
import uk.gov.hmcts.probate.ccd.access.SolicitorCruAccess;
import uk.gov.hmcts.probate.ccd.access.IssuerRAccess;
import uk.gov.hmcts.probate.ccd.access.IssuerCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenRAccess;
import uk.gov.hmcts.probate.ccd.access.RegistrarCruAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCitizenCudAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSuperuserCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseofficerCudAccess;
import uk.gov.hmcts.probate.ccd.access.IssuerCudAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSuperuserSystemupdateCudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerApproverCaseworkerCaaSystemupdateCrudAccess;
import uk.gov.hmcts.probate.ccd.access.APPLICANTSOLICITORCruAccess;
import uk.gov.hmcts.probate.ccd.access.CaseworkerCaaCruAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSuperuserSystemupdateCitizenCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerCrudSolicitorRSuperuserRSystemupdateCrudAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateCruAccess;
import uk.gov.hmcts.probate.ccd.access.GSProfileRAccess;
import uk.gov.hmcts.probate.ccd.access.CitizenCrAccess;
import uk.gov.hmcts.probate.ccd.access.SchedulerSolicitorCrudAccess;
import uk.gov.hmcts.probate.ccd.access.CaseadminCruPlus7RolesYzgfvaAccess;
import uk.gov.hmcts.probate.ccd.access.RparobotSchedulerSuperuserRAccess;
import uk.gov.hmcts.probate.ccd.access.SuperuserRAccess;
import uk.gov.hmcts.probate.ccd.access.SystemupdateRAccess;
import uk.gov.hmcts.probate.ccd.access.TTLProfileCruAccess;
import uk.gov.hmcts.probate.model.ccd.raw.request.DomiciledDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CheckYourAnswersEventTransitions;
import uk.gov.hmcts.probate.model.ccd.raw.request.SOTReview1;
import uk.gov.hmcts.probate.model.ccd.raw.request.SOTReview2;
import uk.gov.hmcts.probate.model.ccd.raw.request.SelectionFixedList;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReissueSelectionFixedList;
import uk.gov.hmcts.probate.model.ccd.raw.request.ResolveStopStateReissueFixedList;
import uk.gov.hmcts.probate.model.ccd.raw.request.Fees;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class CaseData extends CaseDataParent {

    // Tasklist update
    @CCD(label = "Case progress", access = {SystemupdateCudAccess.class})
    private final String taskList;

    // Not final as field set in CaseDataTransformer
    // EVENT = solicitorCreateApplication
    @CCD(
            label = "Name of the firm, or the name under which the applicant operates",
            min = 1,
            max = 100,
            access = {DefaultAccess.class, SolicitorCrudCitizenCuAccess.class, RparobotCudSystemupdateCruAccess.class, CaseworkerApproverCudAccess.class}
    )
    @NotBlank(groups = {ApplicationCreatedGroup.class},
        message = "{solsSolicitorFirmNameIsNull}")
    private String solsSolicitorFirmName;

    @CCD(
            label = "Address of your firm or where the applicant operates",
            typeOverride = FieldType.AddressUK,
            access = {DefaultAccess.class, SolicitorCrudCitizenCuAccess.class, RparobotCudSystemupdateCruAccess.class, CaseworkerApproverCudAccess.class}
    )
    @Valid
    private final SolsAddress solsSolicitorAddress;

    @CCD(
            label = "Your reference for this application",
            max = 100,
            access = {DefaultAccess.class, SolicitorCrudCitizenCuAccess.class, RparobotCudSystemupdateCruAccess.class, CaseworkerCaaCudAccess.class}
    )
    @NotBlank(groups = {ApplicationCreatedGroup.class}, message = "{solsSolicitorAppReferenceIsNull}")
    private final String solsSolicitorAppReference;

    @CCD(
            label = "Contact email address",
            regex = "[a-zA-Z0-9#$%'+=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@[a-zA-Z0-9](?:[a-zA-Z0-9-.]{0,30}[a-zA-Z0-9])?\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,10}[a-zA-Z0-9])?",
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCruAccess.class, CaseworkerApproverCudAccess.class}
    )
    private final String solsSolicitorEmail;

    @CCD(
            label = "Contact phone number",
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final String solsSolicitorPhoneNumber;

    @CCD(
            label = "Is the Probate practitioner named in the will as an executor?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCruAccess.class}
    )
    @NotBlank(groups = {ApplicationCreatedGroup.class}, message = "{solsSolicitorIsExecIsNull}")
    private final String solsSolicitorIsExec;

    @CCD(
            label = "Is the Probate practitioner acting as an executor?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCruAccess.class}
    )
    private String solsSolicitorIsApplying;

    @CCD(
            label = "Why are they not applying?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "solsNotApplyingReasonFixedList",
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCruAccess.class}
    )
    private String solsSolicitorNotApplyingReason;

    // EVENT = solicitorUpdateApplication
    @CCD(
            label = "Does the will dispose of the estate in England and Wales (or if will is in English, does it appoint an executor), or is the deceased a British National?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    private final String willDispose;

    @CCD(
            label = "Is the will in English or Welsh?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    private final String englishWill;

    @CCD(
            label = "Does the will appoint an executor?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    private final String appointExec;

    @CCD(
            label = "Are there any duties set out in the will which could constitute a person as an executor according to the tenor?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    private final String appointExecByDuties;

    @CCD(
            label = "Does the will appoint an executor according to the tenor?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    private final String appointExecNo;

    @CCD(
            label = "Does the whole of the estate in England and Wales consist of immovable property?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    private final String immovableEstate;

    // This is an old schema (prior to 2.0.0) attribute so it should be not blank for
    // an amend of these, but for new trust corp this field is no longer needed & not part of the schema
    // @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{applicationGroundsIsNull}")
    @CCD(
            label = "Please state the grounds for making this application and any information in support:",
            typeOverride = FieldType.TextArea,
            access = {RparobotCudSystemupdateCuAccess.class, CaseadminCaseofficerIssuerRegistrarSuperuserCrudAccess.class, SolicitorCrudAccess.class}
    )
    private final String applicationGrounds;

    // Not final as field set in CaseDataTransformer
    @CCD(
            label = "First name(s)",
            hint = "Include all middle names",
            min = 1,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class, CaseworkerCaaCudCharityRAccess.class, CaseworkerWaTaskConfigurationCruAccess.class}
    )
    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
        message = "{deceasedForenameIsNull}")
    private String deceasedForenames;

    // Not final as field set in CaseDataTransformer
    @CCD(
            label = "Last name(s)",
            min = 1,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class, CaseworkerCaaCudCharityRAccess.class, CaseworkerWaTaskConfigurationCruAccess.class}
    )
    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
        message = "{deceasedSurnameIsNull}")
    private String deceasedSurname;

    @CCD(
            label = "Date of death",
            hint = "Use the date from the death certificate. For example, 30 06 2016",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class, CaseworkerCaaCudCharityRAccess.class}
    )
    @NotNull(groups = {ApplicationProbateGroup.class, ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class,
        ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dodIsNull}")
    private final LocalDate deceasedDateOfDeath;

    @CCD(label = "Date of birth (yyyy-MM-dd)", hint = "For example, 1889-03-31", access = {DefaultAccess.class})
    private final String deceasedDob;

    @CCD(ignore = true)
    private final LocalDate currentDate = LocalDate.now();

    @CCD(ignore = true)
    private final String currentDateFormatted = convertDate(currentDate);

    @CCD(ignore = true)
    @Getter(lazy = true)
    private final String deceasedDateOfDeathFormatted = convertDate(deceasedDateOfDeath);

    @CCD(
            label = "Date of birth",
            hint = "For example, 31 03 1945",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class, CharityRAccess.class}
    )
    @NotNull(groups = {ApplicationProbateGroup.class, ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class,
        ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dobIsNull}")
    private final LocalDate deceasedDateOfBirth;

    @CCD(
            label = "Was the deceased domiciled in England or Wales at the time of their death?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedDomicileInEngWalesIsNull}")
    private final String deceasedDomicileInEngWales;

    @CCD(
            label = "Permanent address of deceased at time of death",
            typeOverride = FieldType.AddressUK,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class, CharityRAccess.class}
    )
    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{deceasedAddressIsNull}")
    private final SolsAddress deceasedAddress;

    @CCD(
            label = "Did the deceased have assets in any other names?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedAnyOtherNamesIsNull}")
    private final String deceasedAnyOtherNames;

    @CCD(
            label = "Alias name",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ProbateSolsAliasName",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    private final List<CollectionMember<AliasName>> solsDeceasedAliasNamesList;

    @CCD(
            label = "Inheritance Tax form",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "IHTFormID",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCudAccess.class}
    )
    private String ihtFormId;

    @CCD(
            label = "Net value of the estate for probate",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCudAccess.class}
    )
    @Min(value = 0, groups = {ApplicationUpdatedGroup.class}, message = "{ihtNetNegative}")
    private final BigDecimal ihtNetValue;

    @CCD(
            label = "Gross value of the estate for probate",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCudAccess.class}
    )
    @Min(value = 0, groups = {ApplicationUpdatedGroup.class}, message = "{ihtGrossNegative}")
    private final BigDecimal ihtGrossValue;

    @CCD(
            label = "Application type",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "solsWillTypes",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    private final String solsWillType;

    @CCD(
            label = "Application type reason",
            access = {DefaultAccess.class, RparobotSystemupdateCruAccess.class, SolicitorCrudAccess.class}
    )
    private final String solsWillTypeReason;

    // EVENT = solicitorUpdateProbate and Admon
    @CCD(
            label = "Do you have access to the original will?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotBlank(groups = {ApplicationProbateGroup.class, ApplicationAdmonGroup.class}, message = "{willAsOriginalIsNull}")
    private final String willAccessOriginal;

    @CCD(
            label = "Do you have access to a notarial or court-sealed copy of the will?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess.class}
    )
    private final String willAccessNotarial;

    @CCD(
            label = "Were any codicils added to the will?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotBlank(groups = {ApplicationProbateGroup.class,
        ApplicationAdmonGroup.class}, message = "{willNumberOfCodicilsIsNull}")
    private final String willHasCodicils;

    @CCD(
            label = "How many were added to the will?",
            min = 1,
            typeOverride = FieldType.Number,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    private final String willNumberOfCodicils;

    @CCD(
            label = "Are any beneficiaries of the estate under the age of 18 years?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsEntitledMinorityIsNull}")
    private final String solsEntitledMinority;

    @CCD(
            label = "Have all of the executors, and residuary legatees or devisees in trust named in the will died or decided not to apply?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsDiedOrNotApplyingIsNull}")
    private final String solsDiedOrNotApplying;

    @CCD(
            label = "Are you named in the will as a residuary legatee or devisee?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsResiduaryIsNull}")
    private final String solsResiduary;

    @CCD(
            label = "The applicant is: ",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "solsResiduaryTypes",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final String solsResiduaryType;

    @CCD(
            label = "Does any life interest arise in respect of the estate?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsLifeInterestIsNull}")
    private final String solsLifeInterest;

    @CCD(
            label = "First name(s)",
            hint = "Include all middle names",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class, CaseworkerCaaCudAccess.class}
    )
    @NotBlank(groups = {ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class},
        message = "{primaryApplicantForenamesIsNull}")
    private String primaryApplicantForenames;

    @CCD(
            label = "Last name(s)",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class, CaseworkerCaaCudAccess.class}
    )
    @NotBlank(groups = {ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class},
        message = "{primaryApplicantSurnameIsNull}")
    private String primaryApplicantSurname;

    @CCD(
            label = "Is this name different to how they are named in the will?",
            hint = "This could be because they changed their name, part of their name was not included, or their name was spelled differently in the will.",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private String primaryApplicantHasAlias;

    @CCD(
            label = "Enter their full name as it appears in the will",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudCitizenCuAccess.class, SystemupdateCuAccess.class}
    )
    private final String solsExecutorAliasNames;

    @CCD(
            label = "Are they applying?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    @NotBlank(groups = {ApplicationIntestacyGroup.class}, message = "{primaryApplicantIsApplyingIsNull}")
    private String primaryApplicantIsApplying;

    @CCD(
            label = "Why aren't they applying?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "notApplyingExecutorReasonFixedList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    private String solsPrimaryExecutorNotApplyingReason;

    @CCD(
            label = "Applicant address",
            typeOverride = FieldType.AddressUK,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotNull(groups = {ApplicationAdmonGroup.class,
        ApplicationIntestacyGroup.class}, message = "{primaryApplicantAddressIsNull}")
    private SolsAddress primaryApplicantAddress;

    @CCD(
            label = "Applicant email address",
            regex = "[a-zA-Z0-9#$%'+=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@[a-zA-Z0-9](?:[a-zA-Z0-9-.]{0,30}[a-zA-Z0-9])?\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,10}[a-zA-Z0-9])?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotBlank(groups = {ApplicationAdmonGroup.class,
        ApplicationIntestacyGroup.class}, message = "{primaryApplicantEmailAddressIsNull}")
    private String primaryApplicantEmailAddress;

    @CCD(
            label = "Are there any more executors?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotBlank(groups = {ApplicationProbateGroup.class}, message = "{otherExecutorExistsIsNull}")
    private final String otherExecutorExists;

    @CCD(
            label = "Additional executor",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "solAdditionalExecutor",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudCitizenCuAccess.class, SystemupdateCuAccess.class}
    )
    private final List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;

    @CCD(
            label = "Notes for this application",
            hint = "Tell us anything we need to know to process this application, for example extra documents you are including",
            typeOverride = FieldType.TextArea,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudCitizenCuAccess.class, SystemupdateCuAccess.class}
    )
    private final String solsAdditionalInfo;

    // EVENT = solicitorUpdateIntestacy
    @CCD(
            label = "Did the deceased leave a will?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotBlank(groups = {ApplicationProbateGroup.class,
        ApplicationAdmonGroup.class,
        ApplicationIntestacyGroup.class}, message = "{willExistsIsNull}")
    private final String willExists;

    @CCD(
            label = "What was the marital status of the deceased at the date of death?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "martialStatusFixedList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @NotNull(groups = {ApplicationIntestacyGroup.class}, message = "{deceasedMaritalStatusIsNull}")
    private final String deceasedMaritalStatus;

    @CCD(
            label = "What is the applicant’s relationship to the deceased?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "solsRelationshipsToDeceased",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    @NotBlank(groups = {ApplicationIntestacyGroup.class}, message = "{solsApplicantRelationshipToDeceasedIsNull}")
    private final String solsApplicantRelationshipToDeceased;

    @CCD(
            label = "Is there a living spouse or civil partner who is renouncing their right to apply now and in the future?",
            hint = "If a spouse is unable to apply because they lack mental capacity you will not be able to continue this application online.",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final String solsSpouseOrCivilRenouncing;

    @CCD(
            label = "Did the adoption take place in England or Wales?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final String solsAdoptedEnglandOrWales;

    @CCD(
            label = "Is there a minority interest in the estate?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    @NotBlank(groups = {ApplicationIntestacyGroup.class}, message = "{solsMinorityInterestIsNull}")
    private final String solsMinorityInterest;

    @CCD(
            label = "Does the deceased have any other issue?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCrudSystemupdateCuAccess.class}
    )
    private final String solsApplicantSiblings;

    @CCD(
            label = "Do you wish to send an email notification for documents received?",
            hint = "Documents received email notification",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boEmailDocsReceivedNotificationRequested;

    @CCD(
            label = "Do you wish to send an email notification for documents received?",
            hint = "Documents received email notification",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boEmailDocsReceivedNotification;

    @CCD(
            label = "Do you wish to send an email notification for grant issued?",
            hint = "Grant issued email notification",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boEmailGrantIssuedNotificationRequested;

    @CCD(
            label = "Do you wish to send an email notification for grant issued?",
            hint = "Grant issued email notification",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boEmailGrantIssuedNotification;

    @CCD(
            label = "Is the grant to be sent to bulk printing?",
            hint = "note if you select no, the grant will need to printed locally and dealt with within the registry",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    @Builder.Default
    private final String boSendToBulkPrint = YES;

    @CCD(
            label = "Is the grant to be sent to bulk printing?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boSendToBulkPrintRequested;

    //EVENT = review
    @CCD(
            label = "Legal statement",
            typeOverride = FieldType.Document,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    private final DocumentLink solsLegalStatementDocument;

    @CCD(
            label = "Legal Statement",
            typeOverride = FieldType.Document,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class}
    )
    private final DocumentLink statementOfTruthDocument;

    @CCD(
            label = "Amended Legal Statement",
            regex = ".pdf",
            typeOverride = FieldType.Document,
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerRAccess.class, SuperuserCrudAccess.class, SystemupdateCuAccess.class}
    )
    private final DocumentLink amendedLegalStatement;

    @CCD(
            label = "Coversheet",
            typeOverride = FieldType.Document,
            access = {IssuerCuAccess.class, SolicitorCudAccess.class, SuperuserCudAccess.class}
    )
    private final DocumentLink solsCoversheetDocument;

    @CCD(
            label = "Documents generated",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ProbateDocument",
            access = {RparobotCudSystemupdateCuAccess.class, CaseadminCaseofficerIssuerRegistrarCudAccess.class, SchedulerSuperuserCrudAccess.class, CharityRAccess.class}
    )
    @Builder.Default
    private final List<CollectionMember<Document>> probateDocumentsGenerated = new ArrayList<>();

    @CCD(
            label = "Notifications generated",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ProbateDocument",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuCitizenCudAccess.class}
    )
    @Builder.Default
    private final List<CollectionMember<Document>> probateNotificationsGenerated = new ArrayList<>();

    @CCD(label = "${matches}", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    @Builder.Default
    private final List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    @CCD(
            label = "Documents uploaded",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "documentUpload",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CharityRAccess.class, CitizenCudAccess.class}
    )
    private final List<CollectionMember<UploadDocument>> boDocumentsUploaded;

    @CCD(
            label = "Do you need to make any changes to the legal statement and declaration?",
            hint = "You can make changes to your legal statement up to and until you submit your application.",
            typeOverride = FieldType.YesOrNo,
            access = {SolicitorCrudAccess.class}
    )
    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTNeedToUpdateIsNull}")
    private final String solsSOTNeedToUpdate;

    @CCD(
            label = "Select file to upload",
            regex = ".jpg,.jpeg,.bmp,.tiff,.png,.pdf",
            typeOverride = FieldType.Document,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CharityRAccess.class}
    )
    private final DocumentLink solsLegalStatementUpload;

    @CCD(
            label = "When did you send the IHT400 and IHT421 to HMRC?",
            hint = "For example, 12 11 2020",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final LocalDate solsIHT400Date;

    @CCD(
            label = "Probate practitioner name",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class, CaseworkerApproverCudAccess.class, SolicitorCudAccess.class}
    )
    private final String solsSOTName;

    @CCD(
            label = "Probate practitioner first name(s)",
            hint = "Include all middle names",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CaseworkerApproverCrudAccess.class}
    )
    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTForenamesIsNull}")
    private final String solsSOTForenames;

    @CCD(
            label = "Probate practitioner last name(s)",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CaseworkerApproverCrudAccess.class}
    )
    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTSurnameIsNull}")
    private final String solsSOTSurname;

    @CCD(
            label = "Probate practitioner job title",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    private final String solsSOTJobTitle;

    @CCD(
            label = " ",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final String solsReviewSOTConfirm;

    @CCD(
            label = " ",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final String solsReviewSOTConfirmCheckbox1Names;

    @CCD(
            label = " ",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    private final String solsReviewSOTConfirmCheckbox2Names;

    @CCD(
            label = "How many extra UK copies of the grant do you need?",
            min = 0,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @Min(value = 0, groups = {ApplicationReviewedGroup.class, AmendCaseDetailsGroup.class}, message =
        "{extraCopiesOfGrantIsNegative}")
    private final Long extraCopiesOfGrant;

    @CCD(
            label = "How many sealed and certified copies of the grant do you need for use outside the UK?",
            hint = "Certain non-UK territories may need your client to provide a sealed and certified grant. Check if this is needed for your case, as a UK grant is still valid in some non-UK countries and jurisdictions",
            min = 0,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    @Min(value = 0, groups = {ApplicationReviewedGroup.class, AmendCaseDetailsGroup.class}, message =
        "{outsideUKGrantCopiesIsNegative}")
    private final Long outsideUKGrantCopies;

    @CCD(
            label = "How do you want to pay?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "solsPaymentMethods",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class, SolicitorCudAccess.class}
    )
    private final String solsPaymentMethods;

    @CCD(
            label = "Fee account number",
            hint = "Enter your account number without the letters PBA, for example 001234",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class}
    )
    private final String solsFeeAccountNumber;

    @CCD(
            label = "Case has been printed and entered into ProbateMan",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "casePrintedTypes",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CaseworkerCaaCrudAccess.class, SolicitorRAccess.class}
    )
    private final String casePrinted;

    @CCD(
            label = "Case stop reason",
            hint = "Reason for stopping the case",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "boCaseStopReason",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final List<CollectionMember<StopReason>> boCaseStopReasonList;

    @CCD(
            label = "Handoff reason",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "boHandoffReason",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, SolicitorCruCitizenCuAccess.class, CaseworkerWaTaskConfigurationCruAccess.class}
    )
    private List<CollectionMember<HandoffReason>> boHandoffReasonList;

    @CCD(
            label = "What information do you need?",
            hint = "Enter details on what information or documents you need from the applicant",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boStopDetails;

    @CCD(
            label = "IHT Reference Number",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String ihtReferenceNumber;

    @CCD(
            label = "IHT Form completed Online",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCudAccess.class}
    )
    private final String ihtFormCompletedOnline;


    //next steps
    @CCD(
            label = "Application fee",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{applicationFeeIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{applicationFeeNegative}")
    private final BigDecimal applicationFee;

    @CCD(
            label = "Fee for additional UK copies",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{feeForUkCopiesIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{feeForUkCopiesNegative}")
    private final BigDecimal feeForUkCopies;

    @CCD(
            label = "Fee for certified copies",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{feeForNonUkCopiesIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{feeForNonUkCopiesNegative}")
    private final BigDecimal feeForNonUkCopies;

    @CCD(
            label = "Fee Amount",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{totalFeeIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{totalFeeNegative}")
    private final BigDecimal totalFee;
    @CCD(
            label = "Application type",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "applicationTypes",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CharityRAccess.class, SolicitorCAccess.class, CitizenCAccess.class}
    )
    private final ApplicationType applicationType;
    @CCD(
            label = "Registry location",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "registryLocations",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CharityRAccess.class, SolicitorCAccess.class, CaseworkerWaTaskConfigurationCruAccess.class, CitizenCuAccess.class}
    )
    private final String registryLocation;
    @CCD(
            label = "Payment Reference Number",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private final String paymentReferenceNumber;
    @CCD(
            label = "Declaration",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private final Declaration declaration;
    @CCD(
            label = "LegalStatement",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
    )
    private final LegalStatement legalStatement;
    @CCD(
            label = "Deceased Married After Will Or Codicil Date",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class}
    )
    private final String deceasedMarriedAfterWillOrCodicilDate;
    @CCD(
            label = "Deceased Alias Names",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuCitizenCudAccess.class}
    )
    private final List<CollectionMember<ProbateAliasName>> deceasedAliasNameList;
    @CCD(
            label = "Applicant phone number",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    private String primaryApplicantPhoneNumber;
    @CCD(
            label = "Does the applicant required to send documents?",
            typeOverride = FieldType.YesOrNo,
            access = {CitizenCruAccess.class}
    )
    private final String primaryApplicantNotRequiredToSendDocuments;
    // EVENT = Amend case details
    @CCD(
            label = "Title",
            hint = "Deceased title (eg Captain)",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String boDeceasedTitle;
    @CCD(
            label = "Honours",
            hint = "Deceased honours (eg OBE)",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String boDeceasedHonours;
    @CCD(
            label = "Will message",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String boWillMessage;
    @CCD(
            label = "Executor limitation",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String boExecutorLimitation;
    @CCD(
            label = "Admin clause limitation",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String boAdminClauseLimitation;
    @CCD(
            label = "Limitation",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String boLimitationText;
    @CCD(label = "Online payments received", access = {CaseadminRPlus8RolesHgcfgrAccess.class})
    private final List<CollectionMember<Payment>> payments;
    @CCD(
            label = "Does all information in the application and Legal Statement match the Will and Death Certificate?",
            typeOverride = FieldType.YesOrNo,
            access = {RparobotCudSystemupdateCuAccess.class, CaseadminCaseofficerIssuerRegistrarSuperuserCrudAccess.class, SchedulerCudAccess.class}
    )
    private final String boExaminationChecklistQ1;
    @CCD(
            label = "Have all key checks been completed?",
            typeOverride = FieldType.YesOrNo,
            access = {RparobotCudSystemupdateCuAccess.class, CaseadminCaseofficerIssuerRegistrarSuperuserCrudAccess.class, SchedulerCudAccess.class}
    )
    private final String boExaminationChecklistQ2;
    @CCD(
            label = "Does this case require a review by QA?",
            typeOverride = FieldType.YesOrNo,
            access = {RparobotCudSystemupdateCuAccess.class, CaseadminCaseofficerIssuerRegistrarSuperuserCrudAccess.class, SchedulerCudAccess.class}
    )
    private final String boExaminationChecklistRequestQA;
    @CCD(
            label = "Date application was submitted",
            hint = "This is the date that the application is received by the Registry either via post or digital",
            typeOverride = FieldType.Date,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CaseworkerCaaCudAccess.class, SolicitorCudAccess.class, CitizenCuAccess.class}
    )
    private String applicationSubmittedDate;
    @CCD(
            label = "Documents scanned",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, SchedulerSuperuserCrudAccess.class, CharityRAccess.class, PcqextractorRAccess.class, RparobotCudAccess.class, SystemupdateCudAccess.class}
    )
    private final List<CollectionMember<ScannedDocument>> scannedDocuments;
    @CCD(
            label = "Supplementary evidence handled",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess.class, RparobotSystemupdateCruAccess.class, CaseworkerCaaCudAccess.class, SchedulerCruAccess.class, CaseworkerWaTaskConfigurationCruAccess.class}
    )
    private String evidenceHandled;
    @CCD(ignore = true)
    private transient String attachDocuments;
    @CCD(
            label = "What case type is this?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "caseTypeFixedList",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CharityRAccess.class, CaseworkerWaTaskConfigurationCruAccess.class, CitizenCuAccess.class}
    )
    private final String caseType;
    @CCD(
            label = "Is this a paper form?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminRPlus8RolesVxljipAccess.class}
    )
    private final String paperForm;
    @CCD(
            label = "What is the channel choice?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "channelChoiceFixedList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private String channelChoice;
    @CCD(
            label = "Is the language preference Welsh?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCuAccess.class}
    )
    private final String languagePreferenceWelsh;
    @CCD(
            label = "Enter their full name as it appears on the will",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private String primaryApplicantAlias;
    @CCD(
            label = "What is the reason for the applicant’s name difference on the will?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "aliasReasonList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String primaryApplicantAliasReason;
    @CCD(
            label = "What is the ‘other’ reason for the applicant name difference on the will? ",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String primaryApplicantOtherReason;
    @CCD(
            label = "Is the applicant name written the same way as on the will?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private final String primaryApplicantSameWillName;
    //paper form case creator fields
    @CCD(label = "Applicant second phone number", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String primaryApplicantSecondPhoneNumber;
    @CCD(
            label = "Relationship to the deceased",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "relationshipToDeceasedFixedList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String primaryApplicantRelationshipToDeceased;
    @CCD(label = "Please Specify", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String paRelationshipToDeceasedOther;
    @CCD(
            label = "Is the will dated before 4 April 1988?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String willDatedBeforeApril;
    @CCD(
            label = "Did the person who has died marry or enter a civil partnership?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String deceasedEnterMarriageOrCP;
    @CCD(
            label = "Date of marriage or civil partnership",
            typeOverride = FieldType.Date,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String dateOfMarriageOrCP;
    @CCD(
            label = "Date of divorce, civil partnership dissolved or judicial separation",
            typeOverride = FieldType.Date,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String dateOfDivorcedCPJudicially;
    @CCD(
            label = "Did the person who died have any wills that were made oustide of England and Wales",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String willsOutsideOfUK;
    @CCD(
            label = "What is the name of the court where the Decree Absolute, Decree of Dissolution of Partnership or Decree of Judicial Separation was issued?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String courtOfDecree;
    @CCD(
            label = "Is there anyone under 18 years old who recieves a gift in the will or a codicil?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String willGiftUnderEighteen;
    @CCD(
            label = "Are you applying as an attorney on behalf of one or more people who are entitled to apply for Grant of Representation?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String applyingAsAnAttorney;
    @CCD(
            label = "Please give the full names of the person or people on whose behalf you are applying and their address",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "AttorneyNamesAndAddress",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyOnBehalfOfNameAndAddress;
    @CCD(
            label = "Is a person on whose behalf you are applying unable to make a decision for themselves due to an impairment of or a disturbance in the functioning of their mind or brain?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String mentalCapacity;
    @CCD(
            label = "Has anyone been appointed by the Court of Protection to act on behalf of a person on whose behalf you are applying",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String courtOfProtection;
    @CCD(
            label = "Has someone been appointed an attorney under the Enduring Power of Attory (EPA) or a Property and Financial Affairs Lasting Power of Attorney (LPA)",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String epaOrLpa;
    @CCD(
            label = "Has the Enduring Power of Attorney (EPA) been registered with the office of the Public Guardian?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String epaRegistered;
    @CCD(
            label = "In what country was the deceased domiciled at the date of death?",
            hint = "Please add the state or province where relevant. For example: Florida, USA.",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudAccess.class}
    )
    private final String domicilityCountry;
    @CCD(
            label = "What does the estate in England and Wales of the person has died consist of?",
            hint = "Only answer this if the deceased is not domiciled in England and Wales.",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "estateItems",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final List<CollectionMember<EstateItem>> ukEstate;
    @CCD(
            label = "Has an entrusting document, a succession certificate or an inheritance certificate been issued in the country of domicile of the person who has died? ",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String domicilityIHTCert;
    @CCD(
            label = "In what capacity are the persons applying entitled to apply? ",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "entitledToApplyFixedList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String entitledToApply;
    @CCD(label = "Please specify", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String entitledToApplyOther;
    @CCD(
            label = "The undersigned declare that written notice has been given to all executors who have power reserved to them and are not making this application.",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String notifiedApplicants;
    @CCD(
            label = "Did the person who has died own any foreign assets?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String foreignAsset;
    @CCD(
            label = "Please specify how much",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String foreignAssetEstateValue;
    @CCD(
            label = "Was any relative of the person who has died legally adopted?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String adopted;
    @CCD(
            label = "Please name the legally adopted relatives and give their relationship to the person who has died.",
            hint = "Please state whether they were adopted into the family of the person who has died, or ‘adopted out’ (became part of someone else’s family). ",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "adopted",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final List<CollectionMember<AdoptedRelative>> adoptiveRelatives;
    @CCD(
            label = "Did the person who has died leave a surviving spouse or civil partner? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String spouseOrPartner;
    @CCD(
            label = "Were there any sons or daughters of the person who died survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String childrenSurvived;
    @CCD(
            label = "How many over 18 years old?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String childrenOverEighteenSurvived;
    @CCD(
            label = "How many under 18 years old?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String childrenUnderEighteenSurvived;
    @CCD(
            label = "Were there any sons or daughters of the person who has died who did not survive them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String childrenDied;
    @CCD(
            label = "How many over 18 years old?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String childrenDiedOverEighteen;
    @CCD(
            label = "How many under 18 years old?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String childrenDiedUnderEighteen;
    @CCD(
            label = "Were there any children of people in the previous question, survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String grandChildrenSurvived;
    @CCD(
            label = "How many over 18 years old?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String grandChildrenSurvivedOverEighteen;
    @CCD(
            label = "How many under 18 years old?",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private final String grandChildrenSurvivedUnderEighteen;
    @CCD(
            label = "Were there any parents of the person who has died survived them?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String parentsExistSurvived;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String parentsExistOverEighteenSurvived;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String parentsExistUnderEighteenSurvived;
    @CCD(
            label = "Were there any Whole-blood brothers or sisters of the person who has died survived them?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String wholeBloodSiblingsSurvived;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodSiblingsSurvivedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodSiblingsSurvivedUnderEighteen;
    @CCD(
            label = "Were there many Whole-blood brothers or sisters of the person who has died did not survive them?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String wholeBloodSiblingsDied;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodSiblingsDiedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodSiblingsDiedUnderEighteen;
    @CCD(
            label = "Were there any Children of people in the previous question, who survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String wholeBloodNeicesAndNephews;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodNeicesAndNephewsOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodNeicesAndNephewsUnderEighteen;
    @CCD(
            label = "Were there any Half-blood brothers or sisters of the person who has died survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String halfBloodSiblingsSurvived;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodSiblingsSurvivedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodSiblingsSurvivedUnderEighteen;
    @CCD(
            label = "Were there any Half-blood brothers or sisters of the person who has died did not survive them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String halfBloodSiblingsDied;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodSiblingsDiedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodSiblingsDiedUnderEighteen;
    @CCD(
            label = "Were there any Children of people mentioned in the previous question survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String halfBloodNeicesAndNephews;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodNeicesAndNephewsOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodNeicesAndNephewsUnderEighteen;
    @CCD(
            label = "Were there any Grandparents of the person who has died survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String grandparentsDied;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String grandparentsDiedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String grandparentsDiedUnderEighteen;
    @CCD(
            label = "Were there any Whole-blood uncles or aunts of the person who has died survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String wholeBloodUnclesAndAuntsSurvived;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodUnclesAndAuntsSurvivedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodUnclesAndAuntsSurvivedUnderEighteen;
    @CCD(
            label = "Were there any Whole-blood uncles or aunts of the person who has died did not survive them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String wholeBloodUnclesAndAuntsDied;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodUnclesAndAuntsDiedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodUnclesAndAuntsDiedUnderEighteen;
    @CCD(
            label = "Were there any Children of people mentioned in the previous question,  who survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String wholeBloodCousinsSurvived;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodCousinsSurvivedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String wholeBloodCousinsSurvivedUnderEighteen;
    @CCD(
            label = "Were there any Half-blood uncles or aunts of the person who has died survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String halfBloodUnclesAndAuntsSurvived;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodUnclesAndAuntsSurvivedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodUnclesAndAuntsSurvivedUnderEighteen;
    @CCD(
            label = "Were there any Half-blood uncles or aunts of the person who has died did not survived them ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String halfBloodUnclesAndAuntsDied;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodUnclesAndAuntsDiedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodUnclesAndAuntsDiedUnderEighteen;
    @CCD(
            label = "Were there any Children of people mentioned in the previous question, who survived them? ",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String halfBloodCousinsSurvived;
    @CCD(label = "How many over 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodCousinsSurvivedOverEighteen;
    @CCD(label = "How many under 18 years old?", access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class})
    private final String halfBloodCousinsSurvivedUnderEighteen;
    @CCD(
            label = "Application fee ",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String applicationFeePaperForm;
    @CCD(
            label = "Fees for copies ",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String feeForCopiesPaperForm;
    @CCD(
            label = "Total fees",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String totalFeePaperForm;
    @CCD(
            label = "Please select the correct payment method",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "paperPaymentMethodFixedList",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String paperPaymentMethod;
    @CCD(label = "Payment reference ", access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class})
    private final String paymentReferenceNumberPaperform;
    @CCD(
            label = "Send letter ID is:",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String bulkPrintSendLetterId;
    @CCD(
            label = "Bulk Print PDF size is:",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String bulkPrintPdfSize;
    @CCD(
            label = "What type of date of death is?",
            hint = "Use this to indicate if the exact date of death is not known",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "dateOfDeathTypeFixedList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CharityRAccess.class}
    )
    private final String dateOfDeathType;
    @CCD(
            label = "Which state do you wish to return the case to?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "resolveStopStateFixedList",
            access = {RparobotCrudSystemupdateCruAccess.class, CaseadminCaseofficerIssuerSchedulerCrudAccess.class, RegistrarCrudAccess.class}
    )
    private final String resolveStopState;
    @CCD(
            label = "State to transfer case to",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "transferToStateFixedList",
            access = {SuperuserCrudAccess.class}
    )
    private final String transferToState;
    @CCD(
            label = "Which state do you wish to return the case to?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "resolveCaveatStopStateFixedList",
            access = {DefaultAccess.class, SolicitorRAccess.class}
    )
    private final String resolveCaveatStopState;
    @CCD(
            label = "Is an order needed?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final String orderNeeded;
    @CCD(
            label = "Reason for reissuing the case",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ReissueExamineCase",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class}
    )
    private final List<CollectionMember<Reissue>> reissueReason;
    @CCD(
            label = "What notation should appear on the grant?",
            hint = "The notation will appear below the grant title",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "reissueNotationFixedList",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String reissueReasonNotation;
    @CCD(
            label = "Last date of reissue",
            typeOverride = FieldType.Date,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, CharityRAccess.class, SystemupdateCuAccess.class}
    )
    private final String latestGrantReissueDate;
    @CCD(
            label = "Do you need to include the legal declaration?",
            hint = "Only select ‘No’ if all of the information needs to be returned by post",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boStopDetailsDeclarationParagraph;
    @CCD(label = "Applying executor", access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class})
    private final List<CollectionMember<ExecutorsApplyingNotification>> executorsApplyingNotifications;
    @CCD(ignore = true)
    private final List<CollectionMember<CaseMatch>> legacySearchResultRows;
    @CCD(label = "ProbateMan Id", access = {CaseadminCPlus7RolesXmptngAccess.class})
    private final String recordId;
    @CCD(label = "id", access = {CaseadminCrPlus7RolesHiilikAccess.class})
    private final String legacyId;
    @CCD(label = "Legacy case type", access = {CaseadminCPlus7RolesXmptngAccess.class})
    private final String legacyType;
    @CCD(label = "Legacy case link", access = {CaseadminCPlus7RolesXmptngAccess.class})
    private final String legacyCaseViewUrl;
    @CCD(ignore = true)
    private final String resendDate;
    @CCD(
            label = "Do you wish to send a caveat stop notification?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boCaveatStopNotificationRequested;
    @CCD(
            label = "Do you wish to send a caveat stop notification?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boCaveatStopNotification;
    @CCD(
            label = "What is the case id of the caveat?",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boCaseStopCaveatId;
    @CCD(
            label = "Do you wish to send an email notification?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boCaveatStopEmailNotificationRequested;
    @CCD(
            label = "Do you wish to send an email notification?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boCaveatStopEmailNotification;
    @CCD(
            label = "Do you wish to send via bulk print?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boCaveatStopSendToBulkPrintRequested;
    @CCD(ignore = true)
    private final String boEmailGrantReIssuedNotificationRequested;
    @CCD(
            label = "Do you wish to send an email notification for grant reissued?",
            hint = "Grant issued email notification",
            typeOverride = FieldType.YesOrNo,
            access = {RparobotCrudSystemupdateCruAccess.class, CaseadminCaseofficerIssuerSchedulerCrudAccess.class, RegistrarCrudAccess.class}
    )
    private final String boEmailGrantReissuedNotification;
    @CCD(
            label = "Do you wish to send via bulk print?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    @Builder.Default
    private final String boCaveatStopSendToBulkPrint = YES;
    @CCD(
            label = "Is the reissued grant to be sent to bulk printing?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    @Builder.Default
    private final String boGrantReissueSendToBulkPrint = YES;
    @CCD(
            label = "Is the reissued grant to be sent to bulk printing?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boGrantReissueSendToBulkPrintRequested;
    @CCD(
            label = "Was the deceased divorced in England or Wales?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String deceasedDivorcedInEnglandOrWales;
    @CCD(
            label = "Was the applicant adoption in England or Wales?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String primaryApplicantAdoptionInEnglandOrWales;
    @CCD(
            label = "Why isn't the deceased's spouse applying?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "spouseNotApplyingReasonFixedList",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String deceasedSpouseNotApplyingReason;
    @CCD(
            label = "Did the deceased have any other children?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String deceasedOtherChildren;
    @CCD(
            label = "Are all of the deceased's children over eighteen?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String allDeceasedChildrenOverEighteen;
    @CCD(
            label = "Did any of the deceased's children die before deceased?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String anyDeceasedChildrenDieBeforeDeceased;
    @CCD(
            label = "Are any of the deceased's grand children under eighteen?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String anyDeceasedGrandChildrenUnderEighteen;
    @CCD(
            label = "Did the deceased have any children?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String deceasedAnyChildren;
    @CCD(
            label = "Assets outside uk",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCruCitizenCuAccess.class}
    )
    private final String deceasedHasAssetsOutsideUK;
    @CCD(
            label = "Do you wish to send an email notification for request of information?",
            hint = "Request information email notification",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boEmailRequestInfoNotificationRequested;
    @CCD(
            label = "Legal Statement Documents generated",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ProbateDocument",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    @Builder.Default
    private final List<CollectionMember<Document>> probateSotDocumentsGenerated = new ArrayList<>();
    @CCD(label = "Choose categories", access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class})
    private final Categories categories;
    @CCD(
            label = "Letter preview",
            typeOverride = FieldType.Document,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final DocumentLink previewLink;
    @CCD(
            label = "Do you wish to send an email notification for the request of information?",
            hint = "Request information email notification",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String boEmailRequestInfoNotification;
    @CCD(
            label = "Is the request of information to be sent to bulk printing?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    @Builder.Default
    private final String boRequestInfoSendToBulkPrint = YES;
    @CCD(
            label = "Is the request of information to be sent to bulk printing?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boRequestInfoSendToBulkPrintRequested;
    @CCD(
            label = "Is the request of information to be sent to bulk printing?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    @Builder.Default
    private final String boAssembleLetterSendToBulkPrint = YES;
    @CCD(
            label = "Is the request of information to be sent to bulk printing?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private final String boAssembleLetterSendToBulkPrintRequested;
    @CCD(
            label = "Executors that are applying ",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ExecutorApplying",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorRAccess.class, CitizenCudAccess.class}
    )
    @JsonProperty(value = "executorsApplying")
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying;
    @CCD(
            label = "Executors not applying",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "ExecutorNotApplying",
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorRAccess.class, CitizenCudAccess.class}
    )
    @JsonProperty(value = "executorsNotApplying")
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying;
    @CCD(ignore = true)
    private List<CollectionMember<AdditionalExecutorApplying>> executorsApplyingLegalStatement;
    @CCD(ignore = true)
    private List<CollectionMember<AdditionalExecutorNotApplying>> executorsNotApplyingLegalStatement;
    @CCD(
            label = "Bulk print send letter Ids",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    @Builder.Default
    private List<CollectionMember<BulkPrint>> bulkPrintId = new ArrayList<>();
    @CCD(
            label = "Enter any requested details",
            access = {CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess.class, RparobotSystemupdateCruAccess.class, SchedulerCrudAccess.class}
    )
    @Builder.Default
    private List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
    @CCD(label = "Bulk scan case reference", access = {CaseadminCrPlus7RolesCkpmppAccess.class})
    private String bulkScanCaseReference;
    @CCD(
            label = "Grant delayed notification date",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private LocalDate grantDelayedNotificationDate;
    @CCD(
            label = "Grant stopped date",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private LocalDate grantStoppedDate;
    @CCD(label = "Case escalated date", access = {DefaultAccess.class, RparobotCrudAccess.class})
    private LocalDate escalatedDate;
    @CCD(
            label = "Select the reason for the escalation",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "registrarEscalateReasonFixedList",
            access = {CaseadminCaseofficerIssuerRegistrarSuperuserCrudAccess.class}
    )
    private RegistrarEscalateReason registrarEscalateReason;
    @CCD(
            label = "SME Referral date",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class}
    )
    private LocalDate caseWorkerEscalationDate;
    @CCD(
            label = "Resolve SME Referral date",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class}
    )
    private LocalDate resolveCaseWorkerEscalationDate;
    @CCD(
            label = "Resolve SME Referral state to?",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "resolveEscalationStateFixedList",
            access = {DefaultAccess.class, RparobotCrudAccess.class}
    )
    private String resolveCaseWorkerEscalationState;
    @CCD(
            label = "Has a grant delayed notification been identified?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private String grantDelayedNotificationIdentified;
    @CCD(
            label = "Has a grant delayed notification been sent?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCuAccess.class}
    )
    private String grantDelayedNotificationSent;
    @CCD(label = "Grant stopped date", access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class})
    private LocalDate grantAwaitingDocumentationNotificationDate;
    @CCD(
            label = "Has a grant awaiting documentation notification been sent?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private String grantAwaitingDocumentatioNotificationSent;
    @CCD(
            label = "PCQ ID",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class, PcqextractorRAccess.class, SolicitorCuAccess.class}
    )
    private String pcqId;
    @CCD(label = "Bulk Scan Envelopes", access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class})
    @Builder.Default
    private final List<CollectionMember<BulkScanEnvelope>> bulkScanEnvelopes = new ArrayList<>();
    @CCD(
            label = "Which document do you wish to reprint?",
            typeOverride = FieldType.DynamicList,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private DynamicList reprintDocument;
    @CCD(
            label = "How many copies are needed?",
            min = 0,
            typeOverride = FieldType.Number,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private String reprintNumberOfCopies;
    @CCD(
            label = "Please select the details you wish to amend",
            typeOverride = FieldType.DynamicList,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private DynamicList solsAmendLegalStatmentSelect;
    @CCD(
            label = "Declaration Checkbox",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private String declarationCheckbox;
    @CCD(
            label = "IHT gross value field as entered by the user",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
    )
    private String ihtGrossValueField;
    @CCD(
            label = "IHT net value field as entered by the user",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
    )
    private String ihtNetValueField;
    @CCD(
            label = "Number of executors",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private Long numberOfExecutors;
    @CCD(
            label = "Number of applicants",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private Long numberOfApplicants;
    @CCD(
            label = "Legal Declaration",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private String legalDeclarationJson;
    @CCD(
            label = "Check Answers Summary",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private String checkAnswersSummaryJson;
    @CCD(
            label = "Registry Address",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private String registryAddress;
    @CCD(
            label = "Registry Email Address",
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
    )
    private String registryEmailAddress;
    @CCD(
            label = "Case handed off to legacy site",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, CitizenCuAccess.class}
    )
    private String caseHandedOffToLegacySite;
    @CCD(
            label = "Deceased",
            access = {DefaultAccess.class, RparobotCudAccess.class, SystemupdateCrudAccess.class, CitizenCudAccess.class}
    )
    private final List<CollectionMember<DeathRecord>> deathRecords;
    @CCD(
            label = "Did the person who died leave any written wishes",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String deceasedWrittenWishes;
    @CCD(
            label = "Do the codicils have any visible damages or marks?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String codicilsHasVisibleDamage;
    @CCD(
            label = "Select types of codicils damage",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final Damage codicilsDamage;
    @CCD(
            label = "Do you know why the codicils have visible damages or marks?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String codicilsDamageReasonKnown;
    @CCD(
            label = "Reason for the visible damages or marks to the codicils",
            typeOverride = FieldType.TextArea,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String codicilsDamageReasonDescription;
    @CCD(
            label = "Do you know who may have made visible damages or marks to the codicils?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String codicilsDamageCulpritKnown;
    @CCD(
            label = "Name of the person who may have made visible damages or marks to the codicils",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final CombinedName codicilsDamageCulpritName;
    @CCD(
            label = "Do you know roughly when the visible damages or marks appeared on the codicils?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String codicilsDamageDateKnown;
    @CCD(
            label = "Date of roughly when the damages or marks happened to the codicils",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String codicilsDamageDate;
    @CCD(
            label = "Does the will have visible damage?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String willHasVisibleDamage;
    @CCD(
            label = "Select types of will damage",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final Damage willDamage;
    @CCD(
            label = "Do you know why the will has damages or marks",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String willDamageReasonKnown;
    @CCD(
            label = "Reason for the damages or marks to the will",
            typeOverride = FieldType.TextArea,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String willDamageReasonDescription;
    @CCD(
            label = "Do you know who may have made damages or marks to the will?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String willDamageCulpritKnown;
    @CCD(
            label = "Name of the person who may have made damages or marks to the will",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final CombinedName willDamageCulpritName;
    @CCD(
            label = "Do you know roughly when the damages or marks appeared on the will?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String willDamageDateKnown;
    @CCD(
            label = "Date of roughly when the damages or marks happened to the will",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorSystemupdateCitizenCudAccess.class}
    )
    private final String willDamageDate;
    @CCD(
            label = "Were any Inheritance Tax (IHT) forms completed to report the value of the estate?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess.class}
    )
    private String ihtFormEstateValuesCompleted;
    @CCD(
            label = "Which IHT forms did you complete to report the estate’s value?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "IHTFormEstate",
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess.class}
    )
    private String ihtFormEstate;
    @CCD(
            label = "Gross estate value for IHT",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess.class}
    )
    private BigDecimal ihtEstateGrossValue;
    @CCD(
            label = "Gross value of the IHT estate, as entered by the user",
            access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
    )
    private final String ihtEstateGrossValueField;
    @CCD(
            label = "Net estate value for IHT",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess.class}
    )
    private BigDecimal ihtEstateNetValue;
    @CCD(
            label = "Net value of the IHT estate, as entered by the user",
            access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
    )
    private final String ihtEstateNetValueField;
    @CCD(
            label = "Net qualifying estate value for IHT",
            hint = "If the Net qualifying value is Nil, enter 0",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess.class}
    )
    private BigDecimal ihtEstateNetQualifyingValue;
    @CCD(
            label = "Net qualifying value of the IHT estate, as entered by the user",
            access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
    )
    private final String ihtEstateNetQualifyingValueField;
    @CCD(
            label = "Did the deceased have a late husband, wife or civil partner?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, SolicitorCrudCitizenCuAccess.class, RparobotCudAccess.class, SystemupdateCudAccess.class}
    )
    private String deceasedHadLateSpouseOrCivilPartner;
    @CCD(
            label = "Are you claiming the unused IHT allowance (‘nil-rate band’) of the deceased’s late husband, wife or civil partner?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess.class, SystemupdateCuCitizenCudAccess.class, RparobotCuAccess.class, SchedulerCruAccess.class, SolicitorCruAccess.class}
    )
    private String ihtUnusedAllowanceClaimed;

    @CCD(label = "Deceased", access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class})
    private final DeathRecord deathRecord;
    @CCD(label = "Number of death records", access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class})
    private final Integer numberOfDeathRecords;
    @CCD(
            label = "Moved to Dormant Date and Time",
            typeOverride = FieldType.DateTime,
            access = {SchedulerSuperuserCrudAccess.class, IssuerRAccess.class}
    )
    private final String moveToDormantDateTime;
    @CCD(
            label = "Last Modified date for Dormant",
            access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class, CaseworkerApproverCrudAccess.class}
    )
    private final LocalDateTime lastModifiedDateForDormant;
    @CCD(
            label = "What type of letter do you want to assemble?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "LetterType",
            access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class}
    )
    private final String letterType;
    @CCD(label = "Caseworker", access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class})
    private final String caseworkerName;
    @CCD(
            label = "Blank letter",
            typeOverride = FieldType.TextArea,
            access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class}
    )
    private final String letterText;
    @CCD(
            label = "Do you want to include a statement of truth?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class}
    )
    private final String includeStatementOfTruth;
    @CCD(
            label = "When was the last scanned or uploaded doc added?",
            hint = "For example, 31 03 2020",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SolicitorCudAccess.class, SystemupdateCudAccess.class}
    )
    private LocalDate lastEvidenceAddedDate;
    @CCD(
            label = "Has a document been scanned or uploaded since the case was stopped?",
            typeOverride = FieldType.YesOrNo,
            access = {IssuerCrudAccess.class}
    )
    private String documentUploadedAfterCaseStopped;
    @CCD(
            label = "Has a document received documentation notification been sent?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class, SolicitorRAccess.class, CitizenRAccess.class}
    )
    private String documentsReceivedNotificationSent;
    @CCD(
            label = "Enter the unique probate code",
            access = {RparobotCudSolicitorCrudSystemupdateCudCitizenCudAccess.class, CaseadminCaseofficerIssuerSchedulerCrudAccess.class, RegistrarCruAccess.class, SuperuserCrudAccess.class}
    )
    private String uniqueProbateCodeId;
    @CCD(
            label = "Have you received a letter or email from HMRC with a unique probate code relating to this application?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCudSystemupdateCuAccess.class, SolicitorCrudCitizenCudAccess.class}
    )
    private String hmrcLetterId;
    @CCD(
            label = "Is the deceased name written the same way as on the will?",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCudAccess.class}
    )
    private String deceasedAnyOtherNameOnWill;
    @CCD(
            label = "Enter their first name(s) as it appears on the will",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCudAccess.class}
    )
    private String deceasedAliasFirstNameOnWill;
    @CCD(
            label = "Enter their last name(s) as it appears on the will",
            access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCudAccess.class}
    )
    private String deceasedAliasLastNameOnWill;
    @CCD(
            label = "Net value of the estate for probate",
            typeOverride = FieldType.MoneyGBP,
            access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
    )
    @Min(value = 0, groups = {ApplicationUpdatedGroup.class}, message = "{ihtNetNegative}")
    private final BigDecimal ihtFormNetValue;
    @CCD(
            label = "Caseworker Forename(s)",
            access = {SchedulerSuperuserCudAccess.class, CaseadminCudAccess.class, CaseofficerCudAccess.class, IssuerCudAccess.class}
    )
    private final String lastModifiedCaseworkerForenames;
    @CCD(
            label = "Caseworker Surname(s)",
            access = {SchedulerSuperuserCudAccess.class, CaseadminCudAccess.class, CaseofficerCudAccess.class, IssuerCudAccess.class}
    )
    private final String lastModifiedCaseworkerSurname;

    @CCD(
            label = "Registrar directions",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, SchedulerSuperuserSystemupdateCudAccess.class}
    )
    @Builder.Default
    private final List<CollectionMember<RegistrarDirection>> registrarDirections = new ArrayList<>();
    @CCD(label = "Registrar direction", access = {DefaultAccess.class, SystemupdateCrudAccess.class})
    private RegistrarDirection registrarDirectionToAdd;

    //transient in-event vars
    @CCD(label = "All original Documents", access = {SuperuserCrudAccess.class})
    private final OriginalDocuments originalDocuments;

    @CCD(
            label = "Change Of Representatives",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, SchedulerSuperuserSystemupdateCudAccess.class, CaseworkerApproverCudAccess.class, SolicitorCudAccess.class}
    )
    @Builder.Default
    private final List<CollectionMember<ChangeOfRepresentative>> changeOfRepresentatives = new ArrayList<>();
    @CCD(
            label = "Change Of Representative",
            access = {DefaultAccess.class, CaseworkerApproverCrudAccess.class, SolicitorCrudAccess.class, SystemupdateCrudAccess.class}
    )
    private ChangeOfRepresentative changeOfRepresentative;
    @CCD(
            label = "Removed Representative",
            access = {DefaultAccess.class, CaseworkerApproverCaseworkerCaaSystemupdateCrudAccess.class}
    )
    private RemovedRepresentative removedRepresentative;
    @CCD(
            label = "Change Organisation Request",
            access = {APPLICANTSOLICITORCruAccess.class, CaseworkerApproverCrudAccess.class, CaseworkerCaaCruAccess.class}
    )
    private ChangeOrganisationRequest changeOrganisationRequestField;
    @CCD(
            label = "Do you need any information?",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "informationNeededFixedList",
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenRAccess.class}
    )
    private final String informationNeeded;
    @CCD(
            label = "Does any of the information need to be returned by post?",
            hint = "You can find a list of documents that need to be returned by post on knowledge bank",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class, CitizenRAccess.class}
    )
    private final String informationNeededByPost;
    @CCD(
            label = "Citizen's response",
            typeOverride = FieldType.TextArea,
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerSuperuserSystemupdateCitizenCrudAccess.class}
    )
    private final String citizenResponse;
    @CCD(
            label = "I'm having trouble uploading some or all of my documents",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerSuperuserSystemupdateCitizenCrudAccess.class}
    )
    private final String documentUploadIssue;
    @CCD(label = "Is Save and Close", typeOverride = FieldType.YesOrNo, access = {CitizenCrudAccess.class})
    private final String isSaveAndClose;
    @CCD(
            label = "I confirm that I understand and accept this declaration",
            typeOverride = FieldType.YesOrNo,
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerSuperuserSystemupdateCitizenCrudAccess.class}
    )
    private final String citizenResponseCheckbox;
    @CCD(
            label = "Response Submitted Date",
            typeOverride = FieldType.Date,
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerSuperuserSystemupdateCitizenCrudAccess.class}
    )
    private final String expectedResponseDate;
    @CCD(
            label = "Citizen Documents uploaded",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "documentUpload",
            access = {DefaultAccess.class, SystemupdateCrudAccess.class, CitizenCrudAccess.class}
    )
    private final List<CollectionMember<UploadDocument>> citizenDocumentsUploaded;
    @CCD(
            label = "Citizen's response",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "citizenHubResponse",
            access = {SchedulerSuperuserSystemupdateCudAccess.class, CitizenCudAccess.class}
    )
    private List<CollectionMember<CitizenResponse>> citizenResponses;
    @CCD(
            label = "Executors named",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
    )
    private final String executorsNamed;
    @CCD(
            label = "First stop reminder sent date",
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerCrudSolicitorRSuperuserRSystemupdateCrudAccess.class}
    )
    private LocalDate firstStopReminderSentDate;
    @CCD(
            label = "First Redec reminder sent date",
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerCrudSolicitorRSuperuserRSystemupdateCrudAccess.class}
    )
    private LocalDate firstRedecReminderSentDate;
    @CCD(
            label = "Supplementary evidence handled Date",
            typeOverride = FieldType.Date,
            access = {CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess.class, RparobotSystemupdateCruAccess.class, CaseworkerCaaCruAccess.class, SchedulerCruAccess.class, CaseworkerWaTaskConfigurationCruAccess.class}
    )
    private final String evidenceHandledDate;

    @CCD(ignore = true)
    private TTL ttl;

    @CCD(
            label = "Modified OCR Fields",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, SuperuserCudAccess.class, SystemupdateCudAccess.class}
    )
    private final List<CollectionMember<ModifiedOCRField>> modifiedOCRFieldList;
    @CCD(
            label = "Warnings",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "Text",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, SuperuserCudAccess.class, SystemupdateCudAccess.class}
    )
    private final List<CollectionMember<String>> autoCaseWarnings;
    @CCD(
            label = "Documents uploaded",
            access = {DefaultAccess.class, CharityRAccess.class, SystemupdateCruAccess.class}
    )
    private final UploadDocument cwDocumentUpload;
    @CCD(
            label = "Request Information upload docs",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "documentUpload",
            access = {CaseadminCaseofficerIssuerRegistrarCudAccess.class, SchedulerSuperuserCudAccess.class, CharityRAccess.class, SystemupdateCuAccess.class}
    )
    private final List<CollectionMember<UploadDocument>> cwDocumentUploadedList;
    @CCD(
            label = "Do you want to attach a file to the Request for Information?",
            typeOverride = FieldType.YesOrNo,
            access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
    )
    private final String uploadFileCheck;

    /**
     * This is only intended for use during migrations and should not be persisted into the case record.
     */
    @CCD(
            label = "Field to provide metadata where needed for callback handling during migration. Not intented to be persisted to the case data.",
            access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerCrudSolicitorRSuperuserRSystemupdateCrudAccess.class}
    )
    private final String migrationCallbackMetadata;

    @CCD(ignore = true)
    private final String caseNameHmctsInternal;

    // @Getter(lazy = true)
    // private final String reissueDateFormatted = convertDate(reissueDate);

    public boolean isPrimaryApplicantApplying() {
        return YES.equals(primaryApplicantIsApplying);
    }

    public boolean isPrimaryApplicantNotApplying() {
        return NO.equals(primaryApplicantIsApplying);
    }

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getPrimaryApplicantFullName() {
        return String.join(" ", primaryApplicantForenames, primaryApplicantSurname);
    }

    public String getValueForEmailNotifications(String emailNotifications) {
        return emailNotifications != null ? emailNotifications : getDefaultValueForEmailNotifications();
    }

    public String getDefaultValueForEmailNotifications() {
        return (primaryApplicantEmailAddress == null || primaryApplicantEmailAddress.isEmpty())
            && (solsSolicitorEmail == null || solsSolicitorEmail.isEmpty()) ? NO : YES;
    }

    public String getValueForCaveatStopEmailNotification() {
        return getBoCaveatStopEmailNotification() != null ? getBoCaveatStopEmailNotification() :
            getDefaultValueForCaveatStopEmailNotification();
    }

    public String getDefaultValueForCaveatStopEmailNotification() {
        return primaryApplicantEmailAddress == null || primaryApplicantEmailAddress.isEmpty() ? NO : YES;
    }

    public boolean isDocsReceivedEmailNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailDocsReceivedNotification()));
    }

    public boolean isGrantIssuedEmailNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailGrantIssuedNotification()));
    }

    public boolean isGrantReissuedEmailNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailGrantReissuedNotification()));
    }

    public boolean isBoEmailRequestInfoNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailRequestInfoNotification()));
    }

    public boolean isCaveatStopEmailNotificationRequested() {
        return YES.equals(getValueForCaveatStopEmailNotification());
    }

    public boolean isSendForBulkPrintingRequested() {
        return YES.equals(getBoSendToBulkPrint());
    }

    public boolean isSendForBulkPrintingRequestedGrantReIssued() {
        return YES.equals(getBoGrantReissueSendToBulkPrint());
    }

    public boolean isCaveatStopNotificationRequested() {
        return YES.equals(getBoCaveatStopNotification());
    }

    public boolean isCaveatStopSendToBulkPrintRequested() {
        return YES.equals(getBoCaveatStopSendToBulkPrint());
    }

    public boolean isBoRequestInfoSendToBulkPrintRequested() {
        return YES.equals(getBoRequestInfoSendToBulkPrint());
    }

    public boolean isBoAssembleLetterSendToBulkPrintRequested() {
        return YES.equals(getBoAssembleLetterSendToBulkPrint());
    }

    public LanguagePreference getLanguagePreference() {
        return getLanguagePreferenceWelsh() != null && YES.equals(getLanguagePreferenceWelsh())
            ? LanguagePreference.WELSH : LanguagePreference.ENGLISH;
    }

    public boolean isLanguagePreferenceWelsh() {
        return YES.equals(getLanguagePreferenceWelsh());
    }

    public void clearPrimaryApplicant() {
        log.debug("Clearing primary applicant information from CaseData");


        this.setPrimaryApplicantIsApplying(null);

        this.setPrimaryApplicantForenames(null);
        this.setPrimaryApplicantSurname(null);

        // This is to be consistent with the behaviour currently exhibited by the service when creating
        // a case with a non-NoneOfThese TitleAndClearingType.
        this.setPrimaryApplicantHasAlias(ANSWER_NO);
        this.setPrimaryApplicantAlias(null);


        // As above this is to be consistent with the behaviour currently exhibited by the service when
        // creating a case with a non-NoneOfThese TitleAndClearingType.
        final SolsAddress nullAddress = SolsAddress.builder().build();
        this.setPrimaryApplicantAddress(nullAddress);

        this.setPrimaryApplicantEmailAddress(null);
        this.setPrimaryApplicantPhoneNumber(null);
    }

    public String getIhtGrossValuePounds() {
        return getPoundValue(ihtGrossValue);
    }

    public String getIhtNetValuePounds() {
        return getPoundValue(ihtNetValue);
    }

    public String getIhtEstateGrossValuePounds() {
        return getPoundValue(ihtEstateGrossValue);
    }

    public String getIhtEstateNetValuePounds() {
        return getPoundValue(ihtEstateNetValue);
    }

    public String getIhtEstateNetQualifyingValuePounds() {
        return getPoundValue(ihtEstateNetQualifyingValue);
    }

    private String getPoundValue(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        final BigDecimal poundsValue = value.divideToIntegralValue(BigDecimal.valueOf(100L));
        return poundsValue.toString();
    }

    public void clearAdditionalExecutorList() {
        getSolsAdditionalExecutorList().clear();
    }

    public void clearSolsDeceasedAliasNamesList() {
        getSolsDeceasedAliasNamesList().clear();
    }

  // ==== ccd-definition-converter: synthesised definition-only fields (retrofit) ====
  @JsonProperty("SearchCriteria")
  @CCD(label = "Search Criteria", access = {GSProfileRAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.SearchCriteria searchCriteria;
  @CCD(label = "${taskList}", typeOverride = FieldType.Label, access = {SystemupdateCudAccess.class})
  private String taskListLabel;
  @CCD(
          label = "Custom History Viewer",
          typeOverride = FieldType.CaseHistoryViewer,
          access = {SystemupdateCuAccess.class}
  )
  private String customHistoryViewer;
  @CCD(
          label = "Custom Payment History Viewer",
          typeOverride = FieldType.CasePaymentHistoryViewer,
          access = {SystemupdateCuAccess.class}
  )
  private String customPaymentHistoryViewer;
  @CCD(
          label = "Application ID",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCrAccess.class}
  )
  private Integer applicationID;
  @CCD(
          label = "### What's the full name of the deceased? \nUse the name on the death certificate",
          hint = "Use the name on the death certificate",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String deceasedNameSection;
  @CCD(
          label = "By selecting yes, you are confirming that you only have a court sealed or notarised copy of the will because the original is being held by the court or notary and cannot be released",
          typeOverride = FieldType.Label
  )
  private String willAccessNotarialYesLabel;
  @CCD(
          label = "Date on the will",
          access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class}
  )
  private java.time.LocalDate willDate;
  @CCD(
          label = "When was the original will signed and witnessed?",
          hint = "If there is more than one will, you need to give the date of the most recent one.For example, 30 06 2016",
          access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class}
  )
  private java.time.LocalDate willSignedDate;
  @CCD(
          label = "Latest Codicil have a date ",
          access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo willLatestCodicilHasDate;
  @CCD(
          label = "Latest Codicil date",
          access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class}
  )
  private java.time.LocalDate willLatestCodicilDate;
  @CCD(
          label = "DOD is after switch date",
          access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo dateOfDeathAfterEstateSwitch;
  @CCD(
          label = "IHT400 switch",
          access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, RparobotSystemupdateCrudAccess.class, SchedulerSolicitorCrudAccess.class, SuperuserCrudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo iht400Switch;
  @CCD(label = "IHT Net value switch", access = {CaseadminCruPlus7RolesYzgfvaAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo ihtNetValueSwitch;
  @CCD(
          label = "Show the IHT400421 page",
          access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo showIht400421Page;
  @CCD(label = "What are the values of the estate for Inheritance tax?", typeOverride = FieldType.Label)
  private String ihtFormEstateValuesLabel;
  @CCD(label = "DON'T SHOW", access = {CitizenCrudAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo calcCheckCompleted;
  @CCD(
          label = "Welsh Declaration",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private Declaration welshDeclaration;
  @CCD(
          label = "Welsh LegalStatement",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
  )
  private LegalStatement welshLegalStatement;
  @CCD(
          label = "Soft Stop",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo softStop;
  @CCD(label = "Has data changed", access = {CitizenCrudAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo hasDataChanged;
  @CCD(
          label = "### How many extra official copies of the grant do you need for use in the UK?",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsExtraCopiesOfGrantLabel1;
  @CCD(
          label = "<p>You'll receive an official copy of a grant of probate with your application fee.</p><p>Order extra official copies of the grant if you need to send them to different asset holders, for example, a copy for banks, insurance policies, shares and property.</p><p>Extra official copies cost £2.00 each.</p>",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsExtraCopiesOfGrantLabel2;
  @CCD(
          label = "<details class=\"govuk-details\" data-module=\"govuk-details\"><summary class=\"govuk-details__summary\"><span class=\"govuk-details__summary-text\">What is an official copy?</span></summary><div class=\"govuk-details__text\"><p>It's a copy of the grant with a holographic silver seal on the front of it. A bank may ask you for a certified copy to release funds to you.</p><p>Official copies are used for assets in the UK only.</p></div></details>",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsExtraCopiesOfGrantLabel3;
  @CCD(
          label = "If you need any additional copies after you have submitted your application, they will cost £16 each.",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsExtraCopiesOfGrantLabel4;
  @CCD(
          label = "### How many extra certified copies of the grant do you need for use outside the UK?",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsOutsideUKGrantCopiesLabel1;
  @CCD(
          label = "<p>Order extra certified copies of the grant if you need to claim the estate outside the UK.</p><p>A certified copy will have the same features as the official copy and extra information confirming the details of the certification on the back of it.</p>",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsOutsideUKGrantCopiesLabel2;
  @CCD(
          label = "Extra certified copies cost £2 each. If you need any additional copies after you have submitted your application, they will cost £16 each.",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsOutsideUKGrantCopiesLabel3;
  @CCD(
          label = "Assets outside uk value",
          typeOverride = FieldType.MoneyGBP,
          access = {CaseadminCaseofficerIssuerRegistrarRparobotSchedulerSuperuserCudAccess.class, SystemupdateCitizenCuAccess.class, SolicitorCudAccess.class}
  )
  private String assetsOutsideNetValue;
  @CCD(label = "Executor name same as will?")
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo primaryApplicantNameSameAsWill;
  @CCD(
          label = "### You don't have a registered PBA\n\n### This case will not be saved\n\n## Register an existing PBA with MyHMCTS \n\nIf you want to pay with a different PBA, you or your organisation administrator will need to email MyHMCTSsupport@justice.gov.uk to ask for your PBA to be registered with your MyHMCTS account. You should include your organisation name and PBA number. \n\nIt can then take up to 3 days for your account to be updated. You’ll need to start your case again to pay the fee. \n\n## Apply to get a new PBA \n\nYou’ll need to provide details for you and your organisation, including the required credit limit for your account. \n\nOnce your account has been registered, you’ll be able to proceed with your case and pay the applicable fee. \n\nRead more information on <a href=\"https://www.gov.uk/guidance/hmcts-payment-by-account-for-online-services \">registering for PBA</a>.",
          typeOverride = FieldType.Label
  )
  private String solsNoPBAsLabel;
  @CCD(
          label = "<details class=\"govuk-details\" data-module=\"govuk-details\"><summary class=\"govuk-details__summary\"><span class=\"govuk-details__summary-text\">Pay with another PBA</span></summary><div class=\"govuk-details__text\"><div class=\"heading-h2\">If there’s 1 PBA listed</div>\n\nIf you want to pay with a different PBA, you or your organisation administrator will need to email MyHMCTSsupport@justice.gov.uk to ask for your PBA to be registered with your MyHMCTS account. You should include your organisation name and PBA number.\n\nIt can then take up to 3 days for your account to be updated. You’ll need to start your claim again to pay the fee\n\n<div class=\"heading-h2\">If there are 2 PBAs listed</div>\n\nYou won’t be able to add another PBA.\n\nYou can only submit this case online using one of the PBAs from the menu.\n\nIf you want to remove a PBA, you or your organisation administrator will need to email MyHMCTSsupport@justice.gov.uk. You should include the PBA number. It can then take up to 3 days for the PBA to be removed. We’ll send you confirmation.\n\nSelect ‘cancel’ below to discard the case.</div></details>",
          typeOverride = FieldType.Label
  )
  private String solsPayWithAnotherPBAsLabel;
  @CCD(
          label = "### By clicking the 'Save or Submit' button on the next page, you will be re-directed to amend the details of chosen option",
          typeOverride = FieldType.Label
  )
  private String solsAmendLegalStatmentInfo;
  @CCD(
          label = "AdditionalInfo",
          typeOverride = FieldType.TextArea,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsConfirmationAddInfo;
  @CCD(
          label = "At the time of their death was the deceased:",
          typeOverride = FieldType.FixedList,
          typeParameterOverride = "DomiciledDetails"
  )
  private DomiciledDetails deceasedDomiciledDet;
  @CCD(
          label = "## Submit grant of probate applications online\n\nYou must submit grant of probate applications online, unless an exemption applies.\n\nYou can submit applications for letters of administration (intestacy) and letters of administration with will annexed (admon will) online when certain conditions apply.\n\n<a href=\"https://www.gov.uk/guidance/probate-paper-applications-for-legal-professionals\" target=\"_blank\">Guidance on exemptions, conditions and when application must be submitted by paper</a>\n\nStep by step guidance on submitting an application online can be found at <a href=\"https://www.gov.uk/government/publications/myhmcts-how-to-apply-for-probate-online/apply-for-probate-with-myhmcts\" target=\"_blank\">Apply for probate with MyHMCTS</a>\n\n## If you have submitted an IHT400 form\nYou must wait to receive a letter or email from HMRC with a probate code. You will need this code to apply.\n\nHMRC will send out the letter or email up to 20 working days after you submit the IHT400 form. If you have not received the letter or email, you should contact HMRC. \n\n### Inheritance Tax helpline\n\nTelephone: 0300 123 1072\n\nMonday to Friday, 9am to 5pm\n\nClosed on bank holidays",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsStartPage;
  @CCD(
          label = "## Probate practitioner details\n\n",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudAccess.class, SolicitorCrudAccess.class, CitizenCruAccess.class}
  )
  private String solsApplyPage;
  @CCD(
          label = "Provide the name of the probate practitioner making this application. This includes solicitors or other qualified persons defined in <a href=\"https://www.legislation.gov.uk/uksi/1987/2024/article/2/made\" target=\"_blank\">Rule 2 of the Non-Contentious probate Rules 1987</a>, or chartered legal executives who are regulated by a professional legal regulator and have appropriate CILEX Regulation authorisation as a result of <a href=\"https://www.legislation.gov.uk/uksi/2014/2937/introduction/made\" target=\"_blank\">The Legal Services Act 2007 (Approved Regulator) (No.2) Order 2014</a> .\n\nSchedule 5 to The Legal Services Act 2007 further sets out the practitioners who are authorised and regulated to carry out probate activities. Schedule 3 to the Legal Services Act 2007 sets out those practitioners who are exempt but are able to conduct probate activities if all criteria as laid out in the Schedule are met.\n\nThis person will sign the <a href=\"https://www.gov.uk/government/publications/myhmcts-how-to-apply-for-probate-online/apply-for-probate-with-myhmcts#statement-of-truth\" target=\"_blank\">statement of truth</a>, confirming that they acting on behalf of the applicants approving the <a href=\"https://www.gov.uk/government/publications/myhmcts-how-to-apply-for-probate-online/apply-for-probate-with-myhmcts#legal-statement\" target=\"_blank\">legal statement</a>.",
          typeOverride = FieldType.Label,
          access = {SchedulerSolicitorCrudAccess.class}
  )
  private String solsPractitionerDtlsHelp;
  @CCD(
          label = "## Previously identified executors",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsPreviouslyIdentifiedExecs;
  @CCD(
          label = "## Previously identified executors",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsPreviouslyIdentifiedExecsCcdCopy;
  @CCD(
          label = "## Other executors\n\nUp to 4 executors can apply for probate.\n\nEnter all executors named in the will, including any who have died.\n\n If a trust corporation is applying as an executor, the trust corporation is classed as one executor, even where multiple nominees choose to sign the legal statement.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsExecutorInfoSection;
  @CCD(
          label = "* Check the information you've given. You can do this on the next pages\n* Review the legal statement and declaration\n* Get authorisation from your client to confirm and sign the statement of truth on their behalf",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsBeforeSubmitPage;
  @CCD(label = "# Details about the will", typeOverride = FieldType.Label)
  private String solsCYAWillDetails;
  @CCD(label = "# Details about the deceased estate", typeOverride = FieldType.Label)
  private String solsCYADeceasedEstateDetails;
  @CCD(label = "# Details about the executor", typeOverride = FieldType.Label)
  private String solsCYAExecutorDetails;
  @CCD(label = "# Details about the deceased", typeOverride = FieldType.Label)
  private String solsCYADeceasedDetails;
  @CCD(
          label = "Do you wish to change your answers to any of the above questions?",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo solsCYANeedToUpdate;
  @CCD(
          label = "Choose which set of information needs updating",
          typeOverride = FieldType.FixedList,
          typeParameterOverride = "checkYourAnswersEventTransitions",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private CheckYourAnswersEventTransitions solsCYAStateTransition;
  @CCD(
          label = "This is the legal statement and declaration. You can download it now for you and the applicants to review.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsReviewLegalStatement1;
  @CCD(
          label = "If all the applicants agree with the legal statement and declaration, the document needs to be signed (typed or handwritten signatures are acceptable). The applicants can sign it, or they can authorise you to sign on their behalf. Where the applicants choose to sign the legal statement themselves, the probate practitioner is not required to sign the legal statement, except in circumstances where you are also an  applicant who will be named on the grant.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsReviewLegalStatement2;
  @CCD(
          label = "If you are signing on behalf of the applicants, you only need to sign next to your name. The practitioner who signs the legal statement must be the qualified practitioner who will also sign the statement of truth.\n\n",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
  )
  private String solsReviewLegalStatement3;
  @CCD(
          label = "Where a probate practitioner signs the legal statement alone, the probate registry’s assumption is that you are signing and are authorised to sign on behalf of all applicants. If any applicants have not authorised you to sign on their behalf, they too should sign the legal statement.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
  )
  private String solsReviewLegalStatement4;
  @CCD(
          label = "The original legal statement and declaration must be sent along with your other documents. You may want to keep a copy for your own records.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
  )
  private String solsReviewLegalStatement5;
  @CCD(
          label = "You will sign a statement of truth on your client’s behalf.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsConfirmSignSOT2;
  @CCD(
          label = "## Upload legal statement (optional)",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsLegalStatementUploadLabel1;
  @CCD(
          label = "You now have the option to upload your signed legal statement. You can upload the signed legal statement as a PDF, a scan or a photo. You can use a phone to do this if it has a camera. If you choose to upload a scan or a photo, the image must be of good quality. No page of the legal statement should be missing, partial, or otherwise obscured in any way.",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsLegalStatementUploadLabel2;
  @CCD(
          label = "If the material you submit is not legible or is missing information, your application will be delayed if we require you to resubmit material to an acceptable standard.",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsLegalStatementUploadLabel3;
  @CCD(
          label = "If you do not wish to upload your legal statement, you can continue with your application. You must then send a signed copy of your legal statement along with any other supporting documentation required from you once you submit your application.",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsLegalStatementUploadLabel4;
  @CCD(
          label = "If you have amended the legal statement, you must notify us upon submission of your original documents. You should clearly highlight the changes in your cover sheet for examination purposes.",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsLegalStatementUploadLabel5;
  @CCD(
          label = "The maximum size of the file must not exceed 300MB. Acceptable file types are JPEG, BMP, TIFF, PNG and PDF.",
          typeOverride = FieldType.Label,
          access = {SolicitorRAccess.class}
  )
  private String solsLegalStatementUploadLabel6;
  @CCD(
          label = "Choose which set of information needs updating",
          typeOverride = FieldType.FixedList,
          typeParameterOverride = "checkYourAnswersEventTransitions",
          access = {SolicitorCrudAccess.class}
  )
  private CheckYourAnswersEventTransitions solsSOTStateTransition;
  @CCD(
          label = "The executor believes that all the information stated in the legal statement is true. They have authorised ${solsSolicitorFirmName} to sign a statement of truth on their behalf.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String solsConfirmSignSOT3;
  @CCD(label = " ", access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class})
  private java.util.Set<SOTReview1> solsReviewSOTConfirmCheckbox1;
  @CCD(label = " ", access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class})
  private java.util.Set<SOTReview2> solsReviewSOTConfirmCheckbox2;
  @CCD(
          label = "Next Steps Document",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.Document solsNextStepsDocument;
  @CCD(
          label = "The will is not entitled to proof in England and Wales.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String noProofEntitlement;
  @CCD(
          label = "If it's not in English or Welsh, you'll need to provide an authenticated will translation. This is a translated copy of the original document with a signed affidavit or an official certificate attesting to the document's accuracy and the competency of the translator or translating service.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String englishWillNo;
  @CCD(
          label = "## Applicant details",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsApplicantNameSection;
  @CCD(label = "## Applicant's relationship", typeOverride = FieldType.Label)
  private String solsApplicantRelationshipSection;
  @CCD(label = "## Deceased's relations", typeOverride = FieldType.Label)
  private String solsDeceasedRelationsSection;
  @CCD(
          label = "You can’t use this service if the spouse or civil partner is not renouncing.",
          typeOverride = FieldType.Label
  )
  private String solsSpouseOrCivilRenouncingInfo;
  @CCD(
          label = "You can’t use this service if there is a minority interest.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsMinorityInterestInfo;
  @CCD(
          label = "The online service can't process cases where the deceased has any other issue, you must apply using the PA1A.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsApplicantSiblingsInfo;
  @CCD(
          label = "You can’t use this service if there is a beneficiary under the age of 18.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsEntitledMinorityInfo;
  @CCD(
          label = "You can’t use this service if an executor/residuary legatee/devisee in trust is alive or applying.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsDiedOrNotApplyingInfo;
  @CCD(
          label = "You can’t use this service if you are not named in the will as a residuary legatee or devisee.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsResiduaryInfo;
  @CCD(
          label = "You can’t use this service if there is a life interest in respect of the estate.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String solsLifeInterestInfo;
  @CCD(label = "State Field", access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class})
  private String state;
  @CCD(
          label = "Please select the information you would like to amend",
          typeOverride = FieldType.FixedList,
          typeParameterOverride = "selectionFixedList",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private SelectionFixedList selectionList;
  @CCD(label = "## Deceased's property", typeOverride = FieldType.Label)
  private String solsDeceasedPropertySection;
  @CCD(
          label = "You are unable to apply online unless the estate in England and Wales consists wholly of immovable property.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class}
  )
  private String immovableEstateInfo;
  @CCD(
          label = "How many of the following blood and adoptive relatives did the person who has died have? ",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String deceasedRelativesLabel;
  @CCD(
          label = "Sorry this option is not applicable for this case type, please press back and select another option",
          typeOverride = FieldType.Label,
          access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, RparobotSchedulerSuperuserRAccess.class}
  )
  private String notApplicableForThisCaseType1;
  @CCD(
          label = "Sorry this option is not applicable for this case type, please press back and select another option",
          typeOverride = FieldType.Label,
          access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, RparobotSchedulerSuperuserRAccess.class}
  )
  private String notApplicableForThisCaseType2;
  @CCD(
          label = "Sorry this option is not applicable for this case type, please press back and select another option",
          typeOverride = FieldType.Label,
          access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, RparobotSchedulerSuperuserRAccess.class}
  )
  private String notApplicableForThisCaseType3;
  @CCD(label = "Possible case matches", access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class})
  private String matches;
  @CCD(label = "Issue Switch?", access = {DefaultAccess.class, SystemupdateCrudAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo issueEarlySwitch;
  @CCD(
          label = "Please note that 7 clear days have not yet passed since the date of death. Therefore, the grant should not be issued at this time unless specific directions have been provided by the Registrar to proceed.",
          typeOverride = FieldType.Label,
          access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerRAccess.class, SuperuserRAccess.class, SystemupdateRAccess.class}
  )
  private String issueEarlyGopORAdmonWill;
  @CCD(
          label = "Please note that 14 clear days have not yet passed since the date of death. Therefore, the grant should not be issued at this time unless specific directions have been provided by the Registrar to proceed.",
          typeOverride = FieldType.Label,
          access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SchedulerRAccess.class, SuperuserRAccess.class, SystemupdateRAccess.class}
  )
  private String issueEarlyIntestacyORAdColligendaBona;
  @CCD(label = "[Click here to open legacy case](${legacyCaseViewUrl})", typeOverride = FieldType.Label)
  private String legacyCaseViewUrlLink;
  @CCD(
          label = "# About the applicant(s)",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String labelAboutApplicant;
  @CCD(
          label = "# About the person who has died",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String labelAboutPersonDied;
  @CCD(
          label = "# Foreign Domicile",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String labelForeigndomicile;
  @CCD(
          label = "# Inheritance Tax",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String labelInheTax;
  @CCD(
          label = "What are the values of the estate for probate?",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class}
  )
  private String labelIhtProbate;
  @CCD(
          label = "### Enter the estate values for IHT\n\n The values you enter must be whole numbers, and must not include full stops or commas. You do not need to include pence. For example 25000, not 25,000.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class, SolicitorCrudAccess.class}
  )
  private String labelIhtEstate;
  @CCD(
          label = "Please select the information you would like to amend",
          typeOverride = FieldType.FixedList,
          typeParameterOverride = "reissueSelectionFixedList",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private ReissueSelectionFixedList reissueSelectionList;
  @JsonProperty("boEmailGrantReissuedNotificationRequested")
  @CCD(
          label = "Do you wish to send an email notification for grant reissued?",
          hint = "Grant issued email notification",
          access = {RparobotCudSystemupdateCuAccess.class, CaseadminCaseofficerIssuerRegistrarCudAccess.class, SchedulerCudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo grantReissuedNotificationRequestedFlag;
  @CCD(
          label = "Which state do you wish to return the case to?",
          typeOverride = FieldType.FixedList,
          typeParameterOverride = "resolveStopStateReissueFixedList",
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private ResolveStopStateReissueFixedList resolveStopReissueState;
  @CCD(
          label = "Deceased Address Found",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo deceasedAddressFound;
  @CCD(
          label = "Deceased Addresses",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String deceasedAddresses;
  @CCD(
          label = "Primary Applicant Address Found",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo primaryApplicantAddressFound;
  @CCD(
          label = "Primary Applicant Addresses",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String primaryApplicantAddresses;
  @CCD(
          label = "Other executors applying",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo otherExecutorsApplying;
  @CCD(
          label = "Executors all alive",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo executorsAllAlive;
  @CCD(
          label = "Executors have alias",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo executorsHaveAlias;
  @CCD(label = "Legal Declaration", typeOverride = FieldType.TextArea)
  private String legalDeclaration;
  @CCD(label = "Check Answers Summary", typeOverride = FieldType.TextArea)
  private String checkAnswersSummary;
  @CCD(
          label = "Fees",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
  )
  private Fees fees;
  @CCD(
          label = "Deceased search postcode",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
  )
  private String deceasedPostCode;
  @CCD(
          label = "Applicant search postcode",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
  )
  private String primaryApplicantPostCode;
  @CCD(
          label = "At least one applicant must be selected to send a notification too",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String applicantNotification;
  @CCD(
          label = "If you wish to save the letter to the case, press Continue.\nTo create another preview, press Cancel and assemble the letter again.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String generateLetter;
  @CCD(
          label = "#### If you have selected 'none of these' because the title and clearing is not covered by the options above, you will not be able to continue making this application online. Please apply with a [paper form](https://www.gov.uk/guidance/probate-paper-applications-for-legal-professionals).",
          typeOverride = FieldType.Label
  )
  private String noneOfTheseOptionStop;
  @CCD(
          label = "#### By selecting 'none of these' you are confirming that the Probate practitioner is making this application on behalf of other named executors who will be acting as executors.",
          typeOverride = FieldType.Label
  )
  private String noneOfTheseOptionNoNo;
  @CCD(
          label = "#### By selecting 'none of these' you are confirming that the Probate practitioner has a right to act as an executor, as they are named in the will.",
          typeOverride = FieldType.Label
  )
  private String noneOfTheseOptionYesYes;
  @CCD(
          label = "#### By selecting 'none of these' you are confirming that the Probate practitioner named in the will is not acting as an executor, and there are other named executors who will be acting as executors.",
          typeOverride = FieldType.Label
  )
  private String noneOfTheseOptionYesNo;
  @CCD(
          label = "If you do not have access to these documents, you cannot continue with this application online. You must complete and send HMCTS: \n*  <a href=\"https://www.gov.uk/government/publications/form-pa1p-apply-for-probate-the-deceased-had-a-will\" target=\"_blank\">paper form PA1P</a>\n*  statement of truth \n*  exhibits that lead to a R54 Order NCPR 1987 to prove the will is lost, and that it has not been revoked",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSolicitorSystemupdateCitizenCrudAccess.class}
  )
  private String willAccessOriginalHintText;
  @CCD(
          label = "##### You should only answer yes if the Probate practitioner named above is appointed  in the Will specifically by their name",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class, SolicitorCrudAccess.class}
  )
  private String applyForProbatePageHint1;
  @CCD(
          label = "##### You should only answer yes if the Probate practitioner named above is acting as executor as a partner/member/shareholder/director in an appointed firm or applying as a nominee of a trust corporation",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class, SolicitorCrudAccess.class}
  )
  private String applyForProbatePageHint2;
  @CCD(
          label = "Are you sure you want to permanently delete this application? You will no longer be able to view the details in the future.",
          access = {SolicitorCrudAccess.class}
  )
  private String deleteApplication;
  @CCD(label = "Hot fix for CaseValidationException field not found error - not used")
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo solsSolicitorIsMainApplicant;
  @CCD(label = "Service request", access = {SchedulerSuperuserSystemupdateCudAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.WaysToPay serviceRequestWaysToPay;
  @CCD(
          label = "Noc prepared date",
          access = {DefaultAccess.class, CaseworkerApproverCaseworkerCaaSystemupdateCrudAccess.class}
  )
  private java.time.LocalDate nocPreparedDate;
  @CCD(
          label = "The values you enter must be whole numbers, and must not include full stops or commas. You do not need to include pence. For example 25000, not 25,000.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String valueOfAssetSection1;
  @CCD(
          label = "You can find these values in the letter or email HMRC sent relating to this application.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCruAccess.class}
  )
  private String valueOfAssetSection2;
  @CCD(
          label = "## Wait 20 working days from when you sent the IHT400 to HMRC\n\nThe letter or email from HMRC will be sent out up to 20 working days after you submit the IHT400.\n\n### If you have not received a letter or email from HMRC after 20 days\n\nContact HMRC on this number:\n\nTelephone: 0300 123 1072\n\nMonday to Friday, 9am to 5pm (except public holidays)\n\n<a href=\"https://www.gov.uk/call-charges\" target=\"_blank\">Find out about call charges</a>.",
          typeOverride = FieldType.Label,
          access = {IssuerCrudAccess.class, SolicitorCrudAccess.class, SuperuserCrudAccess.class}
  )
  private String iht400Wait;
  @CCD(
          label = "## You cannot use this event\n\nYou must select the 'PA1P/PA1A/Solicitors Manual' event on the <a href=\"cases/case-filter\" target=\"_self\">Create case</a> page for paper form cases.",
          typeOverride = FieldType.Label,
          access = {CaseadminCaseofficerIssuerRegistrarSuperuserCruAccess.class}
  )
  private String doNotSaveCase;
  @CCD(
          label = "Review your request for information email.\n\nIf you need to make changes, go back.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotCrudSystemupdateCruAccess.class}
  )
  private String reviewRequest;
  @CCD(
          label = "## You cannot use this event\n\nIf you need to inform the applicant of a caveat stop, you must use the 'Caveat notification' event.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class}
  )
  private String useCaveatNotificationEvent;
  @CCD(
          label = "## You cannot use this event\n\nAs there is no email address recorded for this applicant, you must use the 'Assemble a letter' event to request information instead.",
          typeOverride = FieldType.Label,
          access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class}
  )
  private String useAssembleLetterEvent;
  @CCD(label = "Email preview", access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.Document emailPreview;
  @CCD(
          label = "Information need to be returned by post page switch",
          access = {DefaultAccess.class, RparobotSystemupdateCrudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo informationNeededByPostSwitch;
  @CCD(
          label = "A referral relates to limitations, will numbering, queries concerning sufficient evidence as well as any other questions on how to proceed with a case.",
          typeOverride = FieldType.Label
  )
  private String registrarEscalateLabel1;
  @CCD(
          label = "An order applies if a support officer is satisfied that they have all the evidence needed to make the order.",
          typeOverride = FieldType.Label
  )
  private String registrarEscalateLabel2;
  @JsonProperty("TTL")
  @CCD(
          label = "Set up TTL",
          access = {TTLProfileCruAccess.class, SchedulerCrudAccess.class, SystemupdateCrudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.TTL manageCaseTtl;
  @CCD(
          label = "Any executors died",
          access = {DefaultAccess.class, RparobotCrudSolicitorCrudSystemupdateCruAccess.class, CitizenCrudAccess.class}
  )
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo anyExecutorsDied;
  @CCD(label = "Has Valid Matches", access = {CaseadminCaseofficerIssuerRegistrarSuperuserCrudAccess.class})
  private uk.gov.hmcts.ccd.sdk.type.YesOrNo hasValidMatches;
  @CCD(
          label = "Please note: At least one match has been found in the Case Matches tab (excluding any standing searches and legacy standing searches).\n\nReview this information before issuing the grant.",
          typeOverride = FieldType.Label,
          access = {CaseadminCaseofficerIssuerRegistrarRAccess.class, SuperuserRAccess.class}
  )
  private String hasValidMatchesLabel;
  // ==== end synthesised definition-only fields ====
}
