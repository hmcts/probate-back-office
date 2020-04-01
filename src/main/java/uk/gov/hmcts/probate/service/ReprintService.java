package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ReprintService {

    public void setupReprintDocuments(@Valid CaseDetails caseDetails,
                                      ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder) {
        responseCaseDataBuilder.reprintDocument(getDocumentList(caseDetails.getData()));
    }

    private DynamicList getDocumentList(CaseData caseData) {
        List<DynamicListItem> listItems = new ArrayList<DynamicListItem>();
        listItems.add(buildListItem("Grant", "Grant"));
        listItems.add(buildListItem("ReissuedGrant", "Reissued Grant"));
        listItems.add(buildListItem("Will", "Will (without copy of grant)"));
        listItems.add(buildListItem("SOT", "SOT"));
        DynamicList dynamicList = DynamicList.builder()
            .listItems(listItems)
            .value(DynamicListItem.builder().build())
            .build();

        return dynamicList;
    }
    
    private DynamicListItem buildListItem(String code, String label) {
        return DynamicListItem.builder()
            .code(code)
            .label(label)
            .build();
    }

}
