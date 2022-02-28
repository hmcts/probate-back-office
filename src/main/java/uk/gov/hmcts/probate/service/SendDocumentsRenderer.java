package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_TEXT_AFTER;
import static uk.gov.hmcts.probate.model.Constants.PA14_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_TEXT_AFTER;
import static uk.gov.hmcts.probate.model.Constants.PA15_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA16_FORM_URL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_TEXT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PA17_FORM_URL;

@Service
@RequiredArgsConstructor
public class SendDocumentsRenderer {
    private final LinkFormatterService linkFormatterService;

    public String getPA14NotApplyingExecutorText(String notApplyingExecutorName) {
        return linkFormatterService.formatLink("", PA14_FORM_URL, PA14_FORM_TEXT,
            PA14_FORM_TEXT_AFTER + notApplyingExecutorName);
    }

    public String getPA15NotApplyingExecutorText(String notApplyingExecutorName) {
        return linkFormatterService.formatLink("", PA15_FORM_URL, PA15_FORM_TEXT,
            PA15_FORM_TEXT_AFTER + notApplyingExecutorName);
    }

    public String getPA16FormText() {
        return linkFormatterService.formatLink("", PA16_FORM_URL, PA16_FORM_TEXT, "");
    }

    public String getPA17FormText() {
        return linkFormatterService.formatLink("", PA17_FORM_URL, PA17_FORM_TEXT, "");
    }

    public String getAdmonWillRenunciationText() {
        return linkFormatterService.formatLink(ADMON_WILL_RENUNCIATION_BEFORE_LINKS_TEXT,
            PA15_FORM_URL, PA15_FORM_TEXT_AFTER, ADMON_WILL_RENUNCIATION_MID_LINKS_TEXT)
            + linkFormatterService.formatLink("", PA17_FORM_URL, PA17_FORM_TEXT_ADMON_WILL,
            ADMON_WILL_RENUNCIATION_AFTER_LINKS_TEXT);
    }
}
