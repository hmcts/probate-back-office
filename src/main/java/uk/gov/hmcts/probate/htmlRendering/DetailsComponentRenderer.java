package uk.gov.hmcts.probate.htmlRendering;

import uk.gov.hmcts.probate.model.htmlTemplate.DetailsComponentHtmlTemplate;

import static java.lang.String.format;

public class DetailsComponentRenderer {
    public static String renderByReplace(String title, String detailsText) {
        return DetailsComponentHtmlTemplate.detailsTemplate
                .replaceFirst("<title/>", title)
                .replaceFirst("<detailsText/>", detailsText);
    }
}
