package uk.gov.hmcts.probate.service.docmosis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreviewLetterService {
    private static final String APPLICANT_NAME = "applicantName";
    private static final String CASEWORKER_NAME = "caseworkerName";
    private static final String NAMES_OF_EXECUTORS = "nameOfExecutors";

    private final GenericMapperService genericMapperService;

    public Map<String, Object> addLetterData(@Valid CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        Map<String, Object> placeholders = genericMapperService.addCaseDataWithRegistryProperties(caseDetails);
        placeholders.put(APPLICANT_NAME, caseData.getPrimaryApplicantForenames() + " " + caseData.getPrimaryApplicantSurname());
        addPlaceholderIfPresent(placeholders, CASEWORKER_NAME, ParagraphCode.CASEWORKER, caseData);
        addPlaceholderIfPresent(placeholders, NAMES_OF_EXECUTORS, ParagraphCode.ENT_EXEC_NOT_ACC, caseData);

        return placeholders;
    }

    private void addPlaceholderIfPresent(Map<String, Object> placeholders, String key,
                                         ParagraphCode paragraphCode, CaseData caseData) {
        Optional<CollectionMember<ParagraphDetail>> matchedDetail = caseData.getParagraphDetails()
                .stream().filter(para -> para.getValue().getCode().equals(paragraphCode.getCode()))
                .findFirst();
        if (matchedDetail.isPresent()) {
            placeholders.put(key, matchedDetail.get().getValue().getTextValue());
        }
    }
}
