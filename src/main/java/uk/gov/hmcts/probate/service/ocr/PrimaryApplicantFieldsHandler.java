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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ALIAS;


@Slf4j
@Component
@RequiredArgsConstructor
public class PrimaryApplicantFieldsHandler {

    private final BulkScanConfig bulkScanConfig;

    public void handleGorPrimaryApplicantFields(ExceptionRecordOCRFields ocrFields,
                                                List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(ocrFields::getPrimaryApplicantForenames, ocrFields::setPrimaryApplicantForenames,
                PRIMARY_APPLICANT_FORENAMES, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantSurname, ocrFields::setPrimaryApplicantSurname,
                PRIMARY_APPLICANT_SURNAME, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantAddressLine1, ocrFields::setPrimaryApplicantAddressLine1,
                PRIMARY_APPLICANT_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantAddressPostCode, ocrFields::setPrimaryApplicantAddressPostCode,
                PRIMARY_APPLICANT_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(ocrFields::getPrimaryApplicantHasAlias, ocrFields::setPrimaryApplicantHasAlias,
                PRIMARY_APPLICANT_HAS_ALIAS, bulkScanConfig.getPrimaryApplicantHasAlias(), modifiedFields);

        if (TRUE.equalsIgnoreCase(ocrFields.getPrimaryApplicantHasAlias()) && isBlank(
                ocrFields.getPrimaryApplicantAlias())) {
            addModifiedField(modifiedFields, PRIMARY_APPLICANT_ALIAS, ocrFields.getPrimaryApplicantAlias());
            ocrFields.setPrimaryApplicantAlias(bulkScanConfig.getName());
        }
    }

    private void setFieldIfBlank(Supplier<String> getter, Consumer<String> setter, String fieldName,
                                 String defaultValue, List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(getter.get())) {
            addModifiedField(modifiedFields, fieldName, getter.get());
            setter.accept(defaultValue);
            log.info("Setting {} to {}", fieldName, defaultValue);
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