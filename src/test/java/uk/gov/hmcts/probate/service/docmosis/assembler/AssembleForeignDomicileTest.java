package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;

import java.util.List;

public class AssembleForeignDomicileTest extends AssembleTestBase {
    private AssembleForeignDomicile assembleForeignDomicile = new AssembleForeignDomicile(assemblerBase);

    @Test
    public void shouldPopulateAffidavitOfLaw() {

        List<ParagraphDetail> response =
            assembleForeignDomicile.affidavitOfLaw(ParagraphCode.ForDomAffidavit, caseData);
        assertAllForStaticField(response, "ForDomAffidavit", "FL-PRB-GNO-ENG-00100.docx",
            "Foreign domicile affidavit of law");
    }

    @Test
    public void shouldPopulateInitialEnquiry() {
        List<ParagraphDetail> response =
            assembleForeignDomicile.initialEnquiry(ParagraphCode.ForDomInitialEnq, caseData);
        assertAllForStaticField(response, "ForDomInitial", "FL-PRB-GNO-ENG-00134.docx",
            "Foreign domicile - initial enquiry");
    }
}
