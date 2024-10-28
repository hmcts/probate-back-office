package uk.gov.hmcts.probate.model.htmltemplate;

public class ContactDetailsHtmlTemplate {

    public static final String CONTACT_TEMPLATE =
            "<p>You will need the case reference or the deceased's full name when you call.</p>"
            + "<p>Bydd arnoch angen cyfeirnod yr achos neu enw llawn yr ymadawedig pan fyddwch yn ffonio.</p><br/>"
            + "<p>Telephone: <englishPhoneNumber/></p><p><englishOpeningTimes/></p><br/>"
            + "<p>Llinell Gymraeg: <welshPhoneNumber/></p>"
            + "<p><welshOpeningTimes/></p><br/>";

    public static final String EMAIL_TEMPLATE = "<email><p>We aim to respond within 10 working days</p>"
            + "<p>Rydym yn anelu at ymateb o fewn 10 diwrnod gwaith</p>";

    private ContactDetailsHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
