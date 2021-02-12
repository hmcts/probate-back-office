package uk.gov.hmcts.probate.model.htmltemplate;

public class ContactDetailsHtmlTemplate {

    public static final String CONTACT_TEMPLATE =
            "<p>You will need the case reference or the deceased's full name when you call.</p><br/>"
            + "<p>Telephone: <englishPhoneNumber/></p><p><englishOpeningTimes/></p><br/>"
            + "<p>Welsh language: <welshPhoneNumber/></p><p><welshOpeningTimes/></p><br/>";

    public static final String EMAIL_TEMPLATE = "<email><p>We aim to respond within 10 working days</p>";

    private ContactDetailsHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
