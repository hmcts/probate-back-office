package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.notifications.NotificationTemplates;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.State;

import static uk.gov.hmcts.probate.model.Constants.CTSC;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemplateService {

    private final EnglishTemplateIdDeterminer englishTemplateIdDeterminer;
    private final WelshTemplateIdDeterminer welshTemplateIdDeterminer;

    public String getTemplateId(State state, ApplicationType applicationType, String registryLocation,
                                Boolean isWelshLanguagePreferred) {

        if (!isWelshLanguagePreferred) {
            return englishTemplateIdDeterminer.determineTemplateId(state, applicationType, registryLocation);
        } else {
            return welshTemplateIdDeterminer.determineTemplateId(state, applicationType, registryLocation);
        }
    }


}

