package uk.gov.hmcts.probate.model.htmlTemplate;

public class DetailsComponentHtmlTemplate {
    public static final String DETAILS_TEMPLATE = "<details class=\"govuk-details\" data-module=\"govuk-details\">\n" +
            "  <summary class=\"govuk-details__summary\">\n" +
            "    <span class=\"govuk-details__summary-text\">\n" +
            "      <title/>\n" +
            "    </span>\n" +
            "  </summary>\n" +
            "  <div class=\"govuk-details__text\">\n" +
            "    <detailsText/>\n" +
            "  </div>\n" +
            "</details>";

    private DetailsComponentHtmlTemplate() {
        throw new IllegalStateException("Utility class");
    }
}
