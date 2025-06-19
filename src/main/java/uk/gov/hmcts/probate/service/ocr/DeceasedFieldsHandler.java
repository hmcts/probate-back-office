package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.hmcts.probate.model.Constants.FALSE;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOB;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOD;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOMICILE_IN_ENG_WALES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_FORENAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_SURNAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeceasedFieldsHandler {

    private final BulkScanConfig bulkScanConfig;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    public void handleDeceasedFields(ExceptionRecordOCRFields ocrFields,
                                     List<CollectionMember<ModifiedOCRField>> modifiedFields) {

        if (!isBlank(ocrFields.getDeceasedDateOfDeath()) && isBlank(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            handleDeceasedDateOfDeathPresent(ocrFields, modifiedFields);
        } else if (isBlank(ocrFields.getDeceasedDateOfDeath()) && !isBlank(ocrFields
                .getDeceasedDiedOnAfterSwitchDate())) {
            handleDeceasedDateOfDeathMissing(ocrFields, modifiedFields);
        } else if (isBlank(ocrFields.getDeceasedDateOfDeath()) && isBlank(ocrFields
                .getDeceasedDiedOnAfterSwitchDate())) {
            handleBothDeceasedFieldsMissing(ocrFields, modifiedFields);
        }

        setFieldIfBlank(ocrFields::getDeceasedForenames, ocrFields::setDeceasedForenames,
                DECEASED_FORENAME, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedSurname, ocrFields::setDeceasedSurname,
                DECEASED_SURNAME, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedAddressLine1, ocrFields::setDeceasedAddressLine1,
                DECEASED_ADDRESS_LINE, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedAddressPostCode, ocrFields::setDeceasedAddressPostCode,
                DECEASED_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedDateOfBirth, ocrFields::setDeceasedDateOfBirth,
                DECEASED_DOB, bulkScanConfig.getDob(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedAnyOtherNames, ocrFields::setDeceasedAnyOtherNames,
                DECEASED_ANY_OTHER_NAMES, FALSE, modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedDomicileInEngWales, ocrFields::setDeceasedDomicileInEngWales,
                DECEASED_DOMICILE_IN_ENG_WALES, TRUE, modifiedFields);
    }

    private void handleDeceasedDateOfDeathPresent(ExceptionRecordOCRFields ocrFields,
                                                  List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        addModifiedField(modifiedFields, DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, ocrFields
                .getDeceasedDiedOnAfterSwitchDate());
        String switchDateValue = exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields
                .getDeceasedDateOfDeath())
                ? TRUE : FALSE;
        ocrFields.setDeceasedDiedOnAfterSwitchDate(switchDateValue);
        log.info("Setting deceasedDiedOnAfterSwitchDate to {}", switchDateValue);
    }

    private void handleDeceasedDateOfDeathMissing(ExceptionRecordOCRFields ocrFields,
                                                  List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String switchDateValue = ocrFields.getDeceasedDiedOnAfterSwitchDate();
        addModifiedField(modifiedFields, DECEASED_DOD, ocrFields.getDeceasedDateOfDeath());

        if (TRUE.equalsIgnoreCase(switchDateValue)) {
            log.info("application.yaml date of death {}", bulkScanConfig
                    .getDateOfDeathForDiedOnOrAfterSwitchDateTrue());
            ocrFields.setDeceasedDateOfDeath(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue());
        } else {
            ocrFields.setDeceasedDateOfDeath(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse());
        }
        log.info("Setting deceasedDateOfDeath to {} due to died on or after switch date value",
                ocrFields.getDeceasedDateOfDeath());
    }

    private void handleBothDeceasedFieldsMissing(ExceptionRecordOCRFields ocrFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (!isBlank(ocrFields.getIht205Completed()) || !isBlank(ocrFields.getIhtGrossValue205())
                || !isBlank(ocrFields.getIhtNetValue205())) {
            setDefaultValues(ocrFields, modifiedFields, bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateFalse(),
                    bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse());
        } else if (!isBlank(ocrFields.getIhtEstateNetValue()) || !isBlank(ocrFields.getIhtEstateGrossValue())
                || !isBlank(ocrFields.getExceptedEstate())) {
            setDefaultValues(ocrFields, modifiedFields, bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateTrue(),
                    bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue());
        } else {
            setDefaultValues(ocrFields, modifiedFields, bulkScanConfig.getDeceasedDiedOnOrAfterSwitchDateTrue(),
                    bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue());
        }
    }

    private void setFieldIfBlank(Supplier<String> getter, Consumer<String> setter, String fieldName,
                                 String defaultValue, List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(getter.get())) {
            addModifiedField(modifiedFields, fieldName, getter.get());
            setter.accept(defaultValue);
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

    private void setDefaultValues(ExceptionRecordOCRFields ocrFields,
                                  List<CollectionMember<ModifiedOCRField>> modifiedFields,
                                  String deceasedDiedOnOrAfterSwitchDate, String dateOfDeath) {
        addModifiedField(modifiedFields, DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, ocrFields
                .getDeceasedDiedOnAfterSwitchDate());
        ocrFields.setDeceasedDiedOnAfterSwitchDate(deceasedDiedOnOrAfterSwitchDate);

        addModifiedField(modifiedFields, DECEASED_DOD, ocrFields.getDeceasedDateOfDeath());
        ocrFields.setDeceasedDateOfDeath(dateOfDeath);
    }
}