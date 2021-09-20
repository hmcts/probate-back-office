package uk.gov.hmcts.probate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = LifeEventCallbackResponseService.class)
public class LifeEventCallbackResponseServiceTest {

    @Autowired
    LifeEventCallbackResponseService lifeEventCallbackResponseService;
    
    @MockBean
    LifeEventService lifeEventService;
    @MockBean
    CallbackResponseTransformer callbackResponseTransformer;
    
    
    @Test
    public void shouldAddDeathRecordToCallbackResponse() {
        final DeathRecord deathRecord = mock(DeathRecord.class);
        when(lifeEventService.getDeathRecordById(eq(500035096))).thenReturn(deathRecord);
        
        CallbackResponse response = CallbackResponse.builder().data(ResponseCaseData.builder().build()).build();
        when(callbackResponseTransformer.updateTaskList(any(CallbackRequest.class))).thenReturn(response);
        
        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
            .deathRecordSystemNumber(500035096).build(),
            null, null);

        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        
        final CallbackResponse callbackResponse = lifeEventCallbackResponseService.getDeathRecordById(callbackRequest);
        
        assertSame(callbackResponse.getData().getDeathRecord(),deathRecord);
        assertSame(callbackResponse.getData().getDeathRecords().get(0).getValue(),deathRecord);
        assertEquals(callbackResponse.getData().getDeathRecords().size(), 1);
    }
}
