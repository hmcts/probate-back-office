package uk.gov.hmcts.probate.model.ccd.raw.request;

public enum PrintTemplateApplicationType {

    SOLICITOR("sol"),
    PERSONAL("pa");

    private final String printType;

    PrintTemplateApplicationType(String printType) {
        this.printType = printType;
    }

    public String getPrintType() {
        return printType;
    }
}
