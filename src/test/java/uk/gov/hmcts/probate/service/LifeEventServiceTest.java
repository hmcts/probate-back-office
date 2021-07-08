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
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.DeathRecord;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.LifeEventService.LIFE_EVENT_VERIFICATION_SUCCESSFUL_DESCRIPTION;
import static uk.gov.hmcts.probate.service.LifeEventService.LIFE_EVENT_VERIFICATION_SUCCESSFUL_SUMMARY;
import static uk.gov.hmcts.probate.service.LifeEventService.LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION;
import static uk.gov.hmcts.probate.service.LifeEventService.LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = LifeEventService.class)
public class LifeEventServiceTest {

    final Long caseId = 1234L;
    @Autowired
    LifeEventService lifeEventService;
    @MockBean
    DeathService deathService;
    @MockBean
    CcdClientApi ccdClientApi;
    @MockBean
    DeathRecordService deathRecordService;
    @Mock
    CaseDetails caseDetails;
    @Mock
    SecurityDTO securityDTO;
    @Mock
    CaseData caseData;
    @Captor
    ArgumentCaptor<GrantOfRepresentationData> grantOfRepresentationDataCaptor;
    List<V1Death> deathRecords;
    List<CollectionMember<DeathRecord>> mappedRecords;
    LocalDate localDate;
    V1Death v1Death;

    @Before
    public void setup() {
        final String firstName = "Wibble";
        final String lastName = "Wobble";
        localDate = LocalDate.of(1900, 1, 1);

        final Deceased deceased = new Deceased();
        deceased.setForenames("Firstname");
        deceased.setSurname("LastName");
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

        mappedRecords = mock(List.class);

        when(deathRecordService.mapDeathRecords(any())).thenReturn(mappedRecords);
    }

    @Test
    public void shouldSearchForDeathRecordsByNamesAndDate() {
        lifeEventService.verifyDeathRecord(caseDetails, securityDTO);

        verify(deathService, timeout(1000))
            .searchForDeathRecordsByNamesAndDate(eq("Wibble"), eq("Wobble"), eq(localDate));
    }

    @Test
    public void shouldConvertReturnedDeathRecords() {
        lifeEventService.verifyDeathRecord(caseDetails, securityDTO);
        verify(deathRecordService, timeout(1000)).mapDeathRecords(same(deathRecords));
    }

    @Test
    public void shouldUpdateCCDWhenOneRecordFound() {
        lifeEventService.verifyDeathRecord(caseDetails, securityDTO);
        verify(ccdClientApi, timeout(100))
            .updateCaseAsCitizen(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                eq(caseId.toString()),
                grantOfRepresentationDataCaptor.capture(),
                eq(EventId.DEATH_RECORD_VERIFIED),
                eq(securityDTO),
                eq(LIFE_EVENT_VERIFICATION_SUCCESSFUL_DESCRIPTION),
                eq(LIFE_EVENT_VERIFICATION_SUCCESSFUL_SUMMARY));

        final List<CollectionMember<DeathRecord>> capturedDeathRecords = grantOfRepresentationDataCaptor
                .getValue().getDeathRecords();
        assertSame(capturedDeathRecords, mappedRecords);
    }

    @Test
    public void shouldUpdateCCDWhenDeathRecordVerificationUnsuccessful() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(), any(), any()))
            .thenReturn(emptyList());
        lifeEventService.verifyDeathRecord(caseDetails, securityDTO);
        verify(ccdClientApi, timeout(100))
            .updateCaseAsCitizen(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                eq(caseId.toString()),
                eq(null),
                eq(EventId.DEATH_RECORD_VERIFICATION_FAILED),
                eq(securityDTO),
                eq(LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION),
                eq(LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY));

    }

    @Test
    public void shouldNotUpdateCCDWhenMultipleRecordsFound() {
        deathRecords.add(v1Death);
        when(deathService.searchForDeathRecordsByNamesAndDate(any(), any(), any()))
            .thenReturn(deathRecords);
        lifeEventService.verifyDeathRecord(caseDetails, securityDTO);

        verify(deathService, timeout(1000))
            .searchForDeathRecordsByNamesAndDate(eq("Wibble"), eq("Wobble"), eq(localDate));
        verifyNoInteractions(deathRecordService);
        verifyNoInteractions(ccdClientApi);
    }


    @Test
    public void shouldLookupDeathRecordById() {
        Integer id = 12345;
        when(deathService.getDeathRecordById(eq(id))).thenReturn(v1Death);
        lifeEventService.getDeathRecordById(id);
        verify(deathService).getDeathRecordById(id);
        verify(deathRecordService).mapDeathRecordCCD(v1Death);
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

}
