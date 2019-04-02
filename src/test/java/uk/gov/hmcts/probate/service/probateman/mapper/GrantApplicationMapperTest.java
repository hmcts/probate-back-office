package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.config.ClientTokenGenerator;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GrantApplicationMapperTest {

    @Value("${ccd.gateway.host}")
    private String printServiceHost;

    @Value("${printservice.legacyPath}")
    private String printServiceLegacyPath;

    private static final String ID = "12345";
    private static final String LEGACY_TYPE = "Legacy LEGACY APPLICATION";
    private static final String PRIMARY_APPLICANT_ADDRESS = "PaAddL1, PaAddL2, PaAddPC";
    private static final String PRIMARY_APPLICANT_FORENAMES = "PrimAppFN1 PrimAppFN2";
    private static final String PRIMARY_APPLICANT_SURNAME = "PrimAppSN";
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1999, 1, 1);
    private static final LocalDate DATE_OF_DEATH = LocalDate.of(2018, 1, 1);
    private static final String DECEASED_ADDRESS = "DecAddL1, DeacAddPC";
    private static final Long GROSS_ESTATE = 10000L;
    private static final Long GROSS_ESTATE_TRANSFORMED = 1000000L;
    private static final Long NET_ESTATE = 9000L;
    private static final Long NET_ESTATE_TRANSFORMED = 900000L;
    private static final String SOLICITOR_REFERENCE = "SolRef1";

    @Autowired
    private GrantApplicationMapper grantApplicationMapper;

    @MockBean
    AppInsights appInsights;

    @MockBean
    private ClientTokenGenerator clientTokenGenerator;

    @Test
    public void shouldMapToCcdDataForPersonalApplication() {
        GrantApplication grantApplication = buildBasicApplication();

        GrantOfRepresentationData grantApplicationData = grantApplicationMapper.toCcdData(grantApplication);

        assertBasicApplication(grantApplicationData);
        assertPaSpecificDetails(grantApplicationData);
        assertThat(grantApplicationData.getApplicationType()).isEqualTo(ApplicationType.PERSONAL);

    }

    @Test
    public void shouldMapToCcdDataForSolicitorApplication() {
        GrantApplication grantApplication = buildBasicApplication();
        grantApplication.setSolicitorReference(SOLICITOR_REFERENCE);

        GrantOfRepresentationData grantApplicationData = grantApplicationMapper.toCcdData(grantApplication);

        assertBasicApplication(grantApplicationData);
        assertSolsSpecificDetails(grantApplicationData);
        assertThat(grantApplicationData.getApplicationType()).isEqualTo(ApplicationType.SOLICITORS);

    }

    private void assertPaSpecificDetails(GrantOfRepresentationData grantApplicationData) {
        Address expectedPrimaryAddress = buildAddress(PRIMARY_APPLICANT_ADDRESS);
        assertThat(grantApplicationData.getPrimaryApplicantAddress()).isEqualToComparingFieldByFieldRecursively(expectedPrimaryAddress);
        assertThat(grantApplicationData.getPrimaryApplicantForenames()).isEqualTo(PRIMARY_APPLICANT_FORENAMES);
        assertThat(grantApplicationData.getPrimaryApplicantSurname()).isEqualTo(PRIMARY_APPLICANT_SURNAME);
    }

    private void assertSolsSpecificDetails(GrantOfRepresentationData grantApplicationData) {
        Address expectedPrimaryAddress = buildAddress(PRIMARY_APPLICANT_ADDRESS);
        assertThat(grantApplicationData.getSolsSolicitorAppReference()).isEqualTo(SOLICITOR_REFERENCE);
        assertThat(grantApplicationData.getSolsSolicitorAddress()).isEqualToComparingFieldByFieldRecursively(expectedPrimaryAddress);
        assertThat(grantApplicationData.getSolsSolicitorFirmName())
                .isEqualTo(PRIMARY_APPLICANT_FORENAMES + " " + PRIMARY_APPLICANT_SURNAME);
    }

    private void assertBasicApplication(GrantOfRepresentationData grantApplicationData) {
        String legacyCaseViewUrl = String.format(printServiceHost + printServiceLegacyPath, ProbateManType.GRANT_APPLICATION, ID);
        Address expectedDeceasedAddress = buildAddress(DECEASED_ADDRESS);

        assertThat(grantApplicationData.getDeceasedForenames()).isEqualTo(DECEASED_FORENAMES);
        assertThat(grantApplicationData.getDeceasedSurname()).isEqualTo(DECEASED_SURNAME);
        assertThat(grantApplicationData.getDeceasedDateOfBirth()).isEqualTo(DATE_OF_BIRTH);
        assertThat(grantApplicationData.getDeceasedDateOfDeath()).isEqualTo(DATE_OF_DEATH);
        assertThat(grantApplicationData.getDeceasedAddress()).isEqualToComparingFieldByFieldRecursively(expectedDeceasedAddress);
        assertThat(grantApplicationData.getIhtGrossValue()).isEqualTo(GROSS_ESTATE_TRANSFORMED);
        assertThat(grantApplicationData.getIhtNetValue()).isEqualTo(NET_ESTATE_TRANSFORMED);
        assertThat(grantApplicationData.getGrantType()).isEqualTo(GrantType.GRANT_OF_PROBATE);
        assertThat(grantApplicationData.getLegacyId()).isEqualTo(ID);
        assertThat(grantApplicationData.getLegacyType()).isEqualTo(LEGACY_TYPE);
        assertThat(grantApplicationData.getLegacyCaseViewUrl()).contains(legacyCaseViewUrl);
    }

    private GrantApplication buildBasicApplication() {
        GrantApplication grantApplication = new GrantApplication();
        grantApplication.setDeceasedForenames(DECEASED_FORENAMES);
        grantApplication.setDeceasedSurname(DECEASED_SURNAME);
        grantApplication.setDateOfBirth(DATE_OF_BIRTH);
        grantApplication.setDateOfDeath1(DATE_OF_DEATH);
        grantApplication.setApplicantForenames(PRIMARY_APPLICANT_FORENAMES);
        grantApplication.setApplicantSurname(PRIMARY_APPLICANT_SURNAME);
        grantApplication.setDeceasedAddress(DECEASED_ADDRESS);
        grantApplication.setApplicantAddress(PRIMARY_APPLICANT_ADDRESS);
        grantApplication.setGrossEstateValue(GROSS_ESTATE);
        grantApplication.setNetEstateValue(NET_ESTATE);
        grantApplication.setId(Long.valueOf(ID));
        return grantApplication;
    }

    private Address buildAddress(String addressLine1) {
        return Address.builder()
                .addressLine1(addressLine1)
                .build();
    }

}
