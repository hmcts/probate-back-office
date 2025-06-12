package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.hmcts.probate.model.Constants.FALSE;
import static uk.gov.hmcts.probate.model.Constants.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRFieldModifierUtils {

    private final BulkScanConfig bulkScanConfig;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    public List<CollectionMember<ModifiedOCRField>> setDefaultGorValues(ExceptionRecordOCRFields ocrFields) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();

        handleDeceasedFields(ocrFields, modifiedFields);
        handlePrimaryApplicantFields(ocrFields, modifiedFields);
        handleSolicitorFields(ocrFields, modifiedFields);
        handleIHTFields(ocrFields, modifiedFields);
        handleExecutorsNotApplyingFields(ocrFields, modifiedFields);
        handleExecutorsApplyingFields(ocrFields, modifiedFields);

        return modifiedFields;
    }

    private void handleDeceasedFields(ExceptionRecordOCRFields ocrFields,
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
                "deceasedForenames", bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedSurname, ocrFields::setDeceasedSurname,
                "deceasedSurname", bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedAddressLine1, ocrFields::setDeceasedAddressLine1,
                "deceasedAddressLine1", bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedAddressPostCode, ocrFields::setDeceasedAddressPostCode,
                "deceasedAddressPostCode", bulkScanConfig.getPostcode(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedDateOfBirth, ocrFields::setDeceasedDateOfBirth,
                "deceasedDateOfBirth", bulkScanConfig.getDob(), modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedAnyOtherNames, ocrFields::setDeceasedAnyOtherNames,
                "deceasedAnyOtherNames", FALSE, modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedDomicileInEngWales, ocrFields::setDeceasedDomicileInEngWales,
                "deceasedDomicileInEngWales", TRUE, modifiedFields);
    }

    private void handleDeceasedDateOfDeathPresent(ExceptionRecordOCRFields ocrFields,
                                                  List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate", ocrFields
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
        addModifiedField(modifiedFields, "deceasedDateOfDeath", ocrFields.getDeceasedDateOfDeath());

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

    private void handlePrimaryApplicantFields(ExceptionRecordOCRFields ocrFields,
                                              List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(ocrFields::getPrimaryApplicantForenames, ocrFields::setPrimaryApplicantForenames,
                "primaryApplicantForenames", bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantSurname, ocrFields::setPrimaryApplicantSurname,
                "primaryApplicantSurname", bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantAddressLine1, ocrFields::setPrimaryApplicantAddressLine1,
                "primaryApplicantAddressLine1", bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantAddressPostCode, ocrFields::setPrimaryApplicantAddressPostCode,
                "primaryApplicantAddressPostCode", bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(ocrFields::getPrimaryApplicantAlias, ocrFields::setPrimaryApplicantAlias,
                "primaryApplicantAlias", bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(ocrFields::getPrimaryApplicantHasAlias, ocrFields::setPrimaryApplicantHasAlias,
                "primaryApplicantHasAlias", bulkScanConfig.getPrimaryApplicantHasAlias(), modifiedFields);
    }

    private void handleSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(ocrFields::getSolsSolicitorIsApplying, ocrFields::setSolsSolicitorIsApplying,
                "solsSolicitorIsApplying", bulkScanConfig.getSolicitorApplying(), modifiedFields);
        if (BooleanUtils.toBoolean(ocrFields.getSolsSolicitorIsApplying())) {
            handleGorSolicitorFields(ocrFields, modifiedFields, bulkScanConfig);
        }
    }

    private void handleExecutorsApplyingFields(ExceptionRecordOCRFields ocrFields,
                                               List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (!isBlank(ocrFields.getExecutorsApplying0applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying0applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying0applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, "executorsApplying0applyingExecutorOtherNames", ocrFields
                        .getExecutorsApplying0applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying0applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressLine1,
                    "executorsApplying0applyingExecutorAddressLine1", bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressTown,
                    "executorsApplying0applyingExecutorAddressTown", bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressPostCode,
                    "executorsApplying0applyingExecutorAddressPostCode", bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
        if (!isBlank(ocrFields.getExecutorsApplying1applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying1applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying1applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, "executorsApplying1applyingExecutorOtherNames", ocrFields
                        .getExecutorsApplying1applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying1applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressLine1,
                    "executorsApplying1applyingExecutorAddressLine1", bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressTown,
                    "executorsApplying1applyingExecutorAddressTown", bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressPostCode,
                    "executorsApplying1applyingExecutorAddressPostCode", bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
        if (!isBlank(ocrFields.getExecutorsApplying2applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying2applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying2applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, "executorsApplying2applyingExecutorOtherNames", ocrFields
                        .getExecutorsApplying2applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying2applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressLine1,
                    "executorsApplying2applyingExecutorAddressLine1", bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressTown,
                    "executorsApplying2applyingExecutorAddressTown", bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressPostCode,
                    "executorsApplying2applyingExecutorAddressPostCode", bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
    }

    public void handleExecutorsNotApplyingFields(ExceptionRecordOCRFields ocrFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (!isBlank(ocrFields.getExecutorsNotApplying0notApplyingExecutorName()) && isBlank(
                ocrFields.getExecutorsNotApplying0notApplyingExecutorReason())) {
            addModifiedField(modifiedFields, "executorsNotApplying0notApplyingExecutorReason", ocrFields
                    .getExecutorsNotApplying0notApplyingExecutorReason());
            ocrFields.setExecutorsNotApplying0notApplyingExecutorReason(bulkScanConfig.getExecutorsNotApplyingReason());
        }

        if (!isBlank(ocrFields.getExecutorsNotApplying1notApplyingExecutorName()) && isBlank(
                ocrFields.getExecutorsNotApplying1notApplyingExecutorReason())) {
            addModifiedField(modifiedFields, "executorsNotApplying1notApplyingExecutorReason", ocrFields
                    .getExecutorsNotApplying1notApplyingExecutorReason());
            ocrFields.setExecutorsNotApplying1notApplyingExecutorReason(bulkScanConfig.getExecutorsNotApplyingReason());
        }

        if (!isBlank(ocrFields.getExecutorsNotApplying2notApplyingExecutorName()) && isBlank(
                ocrFields.getExecutorsNotApplying2notApplyingExecutorReason())) {
            addModifiedField(modifiedFields, "executorsNotApplying2notApplyingExecutorReason", ocrFields
                    .getExecutorsNotApplying2notApplyingExecutorReason());
            ocrFields.setExecutorsNotApplying2notApplyingExecutorReason(bulkScanConfig.getExecutorsNotApplyingReason());
        }
    }

    private void setDefaultValues(ExceptionRecordOCRFields ocrFields,
                                  List<CollectionMember<ModifiedOCRField>> modifiedFields,
                                  String switchDateValue, String dateOfDeathValue) {
        addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate", ocrFields
                .getDeceasedDiedOnAfterSwitchDate());
        ocrFields.setDeceasedDiedOnAfterSwitchDate(switchDateValue);

        addModifiedField(modifiedFields, "deceasedDateOfDeath", ocrFields.getDeceasedDateOfDeath());
        ocrFields.setDeceasedDateOfDeath(dateOfDeathValue);

        log.info("Setting deceasedDiedOnAfterSwitchDate to {}", switchDateValue);
    }

    private void handleIHTFields(ExceptionRecordOCRFields ocrFields,
                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isFormVersion3AndSwitchDateValid(ocrFields)
                || isFormVersion2AndSwitchDateValid(ocrFields, exceptedEstateDateOfDeathChecker)
                || isFormVersion1Valid(ocrFields)) {

            setDefaultIHTValues(ocrFields, modifiedFields, bulkScanConfig);
            if (isIhtFormsNotCompleted(ocrFields)) {
                addModifiedField(modifiedFields, "iht400Completed", ocrFields.getIht400Completed());
                ocrFields.setIht400Completed(TRUE);
                log.info("Setting iht400Completed to {}", ocrFields.getIht400Completed());
                setFieldIfBlank(ocrFields::getIht400process, ocrFields::setIht400process,
                        "iht400process", TRUE, modifiedFields);

                setFieldIfBlank(ocrFields::getProbateGrossValueIht400, ocrFields::setProbateGrossValueIht400,
                        "probateGrossValueIht400", bulkScanConfig.getGrossNetValue(), modifiedFields);

                setFieldIfBlank(ocrFields::getProbateNetValueIht400, ocrFields::setProbateNetValueIht400,
                        "probateNetValueIht400", bulkScanConfig.getGrossNetValue(), modifiedFields);
            }
        }
        if (isFormVersion2Or3AndExceptedEstate(ocrFields)) {
            setFieldIfBlank(ocrFields::getIhtEstateGrossValue, ocrFields::setIhtEstateGrossValue,
                    "ihtEstateGrossValue", bulkScanConfig.getGrossNetValue(), modifiedFields);

            setFieldIfBlank(ocrFields::getIhtEstateNetValue, ocrFields::setIhtEstateNetValue,
                    "ihtEstateNetValue", bulkScanConfig.getGrossNetValue(), modifiedFields);

            setFieldIfBlank(ocrFields::getIhtEstateNetQualifyingValue, ocrFields::setIhtEstateNetQualifyingValue,
                    "ihtEstateNetQualifyingValue", bulkScanConfig.getGrossNetValue(), modifiedFields);
        }
    }

    private boolean isFormVersion3AndSwitchDateValid(ExceptionRecordOCRFields ocrFields) {
        return "3".equals(ocrFields.getFormVersion()) && (TRUE.equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate())
                || FALSE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate()));
    }

    private boolean isFormVersion2AndSwitchDateValid(ExceptionRecordOCRFields ocrFields,
                                                     ExceptedEstateDateOfDeathChecker checker) {
        return "2".equals(ocrFields.getFormVersion()) && (checker.isOnOrAfterSwitchDate(ocrFields
                .getDeceasedDateOfDeath()) || !checker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath()));
    }

    private boolean isFormVersion1Valid(ExceptionRecordOCRFields ocrFields) {
        return "1".equals(ocrFields.getFormVersion());
    }

    private boolean isFormVersion2Or3AndExceptedEstate(ExceptionRecordOCRFields ocrFields) {
        return ("2".equals(ocrFields.getFormVersion()) && TRUE.equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate()))
                || ("3".equals(ocrFields.getFormVersion()) && TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate()));
    }

    private boolean isIhtFormsNotCompleted(ExceptionRecordOCRFields ocrFields) {
        return FALSE.equalsIgnoreCase(ocrFields.getIht400421Completed()) && FALSE.equalsIgnoreCase(ocrFields
                .getIht207Completed()) && FALSE.equalsIgnoreCase(ocrFields
                .getIht205Completed());
    }

    private void setDefaultIHTValues(ExceptionRecordOCRFields ocrFields,
                                  List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ModifiedOCRField>>
                                          modifiedList, BulkScanConfig bulkScanConfig) {
        checkAndSetField(ocrFields, modifiedList, "iht400421Completed", ocrFields.getIht400421Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, "iht207Completed", ocrFields.getIht207Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, "iht205Completed", ocrFields.getIht205Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, "iht205completedOnline", ocrFields
                        .getIht205completedOnline(), bulkScanConfig);
    }

    private void checkAndSetField(ExceptionRecordOCRFields ocrFields,
                                  List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ModifiedOCRField>>
                                          modifiedList, String fieldName, String fieldValue,
                                  BulkScanConfig bulkScanConfig) {
        if (isBlank(fieldValue)) {
            addModifiedField(modifiedList, fieldName, fieldValue);
            switch (fieldName) {
                case "iht400421Completed":
                    ocrFields.setIht400421Completed(bulkScanConfig.getIhtForm());
                    log.info("Setting iht400421Completed to {}", ocrFields.getIht400421Completed());
                    break;
                case "iht207Completed":
                    ocrFields.setIht207Completed(bulkScanConfig.getIhtForm());
                    log.info("Setting iht207Completed to {}", ocrFields.getIht207Completed());
                    break;
                case "iht205Completed":
                    ocrFields.setIht205Completed(bulkScanConfig.getIhtForm());
                    log.info("Setting iht205Completed to {}", ocrFields.getIht205Completed());
                    break;
                case "iht205completedOnline":
                    ocrFields.setIht205completedOnline(bulkScanConfig.getIhtForm());
                    break;
            }
        } else if (TRUE.equalsIgnoreCase(fieldValue)) {
            switch (fieldName) {
                case "iht400421Completed":
                    setFieldIfBlank(ocrFields::getIht421grossValue, ocrFields::setIht421grossValue,
                            "iht421grossValue", bulkScanConfig.getGrossNetValue(), modifiedList);

                    setFieldIfBlank(ocrFields::getIht421netValue, ocrFields::setIht421netValue,
                            "iht421netValue", bulkScanConfig.getGrossNetValue(), modifiedList);
                    break;
                case "iht207Completed":
                    setFieldIfBlank(ocrFields::getIht207grossValue, ocrFields::setIht207grossValue,
                            "iht207grossValue", bulkScanConfig.getGrossNetValue(), modifiedList);

                    setFieldIfBlank(ocrFields::getIht207netValue, ocrFields::setIht207netValue,
                            "iht207netValue", bulkScanConfig.getGrossNetValue(), modifiedList);
                    break;
                case "iht205Completed":
                    setFieldIfBlank(ocrFields::getIhtGrossValue205, ocrFields::setIhtGrossValue205,
                            "ihtGrossValue205", bulkScanConfig.getGrossNetValue(), modifiedList);

                    setFieldIfBlank(ocrFields::getIhtNetValue205, ocrFields::setIhtNetValue205,
                            "ihtNetValue205", bulkScanConfig.getGrossNetValue(), modifiedList);
                    break;
                case "iht205completedOnline":
                    setFieldIfBlank(ocrFields::getIhtReferenceNumber, ocrFields::setIhtReferenceNumber,
                            "ihtReferenceNumber", "1234", modifiedList);

                    setFieldIfBlank(ocrFields::getIhtGrossValue205, ocrFields::setIhtGrossValue205,
                            "ihtGrossValue205", bulkScanConfig.getGrossNetValue(), modifiedList);

                    setFieldIfBlank(ocrFields::getIhtNetValue205, ocrFields::setIhtNetValue205,
                            "ihtNetValue205", bulkScanConfig.getGrossNetValue(), modifiedList);
                    break;
            }
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

    private void handleGorSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                          List<CollectionMember<ModifiedOCRField>> modifiedFields,
                                          BulkScanConfig bulkScanConfig) {
        if (isBlank(ocrFields.getSolsSolicitorRepresentativeName())) {
            addModifiedField(modifiedFields, "solsSolicitorRepresentativeName",
                    ocrFields.getSolsSolicitorFirmName());
            if (isNotBlank(ocrFields.getSolsSolicitorFirmName())) {
                ocrFields.setSolsSolicitorRepresentativeName(ocrFields.getSolsSolicitorFirmName());
                log.info("Setting solicitor representative name to {}", ocrFields.getSolsSolicitorFirmName());
            } else {
                ocrFields.setSolsSolicitorFirmName(bulkScanConfig.getName());
                log.info("Setting solicitor representative to {}", bulkScanConfig.getName());
            }
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorFirmName, ocrFields::setSolsSolicitorFirmName,
                "solsSolicitorFirmName", bulkScanConfig.getName(), modifiedFields);

        if (isBlank(ocrFields.getSolsSolicitorAppReference())) {
            addModifiedField(modifiedFields, "solsSolicitorAppReference",
                    ocrFields.getSolsSolicitorAppReference());
            if (isNotBlank(ocrFields.getDeceasedSurname())) {
                ocrFields.setSolsSolicitorAppReference(ocrFields.getDeceasedSurname());
                log.info("Setting legal representative name to {}", ocrFields.getSolsSolicitorAppReference());
            } else {
                ocrFields.setSolsSolicitorFirmName(bulkScanConfig.getName());
                log.info("Setting legal representative name to {}", ocrFields.getSolsSolicitorFirmName());
            }
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorAddressLine1, ocrFields::setSolsSolicitorAddressLine1,
                "solsSolicitorAddressLine1", bulkScanConfig.getName(), modifiedFields);

        if (isBlank(ocrFields.getSolsSolicitorAddressLine2()) && isBlank(ocrFields.getSolsSolicitorAddressLine1())
                && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressLine2",
                    ocrFields.getSolsSolicitorAddressLine2());
            ocrFields.setSolsSolicitorAddressLine2(bulkScanConfig.getName());
            log.info("Setting solicitor address line 2 to {}", ocrFields.getSolsSolicitorAddressLine2());
        }

        if (isBlank(ocrFields.getSolsSolicitorAddressTown()) && isBlank(ocrFields.getSolsSolicitorAddressLine1())
                && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressTown",
                    ocrFields.getSolsSolicitorAddressTown());
            ocrFields.setSolsSolicitorAddressTown(bulkScanConfig.getName());
            log.info("Setting solicitor town or city to {}", ocrFields.getSolsSolicitorAddressTown());
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorAddressPostCode, ocrFields::setSolsSolicitorAddressPostCode,
                "solsSolicitorAddressPostCode", bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(ocrFields::getSolsSolicitorEmail, ocrFields::setSolsSolicitorEmail,
                "solsSolicitorEmail", bulkScanConfig.getEmail(), modifiedFields);

        setFieldIfBlank(ocrFields::getSolsSolicitorPhoneNumber, ocrFields::setSolsSolicitorPhoneNumber,
                "solsSolicitorPhoneNumber", bulkScanConfig.getPhone(), modifiedFields);
    }

    public List<CollectionMember<String>> checkWarnings(ExceptionRecordOCRFields ocrFields) {
        List<CollectionMember<String>> warnings = new ArrayList<>();
        long ihtFormCount = Stream.of(
                        ocrFields.getExceptedEstate(),
                        ocrFields.getIht400Completed(),
                        ocrFields.getIht400process(),
                        ocrFields.getIht400421Completed(),
                        ocrFields.getIht207Completed(),
                        ocrFields.getIht205Completed()
                )
                .filter(TRUE::equalsIgnoreCase)
                .count();

        if (ihtFormCount > 1) {
            warnings.add(new CollectionMember<>(null,
                    "More than one IHT form is marked as TRUE. Only one form should be selected as TRUE."));
        }
        return warnings;
    }

    private void setFieldIfBlank(Supplier<String> getter, Consumer<String> setter, String fieldName,
                                 String defaultValue, List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(getter.get())) {
            addModifiedField(modifiedFields, fieldName, getter.get());
            setter.accept(defaultValue);
            log.info("Setting {} to {}", fieldName, defaultValue);
        }
    }

    public List<CollectionMember<ModifiedOCRField>> setDefaultCaveatValues(ExceptionRecordOCRFields
                                                                                   exceptionRecordOCRFields) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();

        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorForenames, exceptionRecordOCRFields::setCaveatorForenames,
                "caveatorForenames", bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorSurnames, exceptionRecordOCRFields::setCaveatorSurnames,
                "caveatorSurnames", bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getDeceasedForenames, exceptionRecordOCRFields::setDeceasedForenames,
                "deceasedForenames", bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getDeceasedSurname, exceptionRecordOCRFields::setDeceasedSurname,
                "deceasedSurname", bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getDeceasedDateOfDeath,
                exceptionRecordOCRFields::setDeceasedDateOfBirth, "deceasedDateOfDeath",
                bulkScanConfig.getDob(), modifiedFields);

        if (BooleanUtils.toBoolean(exceptionRecordOCRFields.getLegalRepresentative())) {
            handleCaveatSolicitorAddressFields(exceptionRecordOCRFields, modifiedFields);
        } else {
            handleCaveatCitizenAddressFields(exceptionRecordOCRFields, modifiedFields);
        }
        return modifiedFields;
    }

    private void handleCaveatSolicitorAddressFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorAddressLine1,
                exceptionRecordOCRFields::setSolsSolicitorAddressLine1,
                "solsSolicitorAddressLine1", bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorAddressPostCode,
                exceptionRecordOCRFields::setSolsSolicitorAddressPostCode,
                "solsSolicitorAddressPostCode", bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorFirmName,
                exceptionRecordOCRFields::setSolsSolicitorFirmName,
                "solsSolicitorFirmName", bulkScanConfig.getName(), modifiedFields);
    }

    private void handleCaveatCitizenAddressFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                      List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorAddressLine1,
                exceptionRecordOCRFields::setCaveatorAddressLine1,
                "caveatorAddressLine1", bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorAddressPostCode,
                exceptionRecordOCRFields::setCaveatorAddressPostCode,
                "caveatorAddressPostCode", bulkScanConfig.getPostcode(), modifiedFields);
    }

}
