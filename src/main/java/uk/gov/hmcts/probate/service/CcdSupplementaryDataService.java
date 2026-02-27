package uk.gov.hmcts.probate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.SupplementaryDataConfiguration;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

@Service
public class CcdSupplementaryDataService {

    private AuthTokenGenerator authTokenGenerator;

    private CoreCaseDataApi coreCaseDataApi;

    private SupplementaryDataConfiguration supplementaryDataConfiguration;

    private SecurityUtils securityUtils;

    private static final String SUPPLEMENTARY_FIELD = "supplementary_data_updates";
    private static final String SERVICE_ID_FIELD = "HMCTSServiceId";
    private static final String SET_OPERATION = "$set";

    @Autowired
    public CcdSupplementaryDataService(AuthTokenGenerator authTokenGenerator,
                                       CoreCaseDataApi coreCaseDataApi,
                                       SupplementaryDataConfiguration supplementaryDataConfiguration,
                                       SecurityUtils securityUtils) {
        this.authTokenGenerator = authTokenGenerator;
        this.coreCaseDataApi = coreCaseDataApi;
        this.supplementaryDataConfiguration = supplementaryDataConfiguration;
        this.securityUtils = securityUtils;
    }

    public void submitSupplementaryDataToCcd(String caseId) {
        SecurityDTO securityDTO = securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO();
        Map<String, Map<String, Map<String, Object>>> supplementaryDataUpdates = new HashMap<>();
        supplementaryDataUpdates.put(SUPPLEMENTARY_FIELD,
            singletonMap(SET_OPERATION, singletonMap(SERVICE_ID_FIELD,
                supplementaryDataConfiguration.getHmctsId())));

        coreCaseDataApi.submitSupplementaryData(securityDTO.getAuthorisation(),
            authTokenGenerator.generate(),
            caseId,
            supplementaryDataUpdates);
    }

}
