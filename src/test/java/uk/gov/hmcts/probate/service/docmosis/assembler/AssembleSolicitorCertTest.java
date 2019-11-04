package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import org.junit.BeforeClass;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.HashMap;
import java.util.List;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pRedec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aRedec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotNotSigned;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ2;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ3;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ4;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ5;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ6;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ7;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ2;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ3;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ4;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ5;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ6;


public class AssembleSolicitorCertTest extends AssembleTestBase {
    private AssembleSolicitorCert assembleSolicitorCert = new AssembleSolicitorCert(assemblerBase);
    private static HashMap<ParagraphCode, String[]> code2Expected = new HashMap();

    @BeforeClass
    public static void setUpClass() {
        code2Expected.put(SotPa1pRedec, new String[]{"SotPa1pRedec", "FL-PRB-GNO-ENG-00110.docx",
            "PA1P: Part B - Full redec of application"});
        code2Expected.put(SotPa1aRedec, new String[]{"SotPa1aRedec", "FL-PRB-GNO-ENG-00111.docx",
            "PA1A: Part B - Full redec of application"});
        code2Expected.put(SotNotSigned, new String[]{"SotNotSigned", "FL-PRB-GNO-ENG-00158.docx",
            "Paper: Statement of truth not signed"});

        code2Expected.put(SotPa1pQ2, new String[]{"SotPa1pQ2", "FL-PRB-GNO-ENG-00159.docx", "PA1P Q2 incomplete/wrong"});
        code2Expected.put(SotPa1pQ3, new String[]{"SotPa1pQ3", "FL-PRB-GNO-ENG-00160.docx", "PA1P Q3 incomplete/wrong"});
        code2Expected.put(SotPa1pQ4, new String[]{"SotPa1pQ4", "FL-PRB-GNO-ENG-00161.docx", "PA1P Q4 incomplete/wrong"});
        code2Expected.put(SotPa1pQ5, new String[]{"SotPa1pQ5", "FL-PRB-GNO-ENG-00162.docx", "PA1P Q5 incomplete/wrong"});
        code2Expected.put(SotPa1pQ6, new String[]{"SotPa1pQ6", "FL-PRB-GNO-ENG-00163.docx", "PA1P Q6 incomplete/wrong"});
        code2Expected.put(SotPa1pQ7, new String[]{"SotPa1pQ7", "FL-PRB-GNO-ENG-00164.docx", "PA1P Q7 incomplete/wrong"});
        code2Expected.put(SotPa1aQ2, new String[]{"SotPa1aQ2", "FL-PRB-GNO-ENG-00165.docx", "PA1A Q2 incomplete/wrong"});
        code2Expected.put(SotPa1aQ3, new String[]{"SotPa1aQ3", "FL-PRB-GNO-ENG-00166.docx", "PA1A Q3 incomplete/wrong"});
        code2Expected.put(SotPa1aQ4, new String[]{"SotPa1aQ4", "FL-PRB-GNO-ENG-00167.docx", "PA1A Q4 incomplete/wrong"});
        code2Expected.put(SotPa1aQ5, new String[]{"SotPa1aQ5", "FL-PRB-GNO-ENG-00168.docx", "PA1A Q5 incomplete/wrong"});
        code2Expected.put(SotPa1aQ6, new String[]{"SotPa1aQ6", "FL-PRB-GNO-ENG-00169.docx", "PA1A Q6 incomplete/wrong"});
    }

    @Test
    public void shouldPopulateSolsCertAlias() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertAlias(SotPa1pRedec,
            CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertDeceasedAddress() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertDeceasedAddress(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertDeponentsAddress() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertDeponentsAddress(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertDivorce() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertDivorce(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertDivorceDissolved() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertDivorceDissolved(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertDob() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertDob(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertDod() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertDod(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertEpaLpa() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertEpaLpa(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertExecName() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertExecName(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForTextField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertExecNotAccounted() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertExecNotAccounted(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForTextField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertFirmSucceeded() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertFirmSuccceeded(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertLifeMinority() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertLifeMinority(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertOtherWill() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertOtherWill(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForTextField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertPartnersDod() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertPartnersDod(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertPowerReserved() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertPowerReserved(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertSettledLand() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertSettledLand(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertSpouse() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertSpouse(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForTextField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertSurvivalExec() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertSurvivalExec(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertTrustCorp() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertTrustCorp(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForTextField(response, SotPa1pRedec, code2Expected);
    }

    @Test
    public void shouldPopulateSolsCertWillSeparatePages() {

        List<ParagraphDetail> response = assembleSolicitorCert.solsCertWillSeparatePages(SotPa1pRedec,
                CaseData.builder().build());
        assertAllForStaticField(response, SotPa1pRedec, code2Expected);
    }

}
