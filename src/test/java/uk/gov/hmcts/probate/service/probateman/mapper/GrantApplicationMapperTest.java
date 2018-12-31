package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class GrantApplicationMapperTest {

    private GrantApplicationMapper grantApplicationMapper;

    @Before
    public void setUp() {
        grantApplicationMapper = Mappers.getMapper(GrantApplicationMapper.class);
    }

    @Test
    public void shouldMapToCcdData() {
        GrantApplication grantApplication = new GrantApplication();

        CaseData caseData = grantApplicationMapper.toCcdData(grantApplication);

        assertThat(caseData, is(notNullValue()));
    }

}