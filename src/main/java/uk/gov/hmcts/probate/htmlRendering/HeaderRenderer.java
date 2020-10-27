package uk.gov.hmcts.probate.htmlRendering;

import static java.lang.String.format;

public class HeaderRenderer {
    public String render(String headerText) {
        return format("## %s", headerText);
    }
}
