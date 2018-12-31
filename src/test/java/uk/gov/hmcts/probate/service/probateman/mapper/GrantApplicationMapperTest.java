package uk.gov.hmcts.probate.service.probateman.mapper;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.grantapplication.request.GrantApplicationData;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class GrantApplicationMapperTest {

    private static final String PRIMARY_APPLICANT_FORENAMES = "PrimAppFN1 PrimAppFN2";
    private static final String PRIMARY_APPLICANT_SURNAME = "PrimAppSN";
    private static final String DECEASED_FORENAMES = "DeadFN1 DeadFN2";
    private static final String DECEASED_SURNAME = "DeadSN";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1999, 1, 1);
    private static final LocalDate DATE_OF_DEATH = LocalDate.of(2018, 1, 1);
    private static final String DECEASED_ADDRESS = "DecAddL1, DeacAddPC";

    private GrantApplicationMapper grantApplicationMapper;

    @Before
    public void setUp() {
        grantApplicationMapper = Mappers.getMapper(GrantApplicationMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        GrantApplication grantApplication = new GrantApplication();
        grantApplication.setDeceasedForenames(DECEASED_FORENAMES);
        grantApplication.setDeceasedSurname(DECEASED_SURNAME);
        grantApplication.setDateOfBirth(DATE_OF_BIRTH);
        grantApplication.setDateOfDeath1(DATE_OF_DEATH);
        grantApplication.setApplicantForenames(PRIMARY_APPLICANT_FORENAMES);
        grantApplication.setApplicantSurname(PRIMARY_APPLICANT_SURNAME);
        grantApplication.setDeceasedAddress(DECEASED_ADDRESS);

        GrantApplicationData expectedGrantApplicationData = GrantApplicationData.builder()
                .gaPrimaryApplicantForenames(PRIMARY_APPLICANT_FORENAMES)
                .gaPrimaryApplicantSurname(PRIMARY_APPLICANT_SURNAME)
                .gaDeceasedForenames(DECEASED_FORENAMES)
                .gaDeceasedSurname(DECEASED_SURNAME)
                .gaDateOfBirth(DATE_OF_BIRTH)
                .gaDateOfDeath(DATE_OF_DEATH)
                .build();

        GrantApplicationData grantApplicationData = grantApplicationMapper.toCcdData(grantApplication);

        Assertions.assertThat(grantApplicationData).isEqualToComparingFieldByFieldRecursively(expectedGrantApplicationData);
    }

}