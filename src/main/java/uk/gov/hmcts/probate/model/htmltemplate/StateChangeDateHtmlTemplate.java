package uk.gov.hmcts.probate.model.htmltemplate;

public class StateChangeDateHtmlTemplate {
    public static final String STATE_CHANGE_DATE_TEMPLATE =
        "<gridRow><gridCol-two-thirds><p><strong><stateChangeDateText/></strong></p>"
        + "</gridCol-two-thirds><gridCol-one-third>&nbsp;</gridCol-one-third></gridRow>\n";

    private StateChangeDateHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
