package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParagraphDetail {
    private final String code;
    private final String templateName;

    private final ParagraphDetailEnablementType enableType;
    private final String label;

    private final String textValue;
    private final String textAreaValue;
    private final DynamicList dynamicList;
}
