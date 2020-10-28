package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlRendering.HeaderRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

public abstract class BaseTaskListRenderer {
    abstract public String renderHtml(CaseDetails caseDetails);
    protected String renderMainHeader() {
        return new HeaderRenderer().render("Case progress");
    }
}
