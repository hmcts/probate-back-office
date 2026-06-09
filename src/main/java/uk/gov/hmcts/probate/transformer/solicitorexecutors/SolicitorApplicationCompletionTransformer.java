package uk.gov.hmcts.probate.transformer.solicitorexecutors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.DateFormatterService;
import uk.gov.hmcts.probate.service.solicitorexecutor.ExecutorListMapperService;

import java.math.BigDecimal;
import java.util.List;

import static uk.gov.hmcts.probate.model.ApplicationState.CASE_PRINTED;
import static uk.gov.hmcts.probate.model.Constants.CASE_TYPE_GRANT_OF_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.TITLE_AND_CLEARING_NONE_OF_THESE;

@Component
@Slf4j
// Handles some casedata mappings for when a solicitor application becomes a case
// for caseworker or solicitor journeys
public class SolicitorApplicationCompletionTransformer extends LegalStatementExecutorTransformer {

    private static final String NOT_APPLICABLE = "NotApplicable";

    public SolicitorApplicationCompletionTransformer(
            final ExecutorListMapperService executorListMapperService,
            final DateFormatterService dateFormatterService) {
        super(executorListMapperService, dateFormatterService);
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

    public void clearPrimaryApplicantWhenNotInNoneOfTheseTitleAndClearingType(CaseDetails caseDetails) {

        final var caseId = caseDetails.getId();
        final var caseData = caseDetails.getData();

        final var titleAndClearingType = caseData.getTitleAndClearingType();
        final var caseType = caseData.getCaseType();

        final var primaryApplicantApplying = caseData.isPrimaryApplicantApplying();
        final var isNotNoneOfTheseTCT = titleAndClearingType != null
                && !TITLE_AND_CLEARING_NONE_OF_THESE.equalsIgnoreCase(titleAndClearingType);
        final var isGrantOfProbate = CASE_TYPE_GRANT_OF_PROBATE.equalsIgnoreCase(caseType);

        if (isNotNoneOfTheseTCT
                && primaryApplicantApplying
                && isGrantOfProbate) {
            log.info("In GrantOfProbate case {} we have primary applicant applying for non-NoneOfThese "
                    + "TitleAndClearingType {}, clear PrimaryApplicant fields",
                    caseId,
                    titleAndClearingType);

            caseData.clearPrimaryApplicant();
        }
    }

    public void clearAdditionalExecutorWhenUpdatingApplicantDetails(CaseDetails caseDetails) {
        if (NO.equals(caseDetails.getData().getOtherExecutorExists())
                && caseDetails.getData().getSolsAdditionalExecutorList() != null) {
            caseDetails.getData().clearAdditionalExecutorList();
        }
    }
}
