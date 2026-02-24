package uk.gov.hmcts.probate.service.notification;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AutomatedNotificationPersonalisationServiceTest {

    private AutomatedNotificationPersonalisationService underTest;

    @Mock
    private LocalDateToWelshStringConverter localDateToWelshStringConverter;

    @Mock
    private StopReasonService stopReasonService;

    @Mock
    private DateFormatterService dateFormatterService;

    private ObjectMapper objectMapper;

    private static final String urlPrefixToPersonalCase = "http://localhost:8080/personal";

    private static final String urlPrefixSolicitorCase = "http://localhost:8080/solicitor";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        underTest = new AutomatedNotificationPersonalisationService(localDateToWelshStringConverter,
                stopReasonService,
                dateFormatterService,
                objectMapper,
                urlPrefixToPersonalCase,
                urlPrefixSolicitorCase);
    }

    @Test
    void getDisposalReminderPersonalisationShouldBuildCorrectMap() {
        LocalDate dod = LocalDate.of(2025,5,1);
        Map<String, Object> data = Map.of(
                "solsSOTName", "Solicitor Smith",
                "caseType", "foo",
                "deceasedDateOfDeath", dod,
                "deceasedForenames", "Bob",
                "deceasedSurname", "Brown"
        );
        CaseDetails cd = mock(CaseDetails.class);
        when(cd.getData()).thenReturn(data);
        when(cd.getId()).thenReturn(123L);
        when(cd.getCreatedDate()).thenReturn(LocalDateTime.of(2025,6,10,0,0));
        when(dateFormatterService.formatDate(dod))
                .thenReturn("1st May 2025");
        when(localDateToWelshStringConverter.convert(dod))
                .thenReturn("1 Mai 2025");

        Map<String, Object> result =
                underTest.getDisposalReminderPersonalisation(cd, ApplicationType.SOLICITOR);

        assertAll("disposal reminder personalisation",
                () -> assertEquals("10 June 2025", result.get("date_created")),
                () -> assertEquals("123",     result.get("case_ref")),
                () -> assertEquals("Solicitor Smith", result.get("solicitor_name")),
                () -> assertEquals(
                        urlPrefixSolicitorCase + "/cases/PROBATE/GrantOfRepresentation/123",,
                        result.get("link_to_case")),
                () -> assertEquals("1st May 2025", result.get("deceased_dod")),
                () -> assertEquals("1 Mai 2025", result.get("welsh_deceased_date_of_death")),
                () -> assertEquals("Bob Brown", result.get("deceased_name"))
        );
    }

    @Test
    void getDisposalReminderPersonalisationShouldDefaultTextIfDeceasedNameOrDodIsNull() {
        Map<String, Object> data = Map.of(
                "solsSOTName", "Solicitor Smith",
                "caseType", "foo"
        );
        CaseDetails cd = mock(CaseDetails.class);
        when(cd.getData()).thenReturn(data);
        when(cd.getId()).thenReturn(123L);
        when(cd.getCreatedDate()).thenReturn(LocalDateTime.of(2025,6,10,0,0));

        Map<String, Object> result =
                underTest.getDisposalReminderPersonalisation(cd, ApplicationType.SOLICITOR);

        assertAll("disposal reminder personalisation",
                () -> assertEquals("10 June 2025", result.get("date_created")),
                () -> assertEquals("123",     result.get("case_ref")),
                () -> assertEquals("Solicitor Smith", result.get("solicitor_name")),
                () -> assertEquals(
                        urlPrefixSolicitorCase + "/cases/PROBATE/GrantOfRepresentation/123",,
                        result.get("link_to_case")),
                () -> assertEquals("Date not entered yet", result.get("deceased_dod")),
                () -> assertEquals("Dyddiad heb ei nodi eto", result.get("welsh_deceased_date_of_death")),
                () -> assertEquals("Name not entered yet", result.get("deceased_name")),
                () -> assertEquals("Enw heb ei nodi eto", result.get("welsh_deceased_name"))
        );
    }

    @Test
    void getPersonalisationShouldIncludeAllFields() {
        when(stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH, "R1"))
                .thenReturn("Reason One");
        when(stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH, "DocumentsRequired"))
                .thenReturn("Docs Req");
        when(stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH, "SUB2"))
                .thenReturn("Sub2 Desc");
        when(stopReasonService.getStopReasonDescription(LanguagePreference.WELSH, "R1"))
                .thenReturn("Reason One welsh");
        when(stopReasonService.getStopReasonDescription(LanguagePreference.WELSH, "DocumentsRequired"))
                .thenReturn("Docs Req welsh");
        when(stopReasonService.getStopReasonDescription(LanguagePreference.WELSH, "SUB2"))
                .thenReturn("Sub2 Desc welsh");
        when(dateFormatterService.formatDate(any(LocalDate.class)))
                .thenReturn("2025-01-01");
        when(localDateToWelshStringConverter.convert(any(LocalDate.class)))
                .thenReturn("Welsh 2025-01-01");

        StopReason r1 = StopReason.builder()
                .caseStopReason("R1")
                .build();
        StopReason r2 = StopReason.builder()
                .caseStopReason("DocumentsRequired")
                .caseStopSubReasonDocRequired("SUB2")
                .build();
        CollectionMember<StopReason> cm1 = new CollectionMember<>(null, r1);
        CollectionMember<StopReason> cm2 = new CollectionMember<>(null, r2);
        List<CollectionMember<StopReason>> domainList = List.of(cm1, cm2);
        List<Map<String,Object>> rawList = objectMapper.convertValue(domainList, new TypeReference<>() {});
        Map<String,Object> data = new HashMap<>();
        data.put("primaryApplicantForenames", "Alice");
        data.put("primaryApplicantSurname",  "Anderson");
        data.put("deceasedForenames", "Bob");
        data.put("deceasedSurname",  "Brown");
        data.put("caseType", "intestacy");
        data.put("boCaseStopReasonList", rawList);
        data.put("deceasedDateOfDeath", "2025-01-02");

        CaseDetails cd = mock(CaseDetails.class);
        when(cd.getData()).thenReturn(data);
        when(cd.getId()).thenReturn(999L);
        when(cd.getCreatedDate()).thenReturn(LocalDateTime.now());

        Map<String,Object> p = underTest.getPersonalisation(cd, ApplicationType.PERSONAL);

        assertAll("personalisation map",
                // stop reasons
                () -> assertEquals("Reason One\nDocs Req\n&nbsp;&nbsp;&nbsp;&nbsp;Sub2 Desc\n",
                        p.get("stop-reasons")),
                () -> assertEquals(
                "Reason One welsh\nDocs Req welsh\n&nbsp;&nbsp;&nbsp;&nbsp;Sub2 Desc welsh\n",
                        p.get("stop-reasons-welsh")),
                () -> assertEquals("No", p.get("display-single-stop-reason")),
                () -> assertEquals("Yes", p.get("display-multiple-stop-reasons")),
                // names
                () -> assertEquals("Alice Anderson", p.get("applicant_name")),
                () -> assertEquals("Bob Brown", p.get("deceased_name")),
                // dates
                () -> assertEquals("2025-01-01", p.get("deceased_dod")),
                () -> assertEquals("Welsh 2025-01-01", p.get("welsh_deceased_date_of_death")),
                // references & links
                () -> assertEquals("999", p.get("ccd_reference")),
                () -> assertEquals("999", p.get("case_ref")),
                () -> assertEquals(
                        urlPrefixToPersonalCase + "/get-case/999?probateType=INTESTACY",
                        p.get("link_to_case"))
        );
    }

    @Test
    void getStopReasonShouldhandleNulls() {
        final Map<String, String> reasons = new HashMap<>();
        reasons.put("R1", "Reason One");
        reasons.put("DocumentsRequired", "Doc XYZ");
        reasons.put("SUB_NULL", null);
        reasons.put("SUB_A", "Sub Reason A");
        reasons.put("SUB_B", "Sub Reason B");
        when(stopReasonService.getStopReasonDescription(any(), any()))
            .thenAnswer(i -> reasons.get(i.getArgument(1, String.class)));

        BiFunction<String, String, CollectionMember<StopReason>> build = (r, s) -> {
            final StopReason sr = StopReason.builder()
                    .caseStopReason(r)
                    .caseStopSubReasonDocRequired(s)
                    .build();
            return new CollectionMember<>(sr);
        };
        List<CollectionMember<StopReason>> domainList = List.of(
            build.apply("R1", null),
            build.apply("DocumentsRequired", "SUB_A"),
            build.apply("DocumentsRequired", "SUB_NULL"),
            build.apply("DocumentsRequired", null),
            build.apply("DocumentsRequired", "SUB_B")
        );

        assertDoesNotThrow(() -> {
            String result = underTest.getStopReason(domainList, false);
            assertEquals(
                "Reason One\nDoc XYZ\n"
                + "&nbsp;&nbsp;&nbsp;&nbsp;Sub Reason A\n"
                + "&nbsp;&nbsp;&nbsp;&nbsp;Sub Reason B\n",
                result
            );
        });
    }
}
