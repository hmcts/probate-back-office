package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseMatchingService {

    private static final String TEMPLATE_DIRECTORY = "templates/elasticsearch/caseMatching/";
    private static final String ES_QUERY = "main_query.json";
    private static final String ES_ALIASES_SUB_QUERY = "aliases_sub_query.json";
    private static final String ES_ALIASES_TO_ALIASES_SUB_QUERY = "aliases_to_aliases_sub_query.json";
    private static final String ES_DECEASED_DOB_SUB_QUERY = "deceased_dob_sub_query.json";

    private final FileSystemResourceService fileSystemResourceService;
    private final ElasticSearchService elasticSearchService;
    private final CaseMatchBuilderService caseMatchBuilderService;

    public List<CaseMatch> findMatches(CaseType caseType, CaseMatchingCriteria criteria) {

        String optionalAliasesToNameQuery = criteria.getDeceasedAliases().stream()
                .map(alias -> getAliasesToNameSubQueryTemplate().replace(":deceasedAliases", alias))
                .collect(Collectors.joining());

        String optionalAliasesToAliasesQuery = criteria.getDeceasedAliases().stream()
                .map(alias -> getAliasesToAliasesSubQueryTemplate().replace(":deceasedAliases", alias))
                .collect(Collectors.joining());

        String optionalDeceasedDateOfBirth = Optional.ofNullable(criteria.getDeceasedDateOfBirthRaw())
                .map(data -> getDoBTemplate().replace(":deceasedDateOfBirth", criteria.getDeceasedDateOfBirth()))
                .orElse("");

        String jsonQuery = getQueryTemplate()
                .replace(":deceasedForenames", criteria.getDeceasedForenames())
                .replace(":deceasedSurname", criteria.getDeceasedSurname())
                .replace(":deceasedFullName", criteria.getDeceasedFullName())
                .replace(":deceasedDateOfBirth", criteria.getDeceasedDateOfBirth())
                .replace(":deceasedDateOfDeath", criteria.getDeceasedDateOfDeath())
                .replace(":optionalDeceasedDateOfBirth", optionalDeceasedDateOfBirth)
                .replace(":optionalAliasesToNameQuery", optionalAliasesToNameQuery)
                .replace(":optionalAliasesToAliasesQuery", optionalAliasesToAliasesQuery);

        MatchedCases matchedCases = elasticSearchService.runQuery(caseType, jsonQuery);

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

    private String getQueryTemplate() {
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

    private String getDoBTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY
                + ES_DECEASED_DOB_SUB_QUERY);
    }
}
