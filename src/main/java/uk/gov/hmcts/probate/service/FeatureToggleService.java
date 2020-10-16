package uk.gov.hmcts.probate.service;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.EvaluationDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    private final LDClient ldClient;
    private final LDUser ldUser;
    private final LDUser.Builder ldUserBuilder;

    @Autowired
    public FeatureToggleService(@Value("${ld.sdk_key}") String ldClientKey, @Value("${ld.user.key}") String ldUserKey,  @Value("${ld.user.firstName}") String ldUserFirstName, @Value("${ld.user.lastName}") String ldUserLastName) {
        this.ldClient =  new LDClient(ldClientKey);
        System.out.println("========================== FeatureToggleService LAUNCH DARKLY ======> ");
        System.out.println("key" + ldUserKey);
        System.out.println("first name=>" +ldUserFirstName);
        System.out.println("last name=>" +ldUserLastName);
        this.ldUserBuilder = new LDUser.Builder(ldUserKey)
            .firstName(ldUserFirstName)
            .lastName(ldUserLastName)
            .custom("timestamp", String.valueOf(System.currentTimeMillis()));
        this.ldUser = this.ldUserBuilder.build();
        System.out.println("DONE WITH FEATURE TOGGLE SERVICE");
        System.out.println("IS LDCLIENT INITIALISED======>");
        System.out.println(this.ldClient.isInitialized());
    }

    public boolean isNewFeeRegisterCodeEnabled() {
        System.out.println("IS LDCLIENT INITIALISED======>");
        System.out.println(this.ldClient.isInitialized());
        System.out.println("IS FEATURRE KNOWN ===> ");
        System.out.println(String.valueOf(this.ldClient.isFlagKnown("probate-newfee-register-code")));
        EvaluationDetail eval = this.ldClient.boolVariationDetail("probate-newfee-register-code", this.ldUser, false);
        System.out.println("NEW FEE REGISTERR CODE VALUTION DETAILS => ");
        System.out.println(eval.toString());
        return this.ldClient.boolVariation("probate-newfee-register-code", this.ldUser, false);
    }

}
