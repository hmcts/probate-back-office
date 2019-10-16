package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.HashMap;
import java.util.List;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidAlias;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidAliasInt;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidAlterations;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidDate;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidHandwriting;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidIdentity;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidKnowledge;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidRecital;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidSearch;

public class AssembleSolicitorAffidavitTest extends AssembleTestBase {

    private AssembleSolicitorAffidavit assembleSolicitorAffidavit = new AssembleSolicitorAffidavit(assemblerBase);
    private static HashMap<ParagraphCode, String[]> code2Expected = new HashMap();

    @BeforeClass
    public static void setUpClass() {
        code2Expected.put(solsAffidAliasInt, new String[]{"AffidAliasInt", "FL-PRB-GNO-ENG-00115.docx", "Alias affidavit (Intestacy)"});
        code2Expected.put(solsAffidAlias, new String[]{"AffidAliasAffidInt", "FL-PRB-GNO-ENG-00116.docx", "Alias Affidavit"});
        code2Expected.put(solsAffidExec, new String[]{"AffidExecution", "FL-PRB-GNO-ENG-00117.docx",
            "Due Execution or rule 12(3) affidavit"});
        code2Expected.put(solsAffidHandwriting, new String[]{"AffidHandWriting", "FL-PRB-GNO-ENG-00118.docx", "Handwriting affidavit"});
        code2Expected.put(solsAffidIdentity, new String[]{"AffidIdentity", "FL-PRB-GNO-ENG-00119.docx", "Identity affidavit"});
        code2Expected.put(solsAffidKnowledge, new String[]{"AffidKnowledge", "FL-PRB-GNO-ENG-00120.docx",
            "Knowledge of contents affidavit"});
        code2Expected.put(solsAffidAlterations, new String[]{"AffidAlterations", "FL-PRB-GNO-ENG-00121.docx", "Alterations affidavit"});
        code2Expected.put(solsAffidDate, new String[]{"AffidDate", "FL-PRB-GNO-ENG-00197.docx", "Date of execution affidavit"});
        code2Expected.put(solsAffidSearch, new String[]{"AffidSearch", "FL-PRB-GNO-ENG-00198.docx", "Search affidavit"});
        code2Expected.put(solsAffidRecital, new String[]{"AffidMisRecital", "FL-PRB-GNO-ENG-00199.docx",
            "Mis-recital of date of will in codicil affidavit"});

    }

    @Test
    public void shouldPopulateSolsAffidAliasInt() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidAliasIntestacy(solsAffidAliasInt,
                CaseData.builder().build());
        assertAllForStaticField(response, solsAffidAliasInt, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidAlias() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidAlias(solsAffidAlias,
                CaseData.builder().build());
        assertAllForStaticField(response, solsAffidAlias, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidExec() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidExec(solsAffidExec,
                CaseData.builder().build());
        assertAllForStaticField(response, solsAffidExec, code2Expected);
    }

    @Test
    public void shouldPopulatesolSAffidHandwriting() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidHandwriting(solsAffidHandwriting,
                CaseData.builder().build());
        assertAllForStaticField(response, solsAffidHandwriting, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidIdentity() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidIdentity(solsAffidIdentity,
                CaseData.builder().build());
        assertAllForStaticField(response, solsAffidIdentity, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidKnowledge() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidKnowledge(solsAffidKnowledge,
                CaseData.builder().build());
        assertAllForStaticField(response, solsAffidKnowledge, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidAlterations() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidAlterations(solsAffidAlterations,
                CaseData.builder().build());
        assertAllForStaticField(response, solsAffidAlterations, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidDate() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidDate(solsAffidDate,
                CaseData.builder().build());
        assertAllForTextField(response, solsAffidDate, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidSearch() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidSearch(solsAffidSearch,
                CaseData.builder().build());
        assertAllForTextField(response, solsAffidSearch, code2Expected);
    }

    @Test
    public void shouldPopulateSolsAffidRecital() {

        List<ParagraphDetail> response = assembleSolicitorAffidavit.solsAffidMisRecital(solsAffidRecital,
                CaseData.builder().build());
        assertAllForTextField(response, solsAffidRecital, code2Expected);
    }
}
