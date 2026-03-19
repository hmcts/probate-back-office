package uk.gov.hmcts.probate.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.springframework.stereotype.Component;

@Component
public class JsonObjectUtils {
    public JSONObject findObjectInQuery(
            final JSONObject queryObject,
            final JSONPointer pointer,
            final String expectKey,
            final String expectValue) {
        final Object fromPointer = queryObject.query(pointer);
        if (!(fromPointer instanceof JSONObject)) {
            throw new IllegalStateException("Expected JSON object but got " + fromPointer);
        }
        final JSONObject subObject = (JSONObject) fromPointer;
        if (!subObject.has(expectKey)) {
            throw new IllegalStateException(
                    "Expected JSON object with \"" + expectKey + "\" key but got " + subObject);
        }
        if (!subObject.get(expectKey).equals(expectValue)) {
            throw new IllegalStateException(
                    "Expected JSON object with \"" + expectKey + "\" key with value \"" + expectValue
                            + "\" but got " + subObject);
        }
        return subObject;
    }

    public JSONArray findArrayInQuery(
            final JSONObject queryObject,
            final JSONPointer pointer) {
        final Object fromPointer = queryObject.query(pointer);
        if (!(fromPointer instanceof JSONArray)) {
            throw new IllegalStateException("Expected JSON array but got " + fromPointer);
        }
        final JSONArray array = (JSONArray) fromPointer;
        return array;
    }
}
