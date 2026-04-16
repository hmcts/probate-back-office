package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.query.CaseMatchingJson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class CaseMatchingJsonService {
    private static final String JSON_BASE = "templates/elasticsearch/caseMatching/json/";

    static final String ES_BASE = JSON_BASE + "matching_base.json";

    static final String DOB_BASE = JSON_BASE + "deceased_dob_sub_query.json";
    static final String DOD_BASE = JSON_BASE + "deceased_dod_sub_query.json";

    static final String ALIAS_NAME_A_BASE = JSON_BASE + "aliases_sub_query_a.json";
    static final String ALIAS_NAME_B_BASE = JSON_BASE + "aliases_sub_query_b.json";
    static final String ALIAS_NAME_ALIAS_A_BASE = JSON_BASE + "aliases_to_aliases_list_sub_query_a.json";
    static final String ALIAS_NAME_ALIAS_B_BASE = JSON_BASE + "aliases_to_aliases_list_sub_query_b.json";
    static final String ALIAS_NAME_SOLS_ALIAS_A_BASE = JSON_BASE + "aliases_to_aliases_sub_query_a.json";
    static final String ALIAS_NAME_SOLS_ALIAS_B_BASE = JSON_BASE + "aliases_to_aliases_sub_query_b.json";

    static final String QUERY = "query";
    static final String DECEASED_ALIAS = ":deceasedAlias";
    static final String MULTI_MATCH = "/multi_match";

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
        final JSONObject baseQuery = new JSONObject(baseQueryString);
        return new CaseMatchingJson(jsonObjectUtils, baseQuery);
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

    CaseMatchingJson getAliasNameASubquery(final String alias) {
        final String aliasNameAString = fileSystemResourceService.getFileFromResourceAsString(ALIAS_NAME_A_BASE);
        final JSONObject aliasNameAQuery = new JSONObject(aliasNameAString);

        final JSONObject forenameMatch = jsonObjectUtils.findObjectInQuery(
                aliasNameAQuery,
                new JSONPointer("/bool/must/0/multi_match"),
                QUERY,
                DECEASED_ALIAS);
        forenameMatch.put(QUERY, alias);

        final JSONObject surnameMatch = jsonObjectUtils.findObjectInQuery(
                aliasNameAQuery,
                new JSONPointer("/bool/must/1/multi_match"),
                QUERY,
                DECEASED_ALIAS);
        surnameMatch.put(QUERY, alias);

        return new CaseMatchingJson(jsonObjectUtils, aliasNameAQuery);
    }

    CaseMatchingJson getAliasNameBSubquery(final String alias) {
        final String aliasNameBString = fileSystemResourceService.getFileFromResourceAsString(ALIAS_NAME_B_BASE);
        final JSONObject aliasNameBQuery = new JSONObject(aliasNameBString);

        final JSONObject forenameMatch = jsonObjectUtils.findObjectInQuery(
                aliasNameBQuery,
                new JSONPointer("/bool/must/0/multi_match"),
                QUERY,
                DECEASED_ALIAS);
        forenameMatch.put(QUERY, alias);

        final JSONObject surnameMatch = jsonObjectUtils.findObjectInQuery(
                aliasNameBQuery,
                new JSONPointer("/bool/must/1/multi_match"),
                QUERY,
                DECEASED_ALIAS);
        surnameMatch.put(QUERY, alias);

        return new CaseMatchingJson(jsonObjectUtils, aliasNameBQuery);
    }

    CaseMatchingJson getAliasNameAliasASubquery(final String alias) {
        final String aliasNameAliasAString = fileSystemResourceService
                .getFileFromResourceAsString(ALIAS_NAME_ALIAS_A_BASE);
        final JSONObject aliasNameAliasAQuery = new JSONObject(aliasNameAliasAString);

        final JSONObject match = jsonObjectUtils.findObjectInQuery(
                aliasNameAliasAQuery,
                new JSONPointer(MULTI_MATCH),
                QUERY,
                DECEASED_ALIAS);
        match.put(QUERY, alias);

        return new CaseMatchingJson(jsonObjectUtils, aliasNameAliasAQuery);
    }

    CaseMatchingJson getAliasNameAliasBSubquery(final String alias) {
        final String aliasNameAliasBString = fileSystemResourceService
                .getFileFromResourceAsString(ALIAS_NAME_ALIAS_B_BASE);
        final JSONObject aliasNameAliasBQuery = new JSONObject(aliasNameAliasBString);

        final JSONObject match = jsonObjectUtils.findObjectInQuery(
                aliasNameAliasBQuery,
                new JSONPointer(MULTI_MATCH),
                QUERY,
                DECEASED_ALIAS);
        match.put(QUERY, alias);

        return new CaseMatchingJson(jsonObjectUtils, aliasNameAliasBQuery);
    }

    CaseMatchingJson getAliasNameSolsAliasASubquery(final String alias) {
        final String aliasNameSolsAliasAString = fileSystemResourceService
                .getFileFromResourceAsString(ALIAS_NAME_SOLS_ALIAS_A_BASE);
        final JSONObject aliasNameSolsAliasAQuery = new JSONObject(aliasNameSolsAliasAString);

        final JSONObject match = jsonObjectUtils.findObjectInQuery(
                aliasNameSolsAliasAQuery,
                new JSONPointer(MULTI_MATCH),
                QUERY,
                DECEASED_ALIAS);
        match.put(QUERY, alias);

        return new CaseMatchingJson(jsonObjectUtils, aliasNameSolsAliasAQuery);
    }

    CaseMatchingJson getAliasNameSolsAliasBSubquery(final String alias) {
        final String aliasNameSolsAliasBString = fileSystemResourceService
                .getFileFromResourceAsString(ALIAS_NAME_SOLS_ALIAS_B_BASE);
        final JSONObject aliasNameSolsAliasBQuery = new JSONObject(aliasNameSolsAliasBString);

        final JSONObject match = jsonObjectUtils.findObjectInQuery(
                aliasNameSolsAliasBQuery,
                new JSONPointer(MULTI_MATCH),
                QUERY,
                DECEASED_ALIAS);
        match.put(QUERY, alias);

        return new CaseMatchingJson(jsonObjectUtils, aliasNameSolsAliasBQuery);
    }

    List<CaseMatchingJson> getAliasSubqueries(final String alias) {
        final CaseMatchingJson aliasNameA = getAliasNameASubquery(alias);
        final CaseMatchingJson aliasNameB = getAliasNameBSubquery(alias);
        final CaseMatchingJson aliasNameAliasA = getAliasNameAliasASubquery(alias);
        final CaseMatchingJson aliasNameAliasB = getAliasNameAliasBSubquery(alias);
        final CaseMatchingJson aliasNameSolsAliasA = getAliasNameSolsAliasASubquery(alias);
        final CaseMatchingJson aliasNAmeSolsAliasB = getAliasNameSolsAliasBSubquery(alias);

        return List.of(
                aliasNameA,
                aliasNameB,
                aliasNameAliasA,
                aliasNameAliasB,
                aliasNameSolsAliasA,
                aliasNAmeSolsAliasB);
    }

    public List<CaseMatchingJson> getAliasesSubqueries(final List<String> aliases) {
        final List<CaseMatchingJson> collected = new ArrayList<>();
        for (String alias : aliases) {
            collected.addAll(getAliasSubqueries(alias));
        }
        return List.copyOf(collected);
    }

}
