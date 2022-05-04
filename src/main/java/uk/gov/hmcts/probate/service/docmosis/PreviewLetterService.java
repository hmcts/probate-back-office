package uk.gov.hmcts.probate.service.docmosis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField;
import uk.gov.hmcts.probate.service.docmosis.assembler.Template;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetailEnablementType.Date;
import static uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetailEnablementType.List;
import static uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetailEnablementType.Text;
import static uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetailEnablementType.TextArea;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreviewLetterService {

    private final GenericMapperService genericMapperService;
    private final DateFormatterService dateFormatterService;

    public Map<String, Object> addLetterData(@Valid CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        Map<String, Object> placeholders = genericMapperService.addCaseDataWithRegistryProperties(caseDetails);
        ParagraphField.getAll().stream().filter(field -> field.getFieldPlaceholderName() != null)
            .forEach(field -> addPlaceholderIfPresent(placeholders, field, caseData));
        placeholders.put("templateList", addTemplateList(caseDetails));
        return placeholders;
    }

    private List<Template> addTemplateList(@Valid CaseDetails caseDetails) {
        List<Template> templateList = new ArrayList<>();
        for (CollectionMember<ParagraphDetail> paragraphDetail : caseDetails.getData().getParagraphDetails()) {
            if (paragraphDetail.getValue().getTemplateName() != null) {
                Template newTemplate = Template.builder().value(paragraphDetail.getValue().getTemplateName()).build();
                if (!templateList.contains(newTemplate)) {
                    templateList.add(newTemplate);
                }
            }
        }
        return templateList;
    }

    private void addPlaceholderIfPresent(Map<String, Object> placeholders, ParagraphField paragraphField,
                                         CaseData caseData) {
        if (paragraphField.getFieldPlaceholderName() != null) {
            Optional<CollectionMember<ParagraphDetail>> matchedDetailOptional = caseData.getParagraphDetails()
                .stream().filter(para -> para.getValue().getCode().equals(paragraphField.getFieldCode()))
                .findFirst();
            if (matchedDetailOptional.isPresent()) {
                ParagraphDetail matchedDetail = matchedDetailOptional.get().getValue();
                if (Text.equals(matchedDetail.getEnableType())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(), matchedDetail.getTextValue());
                } else if (TextArea.equals(matchedDetail.getEnableType())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(), matchedDetail.getTextAreaValue());
                } else if (List.equals(matchedDetail.getEnableType())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(),
                        matchedDetail.getDynamicList().getValue().getLabel());
                } else if (Date.equals(matchedDetail.getEnableType())) {
                    placeholders.put(paragraphField.getFieldPlaceholderName(),
                        dateFormatterService.formatDate(matchedDetail.getDateValue()));
                }
            }
        }
    }
}
