package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetailEnablementType;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssemblerBase {

    protected List<DynamicListItem> create2ListItems(String item1Code, String item1Label, String item2Code, String item2Label) {
        List<DynamicListItem> listItems = new ArrayList<>();
        DynamicListItem item1 = DynamicListItem.builder()
                .code(item1Code)
                .label(item1Label)
                .build();
        listItems.add(item1);
        DynamicListItem item2 = DynamicListItem.builder()
                .code(item2Code)
                .label(item2Label)
                .build();
        listItems.add(item2);

        return listItems;
    }

    protected List<ParagraphDetail> getStaticParagraphDetails(ParagraphCode paragraphCode) {
        List<ParagraphDetail> paragraphDetails = new ArrayList<>();
        for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
            ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                    .enableType(ParagraphDetailEnablementType.Static)
                    .label(paragraphField.getFieldLabel())
                    .code(paragraphField.getFieldCode())
                    .templateName(paragraphCode.getTemplateName())
                    .build();
            paragraphDetails.add(paragraphDetail);
        }

        return paragraphDetails;
    }

    protected List<ParagraphDetail>  getTextParagraphDetails(ParagraphCode paragraphCode) {
        List<ParagraphDetail> paragraphDetails = new ArrayList<>();
        for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
            ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                    .enableType(ParagraphDetailEnablementType.Text)
                    .label(paragraphField.getFieldLabel())
                    .code(paragraphField.getFieldCode())
                    .templateName(paragraphCode.getTemplateName())
                    .build();
            paragraphDetails.add(paragraphDetail);
        }
        return paragraphDetails;
    }

    protected List<ParagraphDetail>  getTextAreaParagraphDetails(ParagraphCode paragraphCode) {
        List<ParagraphDetail> paragraphDetails = new ArrayList<>();
        for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
            ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                    .enableType(ParagraphDetailEnablementType.TextArea)
                    .label(paragraphField.getFieldLabel())
                    .code(paragraphField.getFieldCode())
                    .templateName(paragraphCode.getTemplateName())
                    .build();
            paragraphDetails.add(paragraphDetail);
        }
        return paragraphDetails;
    }

    protected List<ParagraphDetail> getTextParagraphDetailWithDefaultValue(ParagraphCode paragraphCode, List<String> textValues) {
        List<ParagraphDetail> paragraphDetails = new ArrayList<>();
        int index = 0;
        for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
            ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                    .enableType(ParagraphDetailEnablementType.Text)
                    .label(paragraphField.getFieldLabel())
                    .textValue(textValues.get(index))
                    .code(paragraphField.getFieldCode())
                    .templateName(paragraphCode.getTemplateName())
                    .build();
            paragraphDetails.add(paragraphDetail);
            index ++;
        }
        return paragraphDetails;
    }

    protected List<ParagraphDetail> createDynamicListParagraphDetail(ParagraphCode paragraphCode, List<List<DynamicListItem>> listItems) {
        List<ParagraphDetail> paragraphDetails = new ArrayList<>();
        int index = 0;
        for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
            DynamicList dynamicList = DynamicList.builder()
                    .listItems(listItems.get(index))
                    .value(DynamicListItem.builder().build())
                    .build();

            ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                    .enableType(ParagraphDetailEnablementType.List)
                    .dynamicList(dynamicList)
                    .label(paragraphField.getFieldLabel())
                    .code(paragraphField.getFieldCode())
                    .build();
            paragraphDetails.add(paragraphDetail);
            index ++;
        }

        return paragraphDetails;
    }
}
