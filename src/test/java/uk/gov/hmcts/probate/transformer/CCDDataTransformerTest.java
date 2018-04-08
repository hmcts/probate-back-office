package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CCDDataTransformerTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] LAST_MODIFIED_STR = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final String SOLICITOR_FIRM_NAME = "Sol Firm Name";
    private static final String SOLICITOR_FIRM_POSTCODE = "SW13 6EA";
    private static final String SOLICITOR_SOT_NAME = "Andy Test";
    private static final String SOLICITOR_SOT_JOB_TITLE = "Lawyer";

    private static final String DECEASED_FIRSTNAME = "Firstname";
    private static final String DECEASED_LASTNAME = "Lastname";
    private static final LocalDate DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
    private static final LocalDate DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);

    private static final String IHT_FORM_ID = "IHT207";
    private static final Float IHT_GROSS = 10000f;
    private static final Float IHT_NET = 9000f;
    private static final BigDecimal TOTAL_FEE = new BigDecimal(155.00);

    private static final String YES = "Yes";

    @Mock
    private CallbackRequest callbackRequestMock;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @InjectMocks
    private CCDDataTransformer underTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCovertRequestToDataBean() {

        when(callbackRequestMock.getCaseDetails()).thenReturn(caseDetailsMock);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        when(caseDataMock.getSolsSolicitorFirmName()).thenReturn(SOLICITOR_FIRM_NAME);
        when(caseDataMock.getSolsSolicitorFirmPostcode()).thenReturn(SOLICITOR_FIRM_POSTCODE);
        when(caseDataMock.getSolsSOTName()).thenReturn(SOLICITOR_SOT_NAME);
        when(caseDataMock.getSolsSOTJobTitle()).thenReturn(SOLICITOR_SOT_JOB_TITLE);

        when(caseDataMock.getDeceasedForenames()).thenReturn(DECEASED_FIRSTNAME);
        when(caseDataMock.getDeceasedSurname()).thenReturn(DECEASED_LASTNAME);
        when(caseDataMock.getDeceasedDateOfBirth()).thenReturn(DOB);
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(DOD);

        when(caseDataMock.getSolsIHTFormId()).thenReturn(IHT_FORM_ID);
        when(caseDataMock.getIhtGrossValue()).thenReturn(IHT_GROSS);
        when(caseDataMock.getIhtNetValue()).thenReturn(IHT_NET);
        when(caseDataMock.getTotalFee()).thenReturn(TOTAL_FEE);

        when(caseDetailsMock.getLastModified()).thenReturn(LAST_MODIFIED_STR);

        List<AdditionalExecutors> additionalExecutors = new ArrayList();
        AdditionalExecutors additionalExecutors1 = mock(AdditionalExecutors.class);
        AdditionalExecutors additionalExecutors2 = mock(AdditionalExecutors.class);
        AdditionalExecutor additionalExecutor1 = mock(AdditionalExecutor.class);
        AdditionalExecutor additionalExecutor2 = mock(AdditionalExecutor.class);
        when(additionalExecutors1.getAdditionalExecutor()).thenReturn(additionalExecutor1);
        when(additionalExecutors2.getAdditionalExecutor()).thenReturn(additionalExecutor2);
        additionalExecutors.add(additionalExecutors1);
        additionalExecutors.add(additionalExecutors2);
        when(caseDataMock.getSolsAdditionalExecutorList()).thenReturn(additionalExecutors);
        when(caseDataMock.isPrimaryApplicantApplying()).thenReturn(true);

        CCDData ccdData = underTest.transform(callbackRequestMock);

        assertEquals(SOLICITOR_FIRM_NAME, ccdData.getSolicitor().getFirmName());
        assertEquals(SOLICITOR_FIRM_POSTCODE, ccdData.getSolicitor().getFirmPostcode());
        assertEquals(SOLICITOR_SOT_NAME, ccdData.getSolicitor().getFullname());
        assertEquals(SOLICITOR_SOT_JOB_TITLE, ccdData.getSolicitor().getJobRole());
        assertEquals(DECEASED_FIRSTNAME, ccdData.getDeceased().getFirstname());
        assertEquals(DECEASED_LASTNAME, ccdData.getDeceased().getLastname());
        assertEquals(DOB, ccdData.getDeceased().getDateOfBirth());
        assertEquals(DOD, ccdData.getDeceased().getDateOfDeath());
        assertEquals(IHT_FORM_ID, ccdData.getIht().getFormName());
        assertEquals(IHT_GROSS, ccdData.getIht().getGrossValue());
        assertEquals(IHT_NET, ccdData.getIht().getNetValue());
        assertEquals(TOTAL_FEE.floatValue(), ccdData.getFee().getAmount().floatValue(), 0.01);
        assertEquals(2018, ccdData.getCaseSubmissionDate().getYear());
        assertEquals(1, ccdData.getCaseSubmissionDate().getMonthValue());
        assertEquals(2, ccdData.getCaseSubmissionDate().getDayOfMonth());
        assertEquals(3, ccdData.getExecutors().size());
        assertEquals(true, ccdData.getExecutors().get(2).isApplying());
    }
}
