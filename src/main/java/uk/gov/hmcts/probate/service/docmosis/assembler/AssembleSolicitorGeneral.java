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
public class AssembleSolicitorGeneral {
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> solsGeneralAuthorityPartners(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsGenPowerAttorney(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsGenVoidForUncertainity(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsGenExtendedRenunciation(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }
}
