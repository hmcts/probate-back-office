package uk.gov.hmcts.probate.service;

import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.util.List;

public interface LegacyImportService {

    boolean areLegacyRowsValidToImport(List<CollectionMember<CaseMatch>> rows);

    List<CaseMatch> importLegacyRows(List<CollectionMember<CaseMatch>> rows);
}
