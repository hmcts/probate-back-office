package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.config.SupplementaryDataConfiguration;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcdSupplementaryDataServiceTest {

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private CoreCaseDataApi coreCaseDataApi;

    @Mock
    private SupplementaryDataConfiguration supplementaryDataConfiguration;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CcdSupplementaryDataService ccdSupplementaryDataService;

    @Test
    void shouldSubmitSupplementaryDataToCcd() {
        SecurityDTO securityDTO = SecurityDTO.builder().authorisation("AUTH").build();
        when(supplementaryDataConfiguration.getHmctsId()).thenReturn("PROBATE");
        when(securityUtils.getUserByCaseworkerTokenAndServiceSecurityDTO()).thenReturn(securityDTO);
        when(authTokenGenerator.generate()).thenReturn("AUTH_TOKEN");
        ccdSupplementaryDataService.submitSupplementaryDataToCcd("1234567812345678");

        verify(coreCaseDataApi).submitSupplementaryData(anyString(), anyString(), anyString(), anyMap());
    }
}
