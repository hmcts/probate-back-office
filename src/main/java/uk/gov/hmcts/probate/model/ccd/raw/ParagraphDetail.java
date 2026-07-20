package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class ParagraphDetail {
    @CCD(label = " ")
    private final String code;
    @CCD(label = " ")
    private final String templateName;

    @CCD(
            label = " ",
            showCondition = "enableType=\"NeverShowThisField\"",
            typeOverride = FieldType.FixedList,
            typeParameterOverride = "enablementTypeFixedList"
    )
    private final ParagraphDetailEnablementType enableType;
    @CCD(label = " ")
    private final String label;

    @CCD(label = " ", showCondition = "enableType=\"Text\"")
    private final String textValue;
    @CCD(label = " ", showCondition = "enableType=\"TextArea\"", typeOverride = FieldType.TextArea)
    private final String textAreaValue;
    @CCD(label = " ", showCondition = "enableType=\"List\"", typeOverride = FieldType.DynamicList)
    private final DynamicList dynamicList;
    @CCD(label = " ", showCondition = "enableType=\"Date\"")
    private final LocalDate dateValue;
}
