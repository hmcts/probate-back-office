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
    private static final String SMEE_AND_FORD_POUND_VALUE_TOGGLE = "probate-smee-ford-pound-value";
    private static final String FIRST_STOP_REMINDER_TOGGLE = "probate-cron-first-stop-reminder";
    private static final String SECOND_STOP_REMINDER_TOGGLE = "probate-cron-second-stop-reminder";


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

    public boolean isFeatureToggleOn(String featureToggleCode, boolean defaultValue) {
        return this.ldClient.boolVariation(featureToggleCode, this.ldContext, defaultValue);
    }

    public boolean enableNewAliasTransformation() {
        return this.isFeatureToggleOn("probate-enable-new-alias-transformation", false);
    }

    public boolean enableAmendLegalStatementFiletypeCheck() {
        return this.isFeatureToggleOn("enable-amend-legal-statement-filetype-check", false);
    }

    public boolean isPoundValueFeatureToggleOn() {
        return this.isFeatureToggleOn(
                SMEE_AND_FORD_POUND_VALUE_TOGGLE, false);
    }

    public boolean isFirstStopReminderFeatureToggleOn() {
        return this.isFeatureToggleOn(
                FIRST_STOP_REMINDER_TOGGLE, false);
    }

    public boolean isSecondStopReminderFeatureToggleOn() {
        return this.isFeatureToggleOn(
                SECOND_STOP_REMINDER_TOGGLE, false);
    }
}
