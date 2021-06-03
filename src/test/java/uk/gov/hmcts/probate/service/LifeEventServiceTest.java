package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import com.github.hmcts.lifeevents.client.service.DeathService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = LifeEventService.class)
public class LifeEventServiceTest {

    final Long caseId = 1234L;
    final String firstName = "Wibble";
    final String lastName = "Wobble";
    @Autowired
    LifeEventService lifeEventService;
    @MockBean
    DeathService deathService;
    @MockBean
    CcdClientApi ccdClientApi;
    @MockBean
    DeathRecordCCDService deathRecordCCDService;
    @Mock
    CaseDetails caseDetails;
    @Mock
    CaseData caseData;
    @Captor
    ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataCaptor;
    List<V1Death> deathRecords;
    LocalDate localDate;
    V1Death v1Death;

    @Before
    public void setup() {
        localDate = LocalDate.of(1900, 1, 1);

        final Deceased deceased = new Deceased();
        deceased.setForenames(firstName);
        deceased.setSurname(lastName);
        deceased.setSex(Deceased.SexEnum.INDETERMINATE);
        v1Death = new V1Death();
        v1Death.setDeceased(deceased);
    }
    
    @Test
    public void shouldLookupDeathRecordById() {
        Integer id = 12345;
        when(deathService.getDeathRecordById(eq(id))).thenReturn(v1Death);
        lifeEventService.getDeathRecordById(id);
        verify(deathService).getDeathRecordById(id);
        verify(deathRecordCCDService).mapDeathRecord(v1Death);
    }

    @Test
    public void shouldThowBusinessValidationExceptionWhenDeathRecordNotFound() {
        Integer id = 12345;
        when(deathService.getDeathRecordById(eq(id))).thenReturn(null);
        Exception exception = assertThrows(BusinessValidationException.class, () -> {
            lifeEventService.getDeathRecordById(id);
        });

        assertEquals("No death record found with system number 12345", exception.getMessage());
    }

    @Test
    public void shouldPropagateException() {
        Integer id = 12345;
        when(deathService.getDeathRecordById(eq(id))).thenThrow(new RuntimeException("Test exception"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            lifeEventService.getDeathRecordById(id);
        });

        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    public void shouldPropagateExceptionWhenSearchingByNameAndDate() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(),any(),any())).thenThrow(new RuntimeException(
            "Test exception"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        });

        assertEquals("Test exception", exception.getMessage());
    }
    
    @Test
    public void shouldThowBusinessValidationExceptionWhenNoDeathRecordsFound() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(),any(),any())).thenReturn(emptyList());
        Exception exception = assertThrows(BusinessValidationException.class, () -> {
            lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        });

        assertEquals("No death records found", exception.getMessage());
    }


    @Test
    public void shouldSearchByNameAndDate() {
        lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        verify(deathService).searchForDeathRecordsByNamesAndDate(eq(firstName), eq(lastName), eq(localDate));
        verify(deathRecordCCDService).mapDeathRecords(eq(deathRecords));
    }
}
