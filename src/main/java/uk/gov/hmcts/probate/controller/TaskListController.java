package uk.gov.hmcts.probate.controller;

import uk.gov.hmcts.lifeevents.client.model.Deceased;
import uk.gov.hmcts.lifeevents.client.model.V1Death;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.LifeEventService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.stream.Collectors.toList;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/tasklist")
public class TaskListController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final LifeEventService lifeEventService;

    @PostMapping(path = "/update")
    public ResponseEntity<CallbackResponse> update(@RequestBody CallbackRequest request) {
        final CallbackResponse body = callbackResponseTransformer.updateTaskList(request);
        addDeathRecords(body.getData());
        return ResponseEntity.ok(body);
    }

    private void addDeathRecords(ResponseCaseData caseData) {
        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final String deceasedDateOfDeath = caseData.getDeceasedDateOfDeath();
        final List<V1Death> deathRecords = lifeEventService.findDeathRecords(deceasedForenames, deceasedSurname, deceasedDateOfDeath);
        caseData.setDeathRecords(mapDeathRecords(deathRecords));
    }

    private List<CollectionMember<DeathRecord>> mapDeathRecords(List<V1Death> deathRecords) {
        return deathRecords
                .stream()
                .map(this::mapDeathRecord)
                .collect(toList());
    }

    private CollectionMember<DeathRecord> mapDeathRecord (V1Death deathRecord) {
        final Deceased deceased = deathRecord.getDeceased();

        DeathRecord dr = DeathRecord.
                builder()
                .systemNumber(deathRecord.getId())
                .name(String.format("%s %s", deceased.getForenames(), deceased.getSurname()))
                .dateOfBirth(deceased.getDateOfBirth())
                .sex(deceased.getSex().getValue())
                .address(deceased.getAddress())
                .dateOfDeath(deceased.getDateOfDeath())
                .build();

        return  new CollectionMember<>(null,dr);

    }
}
