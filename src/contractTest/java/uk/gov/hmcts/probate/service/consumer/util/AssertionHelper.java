package uk.gov.hmcts.probate.service.consumer.util;

import java.util.List;
import java.util.Map;

import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;


public final class AssertionHelper {

    public static void assertCaseDetails(final CaseDetails caseDetails) {
        assertTrue(caseDetails.getData().size() > 0);
        Map<String,Object> caseDataMap = caseDetails.getData();
        assertThat(caseDataMap.get("applicationType"),is("Personal"));
        assertThat(caseDataMap.get("deceasedAddress"),notNullValue());
        assertThat(caseDataMap.get("applicationSubmittedDate"), notNullValue());
        assertThat(caseDataMap.get("primaryApplicantEmailAddress"), notNullValue());
        assertThat(caseDataMap.get("deceasedSurname"),notNullValue()) ;

    }

    public static void assertBackOfficeCaseData(final CaseDetails caseDetails) {

        Map<String,Object> caseDataMap = caseDetails.getData();
        assertThat(caseDataMap.get("legalStatement"), instanceOf(Map.class));
        assertThat(caseDataMap.get("legalDeclarationJson"), instanceOf(String.class));
        assertThat(caseDataMap.get("probateNotificationsGenerated"), instanceOf(List.class));
    }
}