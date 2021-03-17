package uk.gov.hmcts.probate.service.docmosis.assembler;

import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleTestBase {
    protected AssemblerBase assemblerBase = new AssemblerBase();
    protected CaseData caseData = CaseData.builder().build();


    protected void assertAllForStaticField(List<ParagraphDetail> response, ParagraphCode code, HashMap<ParagraphCode,
            String[]> code2expected) {
        assertAllForStaticField(response, code2expected.get(code)[0], code2expected.get(code)[1], code2expected.get(code)[2]);

    }

    protected void assertAllForStaticField(List<ParagraphDetail> response, String detailCode, String templateName, String label) {
        assertEquals(detailCode, response.get(0).getCode());
        assertEquals(templateName, response.get(0).getTemplateName());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals(label, response.get(0).getLabel());

    }

    protected void assertAllForTextField(List<ParagraphDetail> response, ParagraphCode code, HashMap<ParagraphCode,
            String[]> code2expected) {
        assertAllForTextField(response, code2expected.get(code)[0], code2expected.get(code)[1], code2expected.get(code)[2]);

    }

    protected void assertAllForTextField(List<ParagraphDetail> response, String detailCode, String templateName, String label) {
        assertEquals(detailCode, response.get(0).getCode());
        assertEquals(templateName, response.get(0).getTemplateName());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals(label, response.get(0).getLabel());

    }

    protected void assertAllForTextFieldWithDefault(List<ParagraphDetail> response, ParagraphCode code, HashMap<ParagraphCode,
        String[]> code2expected) {
        assertAllForTextFieldWithDefault(response, code2expected.get(code)[0], code2expected.get(code)[1], code2expected.get(code)[2],
            code2expected.get(code)[3]);

    }

    protected void assertAllForTextFieldWithDefault(List<ParagraphDetail> response, String detailCode, String templateName, String label,
                                                    String defaultValue) {
        assertEquals(detailCode, response.get(0).getCode());
        assertEquals(templateName, response.get(0).getTemplateName());
        assertEquals(defaultValue, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals(label, response.get(0).getLabel());

    }

}
