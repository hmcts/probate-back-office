package uk.gov.hmcts.probate.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.payments.pba.PBAValidationService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitorPBADefaulter {
    @Autowired
    PBAValidationService pbaValidationService;

    public void defaultFeeAccounts(ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder, String authToken) {
        responseCaseDataBuilder.solsPBANumber(getPBAAccounts(authToken));
    }

    private DynamicList getPBAAccounts(String authToken) {
        List<String> pbas = pbaValidationService.getPBAs(authToken);
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
