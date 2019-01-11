package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentation;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GrantApplicationMapperTest {

    private static final String PRIMARY_APPLICANT_ADDRESS = "PaAddL1, PaAddL2, PaAddPC";
    private static final String PRIMARY_APPLICANT_FORENAMES = "PrimAppFN1 PrimAppFN2";
    private static final String PRIMARY_APPLICANT_SURNAME = "PrimAppSN";
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1999, 1, 1);
    private static final LocalDate DATE_OF_DEATH = LocalDate.of(2018, 1, 1);
    private static final String DECEASED_ADDRESS = "DecAddL1, DeacAddPC";
    private static final Long GROSS_ESTATE = 10000L;
    private static final Long NET_ESTATE = 9000L;
    private static final String SOLICITOR_REFERENCE = "SolRef1";

    @Autowired
    private GrantApplicationMapper grantApplicationMapper;

    @Test
    public void shouldMapToCcdDataForPersonalApplication() {
        GrantApplication grantApplication = buildBasicApplication();

        GrantOfRepresentation grantApplicationData = grantApplicationMapper.toCcdData(grantApplication);

        assertBasicApplication(grantApplicationData);
        assertThat(grantApplicationData.getApplicationType()).isEqualTo(ApplicationType.PERSONAL);

    }

    @Test
    public void shouldMapToCcdDataForSolicitorApplication() {
        GrantApplication grantApplication = buildBasicApplication();
        grantApplication.setSolicitorReference(SOLICITOR_REFERENCE);

        GrantOfRepresentation grantApplicationData = grantApplicationMapper.toCcdData(grantApplication);

        assertBasicApplication(grantApplicationData);
        assertThat(grantApplicationData.getApplicationType()).isEqualTo(ApplicationType.SOLICITORS);

    }

    private void assertBasicApplication(GrantOfRepresentation grantApplicationData) {
        Address expectedDeceasedAddress = buildAddress(DECEASED_ADDRESS);
        Address expectedPrimaryAddress = buildAddress(PRIMARY_APPLICANT_ADDRESS);
        assertThat(grantApplicationData.getPrimaryApplicantForenames()).isEqualTo(PRIMARY_APPLICANT_FORENAMES);
        assertThat(grantApplicationData.getPrimaryApplicantSurname()).isEqualTo(PRIMARY_APPLICANT_SURNAME);
        assertThat(grantApplicationData.getDeceasedForenames()).isEqualTo(DECEASED_FORENAMES);
        assertThat(grantApplicationData.getDeceasedSurname()).isEqualTo(DECEASED_SURNAME);
        assertThat(grantApplicationData.getDeceasedDateOfBirth()).isEqualTo(DATE_OF_BIRTH);
        assertThat(grantApplicationData.getDeceasedDateOfDeath()).isEqualTo(DATE_OF_DEATH);
        assertThat(grantApplicationData.getDeceasedAddress()).isEqualToComparingFieldByFieldRecursively(expectedDeceasedAddress);
        assertThat(grantApplicationData.getPrimaryApplicantAddress()).isEqualToComparingFieldByFieldRecursively(expectedPrimaryAddress);
        assertThat(grantApplicationData.getIhtGrossValue()).isEqualTo(GROSS_ESTATE);
        assertThat(grantApplicationData.getIhtNetValue()).isEqualTo(NET_ESTATE);
        assertThat(grantApplicationData.getCaseType()).isEqualTo(GrantType.GRANT_OF_PROBATE);
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
        return grantApplication;
    }

    private Address buildAddress(String addressLine1) {
        return Address.builder()
                .addressLine1(addressLine1)
                .build();
    }

}
