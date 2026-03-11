package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.CaseMatchingJsonService.CaseMatchingJson;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CaseMatchingService {

    private static final String TEMPLATE_DIRECTORY = "templates/elasticsearch/caseMatching/";
    private static final String ES_QUERY = "main_query.json";
    private static final String ES_ALIASES_SUB_QUERY = "aliases_sub_query.json";
    private static final String ES_ALIASES_TO_ALIASES_SUB_QUERY = "aliases_to_aliases_sub_query.json";
    private static final String ES_ALIASES_TO_ALIASES_NAME_LIST_SUB_QUERY = "aliases_to_aliases_list_sub_query.json";
    private static final String ES_DECEASED_DOB_SUB_QUERY = "deceased_dob_sub_query.json";
    private static final String ES_DECEASED_DOD_SUB_QUERY = "deceased_dod_sub_query.json";


    private final FileSystemResourceService fileSystemResourceService;
    private final ElasticSearchService elasticSearchService;
    private final CaseMatchBuilderService caseMatchBuilderService;
    private final CaseMatchingJsonService caseMatchingJsonService;

    public CaseMatchingService(
            final FileSystemResourceService fileSystemResourceService,
            final ElasticSearchService elasticSearchService,
            final CaseMatchBuilderService caseMatchBuilderService,
            final CaseMatchingJsonService caseMatchingJsonService) {
        this.fileSystemResourceService = Objects.requireNonNull(fileSystemResourceService);
        this.elasticSearchService = Objects.requireNonNull(elasticSearchService);
        this.caseMatchBuilderService = Objects.requireNonNull(caseMatchBuilderService);
        this.caseMatchingJsonService = Objects.requireNonNull(caseMatchingJsonService);
    }

    MatchedCases oldFindMatches(final CaseType caseType, final CaseMatchingCriteria criteria) {
        String optionalAliasesToNameQuery = criteria.getDeceasedAliases().stream()
                .map(alias -> getAliasesToNameSubQueryTemplate().replace(":deceasedAliases", alias))
                .collect(Collectors.joining());

        String optionalAliasesToAliasesQuery = criteria.getDeceasedAliases().stream()
                .map(alias -> getAliasesToAliasesSubQueryTemplate().replace(":deceasedAliases", alias))
                .collect(Collectors.joining());

        String optionalAliasesToAliasesNameListQuery = criteria.getDeceasedAliases().stream()
                .map(alias -> getAliasesToAliasesNameListSubQueryTemplate().replace(":deceasedAliases", alias))
                .collect(Collectors.joining());

        String optionalDeceasedDateOfBirth = Optional.ofNullable(criteria.getDeceasedDateOfBirthRaw())
                .map(data -> getDoBTemplate().replace(":deceasedDateOfBirth", criteria.getDeceasedDateOfBirth()))
                .orElse("");

        String optionalDeceasedDateOfDeath = Optional.ofNullable(criteria.getDeceasedDateOfDeathRaw())
                .map(data -> getDoDTemplate().replace(":deceasedDateOfDeath", criteria.getDeceasedDateOfDeath()))
                .orElse("");

        String stringQuery = getQueryTemplateString()
                .replace(":deceasedForenames", criteria.getDeceasedForenames())
                .replace(":deceasedSurname", criteria.getDeceasedSurname())
                .replace(":deceasedFullName", criteria.getDeceasedFullName())
                .replace(":optionalDeceasedDateOfBirth", optionalDeceasedDateOfBirth)
                .replace(":optionalDeceasedDateOfDeath", optionalDeceasedDateOfDeath)
                .replace(":optionalAliasesToNameQuery", optionalAliasesToNameQuery)
                .replace(":optionalAliasesToAliasesQuery", optionalAliasesToAliasesQuery)
                .replace(":optionalAliasesToAliasesNameListQuery", optionalAliasesToAliasesNameListQuery);

        return elasticSearchService.runQuery(caseType, stringQuery);
    }

    MatchedCases newFindMatches(final CaseType caseType, final CaseMatchingCriteria criteria) {
        final CaseMatchingJson baseQuery = caseMatchingJsonService.getBaseQuery();

        final CaseMatchingJson withForenames = baseQuery.withDeceasedForenames(criteria.getDeceasedForenames());

        final CaseMatchingJson withSurname = withForenames.withDeceasedSurname(criteria.getDeceasedSurname());

        final CaseMatchingJson withFullName = withSurname.withDeceasedFullname(criteria.getDeceasedFullName());

        final Optional<CaseMatchingJson> birthSubquery = caseMatchingJsonService.getDateOfBirthSubquery(
                criteria.getDeceasedDateOfBirthRaw());
        final CaseMatchingJson withBirth = withFullName.withDateOfBirth(birthSubquery);

        final Optional<CaseMatchingJson> deathSubquery = caseMatchingJsonService.getDateOfDeathSubquery(
                criteria.getDeceasedDateOfDeathRaw());
        final CaseMatchingJson withDeath = withBirth.withDateOfDeath(deathSubquery);

        final List<CaseMatchingJson> aliasesSubQueries = caseMatchingJsonService.getAliasesSubqueries(
                criteria.getDeceasedAliases());
        final CaseMatchingJson withAliases = withDeath.withAliases(aliasesSubQueries);

        final JSONObject jsonQuery = withAliases.stealJson().orElseThrow();

        return elasticSearchService.runJsonQuery(caseType, jsonQuery);
    }

    public List<CaseMatch> findMatches(CaseType caseType, CaseMatchingCriteria criteria) {

        log.info("running new findMatches for case type {}", caseType);
        final MatchedCases newMatchedCases = newFindMatches(caseType, criteria);
        log.info("completed new findMatches for case type {}", caseType);
        final MatchedCases matchedCases = oldFindMatches(caseType, criteria);
        log.info("completed old findMatches for case type {}", caseType);

        String caseIds = matchedCases.getCases().stream()
                .map(c -> Optional.ofNullable(c.getId())
                        .map(Object::toString)
                        .orElse("NoCaseID"))
                .collect(Collectors.joining(", "));
        log.info("Case ID: " + criteria.getId() + " caseType: " + caseType + " case matching findMatches: " + caseIds);

        return matchedCases.getCases().stream()
                .filter(c -> c.getId() == null || !criteria.getId().equals(c.getId()))
                .map(caseMatchBuilderService::buildCaseMatch)
                .collect(Collectors.toList());
    }

    public List<CaseMatch> findCrossMatches(List<CaseType> caseTypes, CaseMatchingCriteria criteria) {
        return caseTypes.stream()
                .map(caseType -> findMatches(caseType, criteria))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String getQueryTemplateString() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY + ES_QUERY);
    }

    private String getAliasesToNameSubQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_ALIASES_SUB_QUERY);
    }

    private String getAliasesToAliasesSubQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_ALIASES_TO_ALIASES_SUB_QUERY);
    }

    private String getAliasesToAliasesNameListSubQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_ALIASES_TO_ALIASES_NAME_LIST_SUB_QUERY);
    }

    private String getDoBTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_DECEASED_DOB_SUB_QUERY);
    }

    private String getDoDTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_DECEASED_DOD_SUB_QUERY);
    }
}
