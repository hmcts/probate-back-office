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
public class AssembleMissingInformation {
    private final AssemblerBase assemblerBase;

    public ParagraphDetail missingInfoWill(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("will", "WILL",
                "codicil", "CODICIL");
        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    public ParagraphDetail missingInfoDeathCert(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = assemblerBase.create2ListItems("unclear", "THE ONE SUPPLIED IS UNCLEAR",
                "notSupplied", "ONE WAS NOT SUPPLIED");

        return assemblerBase.createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    public ParagraphDetail missingInfoChangeOfApplicant(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, caseData.getPrimaryApplicantFullName());
    }


}
