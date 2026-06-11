package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

class HasValidMatchesDefaulterTest {
    private HasValidMatchesDefaulter hasValidMatchesDefaulter;

    @BeforeEach
    void setUp() {
        hasValidMatchesDefaulter = new HasValidMatchesDefaulter();
    }

    @ParameterizedTest
    @MethodSource("caseTypes")
    void returnsYesWhenValidMatchExists(String caseType) {
        CaseMatch validMatch = CaseMatch.builder()
                .id("someId")
                .type(caseType)
                .valid(YES)
                .build();

        CollectionMember<CaseMatch> member = new CollectionMember<>(null, validMatch);

        CaseData caseData = CaseData.builder()
                .caseMatches(List.of(member))
                .build();

        assertEquals(YES, hasValidMatchesDefaulter.defaultHasValidMatches(caseData));
    }

    static Stream<String> caseTypes() {
        return Stream.of(
                CaseType.GRANT_OF_REPRESENTATION.getName(),
                CaseType.CAVEAT.getName(),
                CaseType.WILL_LODGEMENT.getName(),
                LegacyCaseType.CAVEAT.getName(),
                LegacyCaseType.GRANT_OF_REPRESENTATION.getName(),
                LegacyCaseType.GRANT_OF_REPRESENTATION_DERIVED.getName(),
                LegacyCaseType.WILL_LODGEMENT.getName()
        );
    }

    @Test
    void returnsNoWhenNoValidMatchExists() {
        CaseMatch nonMatch = CaseMatch.builder()
                .id("someId")
                .type(CaseType.GRANT_OF_REPRESENTATION.getName())
                .valid(NO)
                .build();
        CollectionMember<CaseMatch> member = new CollectionMember<>(null, nonMatch);
        CaseData caseData = CaseData.builder()
                .caseMatches(List.of(member))
                .build();
        assertEquals(NO, hasValidMatchesDefaulter.defaultHasValidMatches(caseData));
    }

    @Test
    void returnsNoWhenCaseMatchesIsNull() {
        CaseData caseData = CaseData.builder()
                .caseMatches(null)
                .build();
        assertEquals(NO, hasValidMatchesDefaulter.defaultHasValidMatches(caseData));
    }

    @Test
    void returnsNoWhenTypeIsNotValid() {
        CaseMatch invalidMatch = CaseMatch.builder()
                .id("someId")
                .type(CaseType.STANDING_SEARCH.getName())
                .valid(YES)
                .build();
        CollectionMember<CaseMatch> member = new CollectionMember<>(null, invalidMatch);
        CaseData caseData = CaseData.builder()
                .caseMatches(List.of(member))
                .build();
        assertEquals(NO, hasValidMatchesDefaulter.defaultHasValidMatches(caseData));
    }

    @Test
    void returnsNoWhenTypeIsNull() {
        CaseMatch invalidMatch = CaseMatch.builder()
                .id("someId")
                .type(null)
                .build();
        CollectionMember<CaseMatch> member = new CollectionMember<>(null, invalidMatch);
        CaseData caseData = CaseData.builder()
                .caseMatches(List.of(member))
                .build();
        assertEquals(NO, hasValidMatchesDefaulter.defaultHasValidMatches(caseData));
    }
}