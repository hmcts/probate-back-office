package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleIHT {
    private final AssemblerBase assemblerBase;

    public ParagraphDetail iht205Missing(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetail(paragraphCode);
    }

    public ParagraphDetail ihtAwait421(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetail(paragraphCode);
    }

}
