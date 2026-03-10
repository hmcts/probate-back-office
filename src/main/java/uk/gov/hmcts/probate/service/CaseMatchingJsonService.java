package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CaseMatchingJsonService {

    private static final String ES_BASE = "templates/elasticsearch/caseMatching/json/matching_base.json";
    private static final String DOB_BASE = "templates/elasticsearch/caseMatching/json/deceased_dob_sub_query.json";
    private static final String DOD_BASE = "templates/elasticsearch/caseMatching/json/deceased_dod_sub_query.json";

    private final FileSystemResourceService fileSystemResourceService;
    private final JsonObjectUtils jsonObjectUtils;

    public CaseMatchingJsonService(
            final FileSystemResourceService fileSystemResourceService,
            final JsonObjectUtils jsonObjectUtils) {
        this.fileSystemResourceService = fileSystemResourceService;
        this.jsonObjectUtils = jsonObjectUtils;
    }

    public CaseMatchingJson getBaseQuery() {
        final String baseQueryString = fileSystemResourceService.getFileFromResourceAsString(ES_BASE);
        return new CaseMatchingJson(jsonObjectUtils, baseQueryString);
    }

    public Optional<CaseMatchingJson> getDateOfBirthSubquery(final LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return Optional.empty();
        }
        final String dobFormatted = dateOfBirth.format(DateTimeFormatter.ISO_DATE);

        final String dobQueryString = fileSystemResourceService.getFileFromResourceAsString(DOB_BASE);
        final JSONObject dobQuery = new JSONObject(dobQueryString);

        final JSONObject valueObject = jsonObjectUtils.findObjectInQuery(
                dobQuery,
                new JSONPointer("/bool/should/0/term/data.deceasedDateOfBirth"),
                "value",
                ":deceasedDateOfBirth");
        valueObject.put("value", dobFormatted);

        return Optional.of(new CaseMatchingJson(jsonObjectUtils, dobQuery));
    }

    public Optional<CaseMatchingJson> getDateOfDeathSubquery(final LocalDate dateOfDeath) {
        if (dateOfDeath == null) {
            return Optional.empty();
        }
        final String dodFormatted = dateOfDeath.format(DateTimeFormatter.ISO_DATE);

        final String dobQueryString = fileSystemResourceService.getFileFromResourceAsString(DOD_BASE);
        final JSONObject dodQuery = new JSONObject(dobQueryString);

        final JSONObject dodRange = jsonObjectUtils.findObjectInQuery(
                dodQuery,
                new JSONPointer("/bool/should/0/bool/should/1/bool/should/1/range/data.deceasedDateOfDeath"),
                "gte",
                ":deceasedDateOfDeath||-3d");
        // we could make this configurable (not in scope)
        dodRange.put("gte", dodFormatted + "||-3d");
        dodRange.put("lte", dodFormatted + "||+3d");

        final JSONObject dodLte = jsonObjectUtils.findObjectInQuery(
                dodQuery,
                new JSONPointer("/bool/should/1/bool/must/2/bool/must/range/data.deceasedDateOfDeath"),
                "lte",
                ":deceasedDateOfDeath");
        dodLte.put("lte", dodFormatted);

        final JSONObject dodGte = jsonObjectUtils.findObjectInQuery(
                dodQuery,
                new JSONPointer("/bool/should/1/bool/must/3/bool/must/range/data.deceasedDateOfDeath2"),
                "gte",
                ":deceasedDateOfDeath");
        dodGte.put("gte", dodFormatted);

        return Optional.of(new CaseMatchingJson(jsonObjectUtils, dodQuery));
    }

    public List<CaseMatchingJson> getAliasesSubqueries(final List<String> aliases) {
        throw new NotImplementedException();
//        final String baseQueryString = fileSystemResourceService.getFileFromResourceAsString(ES_BASE);
//        return new CaseMatchingJson(jsonObjectUtils, baseQueryString);
    }

    public static class CaseMatchingJson {
        private final JsonObjectUtils jsonObjectUtils;
        private final AtomicReference<JSONObject> jsonObject;

        private CaseMatchingJson(
                final JsonObjectUtils jsonObjectUtils,
                final String baseQuery) {
            this.jsonObjectUtils = Objects.requireNonNull(jsonObjectUtils);

            final JSONObject baseQueryJson = new JSONObject(baseQuery);
            jsonObject = new AtomicReference<>(baseQueryJson);
        }

        private CaseMatchingJson(
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
                    "query",
                    ":deceasedForenames");
            forenameQuery.put("query", deceasedForenames);
        }

        private void updateSurname(
                final JSONObject query,
                final String deceasedSurname,
                final JSONPointer pointer) {
            final JSONObject surnameQuery = jsonObjectUtils.findObjectInQuery(
                    query,
                    pointer,
                    "query",
                    ":deceasedSurname");
            surnameQuery.put("query", deceasedSurname);
        }

        private void updateFullName(
                final JSONObject query,
                final String deceasedFullName,
                final JSONPointer pointer) {
            final JSONObject fullNameQuery = jsonObjectUtils.findObjectInQuery(
                    query,
                    pointer,
                    "query",
                    ":deceasedFullName");
            fullNameQuery.put("query", deceasedFullName);
        }

        public CaseMatchingJson withDeceasedForenames(final String deceasedForenames) {
            final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));

            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/0/bool/must/0/multi_match"));
            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/1/bool/must/0/multi_match"));
            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/2/bool/must/0/multi_match"));
            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/3/bool/must/0/multi_match"));

            return new CaseMatchingJson(jsonObjectUtils, query);
        }

        public CaseMatchingJson withDeceasedSurname(final String deceasedSurname) {
            final JSONObject query = Objects.requireNonNull(jsonObject.getAndSet(null));

            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/0/bool/must/1/multi_match"));
            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/1/bool/must/1/multi_match"));
            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/2/bool/must/1/multi_match"));
            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/3/bool/must/1/multi_match"));

            return new CaseMatchingJson(jsonObjectUtils, query);
        }

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

        public Optional<JSONObject> stealJson() {
            final JSONObject queryJson = jsonObject.getAndSet(null);
            if (queryJson == null) {
                log.warn("Attempting to reuse a CaseMatchingJson where the internal state has been cleared");
                return Optional.empty();
            }
            return Optional.of(queryJson);
        }
    }

    static class JsonObjectUtils {
        private JSONObject findObjectInQuery(
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
                        "Expected JSON object with \"" + expectKey + "\" key with value \"" + expectValue + "\" but got "
                                + subObject);
            }
            return subObject;
        }

        private JSONArray findArrayInQuery(
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
}
