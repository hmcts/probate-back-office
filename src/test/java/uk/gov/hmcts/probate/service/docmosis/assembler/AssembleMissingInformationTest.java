package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleMissingInformationTest {

    private static final String YES = "Yes";
    private AssemblerBase assemblerBase = new AssemblerBase();

    private AssembleMissingInformation assembleMissingInformation = new AssembleMissingInformation(assemblerBase);

    @Test
    public void testMissingInfoWill() {

        DynamicListItem dynamicListItem = DynamicListItem.builder().code("will").label("WILL").build();
        DynamicListItem dynamicListItem2 = DynamicListItem.builder().code("codicil").label("CODICIL").build();

        List<DynamicListItem> dynamicList = new ArrayList<DynamicListItem>();
        dynamicList.add(dynamicListItem);
        dynamicList.add(dynamicListItem2);

        List<List<DynamicListItem>> listItems = new ArrayList<List<DynamicListItem>>();
        listItems.add(dynamicList);

        DynamicList dynamicList1 = DynamicList.builder().listItems(listItems.get(0)).value(DynamicListItem.builder().build()).build();


        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleMissingInformation.missingInfoWill(ParagraphCode.IHT421Await, caseData);
        assertEquals(response.get(0).getDynamicList(), dynamicList1);
        assertEquals(response.get(0).getCode(), "IHT421Await");
        assertEquals(response.get(0).getTemplateName(), null);
        assertEquals(response.get(0).getEnableText(), null);
        assertEquals(response.get(0).getTextLabel(), null);
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getListLabel(), "Awaiting IHT421");
        assertEquals(response.get(0).getEnableList(), YES);
        assertEquals(response.get(0).getStaticLabel(), null);

    }

    @Test
    public void testMissingInfoDeathCert() {

        DynamicListItem dynamicListItem = DynamicListItem.builder().code("unclear").label("THE ONE SUPPLIED IS UNCLEAR").build();
        DynamicListItem dynamicListItem2 = DynamicListItem.builder().code("notSupplied").label("ONE WAS NOT SUPPLIED").build();

        List<DynamicListItem> dynamicList = new ArrayList<DynamicListItem>();
        dynamicList.add(dynamicListItem);
        dynamicList.add(dynamicListItem2);

        List<List<DynamicListItem>> listItems = new ArrayList<List<DynamicListItem>>();
        listItems.add(dynamicList);

        DynamicList dynamicList1 = DynamicList.builder().listItems(listItems.get(0)).value(DynamicListItem.builder().build()).build();

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleMissingInformation.missingInfoDeathCert(ParagraphCode.MissInfoDeathCert, caseData);
        assertEquals(response.get(0).getDynamicList(), dynamicList1);
        assertEquals(response.get(0).getCode(), "MissInfoDeathCert");
        assertEquals(response.get(0).getTemplateName(), null);
        assertEquals(response.get(0).getEnableText(), null);
        assertEquals(response.get(0).getTextLabel(), null);
        assertEquals(response.get(0).getTextValue(), null);
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getListLabel(), "Death Certificate");
        assertEquals(response.get(0).getEnableList(), YES);
        assertEquals(response.get(0).getStaticLabel(), null);

    }

    @Test
    public void testMissingInfoChangeOfApplicant() {

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response =
                assembleMissingInformation.missingInfoChangeOfApplicant(ParagraphCode.MissInfoChangeApp, caseData);
        assertEquals(response.get(0).getCode(), "MissInfoChangeApp");
        assertEquals(response.get(0).getTemplateName(), "FL-PRB-GNO-ENG-00127.docx");
        assertEquals(response.get(0).getEnableText(), YES);
        assertEquals(response.get(0).getTextLabel(), "Name change of applicant");
        assertEquals(response.get(0).getTextValue(), "primary fn primary sn");
        assertEquals(response.get(0).getEnableTextArea(), null);
        assertEquals(response.get(0).getTextAreaLabel(), null);
        assertEquals(response.get(0).getTextAreaValue(), null);
        assertEquals(response.get(0).getListLabel(), null);
        assertEquals(response.get(0).getDynamicList(), null);
        assertEquals(response.get(0).getEnableList(), null);
        assertEquals(response.get(0).getStaticLabel(), null);

    }
}
