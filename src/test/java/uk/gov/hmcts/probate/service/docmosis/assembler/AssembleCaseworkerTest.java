package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleCaseworkerTest {

    private static final String YES = "Yes";
    private AssemblerBase assemblerBase = new AssemblerBase();

    private AssembleCaseworker assembleCaseworker = new AssembleCaseworker(assemblerBase);

    @Test
    public void testCaseworker() {

        List<ParagraphDetail> response = assembleCaseworker.caseworker(ParagraphCode.Caseworker, CaseData.builder().build());
        assertEquals(response.get(0).getCode(), "Caseworker");
        assertEquals(response.get(0).getTemplateName(), null);
        assertEquals(response.get(0).getEnableType().name(), "Text");
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
    }

}
