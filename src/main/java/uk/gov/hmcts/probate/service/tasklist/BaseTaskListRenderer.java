package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

public abstract class BaseTaskListRenderer {
    abstract public String renderHtml(CaseDetails caseDetails);
    protected String renderMainHeader() {
        return "<div class='width-50'></div>";
    }
}
