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
        assertEquals(response.get(0).getCode(), "WillSepPages");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00131.docx");
        assertEquals(response.get(0).getEnableType().name(), "Text");
        assertEquals(response.get(0).getLabel(), "Separate pages of will");
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getDynamicList(), null);
    }

    @Test
    public void testWillPlights() {
        CaseData caseData = CaseData.builder().build();
        List<ParagraphDetail> response = assembleWill.willPlight(ParagraphCode.WillPlight, caseData);
        assertEquals(response.get(0).getDynamicList(), null);
        assertEquals(response.get(0).getCode(), "WillPlight");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00130.docx");
        assertEquals(response.get(0).getEnableType().name(), "Text");
        assertEquals(response.get(0).getLabel(), "Plight and condition of will");
        assertEquals(response.get(0).getTextValue(), "Condition reason e.g. a tear / staple holes / punch holes");
        assertEquals(response.get(0).getTextAreaValue(), null);
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
        assertEquals(response.get(0).getDynamicList(), dynamicList1);
        assertEquals(response.get(0).getCode(), "WillAnyOther");
        assertEquals(response.get(0).getTemplateName(), null);
        assertEquals(response.get(0).getEnableType().name(), "List");
        assertEquals(response.get(0).getLabel(), "Any other wills");
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
    }

    @Test
    public void testWillStaple() {

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willStaple(ParagraphCode.WillStaple, caseData);
        assertEquals(response.get(0).getCode(), "WillStaple");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00132.docx");
        assertEquals(response.get(0).getEnableType().name(), "Static");
        assertEquals(response.get(0).getLabel(), "Staple removed for photocopying");
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getDynamicList(), null);
    }

}
