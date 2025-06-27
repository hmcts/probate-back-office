package uk.gov.hmcts.probate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;

@Service
public class DemoInstanceToggleService {
    private final boolean demoInstanceFeatureEnabled;

    public DemoInstanceToggleService(@Value ("${demo_instance_toggle}") boolean demoInstanceFeatureEnabled) {
        this.demoInstanceFeatureEnabled = demoInstanceFeatureEnabled;
    }

    public boolean isDemoInstanceFeatureEnabled() {
        return demoInstanceFeatureEnabled;
    }

    public CcdCaseType getCcdCaseType() {
        return demoInstanceFeatureEnabled
                ? CcdCaseType.GRANT_OF_REPRESENTATION_INT : CcdCaseType.GRANT_OF_REPRESENTATION;
    }

    public CaseType getCaseType() {
        return demoInstanceFeatureEnabled
                ? CaseType.GRANT_OF_REPRESENTATION_INT : CaseType.GRANT_OF_REPRESENTATION;
    }
}
