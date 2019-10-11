package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleWill {
    private static final String CONDITIONS_PLIGHT = "Condition reason e.g. a tear / staple holes / punch holes";
    private static final String CONDITIONS_ANY_OTHER = "Complete limitation / Exemption from the will";
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> willSeparatePages(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> willPlight(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(CONDITIONS_PLIGHT));
    }

    public List<ParagraphDetail> willAnyOther(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(CONDITIONS_ANY_OTHER));
    }

    public List<ParagraphDetail> willStaple(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

}
