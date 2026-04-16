package uk.gov.hmcts.probate.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.exception.JsonObjectUtilsException;
import uk.gov.hmcts.probate.exception.JsonObjectUtilsException.Cause;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonObjectUtilsTest {

    JsonObjectUtils jsonObjectUtils;

    @BeforeEach
    void setUp() {
        this.jsonObjectUtils = new JsonObjectUtils();
    }

    @Test
    void testFindObjectInQuerySucceeds() {
        final String topKey = "value";
        final String subKey = "present";
        final String subKeyValue = "true";

        final JSONObject subObject = new JSONObject()
                .put(subKey, subKeyValue);

        final JSONObject toQuery = new JSONObject()
                .put(topKey, subObject);
        final JSONPointer pointer = new JSONPointer("/" + topKey);

        final JSONObject actual = jsonObjectUtils.findObjectInQuery(
                toQuery,
                pointer,
                subKey,
                subKeyValue);

        assertThat(actual, sameInstance(subObject));
    }

    @Test
    void testFindObjectInQueryThrowsIfSubValueMismatch() {
        final String topKey = "value";
        final String subKey = "present";
        final String subKeyValue = "true";
        final String incorrectSubKeyValue = "incorrect";

        final JSONObject subObject = new JSONObject()
                .put(subKey, subKeyValue);

        final JSONObject toQuery = new JSONObject()
                .put(topKey, subObject);
        final JSONPointer pointer = new JSONPointer("/" + topKey);

        final JsonObjectUtilsException ex = assertThrows(
                JsonObjectUtilsException.class,
                () -> jsonObjectUtils.findObjectInQuery(
                        toQuery,
                        pointer,
                        subKey,
                        incorrectSubKeyValue));

        assertThat(ex.getErrorCause(), is(Cause.MISMATCHED_SUBKEY));
    }

    @Test
    void testFindObjectInQueryThrowsIfSubKeyMismatch() {
        final String topKey = "value";
        final String subKey = "present";
        final String incorrectSubKey = "missing";
        final String subKeyValue = "true";

        final JSONObject subObject = new JSONObject()
                .put(subKey, subKeyValue);

        final JSONObject toQuery = new JSONObject()
                .put(topKey, subObject);
        final JSONPointer pointer = new JSONPointer("/" + topKey);

        final JsonObjectUtilsException ex = assertThrows(
                JsonObjectUtilsException.class,
                () -> jsonObjectUtils.findObjectInQuery(
                        toQuery,
                        pointer,
                        incorrectSubKey,
                        subKeyValue));

        assertThat(ex.getErrorCause(), is(Cause.NO_SUBKEY));
    }

    @Test
    void testFindObjectInQueryThrowsIfSubValueNotObject() {
        final String topKey = "value";
        final String subKey = "present";
        final String subKeyValue = "true";

        final JSONArray subArray = new JSONArray()
                .put(subKey)
                .put(subKeyValue);

        final JSONObject toQuery = new JSONObject()
                .put(topKey, subArray);
        final JSONPointer pointer = new JSONPointer("/" + topKey);

        final JsonObjectUtilsException ex = assertThrows(
                JsonObjectUtilsException.class,
                () -> jsonObjectUtils.findObjectInQuery(
                        toQuery,
                        pointer,
                        subKey,
                        subKeyValue));

        assertThat(ex.getErrorCause(), is(Cause.WRONG_TYPE));
    }

    @Test
    void testFindArrayInQuerySucceeds() {
        final String topKey = "value";
        final String subValue = "present";

        final JSONArray subArray = new JSONArray()
                .put(subValue)
                .put(subValue);

        final JSONObject toQuery = new JSONObject()
                .put(topKey, subArray);
        final JSONPointer pointer = new JSONPointer("/" + topKey);

        final JSONArray actual = jsonObjectUtils.findArrayInQuery(
                toQuery,
                pointer);

        assertThat(actual, sameInstance(subArray));
    }

    @Test
    void testFindArrayInQueryThrowsIfSubValueNotArray() {
        final String topKey = "value";
        final String subKey = "present";
        final String subKeyValue = "true";

        final JSONObject subObject = new JSONObject()
                .put(subKey, subKeyValue);

        final JSONObject toQuery = new JSONObject()
                .put(topKey, subObject);
        final JSONPointer pointer = new JSONPointer("/" + topKey);

        final JsonObjectUtilsException ex = assertThrows(
                JsonObjectUtilsException.class,
                () -> jsonObjectUtils.findArrayInQuery(
                        toQuery,
                        pointer));

        assertThat(ex.getErrorCause(), is(Cause.WRONG_TYPE));
    }
}
