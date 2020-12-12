package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.htmlrendering.HeadingRenderer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

public abstract class BaseTaskListRenderer {
    public abstract String renderHtml(CaseDetails caseDetails);
    protected String renderMainHeader() {
        return HeadingRenderer.render("Case progress");
    }
}
