package uk.gov.hmcts.probate.service.docmosis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreviewLetterService {
    private static final String APPLICANT_NAME = "applicantName";
    private static final String CASEWORKER = "caseworker";

    private final GenericMapperService genericMapperService;

    public Map<String, Object> addLetterData(@Valid CaseData caseData) {
        Map<String, Object> placeholders = genericMapperService.addCaseData(caseData);
        placeholders.put(APPLICANT_NAME, caseData.getPrimaryApplicantForenames() + " " + caseData.getPrimaryApplicantSurname());
        placeholders.put(CASEWORKER, getParagraphDetailTextValue(caseData, ParagraphCode.CASEWORKER));

        return placeholders;
    }

    private String getParagraphDetailTextValue(CaseData caseData, ParagraphCode paragraphCode) {
        return caseData.getCategories().getParagraphDetails()
                .stream().filter(para -> para.getValue().getCode().equals(paragraphCode.getCode()))
                .findFirst().get().getValue().getTextValue();
    }
}
