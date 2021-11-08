package uk.gov.hmcts.probate.model.htmltemplate;

public class AppStoppedHtmlTemplate {

    public static final String BASE_TEMPLATE = "<p>This application has been stopped. "
        + "Based on the information you have entered, "
        + "our online service cannot yet handle this type of application.<br/>"
        + "You will need to apply for a grant of representation using a <paperformLink/>.</p>\n"
        + "<p><guidanceLink/></p>\n"
        + "<p>If having read this guidance you're sure that the online service should handle your application, "
        + "contact <probatefeedback@justice.gov.uk/> "
        + "with the broad details of your application and we will help to progress your application.</p>";

    private AppStoppedHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
