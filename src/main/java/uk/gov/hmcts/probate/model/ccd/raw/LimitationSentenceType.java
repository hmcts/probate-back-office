package uk.gov.hmcts.probate.model.ccd.raw;

public enum LimitationSentenceType {
    Will1("With codicil"),
    Will2("With <> codicils"),
    Grant1("limited until the original Will or a more authentic copy thereof be proved"),
    Grant2("limited until an authentic copy of the Will be proved"),
    AdmonWill1("This is a De Bonis Non grant - Former Grant Type Place Date"),
    AdmnWill2("THE SOLICTOR FOR THE AFFAIRS OF THE DUCHY OF CORNWALL for the use of His Royal " 
                    + "Highness Charles Philip Arthur George Prince of Wales Duke of Cornwall in right of His said " 
                    + "Duchy"),
    Admon1("This is an Ad Colligenda Bona grant and is limited for the purposes only of " 
                          + "collecting getting in and receiving the estate and doing such acts as may be necessary " 
                          + "for the preservation of the same and until further representation be granted"),
    Admon2("limited to settled land vested in the said deceased under <NAME_OF_SETTLEMENT>");

    private String label;
    
    LimitationSentenceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
