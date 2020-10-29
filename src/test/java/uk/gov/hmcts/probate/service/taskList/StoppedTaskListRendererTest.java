package uk.gov.hmcts.probate.service.taskList;

import org.junit.Before;
import org.junit.Test;

import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.tasklist.StoppedTaskListRenderer;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.probate.model.ApplicationState.BO_CASE_STOPPED;

public class StoppedTaskListRendererTest {

    public static final Long ID = 1L;
    public static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private StoppedTaskListRenderer renderer = new StoppedTaskListRenderer();

    private CaseData.CaseDataBuilder caseDataBuilder;


    @Before
    public void setup() {

        caseDataBuilder = CaseData.builder()
                .grantStoppedDate(LocalDate.of(2020,1,1));

    }

    @Test
    public void shouldRenderStoppedCaseProgressHtmlCorrectly() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState(BO_CASE_STOPPED.getId());
        String result = renderer.renderHtml(caseDetails);

        assertTrue(result.contains("Case stopped"));
    }
}