package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

class AssembleIHTTest extends AssembleTestBase {

    private AssembleIHT assembleIHT = new AssembleIHT(assemblerBase);

    @Test
    void testIht205Missing() {

        List<ParagraphDetail> response =
            assembleIHT.iht205Missing(ParagraphCode.IHT205Miss, CaseData.builder().build());
        assertAllForStaticField(response, "IHT205Miss", "FL-PRB-GNO-ENG-00124.docx",
            "IHT205 Missing");
    }

    @Test
    void testIhtAwait421() {

        List<ParagraphDetail> response = assembleIHT.ihtAwait421(ParagraphCode.IHT421Await, CaseData.builder().build());
        assertAllForStaticField(response, "IHT421Await", "FL-PRB-GNO-ENG-00125.docx",
            "Awaiting IHT421");
    }

    @Test
    void shouldPopulateIHTNoAssets() {

        List<ParagraphDetail> response = assembleIHT.ihtNoAssets(ParagraphCode.IHT205NoAssets, caseData);
        assertAllForStaticField(response, "IHT205NoAssets", "FL-PRB-GNO-ENG-00144.docx",
            "IHT - no assets onIHT205");
    }

    @Test
    void shouldPopulateIHTGrossEstate() {

        List<ParagraphDetail> response = assembleIHT.ihtGrossEstate(ParagraphCode.IHT205GrossEstateOver, caseData);
        assertAllForStaticField(response, "IHT205GrossEstateOver", "FL-PRB-GNO-ENG-00145.docx",
            "IHT - gross estate over 325k iht205");
    }

    @Test
    void shouldPopulateIHT217Miss() {

        List<ParagraphDetail> response = assembleIHT.iht217Missing(ParagraphCode.IHT217Miss, caseData);
        assertAllForStaticField(response, "IHT217Miss", "FL-PRB-GNO-ENG-00146.docx",
            "IHT - IHT217 missing");
    }

    @Test
    void shouldPopulateIHT400() {

        List<ParagraphDetail> response = assembleIHT.iht400(ParagraphCode.IHTIHT400, caseData);
        assertAllForStaticField(response, "IHT400", "FL-PRB-GNO-ENG-00147.docx",
            "IHT - IHT400");
    }
}
