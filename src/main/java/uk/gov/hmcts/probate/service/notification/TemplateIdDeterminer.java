package uk.gov.hmcts.probate.service.notification;

import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.State;

public interface TemplateIdDeterminer {

    String determineTemplateId(State state, ApplicationType applicationType, String registryLocation);
}
