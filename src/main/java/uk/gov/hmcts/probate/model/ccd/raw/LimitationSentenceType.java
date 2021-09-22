package uk.gov.hmcts.probate.model.ccd.raw;

public enum LimitationSentenceType {
    Will1("With codicil"),
    Will2("With <> codicils"),
    Grant1("limited until the original Will or a more authentic copy thereof be proved"),
    Grant2("limited until an authentic copy of the Will be proved");

    private String label;
    
    LimitationSentenceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
