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

        List<ParagraphDetail> response = assembleCaseworker.caseworker(ParagraphCode.CASEWORKER, CaseData.builder().build());
        assertEquals(response.get(0).getCode(), "Caseworker");
        assertEquals(response.get(0).getTemplateName(), null);
        assertEquals(response.get(0).getEnableText(), YES);
        assertEquals(response.get(0).getTextLabel(), "Caseworker");
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getEnableList(), null);
        assertEquals(response.get(0).getStaticLabel(), null);

    }

}
