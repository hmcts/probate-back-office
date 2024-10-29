package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.FeatureToggleService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.math.BigDecimal;
import java.util.List;

import static uk.gov.hmcts.probate.model.ApplicationState.CASE_PRINTED;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Component
@Slf4j
// Handles some casedata mappings for when a solicitor application becomes a case
// for caseworker or solicitor journeys
public class SolicitorApplicationCompletionTransformer extends LegalStatementExecutorTransformer {

    private final FeatureToggleService featureToggleService;

    private static final String NOT_APPLICABLE = "NotApplicable";

    public SolicitorApplicationCompletionTransformer(
            final ExecutorListMapperService executorListMapperService,
            final DateFormatterService dateFormatterService,
            final FeatureToggleService featureToggleService) {
        super(executorListMapperService, dateFormatterService);
        this.featureToggleService = featureToggleService;
    }

    /**
     * Map all executors into executors applying and executors not applying lists for the solicitor legal statement.
     */
    public void mapSolicitorExecutorFieldsOnCompletion(CaseData caseData) {

        mapSolicitorExecutorFieldsToCaseworkerExecutorFields(caseData);
        formatFields(caseData);
        createLegalStatementExecutorListsFromTransformedLists(caseData);
    }

    public void mapSolicitorExecutorFieldsOnAppDetailsComplete(CaseData caseData) {
        if (isSolicitorApplying(caseData)) {
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying = createCaseworkerApplyingList(caseData);
            mapExecutorToPrimaryApplicantFields(execsApplying.get(0).getValue(), caseData);
        } else if (!isSolicitorApplying(caseData) && isSolicitorNamedInWillAsAnExecutor(caseData)) {
            createCaseworkerApplyingList(caseData);
            mapExecutorToPrimaryApplicantFieldsNotApplying(caseData);
        }
        formatFields(caseData);
        mapSolicitorExecutorFieldsToLegalStatementExecutorFields(caseData);
    }

    public void eraseCodicilAddedDateIfWillHasNoCodicils(CaseData caseData) {
        if (NO.equals(caseData.getWillHasCodicils())) {
            caseData.setCodicilAddedDateList(null);
            caseData.setCodicilAddedFormattedDateList(null);
        }
    }

    public void setFieldsOnServiceRequest(CaseDetails caseDetails, BigDecimal totalAmount) {
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            caseDetails.getData().setPaymentTaken(NOT_APPLICABLE);
            caseDetails.setState(CASE_PRINTED.getId());
        }
    }
}
