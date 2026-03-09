package uk.gov.hmcts.probate.service;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CaseMatchingJsonService {

    private static final String TEMPLATE_DIRECTORY = "templates/elasticsearch/caseMatching/";
    private static final String ES_BASE = "templates/elasticsearch/caseMatching/json/matching_base.json";

    private final FileSystemResourceService fileSystemResourceService;

    public CaseMatchingJsonService(
            final FileSystemResourceService fileSystemResourceService) {
        this.fileSystemResourceService = fileSystemResourceService;
    }

    public CaseMatchingJson getBaseQuery() {
        final String baseQueryString = fileSystemResourceService.getFileFromResourceAsString(ES_BASE);
        return new CaseMatchingJson(baseQueryString);
    }

    public Optional<CaseMatchingJson> getDateOfBirthSubquery(final LocalDate dateOfBirth) {
        throw new NotImplementedException();
    }

    public Optional<CaseMatchingJson> getDateOfDeathSubquery(final LocalDate dateOfDeath) {
        throw new NotImplementedException();
    }

    public List<CaseMatchingJson> getAliasesToNameSubqueries(final List<String> aliases) {
        throw new NotImplementedException();
    }

    public List<CaseMatchingJson> getAliasesToAliasesSubqueries(final List<String> aliases) {
        throw new NotImplementedException();
    }

    public List<CaseMatchingJson> getAliasesToAliasesNameListSubqueries(final List<String> aliases) {
        throw new NotImplementedException();
    }

    public static class CaseMatchingJson {
        private final AtomicReference<JSONObject> jsonObject;

        private CaseMatchingJson(final String baseQuery) {
            final JSONObject baseQueryJson = new JSONObject(baseQuery);
            jsonObject = new AtomicReference<>(baseQueryJson);
        }

        private CaseMatchingJson(final JSONObject queryJson) {
            Objects.requireNonNull(queryJson);
            jsonObject = new AtomicReference<>(queryJson);
        }

        private JSONObject getJsonObject(
                final JSONObject query,
                final JSONPointer pointer) {
            final Object fromPointer = query.query(pointer);
            if (!(fromPointer instanceof JSONObject)) {
                throw new IllegalStateException("Expected JSON object but got " + fromPointer);
            }
            final JSONObject queryObject = (JSONObject) fromPointer;
            if (!queryObject.has("query")) {
                throw new IllegalStateException("Expected JSON object with \"query\" key but got " + queryObject);
            }
            return queryObject;
        }

        private void updateForenames(
                final JSONObject query,
                final String deceasedForenames,
                final JSONPointer pointer) {
            final JSONObject forenameQuery = getJsonObject(query, pointer);
            if (!forenameQuery.getString("query").equals(":deceasedForenames")) {
                throw new IllegalStateException(
                        "Expected JSON object with \"query\" key with value \":deceasedForenames\" but got "
                                + forenameQuery);
            }
            forenameQuery.put("query", deceasedForenames);
        }

        private void updateSurname(
                final JSONObject query,
                final String deceasedSurname,
                final JSONPointer pointer) {
            final JSONObject surnameQuery = getJsonObject(query, pointer);
            if (!surnameQuery.getString("query").equals(":deceasedForename")) {
                throw new IllegalStateException(
                        "Expected JSON object with \"query\" key with value \":deceasedSurname\" but got "
                                + surnameQuery);
            }
            surnameQuery.put("query", deceasedSurname);
        }

        private void updateFullName(
                final JSONObject query,
                final String deceasedFullName,
                final JSONPointer pointer) {
            final JSONObject fullNameQuery = getJsonObject(query, pointer);
            if (!fullNameQuery.getString("query").equals(":deceasedFullName")) {
                throw new IllegalStateException(
                        "Expected JSON object with \"query\" key with value \":deceasedFullName\" but got "
                                + fullNameQuery);
            }
            fullNameQuery.put("query", deceasedFullName);
        }

        public CaseMatchingJson withDeceasedForenames(final String deceasedForenames) {
            final JSONObject query = Objects.requireNonNull(this.jsonObject.getAndSet(null));

            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/0/bool/must/0/multi_match"));
            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/1/bool/must/0/multi_match"));
            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/3/bool/must/0/multi_match"));
            updateForenames(query, deceasedForenames, new JSONPointer("/query/bool/should/4/bool/must/0/multi_match"));

            return new CaseMatchingJson(query);
        }

        public CaseMatchingJson withDeceasedSurname(final String deceasedSurname) {
            final JSONObject query = Objects.requireNonNull(this.jsonObject.getAndSet(null));

            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/0/bool/must/1/multi_match"));
            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/1/bool/must/1/multi_match"));
            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/3/bool/must/1/multi_match"));
            updateSurname(query, deceasedSurname, new JSONPointer("/query/bool/should/4/bool/must/1/multi_match"));

            return new CaseMatchingJson(query);
        }

        public CaseMatchingJson withDeceasedFullname(final String deceasedFullname) {
            final JSONObject query = Objects.requireNonNull(this.jsonObject.getAndSet(null));

            updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/5/multi_match"));
            updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/6/multi_match"));
            updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/8/multi_match"));
            updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/9/multi_match"));
            updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/11/multi_match"));
            updateFullName(query, deceasedFullname, new JSONPointer("/query/bool/should/12/multi_match"));

            return new CaseMatchingJson(query);
        }

        public CaseMatchingJson withDateOfBirth(final Optional<CaseMatchingJson> dateOfBirthQuery) {
            throw new NotImplementedException();
        }

        public CaseMatchingJson withDateOfDeath(final Optional<CaseMatchingJson> dateOfDeathQuery) {
            throw new NotImplementedException();
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

}
