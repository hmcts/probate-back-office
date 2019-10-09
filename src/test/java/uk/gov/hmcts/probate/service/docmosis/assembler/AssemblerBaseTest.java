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
        assertEquals(response.get(0).getCode(), item1Code);
        assertEquals(response.get(0).getLabel(), item1Label);
        assertEquals(response.get(1).getCode(), item2Code);
        assertEquals(response.get(1).getLabel(), item2Label);
    }

    @Test
    public void testGetStaticParagraphsDetails() {

        List<ParagraphDetail> response = assemblerBase.getStaticParagraphDetails(ParagraphCode.Caseworker);
        assertEquals( ParagraphField.valueOf(ParagraphField.CASEWORKER.toString()).getFieldCode(),
                response.get(0).getCode());
        assertEquals(YES, response.get(0).getEnableStatic());
        assertEquals(ParagraphField.valueOf(ParagraphField.CASEWORKER.toString()).getFieldCode(),
                response.get(0).getStaticLabel());
        assertEquals(null, response.get(0).getTemplateName());
    }

    @Test
    public void testGetTextParagraphsDetails() {

        List<ParagraphDetail> response = assemblerBase.getTextParagraphDetails(ParagraphCode.EntExecNoAcc);
        assertEquals(YES, response.get(0).getEnableText());
        assertEquals("Executor not accounted for", response.get(0).getTextLabel());
        assertEquals("EntExecNoAcc", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00123.docx", response.get(0).getTemplateName());
    }

    @Test
    public void testGetTextAreaParagraphsDetails() {

        List<ParagraphDetail> response = assemblerBase.getTextAreaParagraphDetails(ParagraphCode.FreeText);
        assertEquals(YES, response.get(0).getEnableTextArea());
        assertEquals("Free Text", response.get(0).getTextAreaLabel());
        assertEquals("FreeText", response.get(0).getCode());
        assertEquals(null, response.get(0).getTemplateName());
    }

    @Test
    public void testGetTextParagraphDetailWithDefaultValue() {

        List<String> textValues = new ArrayList<>();
        textValues.add("test value one");
        List<ParagraphDetail> response = assemblerBase.getTextParagraphDetailWithDefaultValue(ParagraphCode.IHT421Await, textValues);
        assertEquals(YES, response.get(0).getEnableText());
        assertEquals("Awaiting IHT421", response.get(0).getTextLabel());
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
        assertEquals(YES, response.get(0).getEnableList());
        assertEquals(dynamicList1, response.get(0).getDynamicList());
        assertEquals("Awaiting IHT421", response.get(0).getListLabel());
        assertEquals("IHT421Await", response.get(0).getCode());
    }
}
