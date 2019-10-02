package uk.gov.hmcts.probate.transformer.documentAssembly;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ParagraphCode {

    CASEWORKER("Caseworker", "Caseworker"),
    ENT_EXEC_NOT_ACC("EntExecNoAcc", "Executor not accounted for"),
    IHT_205_MISSING("IHT205Miss", "IHT205 Missing"),
    IHT_AWAIT_IHT421("IHT421Await", "Awaiting IHT421"),
    MISS_INFO_WILL("MissInfoWill", "Original Will"),
    MISS_INFO_CHANGE_APP("MissInfoChangeApp", "Name change of applicant"),
    MISS_INFO_DEATH_CERT("MissInfoDeathCert", "Death Certificate"),
    WILL_ANY_OTHER("WillAnyOther", "Any other wills"),
    WILL_PLIGHT("WillPlight", "Plight and condition of will"),
    WILL_SEP_PAGES("WillSepPages", "Separate pages of will"),
    WILL_STAPLE("WillStaple", "Staple removed for photocopying");

    private final String code;
    private final String label;

    ParagraphCode(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
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
