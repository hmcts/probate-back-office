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

        CaseData caseData = CaseData.builder().build();

        List<ParagraphDetail> response = assembleMissingInformation.missingInfoWill(ParagraphCode.MissInfoWill, caseData);
        assertEquals(null, response.get(0).getDynamicList());
        assertEquals("MissInfoWill", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00126.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Original Will or Codicil", response.get(0).getLabel());
        assertEquals("Will or Coldicil", response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void testMissingInfoDeathCert() {

        DynamicListItem dynamicListItem = DynamicListItem.builder().code("unclear").label("The one supplied was unclear").build();
        DynamicListItem dynamicListItem2 = DynamicListItem.builder().code("notSupplied").label("One was not supplied").build();

        List<DynamicListItem> dynamicList = new ArrayList<DynamicListItem>();
        dynamicList.add(dynamicListItem);
        dynamicList.add(dynamicListItem2);

        List<List<DynamicListItem>> listItems = new ArrayList<List<DynamicListItem>>();
        listItems.add(dynamicList);

        DynamicList dynamicList1 = DynamicList.builder().listItems(listItems.get(0)).value(DynamicListItem.builder().build()).build();

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleMissingInformation.missingInfoDeathCert(ParagraphCode.MissInfoDeathCert, caseData);
        assertEquals(dynamicList1, response.get(0).getDynamicList());
        assertEquals("MissInfoDeathCert", response.get(0).getCode());
        assertEquals(null, response.get(0).getTemplateName());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals("Death Certificate", response.get(0).getLabel());
        assertEquals("List", response.get(0).getEnableType().name());
    }

    @Test
    public void testMissingInfoChangeOfApplicant() {

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response =
                assembleMissingInformation.missingInfoChangeOfApplicant(ParagraphCode.MissInfoChangeApp, caseData);
        assertEquals("MissInfoChangeApp", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00127.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Name change of applicant", response.get(0).getLabel());
        assertEquals("primary fn primary sn", response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
    }
}
