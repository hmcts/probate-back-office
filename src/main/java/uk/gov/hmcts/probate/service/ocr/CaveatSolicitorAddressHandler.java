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

import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_FIRM_NAME;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class CaveatSolicitorAddressHandler {

    private final BulkScanConfig bulkScanConfig;

    public void handleCaveatSolicitorAddressFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                                   List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorAddressLine1,
                exceptionRecordOCRFields::setSolsSolicitorAddressLine1,
                SOLICITOR_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorAddressPostCode,
                exceptionRecordOCRFields::setSolsSolicitorAddressPostCode,
                SOLICITOR_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorFirmName,
                exceptionRecordOCRFields::setSolsSolicitorFirmName,
                SOLICITOR_FIRM_NAME, bulkScanConfig.getName(), modifiedFields);
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