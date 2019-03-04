package uk.gov.hmcts.probate.service.probateman;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.CaseSearchService;
import uk.gov.hmcts.probate.service.LegacySearchService;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@Slf4j
@Component
@RequiredArgsConstructor
public class LegacySearchServiceImpl implements LegacySearchService {

    private final CaseSearchService caseSearchService;

    @Override
    public List<CollectionMember<CaseMatch>> findLegacyCaseMatches(CaseDetails caseDetails) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(caseDetails);
        return caseSearchService.findCases(LEGACY, caseMatchingCriteria)
                .stream()
                .map(match -> new CollectionMember<>(null, match))
                .collect(Collectors.toList());
    }
}
