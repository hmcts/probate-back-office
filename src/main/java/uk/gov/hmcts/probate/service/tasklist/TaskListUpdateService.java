package uk.gov.hmcts.probate.service.tasklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;
import uk.gov.hmcts.probate.model.ccd.tasklist.Alert;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskListUpdateService {

    public final TaskListRenderService taskListRendererService;

    public ResponseCaseDataBuilder generateTaskList(CaseDetails caseDetails, ResponseCaseDataBuilder builder) {

        String newTaskList = "";

//        if (caseDetails.getState().equals("BoCaseStopped")) {
            Alert caseStoppedAlert = Alert.builder()
                    .inset("Case stopped")
                    .body("The case was stopped on <date> for one of two reasons:\n\n<list>\n\nYou will be notified by email " +
                            "if we need any information from you to progress the case.\n\nOnly contact the CTSC " +
                            "staff if your case has been stopped for 4 weeks or more and you have not received any " +
                            "communication since then.")
                    .date("09/10/2020")
                    .build()
                    .withList(List.of("an internal review is needed", "further information" +
                            " from the applicant or solicitor is needed"));
            newTaskList = taskListRendererService.render(caseStoppedAlert);
//        }

        builder.taskList(newTaskList);

        return builder;
    }
}
