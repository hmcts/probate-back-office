package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class AdmonWillRenunciationCaseExtra {

    private final String showAdmonWillRenunciation;
    private final String pa15FormUrl;
    private final String pa15FormText;
    private final String pa17FormUrl;
    private final String pa17FormText;
    private final String admonWillRenunciationBeforeLinksText;
    private final String admonWillRenunciationMidLinksText;
    private final String admonWillRenunciationAfterLinksText;

}
