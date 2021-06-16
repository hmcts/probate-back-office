package uk.gov.hmcts.probate.insights;

import java.util.Map;

public interface EventRepository {

    void trackEvent(String name, Map<String, String> properties);
}
