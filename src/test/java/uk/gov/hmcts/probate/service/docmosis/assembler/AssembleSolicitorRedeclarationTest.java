package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecClearing;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecCodicil;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecDomicile;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecIntForDom;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecMinority;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecNetEstate;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecSotSigned;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecTitle;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.soplsRedecWillsForDom;

public class AssembleSolicitorRedeclarationTest extends AssembleTestBase {
    private static HashMap<ParagraphCode, String[]> code2Expected = new HashMap();
    private AssembleSolicitorRedeclaration assembleSolicitorRedeclaration =
        new AssembleSolicitorRedeclaration(assemblerBase);

    @BeforeClass
    public static void setUpClass() {
        code2Expected.put(solsRedecCodicil, new String[] {"RedecCodicil", "FL-PRB-GNO-ENG-00201.docx",
            "Re-declare: Codicil omitted"});
        code2Expected.put(solsRedecSotSigned, new String[] {"RedecSotSigned", "FL-PRB-GNO-ENG-00202.docx",
            "Re-declare: SOT not signed"});
        code2Expected.put(solsRedecDomicile, new String[] {"RedecDomcile", "FL-PRB-GNO-ENG-00203.docx",
            "Re-declare: Domicile"});
        code2Expected.put(solsRedecIntForDom, new String[] {"RedecIntForDom", "FL-PRB-GNO-ENG-00204.docx",
            "Re-declare: Intestacy foreign domicile"});
        code2Expected.put(soplsRedecWillsForDom, new String[] {"RedecWillsForDom", "FL-PRB-GNO-ENG-00205.docx",
            "Re-declare: Wills foreign domicile"});
        code2Expected.put(solsRedecMinority, new String[] {"RedecMinority", "FL-PRB-GNO-ENG-00206.docx",
            "Re-declare: Minority interest"});
        code2Expected.put(solsRedecNetEstate, new String[] {"RedecNetEstate", "FL-PRB-GNO-ENG-00207.docx",
            "Re-declare: Net estate over SSL"});
        code2Expected.put(solsRedecTitle, new String[] {"RedecTitle", "FL-PRB-GNO-ENG-00208.docx",
            "Re-declare: Title"});
        code2Expected.put(solsRedecClearing, new String[] {"RedecClearing", "FL-PRB-GNO-ENG-00209.docx",
            "Re-declare: Clearing"});
    }

    @Test
    public void shouldPopulateSolsRedecDate() {

        List<ParagraphDetail> response = assembleSolicitorRedeclaration.solsRedecCodicil(solsRedecCodicil,
            CaseData.builder().build());
        assertAllForStaticField(response, solsRedecCodicil, code2Expected);
    }

    @Test
    public void shouldPopulateSolsRedecSotSigned() {

        List<ParagraphDetail> response = assembleSolicitorRedeclaration.solsRedecSotSigned(solsRedecSotSigned,
            CaseData.builder().build());
        assertAllForStaticField(response, solsRedecSotSigned, code2Expected);
    }

    @Test
    public void shouldPopulateSolsRedecDomicile() {

        List<ParagraphDetail> response = assembleSolicitorRedeclaration.solsRedecDomicile(solsRedecDomicile,
            CaseData.builder().build());
        assertAllForStaticField(response, solsRedecDomicile, code2Expected);
    }

    @Test
    public void shouldPopulateSolssoplsRedecWillsForDom() {

        List<ParagraphDetail> response =
            assembleSolicitorRedeclaration.solsRedecWillsForeignDomicile(soplsRedecWillsForDom,
                CaseData.builder().build());
        assertAllForStaticField(response, soplsRedecWillsForDom, code2Expected);
    }

    @Test
    public void shouldPopulateSolsRedecMinority() {

        List<ParagraphDetail> response = assembleSolicitorRedeclaration.solsRedecMinorityInterest(solsRedecMinority,
            CaseData.builder().build());
        assertAllForStaticField(response, solsRedecMinority, code2Expected);
    }

    @Test
    public void shouldPopulateSolsRedecNetEstate() {

        List<ParagraphDetail> response = assembleSolicitorRedeclaration.solsRedecNetEstate(solsRedecNetEstate,
            CaseData.builder().build());
        assertAllForStaticField(response, solsRedecNetEstate, code2Expected);
    }

    @Test
    public void shouldPopulateSolsRedecTitle() {

        List<ParagraphDetail> response = assembleSolicitorRedeclaration.solsRedecTitle(solsRedecTitle,
            CaseData.builder().build());
        assertAllForStaticField(response, solsRedecTitle, code2Expected);
    }

    @Test
    public void shouldPopulateSolsRedecClearing() {

        List<ParagraphDetail> response = assembleSolicitorRedeclaration.solsRedecClearing(solsRedecClearing,
            CaseData.builder().build());
        assertAllForStaticField(response, solsRedecClearing, code2Expected);
    }

    @Test
    public void shouldPopulateSolsRedecIntestacy() {

        List<ParagraphDetail> response =
            assembleSolicitorRedeclaration.solsRedecIntestacyForeignDomicile(solsRedecClearing,
                CaseData.builder().build());
        assertAllForStaticField(response, solsRedecClearing, code2Expected);
    }


    @Test
    public void shouldPopulateSolsRedecSotDate() {

        CaseData caseData =
            CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response =
            assembleSolicitorRedeclaration.solsRedecDate(ParagraphCode.solsRedecSotDate, caseData);
        assertEquals("RedecSotDate", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00200.docx", response.get(0).getTemplateName());
        assertEquals("Date", response.get(0).getEnableType().name());
        assertEquals("Re-declare: incorrect or missing date of will in SOT - date", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
        assertEquals(null, response.get(0).getDynamicList());
        assertEquals("RedecSotWill", response.get(1).getCode());
        assertEquals("FL-PRB-GNO-ENG-00200.docx", response.get(1).getTemplateName());
        assertEquals("Text", response.get(1).getEnableType().name());
        assertEquals("Re-declare: incorrect or missing date of will in SOT", response.get(1).getLabel());
        assertEquals("Will / Codicil", response.get(1).getTextValue());
        assertEquals(null, response.get(1).getTextAreaValue());
        assertEquals(null, response.get(1).getDynamicList());
    }

}
