package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LifeEventCallbackResponseService.class)
class LifeEventCallbackResponseServiceTest {

    @Autowired
    LifeEventCallbackResponseService lifeEventCallbackResponseService;

    @MockitoBean
    LifeEventService lifeEventService;
    @MockitoBean
    CallbackResponseTransformer callbackResponseTransformer;

    private CallbackResponse response;
    private List<CollectionMember<DeathRecord>> deathRecords;

    @BeforeEach
    public void setup() {
        response = CallbackResponse.builder().data(ResponseCaseData.builder().build()).build();
        when(callbackResponseTransformer.updateTaskList(any(CallbackRequest.class), any())).thenReturn(response);
        deathRecords = mock(List.class);
        when(deathRecords.size()).thenReturn(5);
    }

    @Test
    void shouldSetNumberOfDeathRecordsOnCallbackResponse() {
        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
            .deathRecords(deathRecords)
            .build(),
            null, null);

        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        final CallbackResponse callbackResponse =
            lifeEventCallbackResponseService.setNumberOfDeathRecords(callbackRequest);
        assertEquals(callbackResponse.getData().getNumberOfDeathRecords(), 5);
    }

    @Test
    void shouldDeathRecordsOnCallbackResponse() {
        final CaseDetails caseDetails = mock(CaseDetails.class);
        when(lifeEventService.getDeathRecordsByNamesAndDate(caseDetails)).thenReturn(deathRecords);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        final CallbackResponse callbackResponse =
            lifeEventCallbackResponseService.getDeathRecordsByNamesAndDate(callbackRequest);
        assertEquals(callbackResponse.getData().getNumberOfDeathRecords(), 5);
        assertEquals(callbackResponse.getData().getDeathRecords(), deathRecords);
    }
}
