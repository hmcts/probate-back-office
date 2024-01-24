package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SupplementaryDataService {
    private final CoreCaseDataApi coreCaseDataApi;
    private final AuthTokenGenerator authTokenGenerator;
    private final SecurityUtils securityUtils;

    public void setSupplementaryData(CaseDetails caseDetails) {
        Map<String, Object> usersMap = new HashMap<>();
        usersMap.put("orgs_assigned_users." + "", 0);
        Map<String, Map<String, Map<String, Object>>> supplementaryData = new HashMap<>();
        supplementaryData.put("supplementary_data_updates", Map.of("$set", usersMap));
        coreCaseDataApi.submitSupplementaryData(securityUtils.getSchedulerToken(), authTokenGenerator.generate(),
                caseDetails.getId().toString(), supplementaryData);
    }
}
