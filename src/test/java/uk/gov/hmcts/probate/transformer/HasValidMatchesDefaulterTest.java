package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.NO;

class HasValidMatchesDefaulterTest {
    private HasValidMatchesDefaulter hasValidMatchesDefaulter;

    @BeforeEach
    void setUp() {
        hasValidMatchesDefaulter = new HasValidMatchesDefaulter();
    }

    @Test
    void returnsYesWhenValidMatchExists() {
        CaseMatch validMatch = CaseMatch.builder()
                .id("someId")
                .type("GrantOfRepresentation")
                .valid(YES)
                .build();
        CollectionMember<CaseMatch> member = new CollectionMember<>(null, validMatch);
        CaseData caseData = CaseData.builder()
                .caseMatches(List.of(member))
                .build();
        assertEquals(YES, hasValidMatchesDefaulter.defaultHasValidMatches(caseData));
    }

    @Test
    void returnsNoWhenNoValidMatchExists() {
        CaseMatch nonMatch = CaseMatch.builder()
                .id("someId")
                .type("GrantOfRepresentation")
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
                .type("WillLodgement")
                .valid(YES)
                .build();
        CollectionMember<CaseMatch> member = new CollectionMember<>(null, invalidMatch);
        CaseData caseData = CaseData.builder()
                .caseMatches(List.of(member))
                .build();
        assertEquals(NO, hasValidMatchesDefaulter.defaultHasValidMatches(caseData));
    }
}

