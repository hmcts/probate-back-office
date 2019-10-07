package uk.gov.hmcts.probate.service.docmosis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreviewLetterService {

    private final GenericMapperService genericMapperService;

    public Map<String, Object> addLetterData(@Valid CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        Map<String, Object> placeholders = genericMapperService.addCaseDataWithRegistryProperties(caseDetails);
        addPlaceholderIfPresent(placeholders, ParagraphCode.FREE_TEXT, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.CASEWORKER, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.MISS_INFO_WILL, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.ENT_EXEC_NOT_ACC, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.MISS_INFO_CHANGE_APP, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.MISS_INFO_DEATH_CERT, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.WILL_ANY_OTHER, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.WILL_PLIGHT, caseData);
        addPlaceholderIfPresent(placeholders, ParagraphCode.WILL_SEP_PAGES, caseData);

        return placeholders;
    }

    private void addPlaceholderIfPresent(Map<String, Object> placeholders, ParagraphCode paragraphCode, CaseData caseData) {
        if (paragraphCode.getPlaceholderName() != null) {
            Optional<CollectionMember<ParagraphDetail>> matchedDetail = caseData.getParagraphDetails()
                    .stream().filter(para -> para.getValue().getCode().equals(paragraphCode.getCode()))
                    .findFirst();
            if (matchedDetail.isPresent()) {
                if ("Yes".equals(matchedDetail.get().getValue().getEnableText())) {
                    placeholders.put(paragraphCode.getPlaceholderName(), matchedDetail.get().getValue().getTextValue());
                } else if ("Yes".equals(matchedDetail.get().getValue().getEnableTextArea())) {
                    placeholders.put(paragraphCode.getPlaceholderName(), matchedDetail.get().getValue().getTextAreaValue());
                } else if ("Yes".equals(matchedDetail.get().getValue().getEnableList())) {
                    placeholders.put(paragraphCode.getPlaceholderName(),
                            matchedDetail.get().getValue().getDynamicList().getValue().getCode());
                }
            }
        }
    }
}
