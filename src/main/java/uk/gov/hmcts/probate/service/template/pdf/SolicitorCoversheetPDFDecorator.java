package uk.gov.hmcts.probate.service.template.pdf;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;


@Component
@AllArgsConstructor
public class SolicitorCoversheetPDFDecorator {
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final FileSystemResourceService fileSystemResourceService;

    private static final String TEMPLATE_EXTRAS_DIRECTORY = "templates/pdf/caseExtras/";
    private static final String PA16_FORM_FILE = "PA16Form.json";

    public String decorate(CaseData caseData) {
        String json = "";
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            String jsonTemplate = fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_EXTRAS_DIRECTORY 
                + PA16_FORM_FILE);
            jsonTemplate = jsonTemplate.replaceAll("<PA16FormTEXT>", PA16_FORM_TEXT);
            jsonTemplate = jsonTemplate.replaceAll("<PA16FormURL>", PA16_FORM_URL);
            json +=  jsonTemplate;
        }
        
        return json;
    }
}
