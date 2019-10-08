package uk.gov.hmcts.probate.service.docmosis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField;

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
        ParagraphField.getAll().stream().filter(field -> field.getFieldPlaceholderName() != null)
                .forEach(field -> addPlaceholderIfPresent(placeholders, field, caseData));

        return placeholders;
    }

    private void addPlaceholderIfPresent(Map<String, Object> placeholders, ParagraphField paragraphField, CaseData caseData) {
        if (paragraphField.getFieldPlaceholderName() != null) {
            Optional<CollectionMember<ParagraphDetail>> matchedDetail = caseData.getParagraphDetails()
                    .stream().filter(para -> para.getValue().getCode().equals(paragraphField.getFieldCode()))
                    .findFirst();
            if (matchedDetail.isPresent()) {
                if (YES.equals(matchedDetail.get().getValue().getEnableText())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(), matchedDetail.get().getValue().getTextValue());
                } else if (YES.equals(matchedDetail.get().getValue().getEnableTextArea())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(), matchedDetail.get().getValue().getTextAreaValue());
                } else if (YES.equals(matchedDetail.get().getValue().getEnableList())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(),
                            matchedDetail.get().getValue().getDynamicList().getValue().getCode());
                }
            }
        }
    }
}
