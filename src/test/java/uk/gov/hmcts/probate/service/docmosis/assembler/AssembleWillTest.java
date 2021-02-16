package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleWillTest {

    private AssemblerBase assemblerBase = new AssemblerBase();

    private AssembleWill assembleWill = new AssembleWill(assemblerBase);

    @Test
    public void shouldGetWillSeparatePages() {
        CaseData caseData =
            CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willSeparatePages(ParagraphCode.WillSepPages, caseData);
        assertEquals("WillSepPages", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00131.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Separate pages of will", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }

    @Test
    public void shouldGetWillPlights() {
        CaseData caseData = CaseData.builder().build();
        List<ParagraphDetail> response = assembleWill.willPlight(ParagraphCode.WillPlight, caseData);
        assertEquals(null, response.get(0).getDynamicList());
        assertEquals("WillPlight", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00130.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Plight and condition of will", response.get(0).getLabel());
        assertEquals("Condition reason e.g. a tear / staple holes / punch holes", response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetWillAnyOther() {
        CaseData caseData = CaseData.builder().build();
        List<ParagraphDetail> response = assembleWill.willAnyOther(ParagraphCode.WillAnyOther, caseData);
        assertEquals("WillAnyOther", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00129.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Any other wills", response.get(0).getLabel());
        assertEquals("Complete limitation / Exemption from the will", response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }

    @Test
    public void shouldGetWillStaple() {

        CaseData caseData =
            CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willStaple(ParagraphCode.WillStaple, caseData);
        assertEquals("WillStaple", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00132.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Staple removed for photocopying", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }

    @Test
    public void shouldGetWillRevoked() {

        CaseData caseData =
            CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willRevoked(ParagraphCode.WillStaple, caseData);
        assertEquals("WillStaple", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00132.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Staple removed for photocopying", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }

    @Test
    public void shouldGetWillLost() {

        CaseData caseData =
            CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willLost(ParagraphCode.WillStaple, caseData);
        assertEquals("WillStaple", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00132.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Staple removed for photocopying", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }

    @Test
    public void shouldGetWillList() {

        CaseData caseData =
            CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willList(ParagraphCode.WillStaple, caseData);
        assertEquals("WillStaple", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00132.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Staple removed for photocopying", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }

    @Test
    public void shouldGetWillFiat() {

        CaseData caseData =
            CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willFiat(ParagraphCode.WillStaple, caseData);
        assertEquals("WillStaple", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00132.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Staple removed for photocopying", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }

}
