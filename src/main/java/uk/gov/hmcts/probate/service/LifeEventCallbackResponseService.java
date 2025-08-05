package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.List;
import java.util.Optional;

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

    public CallbackResponse setNumberOfDeathRecords(CallbackRequest request) {
        final List<CollectionMember<DeathRecord>> deathRecords = request.getCaseDetails().getData().getDeathRecords();
        final CallbackResponse response = callbackResponseTransformer.updateTaskList(request, Optional.empty());
        response.getData().setNumberOfDeathRecords(deathRecords == null ? null : deathRecords.size());
        return response;
    }

    public CallbackResponse getDeathRecordsByNamesAndDate(CallbackRequest request) {
        final CaseDetails caseDetails = request.getCaseDetails();
        final List<CollectionMember<DeathRecord>> deathRecords
            = lifeEventService.getDeathRecordsByNamesAndDate(caseDetails);
        log.info("Death records found: {}", deathRecords);
        final CallbackResponse response = callbackResponseTransformer.updateTaskList(request, Optional.empty());
        response.getData().setDeathRecords(deathRecords);
        response.getData().setNumberOfDeathRecords(deathRecords.size());
        return response;
    }
}
