package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;


import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
public class HasValidMatchesDefaulter {
    private static final List<String> VALID_CASE_TYPE_LIST = List.of(
            "Grant of Representation",
            "Caveats",
            "Legacy CAVEAT",
            "Legacy LEGACY APPLICATION",
            "Legacy LEGACY GRANT");

    public String defaultHasValidMatches(CaseData caseData) {
        final List<CollectionMember<CaseMatch>> storedMatches = caseData.getCaseMatches();
        boolean hasValidMatches = storedMatches != null && storedMatches.stream()
                .map(CollectionMember::getValue)
                .anyMatch(this::isValidMatch);
        return hasValidMatches ? YES : NO;
    }

    private boolean isValidMatch(CaseMatch match) {
        return match != null
                && YES.equalsIgnoreCase(match.getValid())
                && match.getType() != null
                && VALID_CASE_TYPE_LIST.contains(match.getType());
    }
}
