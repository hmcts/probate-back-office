package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleFreeTextTest {

    private static final String YES = "Yes";
    private AssemblerBase assemblerBase = new AssemblerBase();

    private AssembleFreeText assembleFreeText = new AssembleFreeText(assemblerBase);

    @Test
    public void testFreeText() {

        List<ParagraphDetail> response =
            assembleFreeText.freeText(ParagraphCode.Caseworker, CaseData.builder().build());
        assertEquals("Caseworker", response.get(0).getCode());
        assertEquals(null, response.get(0).getTemplateName());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals("TextArea", response.get(0).getEnableType().name());
        assertEquals("Caseworker", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

}
