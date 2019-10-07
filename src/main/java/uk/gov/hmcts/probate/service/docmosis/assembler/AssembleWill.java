package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleWill {
    private final AssemblerBase assemblerBase;

    public ParagraphDetail willSeparatePages(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetail(paragraphCode);
    }

    public ParagraphDetail willPlight(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("condition", "CONDITION REASON E.G.A TEAR",
                "staple", "STAPLE HOLES/ PUNCH HOLES");

        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    public ParagraphDetail willAnyOther(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("completeLimit", "COMPLETE LIMITATION",
                "exemption", "EXEMPTION FROM THE WILL");

        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    public ParagraphDetail willStaple(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetail(paragraphCode);
    }

}
