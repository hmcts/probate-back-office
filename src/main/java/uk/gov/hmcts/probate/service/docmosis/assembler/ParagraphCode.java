package uk.gov.hmcts.probate.service.docmosis.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ParagraphCode {

    CASEWORKER("Caseworker", "Caseworker", null),
    ENT_EXEC_NOT_ACC("EntExecNoAcc", "Executor not accounted for", "FL-PRB-GNO-ENG-00123.docx"),
    IHT_205_MISSING("IHT205Miss", "IHT205 Missing", "FL-PRB-GNO-ENG-00124.docx"),
    IHT_AWAIT_IHT421("IHT421Await", "Awaiting IHT421", "FL-PRB-GNO-ENG-00125.docx"),
    MISS_INFO_WILL("MissInfoWill", "Original Will", "FL-PRB-GNO-ENG-00126.docx"),
    MISS_INFO_CHANGE_APP("MissInfoChangeApp", "Name change of applicant", "FL-PRB-GNO-ENG-00127.docx"),
    MISS_INFO_DEATH_CERT("MissInfoDeathCert", "Death Certificate", "FL-PRB-GNO-ENG-00128.docx"),
    WILL_ANY_OTHER("WillAnyOther", "Any other wills", "FL-PRB-GNO-ENG-00129.docx"),
    WILL_PLIGHT("WillPlight", "Plight and condition of will", "FL-PRB-GNO-ENG-00130.docx"),
    WILL_SEP_PAGES("WillSepPages", "Separate pages of will", "FL-PRB-GNO-ENG-00131.docx"),
    WILL_STAPLE("WillStaple", "Staple removed for photocopying", "FL-PRB-GNO-ENG-00132.docx");

    private final String code;
    private final String label;
    private final String templateName;

    ParagraphCode(String code, String label, String templateName) {
        this.code = code;
        this.label = label;
        this.templateName = templateName;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getTemplateName() {
        return templateName;
    }

    public static List<ParagraphCode> getAll() {
        return Arrays.asList(ParagraphCode.values());
    }

    public static Optional<ParagraphCode> fromCode(String code) {
        for (ParagraphCode caseType : ParagraphCode.values()) {
            if (caseType.code.equals(code)) {
                return Optional.of(caseType);
            }
        }

        return Optional.empty();
    }
}
