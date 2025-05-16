package uk.gov.hmcts.probate.service;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    private final LDClient ldClient;
    private final LDContext ldContext;


    @Autowired
    public FeatureToggleService(LDClient ldClient, @Value("${ld.user.key}") String ldUserKey,
                                @Value("${ld.user.firstName}") String ldUserFirstName,
                                @Value("${ld.user.lastName}") String ldUserLastName) {

        final String contextName = new StringBuilder()
                .append(ldUserFirstName)
                .append(" ")
                .append(ldUserLastName)
                .toString();

        this.ldClient = ldClient;
        this.ldContext = LDContext.builder(ldUserKey)
                .name(contextName)
                .kind("application")
                .set("timestamp", String.valueOf(System.currentTimeMillis()))
                .build();

    }

    public boolean isNewFeeRegisterCodeEnabled() {
        return isFeatureToggleOn("probate-newfee-register-code", true);
    }

    public boolean enableNewMarkdownFiltering() {
        return isFeatureToggleOn("probate-enable-new-markdown-filtering", false);
    }

    public boolean enableDuplicateExecutorFiltering() {
        return isFeatureToggleOn("probate-enable-duplicate-executor-filtering", false);
    }

    public boolean isFeatureToggleOn(String featureToggleCode, boolean defaultValue) {
        return this.ldClient.boolVariation(featureToggleCode, this.ldContext, defaultValue);
    }

    public boolean enableAmendLegalStatementFiletypeCheck() {
        return this.isFeatureToggleOn("enable-amend-legal-statement-filetype-check", false);
    }
}
