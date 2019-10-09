package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleIHTTest {

    private static final String YES = "Yes";
    private AssemblerBase assemblerBase = new AssemblerBase();

    private AssembleIHT assembleIHT = new AssembleIHT(assemblerBase);

    @Test
    public void testIht205Missing() {

        List<ParagraphDetail> response = assembleIHT.iht205Missing(ParagraphCode.IHT205Miss, CaseData.builder().build());
        assertEquals(response.get(0).getCode(), "IHT205Miss");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00124.docx");
        assertEquals(response.get(0).getEnableText(), null);
        assertEquals(response.get(0).getTextLabel(), null);
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getEnableList(), null);
        assertEquals(response.get(0).getEnableStatic(), YES);
        assertEquals(response.get(0).getStaticLabel(), "IHT205 Missing");

    }

    @Test
    public void testIhtAwait421() {

        List<ParagraphDetail> response = assembleIHT.ihtAwait421(ParagraphCode.IHT421Await, CaseData.builder().build());
        assertEquals(response.get(0).getCode(), "IHT421Await");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00125.docx");
        assertEquals(response.get(0).getEnableText(), null);
        assertEquals(response.get(0).getTextLabel(), null);
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getEnableList(), null);
        assertEquals(response.get(0).getEnableStatic(), YES);
        assertEquals(response.get(0).getStaticLabel(), "Awaiting IHT421");

    }


}
