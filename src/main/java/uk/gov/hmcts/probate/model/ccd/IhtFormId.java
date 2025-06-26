package uk.gov.hmcts.probate.model.ccd;

import java.util.Arrays;

public enum IhtFormId {
    IHT_205("IHT205"),
    IHT_207("IHT207"),
    IHT_400("IHT400"),
    IHT_400421("IHT400421"),
    IHT_421("IHT421");

    private final String formId;

    IhtFormId(String formId) {
        this.formId = formId;
    }

    public String getFormId() {
        return formId;
    }

    public static boolean isValid(String formId) {
        return Arrays.stream(values()).anyMatch(form -> form.name().equalsIgnoreCase(formId));
    }
}
