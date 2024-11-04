package uk.gov.hmcts.probate.model.htmltemplate;

public class AppStoppedHtmlTemplate {

    public static final String BASE_TEMPLATE = "<p>This application has been stopped. "
        + "Based on the information you have entered, "
        + "our online service cannot yet handle this type of application.</p>"
        + "<p>Mae'r cais hwn wedi'i atal. Yn seiliedig ar yr wybodaeth rydych chi wedi'i rhoi, "
        + "ni all ein gwasanaeth ar-lein ymdrin â'r math hwn o gais eto.</p>"
        + "<p>You will need to apply for a grant of representation using a <paperformLink/>.</p>"
        + "<p>Bydd angen i chi wneud cais am grant cynrychiolaeth gan ddefnyddio <paperformLinkWelsh/>.</p>\n"
        + "<p><guidanceLink/></p>\n"
        + "<p><guidanceLinkWelsh/></p>\n"
        + "<p>If having read this guidance you're sure that the online service should handle your application, "
        + "contact <probatefeedback@justice.gov.uk/> "
        + "with the broad details of your application and we will help to progress your application.</p>"
        + "<p>Wedi darllen y cyfarwyddyd hwn, os ydych yn sicr y dylai'r gwasanaeth ar-lein ymdrin â'ch cais, "
        + "cysylltwch â <probatefeedback@justice.gov.uk/> gyda manylion eich cais a byddwn yn helpu i symud "
        + "eich cais yn ei flaen.</p>";

    private AppStoppedHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
