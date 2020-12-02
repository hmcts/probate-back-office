package uk.hmcts.reform.probate.backoffice.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.Map;

public final class AssertionHelper {

    public static void assertCaseDetails(final CaseDetails caseDetails, boolean isWelsh, boolean executorsApplying) {
        assertTrue(caseDetails.getData().size() > 0);

        Map<String,Object> caseDataMap = caseDetails.getData();

        assertThat(caseDataMap.get("applicationType"),is("Personal"));
        assertThat(caseDataMap.get("deceasedAddress"),notNullValue());
        assertThat(caseDataMap.get("applicationSubmittedDate"), notNullValue());
        assertThat(caseDataMap.get("primaryApplicantEmailAddress"), notNullValue());
        assertThat(caseDataMap.get("deceasedSurname"),notNullValue()) ;

        // TODO Other Assertions that are common to all the Responses received by probate from the CCD API
        if(isWelsh){

        }

        if(executorsApplying){

        }

    }
}
