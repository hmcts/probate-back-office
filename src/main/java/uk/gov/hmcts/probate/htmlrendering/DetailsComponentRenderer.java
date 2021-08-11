package uk.gov.hmcts.probate.htmlrendering;

import uk.gov.hmcts.probate.model.htmltemplate.DetailsComponentHtmlTemplate;

public class DetailsComponentRenderer {
    private DetailsComponentRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static String renderByReplace(String title, String detailsText) {
        return DetailsComponentHtmlTemplate.DETAILS_TEMPLATE
                .replaceFirst("<title/>", title)
                .replaceFirst("<detailsText/>", detailsText);
    }
}
