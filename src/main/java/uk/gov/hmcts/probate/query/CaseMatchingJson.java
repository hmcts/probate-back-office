package uk.gov.hmcts.probate.query;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.springframework.lang.CheckReturnValue;
import uk.gov.hmcts.probate.service.JsonObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CaseMatchingJson {
    private final JsonObjectUtils jsonObjectUtils;
    private final AtomicReference<JSONObject> jsonObject;

    private static final String QUERY = "query";

    public CaseMatchingJson(
            final JsonObjectUtils jsonObjectUtils,
            final JSONObject queryJson) {
        this.jsonObjectUtils = Objects.requireNonNull(jsonObjectUtils);
        Objects.requireNonNull(queryJson);
        jsonObject = new AtomicReference<>(queryJson);
    }

    private void updateForenames(
            final JSONObject query,
            final String deceasedForenames,
            final JSONPointer pointer) {
        final JSONObject forenameQuery = jsonObjectUtils.findObjectInQuery(
                query,
                pointer,
                QUERY,
                ":deceasedForenames");
        forenameQuery.put(QUERY, deceasedForenames);
    }

    private void updateSurname(
            final JSONObject query,
            final String deceasedSurname,
            final JSONPointer pointer) {
        final JSONObject surnameQuery = jsonObjectUtils.findObjectInQuery(
                query,
                pointer,
                QUERY,
                ":deceasedSurname");
        surnameQuery.put(QUERY, deceasedSurname);
    }

    private void updateFullName(
            final JSONObject query,
            final String deceasedFullName,
            final JSONPointer pointer) {
        final JSONObject fullNameQuery = jsonObjectUtils.findObjectInQuery(
                query,
                pointer,
                QUERY,
                ":deceasedFullName");
        fullNameQuery.put(QUERY, deceasedFullName);
    }

    @CheckReturnValue
    public CaseMatchingJson withDeceasedForenames(final String deceasedForenames) {
        final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));

        updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/0/bool/must/0/multi_match"));
        updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/1/bool/must/0/multi_match"));
        updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/2/bool/must/0/multi_match"));
        updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/3/bool/must/0/multi_match"));

        return new CaseMatchingJson(jsonObjectUtils, query);
    }

    @CheckReturnValue
    public CaseMatchingJson withDeceasedSurname(final String deceasedSurname) {
        final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));

        updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/0/bool/must/1/multi_match"));
        updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/1/bool/must/1/multi_match"));
        updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/2/bool/must/1/multi_match"));
        updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/3/bool/must/1/multi_match"));

        return new CaseMatchingJson(jsonObjectUtils, query);
    }

    @CheckReturnValue
    public CaseMatchingJson withDeceasedFullname(final String deceasedFullname) {
        final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));

        updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/4/multi_match"));
        updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/5/multi_match"));
        updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/6/multi_match"));
        updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/7/multi_match"));
        updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/8/multi_match"));
        updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/9/multi_match"));

        return new CaseMatchingJson(jsonObjectUtils, query);
    }

    @CheckReturnValue
    public CaseMatchingJson withDateOfBirth(final Optional<CaseMatchingJson> dateOfBirthQuery) {
        final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));
        if (dateOfBirthQuery.isEmpty()) {
            return new CaseMatchingJson(jsonObjectUtils, query);
        }

        final JSONObject dobQuery = dateOfBirthQuery.get().stealJson().orElseThrow();
        final JSONArray mustFilter = jsonObjectUtils.findArrayInQuery(
                query,
                new JSONPointer("/query/bool/filter/bool/must"));
        mustFilter.put(dobQuery);

        return new CaseMatchingJson(jsonObjectUtils, query);
    }

    @CheckReturnValue
    public CaseMatchingJson withDateOfDeath(final Optional<CaseMatchingJson> dateOfDeathQuery) {
        final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));
        if (dateOfDeathQuery.isEmpty()) {
            return new CaseMatchingJson(jsonObjectUtils, query);
        }

        final JSONObject dodQuery = dateOfDeathQuery.get().stealJson().orElseThrow();
        final JSONArray mustFilter = jsonObjectUtils.findArrayInQuery(
                query,
                new JSONPointer("/query/bool/filter/bool/must")
        );
        mustFilter.put(dodQuery);

        return new CaseMatchingJson(jsonObjectUtils, query);
    }

    @CheckReturnValue
    public CaseMatchingJson withAliases(final List<CaseMatchingJson> aliasesToNameSubqueries) {
        final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));
        if (aliasesToNameSubqueries.isEmpty()) {
            return new CaseMatchingJson(jsonObjectUtils, query);
        }

        final JSONArray shouldFilter = jsonObjectUtils.findArrayInQuery(
                query,
                new JSONPointer("/query/bool/should"));
        for (final CaseMatchingJson caseMatchingJson : aliasesToNameSubqueries) {
            final JSONObject subQuery = caseMatchingJson.stealJson().orElseThrow();
            shouldFilter.put(subQuery);
        }

        return new CaseMatchingJson(jsonObjectUtils, query);
    }

    @CheckReturnValue
    public Optional<JSONObject> stealJson() {
        final JSONObject queryJson = jsonObject.getAndSet(null);
        if (queryJson == null) {
            log.warn("Attempting to reuse a CaseMatchingJson where the internal state has been cleared");
            return Optional.empty();
        }
        return Optional.of(queryJson);
    }
}
