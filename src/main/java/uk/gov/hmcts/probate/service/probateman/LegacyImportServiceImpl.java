package uk.gov.hmcts.probate.service.probateman;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.ProbateManModel;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.service.LegacyImportService;
import uk.gov.hmcts.probate.service.ProbateManService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LegacyImportServiceImpl implements LegacyImportService {

    private static final String DO_IMPORT_YES = "YES";
    public static final String DNM_IND_YES = "Y";

    private final ProbateManService probateManService;
    private final Map<ProbateManType, JpaRepository> repositories;

    @Override
    public List<CaseMatch> importLegacyRows(List<CollectionMember<CaseMatch>> rows) {
        List<CaseMatch> importableRows = rows.stream().map(CollectionMember::getValue)
                .filter(this::canImportRow)
                .collect(Collectors.toList());

        List<CaseMatch> updatedRows = new ArrayList<>();
        for (CaseMatch caseMatch : importableRows) {
            updatedRows.add(importRow(caseMatch));
        }

        return updatedRows;
    }

    private CaseMatch importRow(CaseMatch row) {
        String legacyCaseTypeName = row.getType();
        LegacyCaseType legacyCaseType = LegacyCaseType.getByLegacyCaseTypeName(legacyCaseTypeName);
        String id = getLegacyTableId(row.getLegacyCaseViewUrl());
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
            updateCaseMatch(row, probateManModelOptional.get());
        } else {
            log.info("Case cannot be found when updating legacy case id=" + id + " for probateManType=" + probateManType);
        }
        return row;
    }

    private void updateCaseMatch(CaseMatch row, ProbateManModel probateManModel) {
        row.setCaseLink(CaseLink.builder().caseReference(probateManModel.getCcdCaseNo()).build());
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

    private boolean canImportRow(CaseMatch caseMatch) {
        return DO_IMPORT_YES.equalsIgnoreCase(caseMatch.getDoImport())
                && hasCaseReference(caseMatch)
                && !StringUtils.isEmpty(caseMatch.getLegacyCaseViewUrl());
    }

    private boolean hasCaseReference(CaseMatch row) {
        return row.getCaseLink() == null
                || row.getCaseLink().getCaseReference() == null
                || row.getCaseLink().getCaseReference().equals("");
    }

    private String getLegacyTableId(String legacyCaseViewUrl) {
        return legacyCaseViewUrl.substring(legacyCaseViewUrl.lastIndexOf('/') + 1);
    }

}
