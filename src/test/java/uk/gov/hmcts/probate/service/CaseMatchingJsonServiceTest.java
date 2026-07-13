package uk.gov.hmcts.probate.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.query.CaseMatchingJson;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaseMatchingJsonServiceTest {
    @Mock
    FileSystemResourceService fileSystemResourceServiceMock;
    @Mock
    JsonObjectUtils jsonObjectUtilsMock;

    CaseMatchingJsonService caseMatchingJsonService;

    AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        caseMatchingJsonService = new CaseMatchingJsonService(
                fileSystemResourceServiceMock,
                jsonObjectUtilsMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void testGetBaseQuery() {
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        caseMatchingJsonService.getBaseQuery();

        verify(fileSystemResourceServiceMock).getFileFromResourceAsString(CaseMatchingJsonService.ES_BASE);
    }

    @Test
    void testGetBaseQueryThrowsIfMalformed() {
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{a}");

        assertThrows(
                JSONException.class,
                () -> caseMatchingJsonService.getBaseQuery());

        verify(fileSystemResourceServiceMock).getFileFromResourceAsString(CaseMatchingJsonService.ES_BASE);
    }

    @Test
    void testDoBSubqueryNullDoB() {
        final Optional<CaseMatchingJson> actual = caseMatchingJsonService.getDateOfBirthSubquery(null);

        assertThat(actual, emptyOptional());
    }

    @Test
    void testDoBSubqueryWithDoB() {
        final LocalDate birthDate = LocalDate.of(2020, 1, 1);
        final String birthFormatted = "2020-01-01";

        final JSONObject jsonMock = mock();

        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(jsonMock);

        final Optional<CaseMatchingJson> actual = caseMatchingJsonService.getDateOfBirthSubquery(birthDate);

        assertAll(
                () -> assertThat(actual, optionalWithValue()),

                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.DOB_BASE),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(jsonMock).put("value", birthFormatted));
    }

    @Test
    void testDoBSubqueryWithDoBThrowsIfMalformed() {
        final LocalDate birthDate = LocalDate.of(2020, 1, 1);

        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{a}");

        assertThrows(
                JSONException.class,
                () -> caseMatchingJsonService.getDateOfBirthSubquery(birthDate));

        verify(fileSystemResourceServiceMock).getFileFromResourceAsString(CaseMatchingJsonService.DOB_BASE);
    }

    @Test
    void testDoDSubqueryNullDoD() {
        final Optional<CaseMatchingJson> actual = caseMatchingJsonService.getDateOfDeathSubquery(null);

        assertThat(actual, emptyOptional());
    }

    @Test
    void testDoDSubqueryWithDoD() {
        final LocalDate deathDate = LocalDate.of(2020, 1, 1);
        final String deathFormatted = "2020-01-01";
        final String deathFormattedGte = "2020-01-01||-3d";
        final String deathFormattedLte = "2020-01-01||+3d";

        final JSONObject jsonMockRangeWithin = mock();
        final JSONObject jsonMockRangeLte = mock();
        final JSONObject jsonMockRangeGte = mock();

        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(jsonMockRangeWithin, jsonMockRangeLte, jsonMockRangeGte);

        final Optional<CaseMatchingJson> actual = caseMatchingJsonService.getDateOfDeathSubquery(deathDate);

        assertAll(
                () -> assertThat(actual, optionalWithValue()),

                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.DOD_BASE),

                () -> verify(jsonObjectUtilsMock)
                        .findObjectInQuery(any(), any(), eq("gte"), eq(":deceasedDateOfDeath||-3d")),
                () -> verify(jsonObjectUtilsMock)
                        .findObjectInQuery(any(), any(), eq("lte"), eq(":deceasedDateOfDeath")),
                () -> verify(jsonObjectUtilsMock)
                        .findObjectInQuery(any(), any(), eq("gte"), eq(":deceasedDateOfDeath")),

                () -> verify(jsonMockRangeWithin).put("gte", deathFormattedGte),
                () -> verify(jsonMockRangeWithin).put("lte", deathFormattedLte),

                () -> verify(jsonMockRangeLte).put("lte", deathFormatted),

                () -> verify(jsonMockRangeGte).put("gte", deathFormatted));
    }

    @Test
    void testDoDSubqueryWithDoDThrowsIfMalformed() {
        final LocalDate deathDate = LocalDate.of(2020, 1, 1);

        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{a}");

        assertThrows(
                JSONException.class,
                () -> caseMatchingJsonService.getDateOfDeathSubquery(deathDate));

        verify(fileSystemResourceServiceMock).getFileFromResourceAsString(CaseMatchingJsonService.DOD_BASE);
    }

    @Test
    void testAliasesSubqueriesEmptyListReturnsEmptyList() {
        final List<CaseMatchingJson> actual = caseMatchingJsonService.getAliasesSubqueries(List.of());

        assertThat(actual, empty());
    }

    @Test
    void testAliasesSubQueriesReturnsSixSubqueriesPerAlias() {
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(mock());

        final List<CaseMatchingJson> actual = caseMatchingJsonService.getAliasesSubqueries(List.of("a"));

        assertAll(
                () -> assertThat(actual, hasSize(6)),
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_A_BASE),
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_B_BASE),
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_ALIAS_A_BASE),
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_ALIAS_B_BASE),
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_SOLS_ALIAS_A_BASE),
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_SOLS_ALIAS_B_BASE));
    }

    @Test
    void testAliasNameASubquery() {
        final String alias = "alias";
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        final JSONObject forename = mock();
        final JSONObject surname = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(forename, surname);

        caseMatchingJsonService.getAliasNameASubquery(alias);

        assertAll(
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_A_BASE),
                () -> verify(jsonObjectUtilsMock, times(2))
                        .findObjectInQuery(any(), any(), any(), any()),
                () -> verify(forename).put(CaseMatchingJsonService.QUERY, alias),
                () -> verify(surname).put(CaseMatchingJsonService.QUERY, alias)
        );
    }

    @Test
    void testAliasNameBSubquery() {
        final String alias = "alias";
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        final JSONObject forename = mock();
        final JSONObject surname = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(forename, surname);

        caseMatchingJsonService.getAliasNameBSubquery(alias);

        assertAll(
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_B_BASE),
                () -> verify(jsonObjectUtilsMock, times(2))
                        .findObjectInQuery(any(), any(), any(), any()),
                () -> verify(forename).put(CaseMatchingJsonService.QUERY, alias),
                () -> verify(surname).put(CaseMatchingJsonService.QUERY, alias)
        );
    }

    @Test
    void testAliasNameAliasASubquery() {
        final String alias = "alias";
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        final JSONObject forename = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(forename);

        caseMatchingJsonService.getAliasNameAliasASubquery(alias);

        assertAll(
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_ALIAS_A_BASE),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(forename).put(CaseMatchingJsonService.QUERY, alias)
        );
    }

    @Test
    void testAliasNameAliasBSubquery() {
        final String alias = "alias";
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        final JSONObject forename = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(forename);

        caseMatchingJsonService.getAliasNameAliasBSubquery(alias);

        assertAll(
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_ALIAS_B_BASE),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(forename).put(CaseMatchingJsonService.QUERY, alias)
        );
    }

    @Test
    void testAliasNameSolsAliasASubquery() {
        final String alias = "alias";
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        final JSONObject forename = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(forename);

        caseMatchingJsonService.getAliasNameSolsAliasASubquery(alias);

        assertAll(
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_SOLS_ALIAS_A_BASE),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(forename).put(CaseMatchingJsonService.QUERY, alias)
        );
    }

    @Test
    void testAliasNameSolsAliasBSubquery() {
        final String alias = "alias";
        when(fileSystemResourceServiceMock.getFileFromResourceAsString(any()))
                .thenReturn("{}");

        final JSONObject forename = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(forename);

        caseMatchingJsonService.getAliasNameSolsAliasBSubquery(alias);

        assertAll(
                () -> verify(fileSystemResourceServiceMock)
                        .getFileFromResourceAsString(CaseMatchingJsonService.ALIAS_NAME_SOLS_ALIAS_B_BASE),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(forename).put(CaseMatchingJsonService.QUERY, alias)
        );
    }
}
