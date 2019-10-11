package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class AssemblerBaseTest {

    private AssemblerBase assemblerBase = new AssemblerBase();

    @Test
    public void testCreate2ListItems() {
        String item1Code = "codeOne";
        String item1Label = "labelOne";
        String item2Code = "codeTwo";
        String item2Label = "labelTwo";

        List<DynamicListItem> response = assemblerBase.create2ListItems(item1Code, item1Label, item2Code, item2Label);
        assertEquals(item1Code, response.get(0).getCode());
        assertEquals(item1Label, response.get(0).getLabel());
        assertEquals(item2Code, response.get(1).getCode());
        assertEquals(item2Label, response.get(1).getLabel());
    }

    @Test
    public void testGetStaticParagraphsDetails() {

        List<ParagraphDetail> response = assemblerBase.getStaticParagraphDetails(ParagraphCode.Caseworker);
        assertEquals(ParagraphField.valueOf(ParagraphField.CASEWORKER.toString()).getFieldCode(),
                response.get(0).getCode());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals(ParagraphField.valueOf(ParagraphField.CASEWORKER.toString()).getFieldCode(),
                response.get(0).getLabel());
        assertEquals(null, response.get(0).getTemplateName());
    }

    @Test
    public void testGetTextParagraphsDetails() {

        List<ParagraphDetail> response = assemblerBase.getTextParagraphDetails(ParagraphCode.EntExecNoAcc);
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Executor(s) not accounted for", response.get(0).getLabel());
        assertEquals("EntExecNoAcc", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00123.docx", response.get(0).getTemplateName());
    }

    @Test
    public void testGetTextAreaParagraphsDetails() {

        List<ParagraphDetail> response = assemblerBase.getTextAreaParagraphDetails(ParagraphCode.FreeText);
        assertEquals("TextArea", response.get(0).getEnableType().name());
        assertEquals("Free Text", response.get(0).getLabel());
        assertEquals("FreeText", response.get(0).getCode());
        assertEquals(null, response.get(0).getTemplateName());
    }

    @Test
    public void testGetTextParagraphDetailWithDefaultValue() {

        List<String> textValues = new ArrayList<>();
        textValues.add("test value one");
        List<ParagraphDetail> response = assemblerBase.getTextParagraphDetailWithDefaultValue(ParagraphCode.IHT421Await, textValues);
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals("test value one", response.get(0).getTextValue());
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
    }

    @Test
    public void testCreateDynamicListParagraphDetail() {
        DynamicListItem dynamicListItem = DynamicListItem.builder().code("IHT421Await").label("Awaiting IHT421").build();

        List<DynamicListItem> dynamicList = new ArrayList<DynamicListItem>();
        dynamicList.add(dynamicListItem);

        List<List<DynamicListItem>> listItems = new ArrayList<List<DynamicListItem>>();
        listItems.add(dynamicList);

        DynamicList dynamicList1 = DynamicList.builder().listItems(listItems.get(0)).value(DynamicListItem.builder().build()).build();

        List<ParagraphDetail> response = assemblerBase.createDynamicListParagraphDetail(ParagraphCode.IHT421Await, listItems);
        assertEquals("List", response.get(0).getEnableType().name());
        assertEquals(dynamicList1, response.get(0).getDynamicList());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals("IHT421Await", response.get(0).getCode());
    }
}
