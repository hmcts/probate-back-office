package uk.gov.hmcts.probate.service.docmosis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;
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
            Optional<CollectionMember<ParagraphDetail>> matchedDetailOptional = caseData.getParagraphDetails()
                    .stream().filter(para -> para.getValue().getCode().equals(paragraphField.getFieldCode()))
                    .findFirst();
            if (matchedDetailOptional.isPresent()) {
                ParagraphDetail matchedDetail = matchedDetailOptional.get().getValue();
                if (YES.equals(matchedDetail.getEnableText())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(), matchedDetail.getTextValue());
                } else if (YES.equals(matchedDetail.getEnableTextArea())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(), matchedDetail.getTextAreaValue());
                } else if (YES.equals(matchedDetail.getEnableList())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(),
                            matchedDetail.getDynamicList().getValue());
                }
            }
        }
    }
}
