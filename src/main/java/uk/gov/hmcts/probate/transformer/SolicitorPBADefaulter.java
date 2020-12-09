package uk.gov.hmcts.probate.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.pbavalidation.PBAValidationService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitorPBADefaulter {
    @Autowired
    PBAValidationService pbaValidationService;

    private DynamicList getPBAAccounts(String authToken) {
        List<String> pbas = pbaValidationService.getPBAs(authToken);
        List<DynamicListItem> items = new ArrayList<>();
        for (String pba : pbas) {
            items.add(DynamicListItem.builder()
                .code(pba)
                .label(pba)
                .build());
        }
        return DynamicList.builder().listItems(items).build();
    }

    public void defaultFeeAccounts(ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder, String authToken) {

        responseCaseDataBuilder.solsPBANumber(getPBAAccounts(authToken));


        ///http://rd-professional-api-aat.service.core-compute-aat.internal/refdata/internal/v1/organisations/pbas?probatesolicitorpreprod@gmail.com
        //userAuth + serviceAuth
        //payment-api-aat.service.core-compute-aat.internal/credit-account-payments
        //userAuth + serviceAuth
        /*
        {
          "account_number": "string",
          "amount": 0,
          "case_reference": "string",
          "ccd_case_number": "string",
          "currency": "GBP",
          "customer_reference": "string",
          "description": "string",
          "fees": [
            {
              "allocated_amount": 0,
              "amount_due": 0,
              "apportion_amount": 0,
              "apportioned_payment": 0,
              "calculated_amount": 0,
              "case_reference": "string",
              "ccd_case_number": "string",
              "code": "string",
              "date_apportioned": "2020-12-04T11:53:07.125Z",
              "date_created": "2020-12-04T11:53:07.125Z",
              "date_receipt_processed": "2020-12-04T11:53:07.125Z",
              "date_updated": "2020-12-04T11:53:07.125Z",
              "description": "string",
              "fee_amount": 0,
              "id": 0,
              "jurisdiction1": "string",
              "jurisdiction2": "string",
              "memo_line": "string",
              "natural_account_code": "string",
              "net_amount": 0,
              "payment_group_reference": "string",
              "reference": "string",
              "version": "string",
              "volume": 0
            }
          ],
          "organisation_name": "string",
          "service": "CMC",
          "site_id": "string"
        }
         */
    }

}
