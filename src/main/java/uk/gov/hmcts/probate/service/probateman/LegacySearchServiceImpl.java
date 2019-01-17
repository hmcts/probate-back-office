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
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.service.LegacySearchService;
import uk.gov.hmcts.probate.service.ProbateManService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@Slf4j
@Component
@RequiredArgsConstructor
public class LegacySearchServiceImpl implements LegacySearchService {

    private static final List<CaseType> GRANT_MATCH_TYPES = Arrays.asList(LEGACY);
    private final CaseMatchingService caseMatchingService;
    private final ProbateManService probateManService;
    private final Map<ProbateManType, JpaRepository> repositories;

    @Override
    public List<CollectionMember<CaseMatch>> findLegacyCaseMatches(CaseDetails caseDetails) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(caseDetails);

        List<CaseMatch> caseMatches = new ArrayList<>();
        caseMatches.addAll(caseMatchingService.findCrossMatches(GRANT_MATCH_TYPES, caseMatchingCriteria));

        List<CollectionMember<CaseMatch>> caseMatchesList = new ArrayList();

        caseMatches.forEach(match -> caseMatchesList.add(new CollectionMember<CaseMatch>(null, match)));

        return caseMatchesList;
    }


    @Override
    public List<CollectionMember<CaseMatch>> importLegacyRows(CaseData data) {
        List<CollectionMember<CaseMatch>> rows = data.getLegacySearchResultRows();

        rows.stream().map(CollectionMember::getValue)
                .filter(row -> "YES".equalsIgnoreCase(row.getDoImport()))
                .forEach(row -> importRow(row));
        return rows;
    }

    private void importRow(CaseMatch row) {
        String legacyCaseTypeName = row.getType();
        LegacyCaseType legacyCaseType = LegacyCaseType.getByLegacyCaseTypeName(legacyCaseTypeName);
        String id = row.getId();
        log.info("Importing legacy case into ccd for legacyCaseType=" + legacyCaseTypeName + ", with id=" + id);
        ProbateManType probateManType = ProbateManType.getByLegacyCaseType(legacyCaseType);
        Long legacyId = Long.parseLong(id);
        probateManService.saveToCcd(legacyId, probateManType);
        JpaRepository repository = repositories.get(probateManType);
        Optional<ProbateManModel> probateManModel = repository.findById(legacyId);
        if (probateManModel.isPresent()) {
            probateManModel.get().setDnmInd("Y");
            log.info("Updating legacy case id=" + id + " for probateManType=" + probateManType);
            repository.save(probateManModel.get());
        } else {
            log.info("Case cannot be found when updating legacy case id=" + id + " for probateManType=" + probateManType);
        }
    }

}
