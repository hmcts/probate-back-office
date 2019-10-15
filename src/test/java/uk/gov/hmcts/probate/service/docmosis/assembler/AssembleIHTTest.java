package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleIHTTest extends AssembleTestBase {

    private static final String YES = "Yes";

    private AssembleIHT assembleIHT = new AssembleIHT(assemblerBase);

    @Test
    public void testIht205Missing() {

        List<ParagraphDetail> response = assembleIHT.iht205Missing(ParagraphCode.IHT205Miss, CaseData.builder().build());
        assertAllForStaticField(response, "IHT205Miss", "FL-PRB-GNO-ENG-00124.docx",
                "IHT205 Missing");
    }

    @Test
    public void testIhtAwait421() {

        List<ParagraphDetail> response = assembleIHT.ihtAwait421(ParagraphCode.IHT421Await, CaseData.builder().build());
        assertAllForStaticField(response, "IHT421Await", "FL-PRB-GNO-ENG-00125.docx",
                "Awaiting IHT421");
    }

    @Test
    public void shouldPopulateIHTNoAssets() {

        List<ParagraphDetail> response = assembleIHT.ihtNoAssets(ParagraphCode.IHT205NoAssets, caseData);
        assertAllForStaticField(response, "IHT205NoAssets", "FL-PRB-GNO-ENG-00144.docx",
                "IHT - no assets onIHT205");
    }

    @Test
    public void shouldPopulateIHTGrossEstate() {

        List<ParagraphDetail> response = assembleIHT.ihtGrossEstate(ParagraphCode.IHT205GrossEstateOver, caseData);
        assertAllForStaticField(response, "IHT205GrossEstateOver", "FL-PRB-GNO-ENG-00145.docx",
                "IHT - gross estate over 325k iht205");
    }

    @Test
    public void shouldPopulateIHT217Miss() {

        List<ParagraphDetail> response = assembleIHT.ihtGrossEstate(ParagraphCode.IHT217Miss, caseData);
        assertAllForStaticField(response, "IHT217Miss", "FL-PRB-GNO-ENG-00146.docx",
                "IHT - IHT217 missing");
    }

    @Test
    public void shouldPopulateIHT400() {

        List<ParagraphDetail> response = assembleIHT.iht400(ParagraphCode.IHTIHT400, caseData);
        assertAllForStaticField(response, "IHT400", "FL-PRB-GNO-ENG-00147.docx",
                "IHT - IHT400");
    }
}
