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
        responseCaseDataBuilder.reprintDocumentList(getDocumentList(caseDetails.getData()));
    }

    private DynamicList getDocumentList(CaseData caseData) {
        List<DynamicListItem> listItems = new ArrayList<DynamicListItem>();
        listItems.add(buildListItem("Grant", "Grant"));
        listItems.add(buildListItem("ReissuedGrant", "Reissued Grant"));
        listItems.add(buildListItem("Will", "Will (without copy of grant)"));
        listItems.add(buildListItem("SOT", "SOT"));
        DynamicList dynamicList = DynamicList.builder()
            .listItems(listItems)
            .build();

        return dynamicList;
    }


    public String setupDocumentList(CaseData caseData) {
        String list = "    {\n" +
            "  \"request\": {\n" +
            "    \"method\": \"POST\",\n" +
            "    \"urlPath\": \"/case_type/fe-functional-test/mid_event_dynamic_list\"\n" +
            "  },\n" +
            "  \"response\": {\n" +
            "    \"status\": 200,\n" +
            "    \"headers\": {\n" +
            "      \"Content-Type\": \"application/json\"\n" +
            "    },\n" +
            "    \"jsonBody\": {\n" +
            "      \"reprintDocument\": {\n" +
            "        \"DynamicList\": {\n" +
            "          \"value\": {\n" +
            "            \"code\": \"List1\",\n" +
            "            \"label\": \" List 1\"\n" +
            "          },\n" +
            "          \"list_items\": [{\n" +
            "            \"code\": \"List1\",\n" +
            "            \"label\": \" List 1\"\n" +
            "          }, {\n" +
            "            \"code\": \"List2\",\n" +
            "            \"label\": \" List 2\"\n" +
            "          }, {\n" +
            "            \"code\": \"List3\",\n" +
            "            \"label\": \" List 3\"\n" +
            "          }, {\n" +
            "            \"code\": \"List4\",\n" +
            "            \"label\": \" List 4\"\n" +
            "          }, {\n" +
            "            \"code\": \"List5\",\n" +
            "            \"label\": \" List 5\"\n" +
            "          }, {\n" +
            "            \"code\": \"List6\",\n" +
            "            \"label\": \" List 6\"\n" +
            "          }, {\n" +
            "            \"code\": \"List7\",\n" +
            "            \"label\": \" List 7\"\n" +
            "          }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";

        return list;
    }

    private DynamicListItem buildListItem(String code, String label) {
        return DynamicListItem.builder()
            .code(code)
            .label(label)
            .build();
    }

}
