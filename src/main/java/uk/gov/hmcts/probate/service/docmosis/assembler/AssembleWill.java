package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleWill {
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> willSeparatePages(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> willPlight(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("condition", "CONDITION REASON E.G.A TEAR",
                "staple", "STAPLE HOLES/ PUNCH HOLES");

        List<List<DynamicListItem>> allListItems = new ArrayList<>();
        allListItems.add(listItems);
        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, allListItems);
    }

    public List<ParagraphDetail> willAnyOther(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("completeLimit", "COMPLETE LIMITATION",
                "exemption", "EXEMPTION FROM THE WILL");

        List<List<DynamicListItem>> allListItems = new ArrayList<>();
        allListItems.add(listItems);
        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, allListItems);
    }

    public List<ParagraphDetail> willStaple(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

}
