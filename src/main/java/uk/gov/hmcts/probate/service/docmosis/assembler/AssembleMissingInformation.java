package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleMissingInformation {
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> missingInfoWill(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("will", "WILL",
                "codicil", "CODICIL");
        List<List<DynamicListItem>> allListItems = new ArrayList();
        allListItems.add(listItems);
        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, allListItems);
    }

    public List<ParagraphDetail> missingInfoDeathCert(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("unclear", "THE ONE SUPPLIED IS UNCLEAR",
                "notSupplied", "ONE WAS NOT SUPPLIED");
        List<List<DynamicListItem>> allListItems = new ArrayList();
        allListItems.add(listItems);

        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, allListItems);
    }

    public List<ParagraphDetail> missingInfoChangeOfApplicant(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(caseData.getPrimaryApplicantFullName()));
    }


}
