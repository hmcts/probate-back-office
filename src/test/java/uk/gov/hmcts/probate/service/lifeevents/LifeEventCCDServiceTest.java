package uk.gov.hmcts.probate.service.lifeevents;

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
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityDTO;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.HandOffLegacyService;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.DeathRecord;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_ERROR_DESCRIPTION;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_ERROR_SUMMARY;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_DESCRIPTION;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_SUMMARY;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_SUCCESSFUL_DESCRIPTION;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_SUCCESSFUL_SUMMARY;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION;
import static uk.gov.hmcts.probate.service.lifeevents.LifeEventCCDService.LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LifeEventCCDService.class)
class LifeEventCCDServiceTest {

    final Long caseId = 1234L;
    @Autowired
    LifeEventCCDService lifeEventCCDService;
    @MockitoBean
    DeathService deathService;
    @MockitoBean
    CcdClientApi ccdClientApi;
    @MockitoBean
    DeathRecordService deathRecordService;
    @MockitoBean
    HandOffLegacyService handOffLegacyService;
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
    @Mock
    SecurityUtils securityUtils;

    @BeforeEach
    void setup() {
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
        when(deathService.searchForDeathRecordsByNamesAndDate(firstName, lastName, localDate))
            .thenReturn(deathRecords);

        mappedRecords = mock(List.class);

        when(deathRecordService.mapDeathRecords(any())).thenReturn(mappedRecords);
        securityDTO = SecurityDTO.builder()
                .authorisation("AUTH_TOKEN")
                .serviceAuthorisation("serviceAuth")
                .build();
        when(securityUtils.getSecurityDTO()).thenReturn(securityDTO);
        when(securityUtils.getRoles(anyString())).thenReturn(List.of("citizen"));
    }

    @Test
    void shouldSearchForDeathRecordsByNamesAndDate() {
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, true);

        verify(deathService, timeout(1000))
            .searchForDeathRecordsByNamesAndDate("Wibble", "Wobble", localDate);
    }

    @Test
    void shouldConvertReturnedDeathRecords() {
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, true);
        verify(deathRecordService, timeout(1000)).mapDeathRecords(same(deathRecords));
    }

    @Test
    void shouldUpdateCCDWithCitizenTokenWhenOneRecordFound() {
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, true);
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
    void shouldUpdateCCDWithCaseworkerTokenWhenOneRecordFound() {
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, false);
        verify(ccdClientApi, timeout(100))
                .updateCaseAsCaseworker(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                        eq(caseId.toString()),
                        any(),
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
    void shouldUpdateCCDWithCitizenTokenWhenDeathRecordVerificationUnsuccessful() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(), any(), any()))
            .thenReturn(emptyList());
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, true);
        verify(ccdClientApi, timeout(100))
            .updateCaseAsCitizen(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                eq(caseId.toString()),
                grantOfRepresentationDataCaptor.capture(),
                eq(EventId.DEATH_RECORD_VERIFICATION_FAILED),
                eq(securityDTO),
                eq(LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION),
                eq(LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY));

    }

    @Test
    void shouldUpdateCCDWithCaseworkerTokenWhenDeathRecordVerificationUnsuccessful() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(), any(), any()))
                .thenReturn(emptyList());
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, false);
        verify(ccdClientApi, timeout(100))
                .updateCaseAsCaseworker(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                        eq(caseId.toString()),
                        any(),
                        grantOfRepresentationDataCaptor.capture(),
                        eq(EventId.DEATH_RECORD_VERIFICATION_FAILED),
                        eq(securityDTO),
                        eq(LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_DESCRIPTION),
                        eq(LIFE_EVENT_VERIFICATION_UNSUCCESSFUL_SUMMARY));

    }

    @Test
    void shouldUpdateCCDWithCitizenTokenWhenMultipleRecordsFound() {
        deathRecords.add(v1Death);
        when(deathService.searchForDeathRecordsByNamesAndDate(any(), any(), any()))
            .thenReturn(deathRecords);
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, true);
        verify(ccdClientApi, timeout(100))
            .updateCaseAsCitizen(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                eq(caseId.toString()),
                grantOfRepresentationDataCaptor.capture(),
                eq(EventId.DEATH_RECORD_VERIFICATION_FAILED),
                eq(securityDTO),
                eq(LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_DESCRIPTION),
                eq(LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_SUMMARY));

        final List<CollectionMember<DeathRecord>> capturedDeathRecords = grantOfRepresentationDataCaptor
            .getValue().getDeathRecords();
        assertSame(capturedDeathRecords, mappedRecords);
    }

    @Test
    void shouldUpdateCCDWithCaseworkerTokenWhenMultipleRecordsFound() {
        deathRecords.add(v1Death);
        when(deathService.searchForDeathRecordsByNamesAndDate(any(), any(), any()))
                .thenReturn(deathRecords);
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, false);
        verify(ccdClientApi, timeout(100))
                .updateCaseAsCaseworker(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                        eq(caseId.toString()),
                        any(),
                        grantOfRepresentationDataCaptor.capture(),
                        eq(EventId.DEATH_RECORD_VERIFICATION_FAILED),
                        eq(securityDTO),
                        eq(LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_DESCRIPTION),
                        eq(LIFE_EVENT_VERIFICATION_MULTIPLE_RECORDS_SUMMARY));

        final List<CollectionMember<DeathRecord>> capturedDeathRecords = grantOfRepresentationDataCaptor
                .getValue().getDeathRecords();
        assertSame(capturedDeathRecords, mappedRecords);
    }

    @Test
    void shouldUpdateCCDWithCitizenTokenWhenError() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(),any(),any())).thenThrow(new RuntimeException(
            "Test exception"));
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, true);
        verify(ccdClientApi, timeout(100))
            .updateCaseAsCitizen(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                eq(caseId.toString()),
                grantOfRepresentationDataCaptor.capture(),
                eq(EventId.DEATH_RECORD_VERIFICATION_FAILED),
                eq(securityDTO),
                eq(LIFE_EVENT_VERIFICATION_ERROR_DESCRIPTION),
                eq(LIFE_EVENT_VERIFICATION_ERROR_SUMMARY));
    }

    @Test
    void shouldUpdateCCDWithCaseworkerTokenWhenError() {
        when(deathService.searchForDeathRecordsByNamesAndDate(any(),any(),any())).thenThrow(new RuntimeException(
                "Test exception"));
        lifeEventCCDService.verifyDeathRecord(caseDetails, securityDTO, false);
        verify(ccdClientApi, timeout(100))
                .updateCaseAsCaseworker(eq(CcdCaseType.GRANT_OF_REPRESENTATION),
                        eq(caseId.toString()),
                        any(),
                        grantOfRepresentationDataCaptor.capture(),
                        eq(EventId.DEATH_RECORD_VERIFICATION_FAILED),
                        eq(securityDTO),
                        eq(LIFE_EVENT_VERIFICATION_ERROR_DESCRIPTION),
                        eq(LIFE_EVENT_VERIFICATION_ERROR_SUMMARY));
    }

    @Test
    void shouldReturnLastModifiedDateWithStringArrayInput() {
        String dateTimeStr = "2022-01-01T10:10:10.000";

        when(caseDetails.getLastModified()).thenReturn(new String[]{"2022", "1", "1","10","10","10","0"});

        LocalDateTime lastModified = lifeEventCCDService.getLastModifiedDate(caseDetails);

        assertEquals(LocalDateTime.parse(dateTimeStr), lastModified);
    }

    @Test
    void shouldReturnNullLastModifiedDateWithEmptyStringArrayInput() {
        when(caseDetails.getLastModified()).thenReturn(new String[]{""});

        LocalDateTime lastModified = lifeEventCCDService.getLastModifiedDate(caseDetails);

        assertNull(lastModified);
    }

    @Test
    void shouldReturnNullLastModifiedDateWithNullStringArrayInput() {
        when(caseDetails.getLastModified()).thenReturn(new String[]{null});

        LocalDateTime lastModified = lifeEventCCDService.getLastModifiedDate(caseDetails);

        assertNull(lastModified);
    }

    @Test
    void shouldReturnNullLastModifiedDateWithAllNullValuesInStringArrayInput() {
        when(caseDetails.getLastModified()).thenReturn(new String[]{null, null, null, null, null, null});

        LocalDateTime lastModified = lifeEventCCDService.getLastModifiedDate(caseDetails);

        assertNull(lastModified);
    }

    @Test
    void shouldReturnNullLastModifiedDateWithFewEmptyValuesInStringArrayInput() {
        when(caseDetails.getLastModified()).thenReturn(new String[]{"", "", null});

        LocalDateTime lastModified = lifeEventCCDService.getLastModifiedDate(caseDetails);

        assertNull(lastModified);
    }

    @Test
    void shouldReturnNullWithInvalidStringArrayInput() {
        when(caseDetails.getLastModified()).thenReturn(new String[]{"2022", "1", "50","10","10","10","0"});
        when(caseDetails.getId()).thenReturn(1234L);

        LocalDateTime lastModified = lifeEventCCDService.getLastModifiedDate(caseDetails);

        assertNull(lastModified);
    }
}
