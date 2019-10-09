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
        assertEquals("IHT205Miss", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00124.docx", response.get(0).getTemplateName());
        assertEquals(null, response.get(0).getEnableText());
        assertEquals(null, response.get(0).getTextLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getEnableTextArea());
        assertEquals(null, response.get(0).getTextAreaLabel());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getEnableList());
        assertEquals(YES, response.get(0).getEnableStatic());
        assertEquals( "IHT205 Missing", response.get(0).getStaticLabel());

    }

    @Test
    public void testIhtAwait421() {

        List<ParagraphDetail> response = assembleIHT.ihtAwait421(ParagraphCode.IHT421Await, CaseData.builder().build());
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals(null, response.get(0).getEnableText());
        assertEquals(null, response.get(0).getTextLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getEnableTextArea());
        assertEquals(null, response.get(0).getTextAreaLabel());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getEnableList());
        assertEquals(YES, response.get(0).getEnableStatic());
        assertEquals("Awaiting IHT421", response.get(0).getStaticLabel());

    }


}
