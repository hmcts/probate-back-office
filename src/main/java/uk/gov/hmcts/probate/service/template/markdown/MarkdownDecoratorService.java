package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.businessrule.PA15FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA16FormBusinessRule;
import uk.gov.hmcts.probate.businessrule.PA17FormBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.LinkFormatterService;
import uk.gov.hmcts.probate.service.solicitorexecutor.RenouncingExecutorsMapper;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT_AFTER;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownDecoratorService {
    private final PA15FormBusinessRule pa15FormBusinessRule;
    private final PA16FormBusinessRule pa16FormBusinessRule;
    private final PA17FormBusinessRule pa17FormBusinessRule;
    private final RenouncingExecutorsMapper renouncingExecutorsMapper;
    private final LinkFormatterService linkFormatterService;

    public String getPA15FormLabel(CaseData caseData) {
        String label = "";
        if (pa15FormBusinessRule.isApplicable(caseData)) {
            List<AdditionalExecutorNotApplying> renouncedExecs =
                renouncingExecutorsMapper.getAllRenouncingExecutors(caseData);
            label = renouncedExecs.stream()
                .map(executor -> buildRenouncingExecLabel(executor.getNotApplyingExecutorName()))
                .collect(Collectors.joining());

        }
        return label;
    }

    public String getPA16FormLabel(CaseData caseData) {
        if (pa16FormBusinessRule.isApplicable(caseData)) {
            return "\n*   " + linkFormatterService.formatLink("", PA16_FORM_URL, PA16_FORM_TEXT, "");
        }
        return "";
    }

    public String getPA17FormLabel(CaseData caseData) {
        if (pa17FormBusinessRule.isApplicable(caseData)) {
            return "\n*   " + linkFormatterService.formatLink("", PA17_FORM_URL, PA17_FORM_TEXT, "");
        }
        return "";
    }

    private String buildRenouncingExecLabel(String renouncingExecutorName) {
        return "\n*   " + linkFormatterService.formatLink("", PA15_FORM_URL, PA15_FORM_TEXT,
            PA15_FORM_TEXT_AFTER + renouncingExecutorName);
    }

}
