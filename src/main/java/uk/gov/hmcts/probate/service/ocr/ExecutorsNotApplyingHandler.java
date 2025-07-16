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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_0_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_1_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_2_REASON;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorsNotApplyingHandler {

    private final BulkScanConfig bulkScanConfig;

    public void handleExecutorsNotApplyingFields(ExceptionRecordOCRFields ocrFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        handleExecutorNotApplying(ocrFields.getExecutorsNotApplying0notApplyingExecutorName(),
                ocrFields.getExecutorsNotApplying0notApplyingExecutorReason(),
                ocrFields::setExecutorsNotApplying0notApplyingExecutorReason,
                modifiedFields, EXECUTOR_NOT_APPLYING_0_REASON);

        handleExecutorNotApplying(ocrFields.getExecutorsNotApplying1notApplyingExecutorName(),
                ocrFields.getExecutorsNotApplying1notApplyingExecutorReason(),
                ocrFields::setExecutorsNotApplying1notApplyingExecutorReason,
                modifiedFields, EXECUTOR_NOT_APPLYING_1_REASON);

        handleExecutorNotApplying(ocrFields.getExecutorsNotApplying2notApplyingExecutorName(),
                ocrFields.getExecutorsNotApplying2notApplyingExecutorReason(),
                ocrFields::setExecutorsNotApplying2notApplyingExecutorReason,
                modifiedFields, EXECUTOR_NOT_APPLYING_2_REASON);
    }

    private void handleExecutorNotApplying(String executorName, String executorReason,
                                           Consumer<String> setExecutorReason,
                                           List<CollectionMember<ModifiedOCRField>> modifiedFields,
                                           String fieldName) {
        if (!isBlank(executorName) && isBlank(executorReason)) {
            addModifiedField(modifiedFields, fieldName, executorReason);
            setExecutorReason.accept(bulkScanConfig.getExecutorsNotApplyingReason());
        }
    }

    private void addModifiedField(List<CollectionMember<ModifiedOCRField>> modifiedFields, String fieldName,
                                  String originalValue) {
        ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                .fieldName(fieldName)
                .originalValue(originalValue)
                .build();
        modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
    }
}