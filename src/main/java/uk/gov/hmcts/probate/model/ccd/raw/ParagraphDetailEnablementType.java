package uk.gov.hmcts.probate.model.ccd.raw;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.api.ComplexType;

@ComplexType(name = "enablementTypeFixedList", generate = true)
public enum ParagraphDetailEnablementType {

    @CCD(label = "DO NOT SHOW - Text")
    Text(),
    @CCD(label = "DO NOT SHOW - TextArea")
    TextArea(),
    @CCD(label = "DO NOT SHOW - List")
    List(),
    @CCD(label = "DO NOT SHOW - Static")
    Static(),
    @CCD(label = "DO NOT SHOW - Date")
    Date();
}
