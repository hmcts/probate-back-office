package uk.gov.hmcts.probate.model;

public enum DisposedCaseSearchType {
    ALL("All"),
    CASE_ID("CaseId"),
    DATE_OF_DEATH("DateOfDeath"),
    DATE_OF_DEATH_AND_SURNAME("DateOfDeathAndSurname"),
    FULL_NAME("FullName");

    private final String code;

    DisposedCaseSearchType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DisposedCaseSearchType fromCode(String code) {
        for (DisposedCaseSearchType disposedCaseSearchType : DisposedCaseSearchType.values()) {
            if (disposedCaseSearchType.getCode().equals(code)) {
                return disposedCaseSearchType;
            }
        }
        throw new IllegalArgumentException("No DisposedCaseSearchType found with code " + code);
    }
}
