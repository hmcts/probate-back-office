package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class LifeEventCallbackResponseService {

    private CallbackResponseTransformer callbackResponseTransformer;
    private LifeEventService lifeEventService;
    
    @Autowired
    public LifeEventCallbackResponseService(final LifeEventService lifeEventService,
                                            final CallbackResponseTransformer callbackResponseTransformer) {
        this.lifeEventService = lifeEventService;
        this.callbackResponseTransformer = callbackResponseTransformer;
    }

    public CallbackResponse getDeathRecordById(CallbackRequest request) {
        final Integer systemNumber = request.getCaseDetails().getData().getDeathRecordSystemNumber();
        final DeathRecord deathRecord = lifeEventService.getDeathRecordById(systemNumber);
        final CallbackResponse response = callbackResponseTransformer.updateTaskList(request);
        response.getData().setDeathRecord(deathRecord);
        response.getData().setDeathRecords(List.of(new CollectionMember<>(null, deathRecord)));
        return response;
    }
    
    public CallbackResponse setNumberOfDeathRecords(CallbackRequest request) {
        final List<CollectionMember<DeathRecord>> deathRecords = request.getCaseDetails().getData().getDeathRecords();
        final CallbackResponse response = callbackResponseTransformer.updateTaskList(request);
        response.getData().setNumberOfDeathRecords(deathRecords == null ? null : deathRecords.size());
        return response;
    }
}
