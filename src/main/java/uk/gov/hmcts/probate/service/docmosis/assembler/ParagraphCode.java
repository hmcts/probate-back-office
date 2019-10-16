package uk.gov.hmcts.probate.service.docmosis.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.*;

public enum ParagraphCode {

    FreeText(null, FREE_TEXT),
    Caseworker(null, CASEWORKER),
    ForDomAffidavit("FL-PRB-GNO-ENG-00100.docx", AFFIDAVIT),
    ForDomInitialEnq("FL-PRB-GNO-ENG-00134.docx", INITIAL_ENQ),
    EntExecNoAcc("FL-PRB-GNO-ENG-00123.docx", EXEC_NOT_ACC_EXECUTOR_NAMES),
    EntAttorney("FL-PRB-GNO-ENG-00135.docx", ENT_ATTORNEY),
    EntLeadingApp("FL-PRB-GNO-ENG-00136.docx", ENT_LEADING_APPLICATION),
    EntNoTitle("FL-PRB-GNO-ENG-00137.docx", ENT_NO_TITLE),
    EntTwoApps("FL-PRB-GNO-ENG-00138.docx", ENT_TWO_APPLICATIONS),
    EntFamTree("FL-PRB-GNO-ENG-00139.docx", ENT_FAMILY_TREE),
    EntDeathPa("FL-PRB-GNO-ENG-00140.docx", ENT_CONFIRM_DEATH),
    EntSubExec("FL-PRB-GNO-ENG-00141.docx", ENT_SUB_EXEC),
    EntPrejudiced("FL-PRB-GNO-ENG-00142.docx", ENT_PREJUDICE_WILL),
    EntWrongExec("FL-PRB-GNO-ENG-00143.docx", ENT_WRONG_EXEC),
    IHT205Miss("FL-PRB-GNO-ENG-00124.docx", IHT205_MISSING),
    IHT421Await("FL-PRB-GNO-ENG-00125.docx", IHT421_AWAITING),
    IHT205NoAssets("FL-PRB-GNO-ENG-00144.docx", IHT205_NO_ASSETS),
    IHT205GrossEstateOver("FL-PRB-GNO-ENG-00145.docx", IHT205_GROSS),
    IHT217Miss("FL-PRB-GNO-ENG-00146.docx", IHT217_MISSING),
    IHTIHT400("FL-PRB-GNO-ENG-00147.docx", IHT400),
    MissInfoWill("FL-PRB-GNO-ENG-00126.docx", MISS_INFO_WILL_OR_DOCICIL),
    MissInfoChangeApp("FL-PRB-GNO-ENG-00127.docx", INFO_CHANGE_APP),
    MissInfoDeathCert("FL-PRB-GNO-ENG-00128.docx", INFO_DEATH_CERT),
    MissInfoAwaitResponse("FL-PRB-GNO-ENG-00152.docx", DATE_OF_REQUEST),
    MissInfoAlias("FL-PRB-GNO-ENG-00149.docx", MISS_INFO_ALIAS),
    MissInfoRenunWill("FL-PRB-GNO-ENG-00150.docx", MISS_INFO_RENUN_WILL),
    MissInfoGrantReq("FL-PRB-GNO-ENG-00151.docx", MISS_INFO_GRANT_REQ),
    WillAnyOther("FL-PRB-GNO-ENG-00129.docx", ANY_OTHER),
    WillPlight("FL-PRB-GNO-ENG-00130.docx", PLIGHT),
    WillSepPages("FL-PRB-GNO-ENG-00131.docx", SEP_PAGES),
    WillStaple("FL-PRB-GNO-ENG-00132.docx", STAPLE),
    WillRevoked("FL-PRB-GNO-ENG-00154.docx", WILL_REVOKED),
    WillLost("FL-PRB-GNO-ENG-00155.docx", WILL_LOST),
    WillList("FL-PRB-GNO-ENG-00156.docx", WILL_LIST),
    WillFiat("FL-PRB-GNO-ENG-00157.docx", WILL_FIAT),
    IncapGen("FL-PRB-GNO-ENG-00101.docx", INCAPACITY_GENERAL),
    IncapOneExec("FL-PRB-GNO-ENG-00102.docx", INCAPACITY_ONE_EXEC),
    IncapInstitutedExec("FL-PRB-GNO-ENG-00103.docx", INCAPACITY_INSTITUTE_EXEC),
    IncapMedical("FL-PRB-GNO-ENG-00148.docx", INCAPACITY_MEDICAL),
    IntLifeAndMin("FL-PRB-GNO-ENG-00104.docx", INTESTACY_LIFE_MINORITY),
    IntLife("FL-PRB-GNO-ENG-00105.docx", INTESTACY_LIFE),
    IntMinor("FL-PRB-GNO-ENG-00106.docx", INTESTACY_MINORITY),
    AdmonLife("FL-PRB-GNO-ENG-00107.docx", ADMON_WILL_LIFE),
    AdmonMinor("FL-PRB-GNO-ENG-00108.docx", ADMON_WILL_MINORITY),
    IntParental("FL-PRB-GNO-ENG-00109.docx", INTESTACY_PARENTAL),
    SotPa1pRedec("FL-PRB-GNO-ENG-00110.docx", SOT_PA1P_REDEC),
    SotPa1aRedec("FL-PRB-GNO-ENG-00111.docx", SOT_PA1A_REDEC),
    SotNotSigned("FL-PRB-GNO-ENG-00158.docx", SOT_NOT_SIGNED),
    SotPa1pQ2("FL-PRB-GNO-ENG-00159.docx", SOT_PA1P_Q2),
    SotPa1pQ3("FL-PRB-GNO-ENG-00160.docx", SOT_PA1P_Q3),
    SotPa1pQ4("FL-PRB-GNO-ENG-00161.docx", SOT_PA1P_Q4),
    SotPa1pQ5("FL-PRB-GNO-ENG-00162.docx", SOT_PA1P_Q5),
    SotPa1pQ6("FL-PRB-GNO-ENG-00163.docx", SOT_PA1P_Q6),
    SotPa1pQ7("FL-PRB-GNO-ENG-00164.docx", SOT_PA1P_Q7),
    SotPa1aQ2("FL-PRB-GNO-ENG-00165.docx", SOT_PA1A_Q2),
    SotPa1aQ3("FL-PRB-GNO-ENG-00166.docx", SOT_PA1A_Q3),
    SotPa1aQ4("FL-PRB-GNO-ENG-00167.docx", SOT_PA1A_Q4),
    SotPa1aQ5("FL-PRB-GNO-ENG-00168.docx", SOT_PA1A_Q5),
    SotPa1aQ6("FL-PRB-GNO-ENG-00169.docx", SOT_PA1A_Q6),
    WitnessExecution("FL-PRB-GNO-ENG-00171.docx", WIT_EXECUTION),
    WitnessSignature("FL-PRB-GNO-ENG-00172.docx", WIT_SIGNATURE),
    WitnessDate("FL-PRB-GNO-ENG-00173.docx", WIT_DATE),
    WitnessConsent("FL-PRB-GNO-ENG-00174.docx", WIT_CONSENT),
    solsGenAuthorityPartners("FL-PRB-GNO-ENG-00174.docx", SOLS_GEN_AUTH),
    solsGenExtendedRenun("FL-PRB-GNO-ENG-00175.docx", SOLS_GEN_EXTENDED_RENUN),
    solsGenPowerAttorney("FL-PRB-GNO-ENG-00112.docx", SOLS_GEN_POWER_ATTORNEY),
    solsGenVoid("FL-PRB-GNO-ENG-00113.docx", SOLS_GEN_VOID),
    solsCertOtherWill("FL-PRB-GNO-ENG-00176.docx", SOLS_CERT_OTHER_WILL),
    solsCertAlias("FL-PRB-GNO-ENG-00177.docx", SOLS_CERT_ALIAS),
    solsCertDeceasedAdd("FL-PRB-GNO-ENG-00178.docx", SOLS_CERT_DECEASED_ADD),
    solsCertDeponentAdd("FL-PRB-GNO-ENG-00179.docx", SOLS_CERT_DEPONENT_ADD),
    solsCertDivorce("FL-PRB-GNO-ENG-00180.docx", SOLS_CERT_DIVORCE),
    solsCertDivorceDissolve("FL-PRB-GNO-ENG-00181.docx", SOLS_CERT_DIVORCE_DISOOLVE),
    solsCertDOB("FL-PRB-GNO-ENG-00182.docx", SOLS_CERT_DOB),
    solsCertDOD("FL-PRB-GNO-ENG-00183.docx", SOLS_CERT_DOD),
    solsCertEpaLpa("FL-PRB-GNO-ENG-00184.docx", SOLS_CERT_EPA_LPA),
    solsCertExecNotAcc("FL-PRB-GNO-ENG-00185.docx", SOLS_CERT_EXEC_NOT_ACCOUNTED),
    solsCertFirmSucc("FL-PRB-GNO-ENG-00186.docx", SOLS_CERT_FIRM_SUCC),
    solsCertExecName("FL-PRB-GNO-ENG-00187.docx", SOLS_CERT_EXEC_NAME),
    solsCertLifeMinority("FL-PRB-GNO-ENG-00188.docx", SOLS_CERT_LIFE_MINORITY),
    solsCertPartnersDod("FL-PRB-GNO-ENG-00189.docx", SOLS_CERT_PARTENRS_DOD),
    solsCertPlight("FL-PRB-GNO-ENG-00190.docx", SOLS_CERT_PLIGHT),
    solsCertPowerReserved("FL-PRB-GNO-ENG-00191.docx", SOLS_CERT_POWER_RESERVED),
    solsCertSettledLand("FL-PRB-GNO-ENG-00192.docx", SOLS_CERT_SETTLED_LAND),
    solsCertSpouse("FL-PRB-GNO-ENG-00193.docx", SOLS_CERT_SPOUSE),
    solsCertSurvivalExec("FL-PRB-GNO-ENG-00194.docx", SOLS_CERT_SURVIVAL_EXEC),
    solsCertTrustCorp("FL-PRB-GNO-ENG-00195.docx", SOLS_CERT_TRUST_CORP),
    solsCertWillSepPages("FL-PRB-GNO-ENG-00196.docx", SOLS_CERT_WILL_SEP_PAGES),
    solsAffidAliasInt("FL-PRB-GNO-ENG-00115.docx", SOLS_AFFID_ALIAS_INT),
    solsAffidAlias("FL-PRB-GNO-ENG-00116.docx", SOLS_AFFID_ALIAS),
    solsAffidExec("FL-PRB-GNO-ENG-00117.docx", SOLS_AFFID_EXEC),
    solsAffidHandwriting("FL-PRB-GNO-ENG-00118.docx", SOLS_AFFID_HANDWRITING),
    solsAffidIdentity("FL-PRB-GNO-ENG-00119.docx", SOLS_AFFID_IDENTITY),
    solsAffidKnowledge("FL-PRB-GNO-ENG-00120.docx", SOLS_AFFID_KNOWLEDGE),
    solsAffidAlterations("FL-PRB-GNO-ENG-00121.docx", SOLS_AFFID_ALTERATIONS),
    solsAffidDate("FL-PRB-GNO-ENG-00197.docx", SOLS_AFFID_DATE),
    solsAffidSearch("FL-PRB-GNO-ENG-00198.docx", SOLS_AFFID_SEARCH),
    solsAffidRecital("FL-PRB-GNO-ENG-00199.docx", SOLS_AFFID_RECITAL),
    solsRedecSotDate("FL-PRB-GNO-ENG-00200.docx", SOLS_REDEC_SOT_DATE),
    solsRedecDate("FL-PRB-GNO-ENG-00201.docx", SOLS_REDEC_DATE),
    solsRedecSotSigned("FL-PRB-GNO-ENG-00202.docx", SOLS_REDEC_SOT_SIGNED),
    solsRedecDomicile("FL-PRB-GNO-ENG-00203.docx", SOLS_REDEC_DOMICILE),
    solsRedecIntForDom("FL-PRB-GNO-ENG-00204.docx", SOLS_REDEC_INT_FOR_DOM),
    soplsRedecWillsForDom("FL-PRB-GNO-ENG-00205.docx", SOLS_REDEC_WILLS_FOR_DOM),
    solsRedecMinority("FL-PRB-GNO-ENG-00206.docx", SOLS_REDEC_MINORITY),
    solsRedecNetEstate("FL-PRB-GNO-ENG-00207.docx", SOLS_REDEC_NET_ESTATE),
    solsRedecTitle("FL-PRB-GNO-ENG-00208.docx", SOLS_REDEC_TITLE),
    solsRedecClearing("FL-PRB-GNO-ENG-00209.docx", SOLS_REDEC_CLEARING);

    private List<ParagraphField> paragraphFields;
    private final String templateName;

    ParagraphCode(String templateName, ParagraphField... fieldsUsed) {
        this.paragraphFields = Arrays.asList(fieldsUsed);
        this.templateName = templateName;
    }


    public List<ParagraphField> getParagraphFields() {
        return paragraphFields;
    }

    public String getTemplateName() {
        return templateName;
    }

    public static List<ParagraphCode> getAll() {
        return Arrays.asList(ParagraphCode.values());
    }

    public static Optional<ParagraphCode> fromFieldCode(String fieldCode) {
        for (ParagraphCode paragraphCode : ParagraphCode.values()) {
            for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
                if (paragraphField.getFieldCode().equals(fieldCode)) {
                    return Optional.of(paragraphCode);
                }
            }
        }

        return Optional.empty();
    }
}
