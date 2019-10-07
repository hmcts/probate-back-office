package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.CASEWORKER;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.FREE_TEXT;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleFreeText {

    public ParagraphDetail freeText(ParagraphCode paragraphCode, CaseData caseData) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableTextArea("Yes")
                .textAreaLabel(FREE_TEXT.getLabel())
                .code(FREE_TEXT.getCode())
                .build();

        return paragraphDetail;
    }

}
