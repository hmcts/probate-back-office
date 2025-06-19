package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_POST_CODE;

@Component
@RequiredArgsConstructor
public class CaveatCitizenAddressHandler {

    private final BulkScanConfig bulkScanConfig;

    public void handleCaveatCitizenAddressFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorAddressLine1,
                exceptionRecordOCRFields::setCaveatorAddressLine1,
                CAVEATOR_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorAddressPostCode,
                exceptionRecordOCRFields::setCaveatorAddressPostCode,
                CAVEATOR_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);
    }

    private void setFieldIfBlank(Supplier<String> getter, Consumer<String> setter, String fieldName,
                                 String defaultValue, List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(getter.get())) {
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(fieldName)
                    .originalValue(getter.get())
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            setter.accept(defaultValue);
        }
    }
}