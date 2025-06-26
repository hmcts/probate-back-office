package uk.gov.hmcts.probate.service;

import com.github.hmcts.lifeevents.client.model.Deceased;
import com.github.hmcts.lifeevents.client.model.V1Death;
import com.github.hmcts.lifeevents.client.service.DeathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LifeEventService.class)
class LifeEventServiceTest {

    final Long caseId = 1234L;
    final String firstName = "Wibble";
    final String lastName = "Wobble";
    @Autowired
    LifeEventService lifeEventService;
    @MockitoBean
    DeathService deathService;
    @MockitoBean
    CcdClientApi ccdClientApi;
    @MockitoBean
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

    @BeforeEach
    public void setup() {
        localDate = LocalDate.of(1900, 1, 1);

        final Deceased deceased = new Deceased();
        deceased.setForenames(firstName);
        deceased.setSurname(lastName);
        deceased.setSex(Deceased.SexEnum.INDETERMINATE);
        v1Death = new V1Death();
        v1Death.setDeceased(deceased);
        deathRecords = new ArrayList<>();
        deathRecords.add(v1Death);

        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getDeceasedForenames()).thenReturn(firstName);
        when(caseData.getDeceasedSurname()).thenReturn(lastName);
        when(caseData.getDeceasedDateOfDeath()).thenReturn(localDate);
        when(caseDetails.getId()).thenReturn(caseId);
        when(deathService.searchForDeathRecordsByNamesAndDate(eq(firstName), eq(lastName), eq(localDate)))
            .thenReturn(deathRecords);
    }

    @Test
    void shouldPropagateExceptionWhenSearchingByNameAndDate() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(),any(),any())).thenThrow(new RuntimeException(
            "Test exception"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        });

        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    void shouldThowBusinessValidationExceptionWhenNoDeathRecordsFound() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(),any(),any())).thenReturn(emptyList());
        Exception exception = assertThrows(BusinessValidationException.class, () -> {
            lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        });

        assertEquals("No death records found", exception.getMessage());
    }


    @Test
    void shouldSearchByNameAndDate() {
        lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        verify(deathService).searchForDeathRecordsByNamesAndDate(eq(firstName), eq(lastName), eq(localDate));
        verify(deathRecordCCDService).mapDeathRecords(eq(deathRecords));
    }
}
