package uk.gov.hmcts.probate.service.taskList;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData.CaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.tasklist.EscalatedTaskListRenderer;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.probate.model.ApplicationState.REGISTRAR_ESCALATION;

public class EscalatedTaskListRendererTest {

    public static final Long ID = 1L;
    public static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    private EscalatedTaskListRenderer renderer = new EscalatedTaskListRenderer();

    private CaseDataBuilder caseDataBuilder;


    @Before
    public void setup() {

        caseDataBuilder = CaseData.builder()
                .escalatedDate(LocalDate.of(2020,1,1));

    }

    @Test
    public void shouldRenderStoppedCaseProgressHtmlCorrectly() {
        CaseDetails caseDetails = new CaseDetails(caseDataBuilder.build(), LAST_MODIFIED, ID);
        caseDetails.setState(REGISTRAR_ESCALATION.getId());
        String result = renderer.renderHtml(caseDetails);

        assertTrue(result.contains("Case escalated to a Registrar"));
    }
}
