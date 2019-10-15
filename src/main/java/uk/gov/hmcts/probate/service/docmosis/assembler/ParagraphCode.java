package uk.gov.hmcts.probate.service.docmosis.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.ANY_OTHER;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.CASEWORKER;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.DATE_OF_REQUEST;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.EXEC_NOT_ACC_EXECUTOR_NAMES;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.FREE_TEXT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT205_MISSING;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.IHT421_AWAITING;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INFO_CHANGE_APP;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.INFO_DEATH_CERT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.MISS_INFO_WILL_OR_DOCICIL;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.PLIGHT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.SEP_PAGES;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.STAPLE;

public enum ParagraphCode {

    FreeText(null, FREE_TEXT),
    Caseworker(null, CASEWORKER),
    EntExecNoAcc("FL-PRB-GNO-ENG-00123.docx", EXEC_NOT_ACC_EXECUTOR_NAMES),
    IHT205Miss("FL-PRB-GNO-ENG-00124.docx", IHT205_MISSING),
    IHT421Await("FL-PRB-GNO-ENG-00125.docx", IHT421_AWAITING),
    MissInfoWill("FL-PRB-GNO-ENG-00126.docx", MISS_INFO_WILL_OR_DOCICIL),
    MissInfoChangeApp("FL-PRB-GNO-ENG-00127.docx", INFO_CHANGE_APP),
    MissInfoDeathCert("FL-PRB-GNO-ENG-00128.docx", INFO_DEATH_CERT),
    MissInfoAwaitResponse ("FL-PRB-GNO-ENG-00152.docx", DATE_OF_REQUEST),
    WillAnyOther("FL-PRB-GNO-ENG-00129.docx", ANY_OTHER),
    WillPlight("FL-PRB-GNO-ENG-00130.docx", PLIGHT),
    WillSepPages("FL-PRB-GNO-ENG-00131.docx", SEP_PAGES),
    WillStaple("FL-PRB-GNO-ENG-00132.docx", STAPLE);

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
