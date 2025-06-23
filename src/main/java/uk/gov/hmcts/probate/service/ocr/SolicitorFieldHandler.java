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
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.APPLYING_ATTORNEY;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_FIRM_NAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_REPRESENTATIVE_NAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_APP_REFERENCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_LINE2;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_EMAIL;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_PHONE_NUMBER;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitorFieldHandler {

    private final BulkScanConfig bulkScanConfig;

    public void handleGorSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                         List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(ocrFields.getSolsSolicitorRepresentativeName())) {
            addModifiedField(modifiedFields, SOLICITOR_REPRESENTATIVE_NAME,
                    ocrFields.getSolsSolicitorRepresentativeName());
            if (isNotBlank(ocrFields.getSolsSolicitorFirmName())) {
                ocrFields.setSolsSolicitorRepresentativeName(ocrFields.getSolsSolicitorFirmName());
                log.info("Setting solicitor representative name to {}", ocrFields.getSolsSolicitorFirmName());
            } else {
                ocrFields.setSolsSolicitorRepresentativeName(bulkScanConfig.getName());
                log.info("Setting solicitor representative to {}", bulkScanConfig.getName());
            }
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorFirmName, ocrFields::setSolsSolicitorFirmName,
                SOLICITOR_FIRM_NAME, bulkScanConfig.getName(), modifiedFields);

        if (isBlank(ocrFields.getSolsSolicitorAppReference())) {
            addModifiedField(modifiedFields, SOLICITOR_APP_REFERENCE,
                    ocrFields.getSolsSolicitorAppReference());
            if (isNotBlank(ocrFields.getDeceasedSurname())) {
                ocrFields.setSolsSolicitorAppReference(ocrFields.getDeceasedSurname());
                log.info("Setting legal representative name to {}", ocrFields.getSolsSolicitorAppReference());
            } else {
                ocrFields.setSolsSolicitorAppReference(bulkScanConfig.getName());
                log.info("Setting legal representative name to {}", ocrFields.getSolsSolicitorFirmName());
            }
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorAddressLine1, ocrFields::setSolsSolicitorAddressLine1,
                SOLICITOR_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);

        if (isBlank(ocrFields.getSolsSolicitorAddressLine2()) && isBlank(ocrFields.getSolsSolicitorAddressLine1())
                && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, SOLICITOR_ADDRESS_LINE2,
                    ocrFields.getSolsSolicitorAddressLine2());
            ocrFields.setSolsSolicitorAddressLine2(bulkScanConfig.getName());
            log.info("Setting solicitor address line 2 to {}", ocrFields.getSolsSolicitorAddressLine2());
        }

        if (isBlank(ocrFields.getSolsSolicitorAddressTown()) && isBlank(ocrFields.getSolsSolicitorAddressLine1())
                && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, SOLICITOR_ADDRESS_TOWN,
                    ocrFields.getSolsSolicitorAddressTown());
            ocrFields.setSolsSolicitorAddressTown(bulkScanConfig.getName());
            log.info("Setting solicitor town or city to {}", ocrFields.getSolsSolicitorAddressTown());
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorAddressPostCode, ocrFields::setSolsSolicitorAddressPostCode,
                SOLICITOR_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(ocrFields::getSolsSolicitorEmail, ocrFields::setSolsSolicitorEmail,
                SOLICITOR_EMAIL, bulkScanConfig.getEmail(), modifiedFields);

        setFieldIfBlank(ocrFields::getSolsSolicitorPhoneNumber, ocrFields::setSolsSolicitorPhoneNumber,
                SOLICITOR_PHONE_NUMBER, bulkScanConfig.getPhone(), modifiedFields);

        setFieldIfBlank(ocrFields::getApplyingAsAnAttorney, ocrFields::setApplyingAsAnAttorney,
                APPLYING_ATTORNEY, bulkScanConfig.getFieldsNotCompleted(), modifiedFields);
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