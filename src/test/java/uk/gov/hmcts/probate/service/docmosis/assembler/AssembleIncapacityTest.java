package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.HashMap;
import java.util.List;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapGen;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapInstitutedExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapMedical;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapOneExec;

@ExtendWith(SpringExtension.class)
class AssembleIncapacityTest extends AssembleTestBase {
    private AssembleIncapacity assembleIncapacity =
        new AssembleIncapacity(assemblerBase);
    private static HashMap<ParagraphCode, String[]> code2Expected = new HashMap();

    @BeforeAll
    public static void setUpClass() {
        code2Expected.put(IncapGen, new String[]{"IncapGen", "FL-PRB-GNO-ENG-00101.docx", "General"});
        code2Expected.put(IncapOneExec, new String[]{"IncapOneExec", "FL-PRB-GNO-ENG-00102.docx", "One executor"});
        code2Expected.put(IncapInstitutedExec, new String[]{"IncapInstitutedExec", "FL-PRB-GNO-ENG-00103.docx",
            "Instituted executor"});
        code2Expected.put(IncapMedical, new String[]{"IncapMedical", "FL-PRB-GNO-ENG-00148.docx",
            "Name of person without capacity"});
    }

    @Test
    void shouldPopulateIncapGen() {

        List<ParagraphDetail> response = assembleIncapacity.incapacityGeneral(IncapGen,
            CaseData.builder().build());
        assertAllForStaticField(response, IncapGen, code2Expected);
    }

    @Test
    void shouldPopulateIncapOneExec() {

        List<ParagraphDetail> response = assembleIncapacity.incapacityOneExecutor(IncapOneExec,
            CaseData.builder().build());
        assertAllForStaticField(response, IncapOneExec, code2Expected);
    }

    @Test
    void shouldPopulateIncapInstitutedExec() {

        List<ParagraphDetail> response = assembleIncapacity.incapacityInstitutedExecutor(IncapInstitutedExec,
            CaseData.builder().build());
        assertAllForStaticField(response, IncapInstitutedExec, code2Expected);
    }

    @Test
    void shouldPopulateIncapMedical() {

        List<ParagraphDetail> response = assembleIncapacity.incapacityMedicalEvidence(IncapMedical,
            CaseData.builder().build());
        assertAllForTextField(response, IncapMedical, code2Expected);
    }

}
