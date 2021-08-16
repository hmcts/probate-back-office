package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.HashMap;
import java.util.List;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessConsent;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessDate;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessExecution;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessSignature;

public class AssembleWitnessTest extends AssembleTestBase {
    private AssembleWitness assembleWitness = new AssembleWitness(assemblerBase);
    private static HashMap<ParagraphCode, String[]> code2Expected = new HashMap();

    @BeforeClass
    public static void setUpClass() {
        code2Expected.put(WitnessExecution, new String[]{"WitExecution", "FL-PRB-GNO-ENG-00171.docx",
            "Due execution of will affidavit - witness required", "Will / Codicil"});
        code2Expected.put(WitnessSignature, new String[]{"WitSignature", "FL-PRB-GNO-ENG-00172.docx",
            "Signature of affidavit - witness required"});
        code2Expected.put(WitnessDate, new String[]{"WitDate", "FL-PRB-GNO-ENG-00173.docx",
            "Date of will affidavit - witness required", "Will / Codicil"});
        code2Expected
            .put(WitnessConsent, new String[] {"WitConsent", "FL-PRB-GNO-ENG-00174.docx", "Consent of proof of will",
                "Will / Codicil"});

    }

    @Test
    public void shouldPopulateWitnessExecution() {

        List<ParagraphDetail> response = assembleWitness.witnessExecution(WitnessExecution,
            CaseData.builder().build());
        assertAllForTextFieldWithDefault(response, WitnessExecution, code2Expected);
    }

    @Test
    public void shouldPopulateWitnessSignature() {

        List<ParagraphDetail> response = assembleWitness.witnessSignature(WitnessSignature,
            CaseData.builder().build());
        assertAllForTextField(response, WitnessSignature, code2Expected);
    }

    @Test
    public void shouldPopulateWitnessDate() {

        List<ParagraphDetail> response = assembleWitness.witnessDate(WitnessDate,
            CaseData.builder().build());
        assertAllForTextFieldWithDefault(response, WitnessDate, code2Expected);
    }

    @Test
    public void shouldPopulateWitnessConsent() {

        List<ParagraphDetail> response = assembleWitness.witnessConsent(WitnessConsent,
            CaseData.builder().build());
        assertAllForTextFieldWithDefault(response, WitnessConsent, code2Expected);
    }

}
