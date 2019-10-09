package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleWillTest {

    private static final String YES = "Yes";
    private AssemblerBase assemblerBase = new AssemblerBase();

    private AssembleWill assembleWill = new AssembleWill(assemblerBase);

    @Test
    public void testWillSeparatePages() {
        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willSeparatePages(ParagraphCode.WillSepPages, caseData);
        assertEquals( "WillSepPages", response.get(0).getCode());
        assertEquals( "FL-PRB-GNO-ENG-00131.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals( "Separate pages of will", response.get(0).getLabel());
        assertEquals( null, response.get(0).getTextValue());
        assertEquals( null, response.get(0).getTextAreaValue());
        assertEquals( null, response.get(0).getDynamicList());
    }

    @Test
    public void testWillPlights() {
        CaseData caseData = CaseData.builder().build();
        List<ParagraphDetail> response = assembleWill.willPlight(ParagraphCode.WillPlight, caseData);
        assertEquals( null, response.get(0).getDynamicList());
        assertEquals( "WillPlight", response.get(0).getCode());
        assertEquals( "FL-PRB-GNO-ENG-00130.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals( "Plight and condition of will", response.get(0).getLabel());
        assertEquals( "Condition reason e.g. a tear / staple holes / punch holes", response.get(0).getTextValue());
        assertEquals( null, response.get(0).getTextAreaValue());
    }

    @Test
    public void testWillAnyOther() {
        DynamicListItem dynamicListItem = DynamicListItem.builder().code("completeLimit").label("COMPLETE LIMITATION").build();
        DynamicListItem dynamicListItem2 = DynamicListItem.builder().code("exemption").label("EXEMPTION FROM THE WILL").build();

        List<DynamicListItem> dynamicList = new ArrayList<DynamicListItem>();
        dynamicList.add(dynamicListItem);
        dynamicList.add(dynamicListItem2);

        List<List<DynamicListItem>> listItems = new ArrayList<List<DynamicListItem>>();
        listItems.add(dynamicList);

        DynamicList dynamicList1 = DynamicList.builder().listItems(listItems.get(0)).value(DynamicListItem.builder().build()).build();

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willAnyOther(ParagraphCode.WillAnyOther, caseData);
        assertEquals( dynamicList1, response.get(0).getDynamicList());
        assertEquals( "WillAnyOther", response.get(0).getCode());
        assertEquals( null, response.get(0).getTemplateName());
        assertEquals("List", response.get(0).getEnableType().name());
        assertEquals( "Any other wills", response.get(0).getLabel());
        assertEquals( null, response.get(0).getTextValue());
        assertEquals( null, response.get(0).getTextAreaValue());
    }

    @Test
    public void testWillStaple() {

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willStaple(ParagraphCode.WillStaple, caseData);
        assertEquals( "WillStaple", response.get(0).getCode());
        assertEquals( "FL-PRB-GNO-ENG-00132.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals( "Staple removed for photocopying", response.get(0).getLabel());
        assertEquals( null, response.get(0).getTextValue());
        assertEquals( null, response.get(0).getTextAreaValue());
        assertEquals( null, response.get(0).getDynamicList());
    }

}
