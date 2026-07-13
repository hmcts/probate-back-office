package uk.gov.hmcts.probate.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.JsonObjectUtilsException;
import uk.gov.hmcts.probate.exception.JsonObjectUtilsException.Cause;

@Component
public class JsonObjectUtils {
    public JSONObject findObjectInQuery(
            final JSONObject queryObject,
            final JSONPointer pointer,
            final String expectKey,
            final String expectValue) {
        final Object fromPointer = queryObject.query(pointer);
        if (!(fromPointer instanceof JSONObject)) {
            throw new JsonObjectUtilsException(
                    "Expected JSON object but got " + fromPointer,
                    Cause.WRONG_TYPE);
        }
        final JSONObject subObject = (JSONObject) fromPointer;
        if (!subObject.has(expectKey)) {
            throw new JsonObjectUtilsException(
                    "Expected JSON object with \"" + expectKey + "\" key but got " + subObject,
                    Cause.NO_SUBKEY);
        }
        if (!subObject.get(expectKey).equals(expectValue)) {
            throw new JsonObjectUtilsException(
                    "Expected JSON object with \"" + expectKey + "\" key with value \"" + expectValue
                            + "\" but got " + subObject,
                    Cause.MISMATCHED_SUBKEY);
        }
        return subObject;
    }

    public JSONArray findArrayInQuery(
            final JSONObject queryObject,
            final JSONPointer pointer) {
        final Object fromPointer = queryObject.query(pointer);
        if (!(fromPointer instanceof JSONArray)) {
            throw new JsonObjectUtilsException(
                    "Expected JSON array but got " + fromPointer,
                    Cause.WRONG_TYPE);
        }
        return (JSONArray) fromPointer;
    }
}
