package uk.gov.hmcts.probate.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplementaryDataServiceTest {

    @InjectMocks
    private SupplementaryDataService supplementaryDataService;
    @Mock
    private CoreCaseDataApi coreCaseDataApi;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private AuthTokenGenerator authTokenGenerator;

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    private CaseDetails caseDetails;

    @BeforeEach
    public void setUpTest() {
        caseDetails = new CaseDetails(CaseData.builder()
            .caseType("gop")
            .applicationType(ApplicationType.PERSONAL)
            .build(),
            LAST_MODIFIED, CASE_ID);
    }

    @Test
    void shouldSetSupplementaryData() {
        when(securityUtils.getSchedulerToken()).thenReturn("schedulerToken");
        when(authTokenGenerator.generate()).thenReturn("authToken");
        supplementaryDataService.setSupplementaryData(caseDetails);
    }
}
