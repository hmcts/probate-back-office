package uk.gov.hmcts.probate.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.payments.pba.PBARetrievalService;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Service
public class SolicitorPBADefaulter {
    @Autowired
    PBARetrievalService pbaRetrievalService;

    public void defaultFeeAccounts(CaseData data,
                                   ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder,
                                   String authToken) {
        DynamicList pbas = getPBAAccounts(authToken);
        responseCaseDataBuilder.solsPBANumber(pbas);
        responseCaseDataBuilder.solsOrgHasPBAs(!pbas.getListItems().isEmpty() ? YES : NO);
        responseCaseDataBuilder.solsPBAPaymentReference(data.getSolsSolicitorAppReference());
    }

    public void defaultCaveatFeeAccounts(CaveatData data,
                                         ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder,
                                         String authToken) {
        DynamicList pbas = getPBAAccounts(authToken);
        responseCaseDataBuilder.solsPBANumber(pbas);
        responseCaseDataBuilder.solsOrgHasPBAs(!pbas.getListItems().isEmpty() ? YES : NO);
        responseCaseDataBuilder.solsPBAPaymentReference(data.getSolsSolicitorAppReference());
    }

    private DynamicList getPBAAccounts(String authToken) {
        List<String> pbas = pbaRetrievalService.getPBAs(authToken);
        List<DynamicListItem> items = new ArrayList<>();
        for (String pba : pbas) {
            items.add(DynamicListItem.builder()
                .code(pba)
                .label(pba)
                .build());
        }
        return DynamicList.builder().listItems(items).value(DynamicListItem.builder().build()).build();
    }

}
