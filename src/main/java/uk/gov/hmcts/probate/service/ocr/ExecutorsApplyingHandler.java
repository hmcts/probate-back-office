package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_POST_CODE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorsApplyingHandler {

    private final BulkScanConfig bulkScanConfig;

    public void handleExecutorsApplyingFields(ExceptionRecordOCRFields ocrFields,
                                               List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (!isBlank(ocrFields.getExecutorsApplying0applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying0applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying0applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, EXECUTORS_APPLYING_0_OTHER_NAMES, ocrFields
                        .getExecutorsApplying0applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying0applyingExecutorOtherNames(bulkScanConfig.getName());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressLine1,
                    EXECUTORS_APPLYING_0_ADDRESS_LINE, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressTown,
                    EXECUTORS_APPLYING_0_ADDRESS_TOWN, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressPostCode,
                    EXECUTORS_APPLYING_0_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
        if (!isBlank(ocrFields.getExecutorsApplying1applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying1applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying1applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, EXECUTORS_APPLYING_1_OTHER_NAMES, ocrFields
                        .getExecutorsApplying1applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying1applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressLine1,
                    EXECUTORS_APPLYING_1_ADDRESS_LINE, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressTown,
                    EXECUTORS_APPLYING_1_ADDRESS_TOWN, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressPostCode,
                    EXECUTORS_APPLYING_1_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
        if (!isBlank(ocrFields.getExecutorsApplying2applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying2applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying2applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, EXECUTORS_APPLYING_2_OTHER_NAMES, ocrFields
                        .getExecutorsApplying2applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying2applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressLine1,
                    EXECUTORS_APPLYING_2_ADDRESS_LINE, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressTown,
                    EXECUTORS_APPLYING_2_ADDRESS_TOWN, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressPostCode,
                    EXECUTORS_APPLYING_2_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
    }

    private void addModifiedField(List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ModifiedOCRField>>
                                          modifiedList, String fieldName,
                                  String originalValue) {
        ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                .fieldName(fieldName)
                .originalValue(originalValue)
                .build();
        modifiedList.add(new CollectionMember<>(null, modifiedOCRField));
    }

    private void setFieldIfBlank(Supplier<String> getter, Consumer<String> setter, String fieldName,
                                 String defaultValue, List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(getter.get())) {
            addModifiedField(modifiedFields, fieldName, getter.get());
            setter.accept(defaultValue);
            log.info("Setting {} to {}", fieldName, defaultValue);
        }
    }
}
