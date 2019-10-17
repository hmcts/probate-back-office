package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.SOLS_REDEC_SOT_DATE;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.SOLS_REDEC_SOT_WILL;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleSolicitorRedeclaration {
    private static final String CONDITIONS_WILL = "Will / Codicil";
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> solsRedecCodicil(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecSotSigned(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecDomicile(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecIntestacyForeignDomicile(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecWillsForeignDomicile(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecMinorityInterest(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecNetEstate(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecTitle(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecClearing(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> solsRedecDate(ParagraphCode paragraphCode, CaseData caseData) {
        List<ParagraphDetail> paragraphDetails = new ArrayList<>();
        for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
            if (SOLS_REDEC_SOT_WILL.name().equals(paragraphField.name())) {
                paragraphDetails.add(
                        assemblerBase.getSingleTextParagraphDetailWithDefaultValue(
                                paragraphField, CONDITIONS_WILL, paragraphCode.getTemplateName()));
            } else if (SOLS_REDEC_SOT_DATE.name().equals(paragraphField.name())) {
                paragraphDetails.add(assemblerBase.getSingleDateParagraphDetails(paragraphField, paragraphCode.getTemplateName()));
            }
        }
        return paragraphDetails;
    }
}
