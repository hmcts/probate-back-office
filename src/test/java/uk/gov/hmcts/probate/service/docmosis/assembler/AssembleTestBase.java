package uk.gov.hmcts.probate.service.docmosis.assembler;

import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleTestBase {
    protected AssemblerBase assemblerBase = new AssemblerBase();
    protected CaseData caseData = CaseData.builder().build();


    protected void assertAllForStaticField(List<ParagraphDetail> response, String detailCode, String templateName, String label) {
        assertEquals(detailCode, response.get(0).getCode());
        assertEquals(templateName, response.get(0).getTemplateName());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals(label, response.get(0).getLabel());

    }

}
