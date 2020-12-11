package uk.gov.hmcts.probate.model.htmlTemplate;

public class ContactDetailsHtmlTemplate {

    public static final String contactTemplate = "<p>You will need the case reference or the deceased's full name when you call.</p><br/>" +
            "<p>Telephone: <englishPhoneNumber></p><p>Monday to Thursday, 8:00am to 5pm</p><p>Friday, 8am to 4:30pm</p><br/>" +
            "<p>Welsh language: <welshPhoneNumber></p><p>Monday to Friday, 8:00am to 5pm</p><br/>";

    public static final String emailTemplate = "<email><p>We aim to respond within 10 working days</p>";

    private ContactDetailsHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
