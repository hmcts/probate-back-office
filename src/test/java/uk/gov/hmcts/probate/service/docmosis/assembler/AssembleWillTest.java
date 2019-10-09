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

        List<ParagraphDetail> response = assembleWill.willSeparatePages(ParagraphCode.WILL_SEP_PAGES, caseData);
        assertEquals(response.get(0).getCode(), "WillSepPages");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00131.docx");
        assertEquals(response.get(0).getEnableText(), YES);
        assertEquals(response.get(0).getTextLabel(), "Separate pages of will");
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getListLabel(), null);
        assertEquals(response.get(0).getDynamicList(), null);
        assertEquals(response.get(0).getEnableList(), null);
        assertEquals(response.get(0).getStaticLabel(), null);
    }

    @Test
    public void testWillPlights() {
        DynamicListItem dynamicListItem = DynamicListItem.builder().code("condition").label("CONDITION REASON E.G.A TEAR").build();
        DynamicListItem dynamicListItem2 = DynamicListItem.builder().code("staple").label("STAPLE HOLES/ PUNCH HOLES").build();

        List<DynamicListItem> dynamicList = new ArrayList<DynamicListItem>();
        dynamicList.add(dynamicListItem);
        dynamicList.add(dynamicListItem2);

        List<List<DynamicListItem>> listItems = new ArrayList<List<DynamicListItem>>();
        listItems.add(dynamicList);

        DynamicList dynamicList1 = DynamicList.builder().listItems(listItems.get(0)).value(DynamicListItem.builder().build()).build();

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willPlight(ParagraphCode.WILL_PLIGHT, caseData);
        assertEquals(response.get(0).getDynamicList(), dynamicList1);
        assertEquals(response.get(0).getCode(), "WillPlight");
        assertEquals(response.get(0).getTemplateName(), null);
        assertEquals(response.get(0).getEnableText(), null);
        assertEquals(response.get(0).getTextLabel(), null);
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getListLabel(), "Plight and condition of will");
        assertEquals(response.get(0).getEnableList(), YES);
        assertEquals(response.get(0).getStaticLabel(), null);
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

        List<ParagraphDetail> response = assembleWill.willAnyOther(ParagraphCode.WILL_ANY_OTHER, caseData);
        assertEquals(response.get(0).getDynamicList(), dynamicList1);
        assertEquals(response.get(0).getCode(), "WillAnyOther");
        assertEquals(response.get(0).getTemplateName(), null);
        assertEquals(response.get(0).getEnableText(), null);
        assertEquals(response.get(0).getTextLabel(), null);
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getListLabel(), "Any other wills");
        assertEquals(response.get(0).getEnableList(), YES);
        assertEquals(response.get(0).getStaticLabel(), null);
    }

    @Test
    public void testWillStaple() {

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleWill.willStaple(ParagraphCode.WILL_STAPLE, caseData);
        assertEquals(response.get(0).getCode(), "WillStaple");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00132.docx");
        assertEquals(response.get(0).getEnableText(), null);
        assertEquals(response.get(0).getTextLabel(), null);
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getListLabel(), null);
        assertEquals(response.get(0).getDynamicList(), null);
        assertEquals(response.get(0).getEnableList(), null);
        assertEquals(response.get(0).getStaticLabel(), "Staple removed for photocopying");
        assertEquals(response.get(0).getEnableStatic(), YES);
    }

}
