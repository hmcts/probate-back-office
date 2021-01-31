package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.STATE_GRANT_TYPE_CREATED;

@Service
@Slf4j
@AllArgsConstructor
public class SolicitorLegalStatementNextStepsTransformer {
    public static final String STATE_SOLS_APP_CREATED_LABEL = "Deceased Details";
    public static final String GRANT_TYPE_PROBATE_LABEL = "Grant of probate where the deceased left a will";
    public static final String GRANT_TYPE_INTESTACY_LABEL = "Letters of administration where the deceased left no will";
    public static final String GRANT_TYPE_ADMON_LABEL =
        "Letters of administration with will annexed where the deceased left a will but none of the executors can "
            + "apply";


    public void transformLegalStatmentAmendStates(@Valid CaseDetails caseDetails,
                                                  ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {
        responseCaseDataBuilder.solsAmendLegalStatmentSelect(
            getAppropriateNextEventsForSolicitorLegalStatementAmend(caseDetails.getData()));
    }

    private DynamicList getAppropriateNextEventsForSolicitorLegalStatementAmend(CaseData caseData) {
        List<DynamicListItem> listItems = new ArrayList<>();
        listItems.add(getDeceasedDetailsListItem());
        switch (caseData.getSolsWillType()) {
            case GRANT_TYPE_PROBATE:
                listItems.add(getProbateListItem());
                break;
            case GRANT_TYPE_INTESTACY:
                listItems.add(getIntestacyListItem());
                break;
            case GRANT_TYPE_ADMON:
                listItems.add(getAdmonListItem());
                break;
            default:
                log.error("Solicitor will type not found : " + caseData.getSolsWillType());
                throw new BadRequestException("Solicitor will type not found : " + caseData.getSolsWillType());
        }

        return DynamicList.builder()
            .listItems(listItems)
            .value(DynamicListItem.builder().build())
            .build();
    }

    private DynamicListItem getProbateListItem() {
        return buildListItem(GRANT_TYPE_PROBATE, GRANT_TYPE_PROBATE_LABEL);
    }

    private DynamicListItem getIntestacyListItem() {
        return buildListItem(GRANT_TYPE_INTESTACY, GRANT_TYPE_INTESTACY_LABEL);
    }

    private DynamicListItem getAdmonListItem() {
        return buildListItem(GRANT_TYPE_ADMON, GRANT_TYPE_ADMON_LABEL);
    }

    private DynamicListItem getDeceasedDetailsListItem() {
        return buildListItem(STATE_GRANT_TYPE_CREATED, STATE_SOLS_APP_CREATED_LABEL);
    }

    private DynamicListItem buildListItem(String code, String label) {
        return DynamicListItem.builder()
            .code(code)
            .label(label)
            .build();
    }

}
