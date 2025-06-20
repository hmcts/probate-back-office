package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.validator.IhtEstateValidationRule;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.hmcts.probate.model.Constants.FALSE;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT205;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT207;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT400;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT400421;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_207;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_207_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_207_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400421;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_421_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_421_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_ESTATE_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_ESTATE_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_ESTATE_NET_QUALIFYING_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_GROSS_VALUE_EXCEPTED_ESTATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_NET_VALUE_EXCEPTED_ESTATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400_PROCESS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_FORM_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_REFERENCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_FORM_ID;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_UNUSED_ALLOWANCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_MARITAL_STATUS_WIDOWED;

@Slf4j
@Component
@RequiredArgsConstructor
public class IHTFieldHandler {

    private final BulkScanConfig bulkScanConfig;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    private final IhtEstateValidationRule ihtEstateValidationRule;

    public void handleIHTFields(ExceptionRecordOCRFields ocrFields,
                                List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isFormVersionValid(ocrFields)) {
            setDefaultIHTValues(ocrFields, modifiedFields, bulkScanConfig);

            if (isFormVersion2Or3AndExceptedEstate(ocrFields)) {
                setEstateValues(ocrFields, modifiedFields);
            } else if (isIhtFormsNotCompleted(ocrFields)) {
                setIht400Values(ocrFields, modifiedFields);
            }
        }
        if (isFormVersion1Valid(ocrFields)) {
            handleFormVersion1(ocrFields, modifiedFields);
        }
    }

    private void setDefaultIHTValues(ExceptionRecordOCRFields ocrFields,
                                     List<CollectionMember<ModifiedOCRField>> modifiedList,
                                     BulkScanConfig bulkScanConfig) {
        boolean isAnyFormSelectedTrue = Stream.of(
                ocrFields.getIht400421Completed(),
                ocrFields.getIht400Completed(),
                ocrFields.getIht207Completed(),
                ocrFields.getIht205Completed(),
                ocrFields.getIht205completedOnline(),
                ocrFields.getExceptedEstate()
        ).anyMatch(BooleanUtils::toBoolean) || isFormVersion2Or3AndExceptedEstate(ocrFields);

        if (!isAnyFormSelectedTrue) {
            setFormsToDefault(ocrFields, modifiedList, bulkScanConfig);
        } else {
            updateFieldsIfTrue(ocrFields, modifiedList, bulkScanConfig);
        }
    }

    private void setFormsToDefault(ExceptionRecordOCRFields ocrFields,
                                   List<CollectionMember<ModifiedOCRField>> modifiedList,
                                   BulkScanConfig bulkScanConfig) {
        List<String> fieldNames = List.of(IHT_400421, IHT_207, IHT_205, IHT_205_COMPLETED_ONLINE);
        List<Supplier<String>> getters = List.of(
                ocrFields::getIht400421Completed,
                ocrFields::getIht207Completed,
                ocrFields::getIht205Completed,
                ocrFields::getIht205completedOnline
        );

        IntStream.range(0, fieldNames.size())
                .filter(i -> isBlank(getters.get(i).get()))
                .forEach(i -> {
                    addModifiedField(modifiedList, fieldNames.get(i), getters.get(i).get());
                    setFieldValue(ocrFields, fieldNames.get(i), bulkScanConfig.getIhtForm());
                });
    }

    private void updateFieldsIfTrue(ExceptionRecordOCRFields ocrFields,
                                    List<CollectionMember<ModifiedOCRField>> modifiedList,
                                    BulkScanConfig bulkScanConfig) {
        List<String> fieldNames = List.of(IHT_400421, IHT_207, IHT_205, IHT_205_COMPLETED_ONLINE, IHT_400);
        List<Supplier<String>> getters = List.of(
                ocrFields::getIht400421Completed,
                ocrFields::getIht207Completed,
                ocrFields::getIht205Completed,
                ocrFields::getIht205completedOnline,
                ocrFields::getIht400Completed
        );

        IntStream.range(0, fieldNames.size())
                .forEach(i -> checkAndSetField(ocrFields, modifiedList, fieldNames.get(i), getters.get(i).get(),
                        bulkScanConfig));
    }

    private void setFieldValue(ExceptionRecordOCRFields ocrFields, String fieldName, String value) {
        switch (fieldName) {
            case IHT_400421 -> ocrFields.setIht400421Completed(value);
            case IHT_400 -> ocrFields.setIht400Completed(value);
            case IHT_207 -> ocrFields.setIht207Completed(value);
            case IHT_205 -> ocrFields.setIht205Completed(value);
            case IHT_205_COMPLETED_ONLINE -> ocrFields.setIht205completedOnline(value);
        }
        log.info("Setting {} to {}", fieldName, value);
    }

    private void checkAndSetField(ExceptionRecordOCRFields ocrFields,
                                  List<CollectionMember<ModifiedOCRField>> modifiedList,
                                  String fieldName, String fieldValue,
                                  BulkScanConfig bulkScanConfig) {
        if (TRUE.equalsIgnoreCase(fieldValue)) {
            if (isFormVersion3Valid(ocrFields)) {
                setFormVersion3Fields(ocrFields, modifiedList, fieldName, bulkScanConfig);
            } else if (isFormVersion2Valid(ocrFields)) {
                setFormVersion2Fields(ocrFields, modifiedList, bulkScanConfig);
            }
        }
    }

    private void setFormVersion3Fields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedList,
                                       String fieldName, BulkScanConfig bulkScanConfig) {
        switch (fieldName) {
            case IHT_400421 -> {
                setFieldIfBlank(ocrFields::getIht421grossValue, ocrFields::setIht421grossValue,
                        IHT_421_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIht421netValue, ocrFields::setIht421netValue,
                        IHT_421_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
            }
            case IHT_207 -> {
                setFieldIfBlank(ocrFields::getIht207grossValue, ocrFields::setIht207grossValue,
                        IHT_207_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIht207netValue, ocrFields::setIht207netValue,
                        IHT_207_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
            }
            case IHT_205 -> {
                setFieldIfBlank(ocrFields::getIhtGrossValue205, ocrFields::setIhtGrossValue205,
                        IHT_205_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIhtNetValue205, ocrFields::setIhtNetValue205,
                        IHT_205_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
            }
            case IHT_205_COMPLETED_ONLINE -> {
                setFieldIfBlank(ocrFields::getIhtReferenceNumber, ocrFields::setIhtReferenceNumber,
                        IHT_REFERENCE, "1234", modifiedList);
                setFieldIfBlank(ocrFields::getIhtGrossValue205, ocrFields::setIhtGrossValue205,
                        IHT_205_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIhtNetValue205, ocrFields::setIhtNetValue205,
                        IHT_207_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
            }
            case IHT_400 -> {
                setFieldIfBlank(ocrFields::getIht400process, ocrFields::setIht400process,
                        IHT_400_PROCESS, TRUE, modifiedList);
                setFieldIfBlank(ocrFields::getProbateGrossValueIht400, ocrFields::setProbateGrossValueIht400,
                        IHT_400_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getProbateNetValueIht400, ocrFields::setProbateNetValueIht400,
                        IHT_400_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
            }
        }
    }

    private void setFormVersion2Fields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedList,
                                       BulkScanConfig bulkScanConfig) {
        setFieldIfBlank(ocrFields::getIhtGrossValue, ocrFields::setIhtGrossValue,
                IHT_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
        setFieldIfBlank(ocrFields::getIhtNetValue, ocrFields::setIhtNetValue,
                IHT_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
    }

    private boolean isFormVersionValid(ExceptionRecordOCRFields ocrFields) {
        return isFormVersion3AndSwitchDateValid(ocrFields)
                || isFormVersion2AndSwitchDateValid(ocrFields, exceptedEstateDateOfDeathChecker);
    }

    private boolean isFormVersion3AndSwitchDateValid(ExceptionRecordOCRFields ocrFields) {
        return isFormVersion3Valid(ocrFields) && (TRUE.equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate())
                || FALSE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate()));
    }

    private void setEstateValues(ExceptionRecordOCRFields ocrFields,
                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isFormVersion2DiedAfter(ocrFields) && hasLateSpouseCivilPartner(ocrFields)
                && nqvBetweenThresholds(ocrFields)) {
            setFieldIfBlank(ocrFields::getIhtUnusedAllowanceClaimed, ocrFields::setIhtUnusedAllowanceClaimed,
                    IHT_UNUSED_ALLOWANCE, bulkScanConfig.getFieldsNotCompleted(), modifiedFields);
        }
        setFieldIfBlank(ocrFields::getIhtEstateGrossValue, ocrFields::setIhtEstateGrossValue,
                IHT_ESTATE_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
        setFieldIfBlank(ocrFields::getIhtEstateNetValue, ocrFields::setIhtEstateNetValue,
                IHT_ESTATE_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
        setFieldIfBlank(ocrFields::getIhtEstateNetQualifyingValue, ocrFields::setIhtEstateNetQualifyingValue,
                IHT_ESTATE_NET_QUALIFYING_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);

        if (isFormVersion3Valid(ocrFields)) {
            setFieldIfBlank(ocrFields::getIhtGrossValueExceptedEstate, ocrFields::setIhtGrossValueExceptedEstate,
                    IHT_GROSS_VALUE_EXCEPTED_ESTATE, bulkScanConfig.getGrossNetValue(), modifiedFields);
            setFieldIfBlank(ocrFields::getIhtNetValueExceptedEstate, ocrFields::setIhtNetValueExceptedEstate,
                    IHT_NET_VALUE_EXCEPTED_ESTATE, bulkScanConfig.getGrossNetValue(), modifiedFields);
        } else {
            setFieldIfBlank(ocrFields::getIhtGrossValue, ocrFields::setIhtGrossValue,
                    IHT_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
            setFieldIfBlank(ocrFields::getIhtNetValue, ocrFields::setIhtNetValue,
                    IHT_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
        }
    }

    private void setIht400Values(ExceptionRecordOCRFields ocrFields,
                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        addModifiedField(modifiedFields, IHT_400, ocrFields.getIht400Completed());
        ocrFields.setIht400Completed(TRUE);
        log.info("Setting iht400Completed to {}", ocrFields.getIht400Completed());

        setFieldIfBlank(ocrFields::getIht400process, ocrFields::setIht400process,
                IHT_400_PROCESS, TRUE, modifiedFields);
        setFieldIfBlank(ocrFields::getProbateGrossValueIht400, ocrFields::setProbateGrossValueIht400,
                IHT_400_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
        setFieldIfBlank(ocrFields::getProbateNetValueIht400, ocrFields::setProbateNetValueIht400,
                IHT_400_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
    }

    private boolean isFormVersion2AndSwitchDateValid(ExceptionRecordOCRFields ocrFields,
                                                     ExceptedEstateDateOfDeathChecker checker) {
        return isFormVersion2Valid(ocrFields) && (checker.isOnOrAfterSwitchDate(ocrFields
                .getDeceasedDateOfDeath()) || !checker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath()));
    }

    private boolean isFormVersion1Valid(ExceptionRecordOCRFields ocrFields) {
        return "1".equals(ocrFields.getFormVersion());
    }

    public boolean isFormVersion2Valid(ExceptionRecordOCRFields ocrFields) {
        return "2".equals(ocrFields.getFormVersion());
    }

    public boolean isFormVersion3Valid(ExceptionRecordOCRFields ocrFields) {
        return "3".equals(ocrFields.getFormVersion());
    }

    private boolean isFormVersion2Or3AndExceptedEstate(ExceptionRecordOCRFields ocrFields) {
        return (isFormVersion2Valid(ocrFields) && TRUE.equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate()))
                || (isFormVersion3Valid(ocrFields) && TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate()));
    }

    private boolean isFormVersion2DiedAfter(ExceptionRecordOCRFields ocrFields) {
        return (isFormVersion2Valid(ocrFields) && TRUE.equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate()));
    }

    private boolean isIhtFormsNotCompleted(ExceptionRecordOCRFields ocrFields) {
        return FALSE.equalsIgnoreCase(ocrFields.getIht400421Completed()) && FALSE.equalsIgnoreCase(ocrFields
                .getIht207Completed()) && FALSE.equalsIgnoreCase(ocrFields
                .getIht205Completed());
    }

    private void handleFormVersion1(ExceptionRecordOCRFields ocrFields,
                                    List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(ocrFields::getIhtFormCompletedOnline, ocrFields::setIhtFormCompletedOnline,
                IHT_FORM_COMPLETED_ONLINE, bulkScanConfig.getFieldsNotCompleted(), modifiedFields);

        if (BooleanUtils.toBoolean(ocrFields.getIhtFormCompletedOnline())) {
            setFieldIfBlank(ocrFields::getIhtReferenceNumber, ocrFields::setIhtReferenceNumber,
                    IHT_REFERENCE, bulkScanConfig.getName(), modifiedFields);
        } else {
            String ihtFormId = ocrFields.getIhtFormId();
            if (isBlank(ihtFormId)) {
                addModifiedField(modifiedFields, IHT_FORM_ID, ocrFields.getIhtFormId());
                ocrFields.setIhtFormId(bulkScanConfig.getDefaultForm());
                log.info("Setting IHT Form ID to {}", ocrFields.getIhtFormId());
            }
            if (isValidIhtFormId(ocrFields.getIhtFormId())) {
                log.info("Setting IHT Form gross and net values based on form ID: {}", ihtFormId);
                setFieldIfBlank(ocrFields::getIhtGrossValue, ocrFields::setIhtGrossValue,
                        IHT_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
                setFieldIfBlank(ocrFields::getIhtNetValue, ocrFields::setIhtNetValue,
                        IHT_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
            }
        }
    }

    public boolean nqvBetweenThresholds(ExceptionRecordOCRFields ocrFields) {
        if (!isBlank(ocrFields.getIhtEstateNetQualifyingValue())) {
            String ihtEstateNetQualifyingValue = ocrFields.getIhtEstateNetQualifyingValue();
            if (ihtEstateNetQualifyingValue != null) {
                String numericalMonetaryValue = ihtEstateNetQualifyingValue.replaceAll("[^\\d^\\.]",
                        "");
                if (NumberUtils.isCreatable((numericalMonetaryValue))) {
                    BigDecimal nqv = new BigDecimal(numericalMonetaryValue).multiply(BigDecimal.valueOf(100));
                    return ihtEstateValidationRule.isNqvBetweenValues(nqv);
                }
            }
        }

        return false;
    }

    public boolean hasLateSpouseCivilPartner(ExceptionRecordOCRFields ocrFields) {
        if (!isBlank(ocrFields.getDeceasedMartialStatus())) {
            String deceasedMaritalStatus = ocrFields.getDeceasedMartialStatus().trim();
            return DECEASED_MARITAL_STATUS_WIDOWED.equals(deceasedMaritalStatus);
        }
        return false;
    }

    private void setFieldIfBlank(Supplier<String> getter, Consumer<String> setter, String fieldName,
                                 String defaultValue, List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(getter.get())) {
            addModifiedField(modifiedFields, fieldName, getter.get());
            setter.accept(defaultValue);
            log.info("Setting {} to {}", fieldName, defaultValue);
        }
    }

    private boolean isValidIhtFormId(String ihtFormId) {
        log.info("Checking if IHT form ID {} is valid", ihtFormId);
        return Stream.of(FORM_IHT205, FORM_IHT207, FORM_IHT400421, FORM_IHT400)
                .anyMatch(form -> form.equalsIgnoreCase(ihtFormId));
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