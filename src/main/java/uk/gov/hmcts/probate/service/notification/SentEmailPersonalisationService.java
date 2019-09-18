package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.SentEmail;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SentEmailPersonalisationService {

    private static final String PERSONALISATION_SENT_EMAIL_BODY = "body";
    private static final String PERSONALISATION_SENT_EMAIL_TO = "to";
    private static final String PERSONALISATION_SENT_EMAIL_FROM = "from";
    private static final String PERSONALISATION_SENT_EMAIL_SUBJECT = "subject";
    private static final String PERSONALISATION_SENT_EMAIL_SENT_ON = "sentOn";

    public Map<String, Object> getPersonalisation(SentEmail sentEmail) {
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_SENT_EMAIL_BODY, sentEmail.getBody());
        personalisation.put(PERSONALISATION_SENT_EMAIL_FROM, sentEmail.getFrom());
        personalisation.put(PERSONALISATION_SENT_EMAIL_TO, sentEmail.getTo());
        personalisation.put(PERSONALISATION_SENT_EMAIL_SUBJECT, sentEmail.getSubject());
        personalisation.put(PERSONALISATION_SENT_EMAIL_SENT_ON, sentEmail.getSentOn());

        return personalisation;
    }
}
