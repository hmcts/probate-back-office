package uk.gov.hmcts.probate.service.docmosis.assembler;

import java.util.Arrays;
import java.util.List;

public enum ParagraphField {

    FREE_TEXT("FreeText", "Free Text", "FreeText"),
    CASEWORKER("Caseworker", "Caseworker", "caseworkerName"),
    EXEC_NOT_ACC_EXECUTOR_NAMES("EntExecNoAcc", "Executor not accounted for", "nameOfExecutors"),
    IHT205_MISSING("IHT205Miss", "IHT205 Missing", null),
    IHT421_AWAITING("IHT421Await", "Awaiting IHT421", null),
    MISS_INFO_WILL_OR_DOCICIL("MissInfoWill", "Original Will or Codicil", "willOrCodicil"),
    INFO_CHANGE_APP("MissInfoChangeApp", "Name change of applicant", "applicantName"),
    INFO_DEATH_CERT("MissInfoDeathCert", "Death Certificate", "reason"),
    ANY_OTHER("WillAnyOther", "Any other wills", "limitation"),
    PLIGHT("WillPlight", "Plight and condition of will", "conditionReason"),
    SEP_PAGES("WillSepPages", "Separate pages of will", "numberOfPages"),
    STAPLE("WillStaple", "Staple removed for photocopying", null);

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
