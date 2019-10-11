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
public class AssembleMissingInformation {
    private static final String CONDITIONS_WILL = "Will / Coldicil";
    private static final String CONDITIONS_DEATH_CERT = "The one supplied was unclear / One was not supplied";
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> missingInfoWill(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(CONDITIONS_WILL));
    }

    public List<ParagraphDetail> missingInfoDeathCert(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(CONDITIONS_DEATH_CERT));
    }

    public List<ParagraphDetail> missingInfoChangeOfApplicant(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(caseData.getPrimaryApplicantFullName()));
    }


}
