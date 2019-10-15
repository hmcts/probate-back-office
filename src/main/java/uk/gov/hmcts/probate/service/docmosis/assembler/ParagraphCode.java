package uk.gov.hmcts.probate.service.docmosis.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.AFFIDAVIT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ANY_OTHER;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.CASEWORKER;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.DATE_OF_REQUEST;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_ATTORNEY;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_CONFIRM_DEATH;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_FAMILY_TREE;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_LEADING_APPLICATION;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_NO_TITLE;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_PREJUDICE_WILL;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_SUB_EXEC;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_TWO_APPLICATIONS;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ENT_WRONG_EXEC;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.EXEC_NOT_ACC_EXECUTOR_NAMES;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.FREE_TEXT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT205_GROSS;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT205_MISSING;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT205_NO_ASSETS;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT217_MISSING;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT400;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT421_AWAITING;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INCAPACITY_GENERAL;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INCAPACITY_INSTITUTE_EXEC;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INCAPACITY_MEDICAL;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INCAPACITY_ONE_EXEC;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INFO_CHANGE_APP;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INFO_DEATH_CERT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INITIAL_ENQ;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.MISS_INFO_WILL_OR_DOCICIL;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.PLIGHT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.SEP_PAGES;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.STAPLE;

public enum ParagraphCode {

    FreeText(null, FREE_TEXT),
    Caseworker(null, CASEWORKER),
    ForDomAffidavit("FL-PRB-GNO-ENG-00100.docx", AFFIDAVIT),
    ForDomInitialEnq("FL-PRB-GNO-ENG-00134.docx", INITIAL_ENQ),
    EntExecNoAcc("FL-PRB-GNO-ENG-00123.docx", EXEC_NOT_ACC_EXECUTOR_NAMES),
    EntAttorney("FL-PRB-GNO-ENG-00135.docx", ENT_ATTORNEY),
    EntLeadingApp("FL-PRB-GNO-ENG-00136.docx", ENT_LEADING_APPLICATION),
    EntNoTitle("FL-PRB-GNO-ENG-00137.docx", ENT_NO_TITLE),
    EntTwoApps("FL-PRB-GNO-ENG-00138.docx", ENT_TWO_APPLICATIONS),
    EntFamTree("FL-PRB-GNO-ENG-00139.docx", ENT_FAMILY_TREE),
    EntDeathPa("FL-PRB-GNO-ENG-00140.docx", ENT_CONFIRM_DEATH),
    EntSubExec("FL-PRB-GNO-ENG-00141.docx", ENT_SUB_EXEC),
    EntPrejudiced("FL-PRB-GNO-ENG-00142.docx", ENT_PREJUDICE_WILL),
    EntWrongExec("FL-PRB-GNO-ENG-00143.docx", ENT_WRONG_EXEC),
    IHT205Miss("FL-PRB-GNO-ENG-00124.docx", IHT205_MISSING),
    IHT421Await("FL-PRB-GNO-ENG-00125.docx", IHT421_AWAITING),
    IHT205NoAssets("FL-PRB-GNO-ENG-00144.docx", IHT205_NO_ASSETS),
    IHT205GrossEstateOver("FL-PRB-GNO-ENG-00145.docx", IHT205_GROSS),
    IHT217Miss("FL-PRB-GNO-ENG-00146.docx", IHT217_MISSING),
    IHTIHT400("FL-PRB-GNO-ENG-00147.docx", IHT400),
    MissInfoWill("FL-PRB-GNO-ENG-00126.docx", MISS_INFO_WILL_OR_DOCICIL),
    MissInfoChangeApp("FL-PRB-GNO-ENG-00127.docx", INFO_CHANGE_APP),
    MissInfoDeathCert("FL-PRB-GNO-ENG-00128.docx", INFO_DEATH_CERT),
    MissInfoAwaitResponse("FL-PRB-GNO-ENG-00152.docx", DATE_OF_REQUEST),
    WillAnyOther("FL-PRB-GNO-ENG-00129.docx", ANY_OTHER),
    WillPlight("FL-PRB-GNO-ENG-00130.docx", PLIGHT),
    WillSepPages("FL-PRB-GNO-ENG-00131.docx", SEP_PAGES),
    WillStaple("FL-PRB-GNO-ENG-00132.docx", STAPLE),
    IncapGen("FL-PRB-GNO-ENG-00101.docx", INCAPACITY_GENERAL),
    IncapOneExec("FL-PRB-GNO-ENG-00102.docx", INCAPACITY_ONE_EXEC),
    IncapInstitutedExec("FL-PRB-GNO-ENG-00103.docx", INCAPACITY_INSTITUTE_EXEC),
    IncapMedical("FL-PRB-GNO-ENG-00148.docx", INCAPACITY_MEDICAL);

    private List<ParagraphField> paragraphFields;
    private final String templateName;

    ParagraphCode(String templateName, ParagraphField... fieldsUsed) {
        this.paragraphFields = Arrays.asList(fieldsUsed);
        this.templateName = templateName;
    }


    public List<ParagraphField> getParagraphFields() {
        return paragraphFields;
    }

    public String getTemplateName() {
        return templateName;
    }

    public static List<ParagraphCode> getAll() {
        return Arrays.asList(ParagraphCode.values());
    }

    public static Optional<ParagraphCode> fromFieldCode(String fieldCode) {
        for (ParagraphCode paragraphCode : ParagraphCode.values()) {
            for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
                if (paragraphField.getFieldCode().equals(fieldCode)) {
                    return Optional.of(paragraphCode);
                }
            }
        }

        return Optional.empty();
    }
}
