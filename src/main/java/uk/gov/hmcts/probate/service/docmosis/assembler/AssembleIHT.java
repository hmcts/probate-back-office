package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleIHT {
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> iht205Missing(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> ihtAwait421(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> ihtNoAssets(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> ihtGrossEstate(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> iht217Missing(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> iht400(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }
}
