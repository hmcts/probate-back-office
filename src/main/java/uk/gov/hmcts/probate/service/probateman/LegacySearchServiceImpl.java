package uk.gov.hmcts.probate.service.probateman;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.ProbateManModel;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.service.LegacyCaseMatchingService;
import uk.gov.hmcts.probate.service.LegacySearchService;
import uk.gov.hmcts.probate.service.ProbateManService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@Slf4j
@Component
@RequiredArgsConstructor
public class LegacySearchServiceImpl implements LegacySearchService {

    private static final List<CaseType> GRANT_MATCH_TYPES = Arrays.asList(LEGACY);
    private static final String DO_IMPORT_YES = "YES";
    public static final String DNM_IND_YES = "Y";

    private final LegacyCaseMatchingService legacyCaseMatchingService;
    private final ProbateManService probateManService;
    private final Map<ProbateManType, JpaRepository> repositories;

    @Override
    public List<CollectionMember<CaseMatch>> findLegacyCaseMatches(CaseDetails caseDetails) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(caseDetails);
        return legacyCaseMatchingService.findCrossMatches(GRANT_MATCH_TYPES, caseMatchingCriteria)
            .stream()
            .map(match -> new CollectionMember<>(null, match))
            .collect(Collectors.toList());
    }

    @Override
    public List<CollectionMember<CaseMatch>> importLegacyRows(CaseData data) {
        List<CollectionMember<CaseMatch>> rows = data.getLegacySearchResultRows();
        rows.stream().map(CollectionMember::getValue)
            .filter(row -> DO_IMPORT_YES.equalsIgnoreCase(row.getDoImport()))
            .forEach(this::importRow);
        return rows;
    }

    private void importRow(CaseMatch row) {
        String legacyCaseTypeName = row.getType();
        LegacyCaseType legacyCaseType = LegacyCaseType.getByLegacyCaseTypeName(legacyCaseTypeName);
        String id = row.getId();
        log.info("Importing legacy case into ccd for legacyCaseType=" + legacyCaseTypeName + ", with id=" + id);
        ProbateManType probateManType = legacyCaseType.getProbateManType();
        Long legacyId = Long.parseLong(id);
        uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetails = probateManService.saveToCcd(legacyId, probateManType);
        String ccdCaseId = caseDetails.getId().toString();
        log.info("Imported legacy case as CCD case, id=" + ccdCaseId);
        JpaRepository repository = repositories.get(probateManType);
        Optional<ProbateManModel> probateManModelOptional = repository.findById(legacyId);
        if (probateManModelOptional.isPresent()) {
            updateLegacyModel(id, probateManType, ccdCaseId, repository, probateManModelOptional.get());
        } else {
            log.info("Case cannot be found when updating legacy case id=" + id + " for probateManType=" + probateManType);
        }
    }

    private void updateLegacyModel(String id, ProbateManType probateManType, String ccdCaseId, JpaRepository repository,
                                   ProbateManModel probateManModel) {
        probateManModel.setDnmInd(DNM_IND_YES);
        probateManModel.setCcdCaseNo(ccdCaseId);
        probateManModel.setLastModified(LocalDateTime.now());
        log.info("Updating legacy case id=" + id + " for probateManType=" + probateManType);
        repository.saveAndFlush(probateManModel);
        log.info("Updated legacy case");
    }
}
