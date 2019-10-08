package uk.gov.hmcts.probate.service.docmosis.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ParagraphCode {

    FREE_TEXT(null, ParagraphField.FREE_TEXT),
    CASEWORKER(null, ParagraphField.CASEWORKER),
    ENT_EXEC_NOT_ACC("FL-PRB-GNO-ENG-00123.docx", ParagraphField.EXEC_NOT_ACC),
    IHT_205_MISSING("FL-PRB-GNO-ENG-00124.docx", ParagraphField.IHT205_MISSING),
    IHT_AWAIT_IHT421("FL-PRB-GNO-ENG-00125.docx", ParagraphField.AWAIT_IHT421),
    MISS_INFO_WILL("FL-PRB-GNO-ENG-00126.docx", ParagraphField.INFO_WILL),
    MISS_INFO_CHANGE_APP("FL-PRB-GNO-ENG-00127.docx", ParagraphField.INFO_CHANGE_APP),
    MISS_INFO_DEATH_CERT("FL-PRB-GNO-ENG-00128.docx", ParagraphField.INFO_DEATH_CERT),
    WILL_ANY_OTHER("FL-PRB-GNO-ENG-00129.docx", ParagraphField.ANY_OTHER),
    WILL_PLIGHT("FL-PRB-GNO-ENG-00130.docx", ParagraphField.PLIGHT),
    WILL_SEP_PAGES("FL-PRB-GNO-ENG-00131.docx", ParagraphField.SEP_PAGES),
    WILL_STAPLE("FL-PRB-GNO-ENG-00132.docx", ParagraphField.STAPLE);

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
