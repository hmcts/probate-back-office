package uk.gov.hmcts.probate.service.tasklist;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

public abstract class BaseTaskListRenderer {
    public String renderHtml(CaseDetails caseDetails) {
        String inset = renderInset(caseDetails);
        StringBuilder sb = new StringBuilder(inset);
        return sb.toString();
    }

    protected String renderInset(CaseDetails caseDetails) {

    }

    abstract protected String renderBody(CaseDetails caseDetails);
}
