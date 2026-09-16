package uk.gov.hmcts.probate.service;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import uk.gov.hmcts.probate.config.SupplementaryDataConfiguration;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.wa.WorkAllocationToggleService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = CcdSupplementaryDataService.class)
class CcdSupplementaryDataServiceTest {

    @MockitoBean
    private AuthTokenGenerator authTokenGenerator;

    @MockitoBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockitoBean
    private SupplementaryDataConfiguration supplementaryDataConfiguration;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private WorkAllocationToggleService workAllocationToggleService;

    @Autowired
    private CcdSupplementaryDataService ccdSupplementaryDataService;

    @MockitoSpyBean
    private CcdSupplementaryDataService spyService;

    @Test
    void shouldSubmitSupplementaryDataToCcd() {
        SecurityDTO securityDTO = SecurityDTO.builder().authorisation("AUTH").build();
        when(workAllocationToggleService.isProbateGSEnabled()).thenReturn(true);
        when(supplementaryDataConfiguration.getHmctsId()).thenReturn("PROBATE");
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(authTokenGenerator.generate()).thenReturn("AUTH_TOKEN");
        ccdSupplementaryDataService.submitSupplementaryDataToCcd("1234567812345678");

        verify(coreCaseDataApi).submitSupplementaryData(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldNotSubmitSupplementaryDataToCcdWhenGsToggleDisabled() {

        when(workAllocationToggleService.isProbateGSEnabled()).thenReturn(false);
        ccdSupplementaryDataService.submitSupplementaryDataToCcd("1234567812345678");
        verify(coreCaseDataApi, never())
                .submitSupplementaryData(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldHandleFailureWhenSubmittingSupplementaryData() {

        SecurityDTO securityDTO = SecurityDTO.builder().authorisation("AUTH").build();

        when(workAllocationToggleService.isProbateGSEnabled()).thenReturn(true);
        when(supplementaryDataConfiguration.getHmctsId()).thenReturn("PROBATE");
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(authTokenGenerator.generate()).thenReturn("AUTH_TOKEN");

        when(coreCaseDataApi.submitSupplementaryData(
                anyString(),
                anyString(),
                anyString(),
                anyMap()
        )).thenThrow(
                feignException(503, "Service Unavailable")
        );


        ccdSupplementaryDataService
                .submitSupplementaryDataToCcd("1234567812345678");

        verify(coreCaseDataApi, times(3))
            .submitSupplementaryData(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyMap()
            );

        verify(spyService).recover(
                any(),
                eq("1234567812345678")
        );
    }

    @ParameterizedTest
    @MethodSource("nonRetryableCcdErrors")
    void shouldNotRetryForNonRetryableCcdErrors(
            FeignException exception) {
        SecurityDTO securityDTO = SecurityDTO.builder().authorisation("AUTH").build();

        when(workAllocationToggleService.isProbateGSEnabled()).thenReturn(true);
        when(supplementaryDataConfiguration.getHmctsId()).thenReturn("PROBATE");
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(authTokenGenerator.generate()).thenReturn("AUTH_TOKEN");

        when(coreCaseDataApi.submitSupplementaryData(
                anyString(),
                anyString(),
                anyString(),
                anyMap()
        )).thenThrow(exception);

        ccdSupplementaryDataService
                .submitSupplementaryDataToCcd("1234567812345678");

        verify(coreCaseDataApi, times(1))
            .submitSupplementaryData(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyMap()
            );
    }

    @Test
    void shouldThrowUnexpectedRuntimeException() {

        SecurityDTO securityDTO =
                SecurityDTO.builder().authorisation("AUTH").build();

        when(workAllocationToggleService.isProbateGSEnabled())
                .thenReturn(true);

        when(supplementaryDataConfiguration.getHmctsId())
                .thenReturn("PROBATE");

        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO())
                .thenReturn(securityDTO);

        when(authTokenGenerator.generate())
                .thenReturn("AUTH_TOKEN");

        when(coreCaseDataApi.submitSupplementaryData(
                anyString(),
                anyString(),
                anyString(),
                anyMap()
        )).thenThrow(new RuntimeException("Unexpected"));

        assertThrows(
                RuntimeException.class,
                () -> ccdSupplementaryDataService
                        .submitSupplementaryDataToCcd("1234567812345678")
        );

        verify(coreCaseDataApi, times(1))
            .submitSupplementaryData(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyMap()
            );
    }

    private static Stream<FeignException> nonRetryableCcdErrors() {

        return Stream.of(
                feignException(400, "Bad Request"),
                feignException(403, "Forbidden"),
                feignException(404, "Not Found")
        );
    }

    private static FeignException feignException(
            int status,
            String reason) {

        return FeignException.errorStatus(
                "submitSupplementaryData",
                Response.builder()
                        .status(status)
                        .reason(reason)
                        .request(
                                Request.create(
                                        Request.HttpMethod.POST,
                                        "/supplementary-data",
                                        Map.of(),
                                        null,
                                        StandardCharsets.UTF_8,
                                        null
                                )
                        )
                        .build()
        );
    }
}