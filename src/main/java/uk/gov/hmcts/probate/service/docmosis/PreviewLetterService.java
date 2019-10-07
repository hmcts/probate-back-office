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

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreviewLetterService {

    private final GenericMapperService genericMapperService;

    public Map<String, Object> addLetterData(@Valid CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        Map<String, Object> placeholders = genericMapperService.addCaseDataWithRegistryProperties(caseDetails);
        ParagraphCode.getAll().stream().filter(code -> code.getPlaceholderName() != null).
                forEach(code -> addPlaceholderIfPresent(placeholders, code, caseData));

        return placeholders;
    }

    private void addPlaceholderIfPresent(Map<String, Object> placeholders, ParagraphCode paragraphCode, CaseData caseData) {
        if (paragraphCode.getPlaceholderName() != null) {
            Optional<CollectionMember<ParagraphDetail>> matchedDetail = caseData.getParagraphDetails()
                    .stream().filter(para -> para.getValue().getCode().equals(paragraphCode.getCode()))
                    .findFirst();
            if (matchedDetail.isPresent()) {
                if (YES.equals(matchedDetail.get().getValue().getEnableText())) {
                    placeholders.put(paragraphCode.getPlaceholderName(), matchedDetail.get().getValue().getTextValue());
                } else if (YES.equals(matchedDetail.get().getValue().getEnableTextArea())) {
                    placeholders.put(paragraphCode.getPlaceholderName(), matchedDetail.get().getValue().getTextAreaValue());
                } else if (YES.equals(matchedDetail.get().getValue().getEnableList())) {
                    placeholders.put(paragraphCode.getPlaceholderName(),
                            matchedDetail.get().getValue().getDynamicList().getValue().getCode());
                }
            }
        }
    }
}
