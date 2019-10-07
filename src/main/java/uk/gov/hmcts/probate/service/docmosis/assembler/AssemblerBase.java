package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;

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

    protected ParagraphDetail getStaticParagraphDetail(ParagraphCode paragraphCode) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableStatic(YES)
                .staticLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .templateName(paragraphCode.getTemplateName())
                .build();

        return paragraphDetail;
    }

    protected ParagraphDetail getTextParagraphDetail(ParagraphCode paragraphCode) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableText(YES)
                .textLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .templateName(paragraphCode.getTemplateName())
                .build();

        return paragraphDetail;
    }

    protected ParagraphDetail getTextParagraphDetailWithDefaultValue(ParagraphCode paragraphCode, String textValue) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableText(YES)
                .textLabel(paragraphCode.getLabel())
                .textValue(textValue)
                .code(paragraphCode.getCode())
                .templateName(paragraphCode.getTemplateName())
                .build();

        return paragraphDetail;
    }

    protected ParagraphDetail createDynamicListParagraphDetail(ParagraphCode paragraphCode, List<DynamicListItem> listItems) {
        DynamicList dynamicList = DynamicList.builder()
                .listItems(listItems)
                .value(DynamicListItem.builder().build())
                .build();

        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableList(YES)
                .dynamicList(dynamicList)
                .listLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .build();

        return paragraphDetail;
    }
}
