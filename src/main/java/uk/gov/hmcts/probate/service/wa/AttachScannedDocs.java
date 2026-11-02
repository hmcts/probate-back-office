package uk.gov.hmcts.probate.service.wa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.user.UserInfoService;

import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttachScannedDocs implements CreateTaskProcessor {

    private static final String CASEWORKER_PROBATE_SYSTEM_UPDATE = "caseworker-probate-systemupdate";
    private static final String IDAM_SERVICE_ACCOUNT = "idam-service-account";
    public static final String ATTACH_SCANNED_DOCS = "attachScannedDocs";
    private final UserInfoService caseworkerInfo;

    @Override
    public String getEventId() {
        return ATTACH_SCANNED_DOCS;
    }

    @Override
    public void process(String authToken, CallbackRequest callbackRequest, ResponseCaseData responseCaseData) {
        String createTask = caseworkerInfo.getCaseworkerInfoIncludingSystemUsers()
                .map(userInfo -> {
                    List<String> roles = userInfo.getRoles();
                    boolean shouldCreateTask = roles.contains(CASEWORKER_PROBATE_SYSTEM_UPDATE)
                            || roles.contains(IDAM_SERVICE_ACCOUNT);
                    log.info("Processing attach scanned docs. Roles: {}, createTask: {}", roles, shouldCreateTask);
                    return shouldCreateTask ? YES : NO;
                })
                .orElse(NO);
        log.info("Processing attach scanned docs set createTask : {} ", createTask);
        responseCaseData.setCreateTask(createTask);
    }
}
