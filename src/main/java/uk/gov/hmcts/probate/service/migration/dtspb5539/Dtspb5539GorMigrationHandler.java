package uk.gov.hmcts.probate.service.migration.dtspb5539;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.service.CcdSupplementaryDataService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.migration.GorMigrationHandler;

import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaseDataTransformer;
import uk.gov.hmcts.probate.validator.NumberOfApplyingExecutorsValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;

import java.util.List;

@Component
public class Dtspb5539GorMigrationHandler implements GorMigrationHandler {

    private final CaseDataTransformer caseDataTransformer;
    private final NumberOfApplyingExecutorsValidationRule numberOfApplyingExecutorsValidationRule;
    private final EventValidationService eventValidationService;
    private final List<ValidationRule> allCaseworkerAmendAndCreateValidationRules;
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CcdSupplementaryDataService ccdSupplementaryDataService;

    public Dtspb5539GorMigrationHandler(CaseDataTransformer caseDataTransformer,
                                        NumberOfApplyingExecutorsValidationRule numberOfApplyingExecutorsValidationRule,
                                        EventValidationService eventValidationService,
                                        List<ValidationRule> allCaseworkerAmendAndCreateValidationRules,
                                        CallbackResponseTransformer callbackResponseTransformer,
                                        CcdSupplementaryDataService ccdSupplementaryDataService) {
        this.caseDataTransformer = caseDataTransformer;
        this.numberOfApplyingExecutorsValidationRule = numberOfApplyingExecutorsValidationRule;
        this.eventValidationService = eventValidationService;
        this.allCaseworkerAmendAndCreateValidationRules = allCaseworkerAmendAndCreateValidationRules;
        this.callbackResponseTransformer = callbackResponseTransformer;
        this.ccdSupplementaryDataService = ccdSupplementaryDataService;
    }

    @Override
    public CallbackRequest migrate(CallbackRequest callbackRequest,
                                   JSONObject migrationData) {

        caseDataTransformer.transformFormCaseData(callbackRequest);

        ccdSupplementaryDataService.submitSupplementaryDataToCcd(
                callbackRequest.getCaseDetails().getId().toString());

        return callbackRequest;
    }
}
