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
    MISS_INFO_WILL_OR_DOCICIL("MissInfoWill", "Original Will or Codicil", "willOrCodicil"),
    DATE_OF_REQUEST("MissInfoAwaitResponse", "Date of request", "dateOfRequest"),
    INFO_CHANGE_APP("MissInfoChangeApp", "Name change of applicant", "applicantName"),
    INFO_DEATH_CERT("MissInfoDeathCert", "Death Certificate", "reason"),
    ANY_OTHER("WillAnyOther", "Any other wills", "limitation"),
    PLIGHT("WillPlight", "Plight and condition of will", "conditionReason"),
    SEP_PAGES("WillSepPages", "Separate pages of will", "numberOfPages"),
    STAPLE("WillStaple", "Staple removed for photocopying", null),
    INCAPACITY_GENERAL("IncapGen", "General", null),
    INCAPACITY_ONE_EXEC("IncapOneExec", "One executor", null),
    INCAPACITY_INSTITUTE_EXEC("IncapInstitutedExec", "Instituted executor", null),
    INCAPACITY_MEDICAL("IncapMedical", "Medical evidence required", "nameOfPersonWithoutCapacity");

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
