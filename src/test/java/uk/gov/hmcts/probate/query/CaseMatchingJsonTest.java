package uk.gov.hmcts.probate.query;

import org.apache.commons.lang3.RandomUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.service.JsonObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaseMatchingJsonTest {
    @Mock
    JsonObjectUtils jsonObjectUtilsMock;
    @Mock
    JSONObject jsonMock;

    CaseMatchingJson caseMatchingJson;

    AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        caseMatchingJson = new CaseMatchingJson(jsonObjectUtilsMock, jsonMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    private static class JsonPointerMatcher implements ArgumentMatcher<JSONPointer> {
        final String expPath;

        public JsonPointerMatcher(String expPath) {
            this.expPath = expPath;
        }

        @Override
        public boolean matches(JSONPointer argument) {
            return argument.toString().equals(expPath);
        }
    }

    @Test
    void testDeceasedForenames() {
        final String forename = "forename";
        final JSONObject forenameJson = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(forenameJson);

        final CaseMatchingJson result = caseMatchingJson.withDeceasedForenames(forename);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(forenameJson, times(4)).put("query", forename),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/0/bool/must/0/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/1/bool/must/0/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/2/bool/must/0/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/3/bool/must/0/multi_match")),
                        any(),
                        any()));
    }

    @Test
    void testDeceasedSurname() {
        final String surname = "surname";
        final JSONObject surnameJson = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(surnameJson);

        final CaseMatchingJson result = caseMatchingJson.withDeceasedSurname(surname);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(surnameJson, times(4)).put("query", surname),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/0/bool/must/1/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/1/bool/must/1/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/2/bool/must/1/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/3/bool/must/1/multi_match")),
                        any(),
                        any()));
    }

    @Test
    void testDeceasedFullname() {
        final String fullname = "full name";
        final JSONObject fullnameJson = mock();
        when(jsonObjectUtilsMock.findObjectInQuery(any(), any(), any(), any()))
                .thenReturn(fullnameJson);

        final CaseMatchingJson result = caseMatchingJson.withDeceasedFullname(fullname);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(fullnameJson, times(6)).put("query", fullname),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/4/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/5/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/6/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/7/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/8/multi_match")),
                        any(),
                        any()),
                () -> verify(jsonObjectUtilsMock).findObjectInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/should/9/multi_match")),
                        any(),
                        any()));
    }

    @Test
    void testDateOfBirthSet() {
        final CaseMatchingJson dateOfBirth = mock();
        final JSONObject dateOfBirthJson = mock();
        final Optional<CaseMatchingJson> dateOfBirthOpt = Optional.of(dateOfBirth);

        final JSONArray queryArray = mock();

        when(dateOfBirth.stealJson())
                .thenReturn(Optional.of(dateOfBirthJson));

        when(jsonObjectUtilsMock.findArrayInQuery(any(), any()))
                .thenReturn(queryArray);

        final CaseMatchingJson result = caseMatchingJson.withDateOfBirth(dateOfBirthOpt);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(queryArray).put(dateOfBirthJson),
                () -> verify(jsonObjectUtilsMock).findArrayInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/filter/bool/must"))));
    }

    @Test
    void testDateOfBirthUnset() {
        final Optional<CaseMatchingJson> dateOfBirth = Optional.empty();

        final CaseMatchingJson result = caseMatchingJson.withDateOfBirth(dateOfBirth);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(jsonObjectUtilsMock, never()).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(jsonObjectUtilsMock, never()).findArrayInQuery(any(), any()));
    }

    @Test
    void testDateOfDeathSet() {
        final CaseMatchingJson dateOfDeath = mock();
        final JSONObject dateOfDeathJson = mock();
        final Optional<CaseMatchingJson> dateOfDeathOpt = Optional.of(dateOfDeath);

        final JSONArray queryArray = mock();

        when(dateOfDeath.stealJson())
                .thenReturn(Optional.of(dateOfDeathJson));

        when(jsonObjectUtilsMock.findArrayInQuery(any(), any()))
                .thenReturn(queryArray);

        final CaseMatchingJson result = caseMatchingJson.withDateOfDeath(dateOfDeathOpt);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(queryArray).put(dateOfDeathJson),
                () -> verify(jsonObjectUtilsMock).findArrayInQuery(
                        eq(jsonMock),
                        argThat(new JsonPointerMatcher("/query/bool/filter/bool/must"))));
    }

    @Test
    void testDateOfDeathUnset() {
        final Optional<CaseMatchingJson> dateOfDeath = Optional.empty();

        final CaseMatchingJson result = caseMatchingJson.withDateOfDeath(dateOfDeath);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(jsonObjectUtilsMock, never()).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(jsonObjectUtilsMock, never()).findArrayInQuery(any(), any()));
    }

    @Test
    void testWithAliasesAddsAllAliases() {
        final int aliasCount = RandomUtils.insecure().randomInt(1, 16);

        final JSONArray queryArray = mock();
        when(jsonObjectUtilsMock.findArrayInQuery(any(), any()))
                .thenReturn(queryArray);

        final List<CaseMatchingJson> aliases = new ArrayList<>();
        final List<Executable> verifications = new ArrayList<>();
        for (int i = 0; i < aliasCount; i++) {
            final CaseMatchingJson alias = mock();
            final JSONObject aliasJson = mock();
            when(alias.stealJson())
                    .thenReturn(Optional.of(aliasJson));
            verifications.add(() -> verify(queryArray).put(aliasJson));

            aliases.add(alias);
        }

        final CaseMatchingJson result = caseMatchingJson.withAliases(aliases);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(jsonObjectUtilsMock, never()).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(jsonObjectUtilsMock)
                        .findArrayInQuery(eq(jsonMock), argThat(new JsonPointerMatcher("/query/bool/should"))));
        assertAll(verifications);
    }

    @Test
    void testWithAliasesEmptyAliases() {
        final List<CaseMatchingJson> aliases = Collections.emptyList();

        final CaseMatchingJson result = caseMatchingJson.withAliases(aliases);

        assertAll(
                () -> assertThat(result, not(sameInstance(caseMatchingJson))),
                () -> verify(jsonObjectUtilsMock, never()).findObjectInQuery(any(), any(), any(), any()),
                () -> verify(jsonObjectUtilsMock, never()).findArrayInQuery(any(), any()));
    }

    @Test
    void testStealJsonReturnsOnce() {
        final Optional<JSONObject> first = caseMatchingJson.stealJson();
        final Optional<JSONObject> second = caseMatchingJson.stealJson();

        assertAll(
                () -> assertThat(first, optionalWithValue(sameInstance(jsonMock))),
                () -> assertThat(second, emptyOptional()));

    }
}
