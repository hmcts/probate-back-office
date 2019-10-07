package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.CASEWORKER;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleCaseworker {

    public ParagraphDetail caseworker(ParagraphCode paragraphCode, CaseData caseData) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableText(YES)
                .textLabel(CASEWORKER.getLabel())
                .code(CASEWORKER.getCode())
                .build();

        return paragraphDetail;
    }

}
