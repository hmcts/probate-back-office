package uk.gov.hmcts.probate.service;

import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.SupplementaryDataConfiguration;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import java.util.HashMap;
import java.util.Map;


import static java.util.Collections.singletonMap;

@Slf4j
@Service
@EnableRetry
public class CcdSupplementaryDataService {

    private AuthTokenGenerator authTokenGenerator;

    private CoreCaseDataApi coreCaseDataApi;

    private SupplementaryDataConfiguration supplementaryDataConfiguration;

    private SecurityUtils securityUtils;

    private WorkAllocationToggleService workAllocationToggleService;

    private static final String SUPPLEMENTARY_FIELD = "supplementary_data_updates";
    private static final String SERVICE_ID_FIELD = "HMCTSServiceId";
    private static final String SET_OPERATION = "$set";

    @Autowired
    public CcdSupplementaryDataService(AuthTokenGenerator authTokenGenerator,
                                       CoreCaseDataApi coreCaseDataApi,
                                       SupplementaryDataConfiguration supplementaryDataConfiguration,
                                       SecurityUtils securityUtils,
                                       WorkAllocationToggleService workAllocationToggleService) {
        this.authTokenGenerator = authTokenGenerator;
        this.coreCaseDataApi = coreCaseDataApi;
        this.supplementaryDataConfiguration = supplementaryDataConfiguration;
        this.securityUtils = securityUtils;
        this.workAllocationToggleService = workAllocationToggleService;
    }


    @Retryable(
            retryFor = {
                RetryableException.class,
                FeignException.InternalServerError.class,
                FeignException.ServiceUnavailable.class,
                FeignException.GatewayTimeout.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void submitSupplementaryDataToCcd(String caseId) {
        if (workAllocationToggleService.isProbateGSEnabled()) {
            try {
                log.info("Global Search is enabled creating Supplementary data for case id {}", caseId);
                SecurityDTO securityDTO = securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO();
                Map<String, Map<String, Map<String, Object>>> supplementaryDataUpdates = new HashMap<>();
                supplementaryDataUpdates.put(SUPPLEMENTARY_FIELD,
                        singletonMap(SET_OPERATION, singletonMap(SERVICE_ID_FIELD,
                                supplementaryDataConfiguration.getHmctsId())));

                coreCaseDataApi.submitSupplementaryData(securityDTO.getAuthorisation(),
                        authTokenGenerator.generate(),
                        caseId,
                        supplementaryDataUpdates);
                log.info("Global Search supplementary data added for case id {}", caseId);
            } catch (FeignException.BadRequest
                     | FeignException.Forbidden
                     | FeignException.NotFound ex) {
                // Non-retryable CCD errors:
                // 400 validation/business errors
                // 403 authorization errors
                // 404 case not found
                log.warn(
                        "Supplementary data request failed with non-retryable error for case id {}",
                        caseId,
                        ex
                );
            } catch (RetryableException ex) {

                log.error(
                        "Transient CCD connectivity issue for case id {}. Retrying...",
                        caseId,
                        ex
                );

                throw ex;

            } catch (FeignException.InternalServerError
                     | FeignException.ServiceUnavailable
                     | FeignException.GatewayTimeout ex) {

                log.error(
                        "Transient CCD server issue for case id {}. Retrying...",
                        caseId,
                        ex
                );
                throw ex;
            } catch (Exception ex) {
                // Unexpected application/runtime errors
                log.error(
                        "Unexpected error submitting supplementary data for case id {}",
                        caseId,
                        ex
                );
                throw ex;
            }
        }
    }

    @Recover
    public void recover(FeignException ex, String caseId) {

        log.error(
                "Retries exhausted submitting supplementary data for case id {}",
                caseId,
                ex
        );
    }

    public Map<String, Map<String, Object>> buildSupplementaryDataRequest() {

        return Map.of(
                SET_OPERATION,
                Map.of(
                        SERVICE_ID_FIELD,
                        supplementaryDataConfiguration.getHmctsId()
                )
        );
    }

}
