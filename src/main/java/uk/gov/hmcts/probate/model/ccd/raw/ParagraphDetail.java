package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParagraphDetail {
    private final String code;
    private final String templateName;

    private final String enableText;
    private final String textLabel;
    private final String textValue;

    private final String enableList;
    private final String listLabel;
    private final DynamicList dynamicList;

    private final String enableStatic;
    private final String staticLabel;
}
