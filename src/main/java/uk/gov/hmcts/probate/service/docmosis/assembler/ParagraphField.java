package uk.gov.hmcts.probate.service.docmosis.assembler;

import java.util.Arrays;
import java.util.List;

public enum ParagraphField {

    FREE_TEXT("FreeText", "Free Text", "freeText"),
    CASEWORKER("Caseworker", "Caseworker", "caseworkerName"),
    AFFIDAVIT("ForDomAffidavit", "Foreign domicile affidavit of law", null),
    INITIAL_ENQ("ForDomInitial", "Foreign domicile - initial enquiry", null),
    EXEC_NOT_ACC_EXECUTOR_NAMES("EntExecNoAcc", "Executor(s) not accounted for", "nameOfExecutors"),
    ENT_ATTORNEY("EntAttorney", "Attorney and Executor cannot apply together", null),
    ENT_LEADING_APPLICATION("EntLeadingApp", "Leading Grant Application required", "personEntitled"),
    ENT_NO_TITLE("EntNoTitle", "No title", "personEntitledNoTitle"),
    ENT_TWO_APPLICATIONS("EntTwoApps", "Two applications pending", "nameOfApplicantOnOtherApplication"),
    ENT_FAMILY_TREE("EntFamTree", "Family tree", null),
    ENT_CONFIRM_DEATH("EntDeathPa", "Confirm death of 1st named executor", "firstNamedExec"),
    ENT_SUB_EXEC("EntSubExec", "Substituted executor applying when sole executor survives", "nameOfSoleExec"),
    ENT_PREJUDICE_WILL("EntPrejudiced", "Prejudiced by proof of will", null),
    ENT_WRONG_EXEC("EntWrongExec", "Wrongly accounted executor", "nameOfExecutorEnt"),
    IHT205_MISSING("IHT205Miss", "IHT205 Missing", null),
    IHT421_AWAITING("IHT421Await", "Awaiting IHT421", null),
    IHT205_NO_ASSETS("IHT205NoAssets", "IHT - no assets onIHT205", null),
    IHT205_GROSS("IHT205GrossEstateOver", "IHT - gross estate over 325k iht205", null),
    IHT217_MISSING("IHT217Miss", "IHT - IHT217 missing", null),
    IHT400("IHT400", "IHT - IHT400", null),
    MISS_INFO_WILL_OR_DOCICIL("MissInfoWill", "Original Will or Codicil", "willOrCodicil"),
    DATE_OF_REQUEST("MissInfoAwaitResponse", "Date of request", "dateOfRequest"),
    MISS_INFO_ALIAS("MissInfoAlias", "True name/Alias", null),
    MISS_INFO_RENUN_WILL("MissInfoRenunWill", "Renunciation (will)", null),
    MISS_INFO_GRANT_REQ("MissInfoGrantReq", "Why is a grant required?", null),
    INFO_CHANGE_APP("MissInfoChangeApp", "Name change of applicant", "applicantName"),
    INFO_DEATH_CERT("MissInfoDeathCert", "Death Certificate", "reason"),
    ANY_OTHER("WillAnyOther", "Any other wills", "limitation"),
    PLIGHT("WillPlight", "Plight and condition of will", "conditionReason"),
    SEP_PAGES("WillSepPages", "Separate pages of will", "numberOfPages"),
    STAPLE("WillStaple", "Staple removed for photocopying", null),
    WILL_REVOKED("WillRevoked", "Will revoked by marriage", null),
    WILL_LOST("WillLost", "Lost will", null),
    WILL_LIST("WillList", "List/Memo named in will", null),
    WILL_FIAT("WillFiat", "FIAT will SOT", null),
    INCAPACITY_GENERAL("IncapGen", "General", null),
    INCAPACITY_ONE_EXEC("IncapOneExec", "One executor", null),
    INCAPACITY_INSTITUTE_EXEC("IncapInstitutedExec", "Instituted executor", null),
    INCAPACITY_MEDICAL("IncapMedical", "Name of person without capacity", "nameOfPersonWithoutCapacity"),
    INTESTACY_LIFE_MINORITY("LMIntLifeAndMin", "Intestacy: Life and Minority interest", null),
    INTESTACY_LIFE("LMIntLife", "Intestacy: Life interest", null),
    INTESTACY_MINORITY("LMIntMinor", "Intestacy: Minority interest", null),
    ADMON_WILL_LIFE("LMAdmonLife", "Admon Will: Life interest", null),
    ADMON_WILL_MINORITY("LMAdmonMinor", "Admon Will: Minority interest", null),
    INTESTACY_PARENTAL("LMIntParental", "Intestacy: Minority interest parental responsibility", null),
    SOT_PA1P_REDEC("SotPa1pRedec", "PA1P: Part B - Full redec of application", null),
    SOT_PA1A_REDEC("SotPa1aRedec", "PA1A: Part B - Full redec of application", null),
    SOT_NOT_SIGNED("SotNotSigned", "Paper: Statement of truth not signed", null),
    SOT_PA1P_Q2("SotPa1pQ2", "PA1P Q2 incomplete/wrong", null),
    SOT_PA1P_Q3("SotPa1pQ3", "PA1P Q3 incomplete/wrong", null),
    SOT_PA1P_Q4("SotPa1pQ4", "PA1P Q4 incomplete/wrong", null),
    SOT_PA1P_Q5("SotPa1pQ5", "PA1P Q5 incomplete/wrong", null),
    SOT_PA1P_Q6("SotPa1pQ6", "PA1P Q6 incomplete/wrong", null),
    SOT_PA1P_Q7("SotPa1pQ7", "PA1P Q7 incomplete/wrong", null),
    SOT_PA1A_Q2("SotPa1aQ2", "PA1A Q2 incomplete/wrong", null),
    SOT_PA1A_Q3("SotPa1aQ3", "PA1A Q3 incomplete/wrong", null),
    SOT_PA1A_Q4("SotPa1aQ4", "PA1A Q4 incomplete/wrong", null),
    SOT_PA1A_Q5("SotPa1aQ5", "PA1A Q5 incomplete/wrong", null),
    SOT_PA1A_Q6("SotPa1aQ6", "PA1A Q6 incomplete/wrong", null),
    WIT_EXECUTION("WitExecution", "Due execution of will affidavit - witness required", "willOrCodicilExecutionAffidavit"),
    WIT_SIGNATURE("WitSignature", "Signature of affidavit - witness required", "willOrCodicilSignatureAffidavit"),
    WIT_DATE("WitDate", "Date of will affidavit - witness required", "willOrCodicilDateOfAffidavit"),
    WIT_CONSENT("WitConsent", "Consent of proof of will", "personsPrejudiced"),
    SOLS_GEN_AUTH("GenAuthPartners", "Sol: Renunciation does not cover authority partners", null),
    SOLS_GEN_EXTENDED_RENUN("GenExtendRenunciation", "Sol: Extended renunciation", null),
    SOLS_GEN_POWER_ATTORNEY("GenPowerOfAttorney", "Power of attorney signed abroad", null),
    SOLS_GEN_VOID("GenUncertainty", "Void for uncertainty", null),
    SOLS_CERT_OTHER_WILL("CertsotherWill", "Any other will", "clauseNumberOtherWill"),
    SOLS_CERT_ALIAS("Certsalias", "Alias of deceased", null),
    SOLS_CERT_DECEASED_ADD("CertsDeceasedAdd", "Deceased address", null),
    SOLS_CERT_DEPONENT_ADD("CertsDeponentAdd", "Deponents address", null),
    SOLS_CERT_DIVORCE("CertsDivorceDet", "Divorce details", null),
    SOLS_CERT_DIVORCE_DISOOLVE("CertsDivorceDissolved", "Divorce dissolved in E&W", null),
    SOLS_CERT_DOB("CertsDOB", "D.O.B", null),
    SOLS_CERT_DOD("CertsDOD", "D.O.D", null),
    SOLS_CERT_EPA_LPA("CertsEpaLpa", "EPA/LPA capacity", null),
    SOLS_CERT_EXEC_NOT_ACCOUNTED("CertsExecNotAcc", "Executor not accounted for", "nameOfExecutorsEpaLpa"),
    SOLS_CERT_FIRM_SUCC("CertsFirmSucc", "Firm succeeded to", null),
    SOLS_CERT_EXEC_NAME("CertsExecName", "Executor name", "nameOfExecutorSolsCert"),
    SOLS_CERT_LIFE_MINORITY("CertsNoLifeOrMinor", "No life or minority interest", null),
    SOLS_CERT_PARTENRS_DOD("CertsPartnersDOD", "Partners at the D.O.D", null),
    SOLS_CERT_PLIGHT("CertsPlightAndCon", "Plight and condition", "conditionReason"),
    SOLS_CERT_POWER_RESERVED("CertsPowerReserved", "Power reserved to partners", null),
    SOLS_CERT_SETTLED_LAND("CertsSettledLand", "Settled land", null),
    SOLS_CERT_SPOUSE("CertsSpouse", "Spouse only person entitled", "nameOfDeponent"),
    SOLS_CERT_SURVIVAL_EXEC("CertsSurvivalExec", "Survival of executo", null),
    SOLS_CERT_TRUST_CORP("CertsTrustCorp", "Trust corporation", "nameOfCorporation"),
    SOLS_CERT_WILL_SEP_PAGES("CertsWillSepPages", "Will of separate pages", null),
    SOLS_AFFID_ALIAS_INT("AffidAliasInt", "Alias affidavit (Intestacy)", "nameOfCorporation"),
    SOLS_AFFID_ALIAS("AffidAliasAffidInt", "Alias Affidavit", null),
    SOLS_AFFID_EXEC("AffidExecution", "Due Execution or rule 12(3) affidavit", null),
    SOLS_AFFID_HANDWRITING("AffidHandWriting", "Handwriting affidavit", null),
    SOLS_AFFID_IDENTITY("AffidIdentity", "Identity affidavit", null),
    SOLS_AFFID_KNOWLEDGE("AffidKnowledge", "Knowledge of contents affidavit", null),
    SOLS_AFFID_ALTERATIONS("AffidAlterations", "Alterations affidavit", null),
    SOLS_AFFID_DATE("AffidDate", "Date of execution affidavit", "reasonForAffidavitExecution"),
    SOLS_AFFID_SEARCH("AffidSearch", "Search affidavit", "reasonForAffidavitSearch"),
    SOLS_AFFID_RECITAL("AffidMisRecital", "Mis-recital of date of will in codicil affidavit", "reasonForAffidavitMissRecital"),
    SOLS_REDEC_SOT_DATE("RedecSotDate", "Re-declare: incorrect or missing date of will in SOT", null),
    SOLS_REDEC_DATE("RedecSotDate", "Re-declare: Codicil omitted", null),
    SOLS_REDEC_SOT_SIGNED("RedecSotSigned", "Re-declare: SOT not signed", null),
    SOLS_REDEC_DOMICILE("RedecDomcile", "Re-declare: Domicile", null),
    SOLS_REDEC_INT_FOR_DOM("RedecIntForDom", "Re-declare: Intestacy foreign domicile", null),
    SOLS_REDEC_WILLS_FOR_DOM("RedecWillsForDom", "Re-declare: Wills foreign domicile", null),
    SOLS_REDEC_MINORITY("RedecMinority", "Re-declare: Minority interest", null),
    SOLS_REDEC_NET_ESTATE("RedecNetEstate", "Re-declare: Net estate over SSL", null),
    SOLS_REDEC_TITLE("RedecTitle", "Re-declare: Title", null),
    SOLS_REDEC_CLEARING("RedecClearing", "Re-declare: Clearing", null);

    private final String fieldCode;
    private final String fieldLabel;
    private final String fieldPlaceholderName;

    ParagraphField(String fieldCode, String fieldLabel, String fieldPlaceholderName) {
        this.fieldCode = fieldCode;
        this.fieldLabel = fieldLabel;
        this.fieldPlaceholderName = fieldPlaceholderName;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public String getFieldPlaceholderName() {
        return fieldPlaceholderName;
    }

    public static List<ParagraphField> getAll() {
        return Arrays.asList(ParagraphField.values());
    }
}
