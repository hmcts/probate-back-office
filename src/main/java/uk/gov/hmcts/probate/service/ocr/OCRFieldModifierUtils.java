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

        return modifiedFields;
    }

    private void handleDeceasedFields(ExceptionRecordOCRFields ocrFields,
                                      List<CollectionMember<ModifiedOCRField>> modifiedFields) {

        if (!isBlank(ocrFields.getDeceasedDateOfDeath()) && isBlank(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate",
                    ocrFields.getDeceasedDiedOnAfterSwitchDate());
            String switchDateValue = exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields
                    .getDeceasedDateOfDeath()) ? "TRUE" : "FALSE";
            ocrFields.setDeceasedDiedOnAfterSwitchDate(switchDateValue);
            log.info("Setting deceasedDiedOnAfterSwitchDate to {}", switchDateValue);
        }

        // If DoD is blank but switch date is set we can avoid warnings and auto create by default setting DoD
        if (isBlank(ocrFields.getDeceasedDateOfDeath()) && !isBlank(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            String switchDateValue = exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields
                    .getDeceasedDateOfDeath()) ? "TRUE" : "FALSE";
            addModifiedField(modifiedFields, "deceasedDateOfDeath",
                    ocrFields.getDeceasedDiedOnAfterSwitchDate());
            if (switchDateValue.equals("TRUE")) {
                ocrFields.setDeceasedDateOfDeath(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue());
                log.info("Setting Date of Death to {} due to died on or after switch date value",
                        ocrFields.getDeceasedDateOfDeath());
            } else if (switchDateValue.equals("FALSE")) {
                ocrFields.setDeceasedDateOfDeath(bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse());
                log.info("Setting Date of Death to {} due to died on or after switch date value",
                        ocrFields.getDeceasedDateOfDeath());
            }
        }


        if (isBlank(ocrFields.getDeceasedDateOfDeath()) && isBlank(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            if (!isBlank(ocrFields.getIht205Completed()) || (!isBlank(ocrFields.getIhtGrossValue205()))
                    || (!isBlank(ocrFields.getIhtNetValue205()))) {

                addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate",
                        ocrFields.getDeceasedDiedOnAfterSwitchDate());
                ocrFields.setDeceasedDiedOnAfterSwitchDate(
                        bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateFalse());

                log.info("Setting Died on or after switch date to {}",
                        ocrFields.getDeceasedDiedOnAfterSwitchDate());

            } else if (!isBlank(ocrFields.getIhtEstateNetValue()) || (!isBlank(ocrFields.getIhtEstateGrossValue()))
                || !isBlank(ocrFields.getExceptedEstate())) {
                addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate",
                        ocrFields.getDeceasedDiedOnAfterSwitchDate());

                ocrFields.setDeceasedDiedOnAfterSwitchDate(
                        bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue());
                log.info("Setting Died on or after switch date to {}",
                        ocrFields.getDeceasedDiedOnAfterSwitchDate());
            } else {
                addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate",
                        ocrFields.getDeceasedDiedOnAfterSwitchDate());

                ocrFields.setDeceasedDiedOnAfterSwitchDate(
                        bulkScanConfig.getDateOfDeathForDiedOnOrAfterSwitchDateTrue());
                log.info("Setting Died on or after switch date to {}",
                        ocrFields.getDeceasedDiedOnAfterSwitchDate());
            }
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
                "deceasedAnyOtherNames", "FALSE", modifiedFields);
        setFieldIfBlank(ocrFields::getDeceasedDomicileInEngWales, ocrFields::setDeceasedDomicileInEngWales,
                "deceasedDomicileInEngWales", "TRUE", modifiedFields);
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

        if (isBlank(ocrFields.getPrimaryApplicantAlias())) {
            addModifiedField(modifiedFields, "primaryApplicantAlias", ocrFields.getPrimaryApplicantAlias());
            ocrFields.setPrimaryApplicantAlias(bulkScanConfig.getName());
            log.info("Setting primaryApplicantAlias to {}", ocrFields.getPrimaryApplicantAlias());
        }

        if (isBlank(ocrFields.getPrimaryApplicantHasAlias())) {
            addModifiedField(modifiedFields, "primaryApplicantHasAlias",
                    ocrFields.getPrimaryApplicantHasAlias());
            ocrFields.setPrimaryApplicantAlias(bulkScanConfig.getPrimaryApplicantHasAlias()); //Set False
            log.info("Setting primaryApplicantHasAlias to {}", ocrFields.getPrimaryApplicantHasAlias());
        }
    }

    private void handleSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (BooleanUtils.toBoolean(ocrFields.getSolsSolicitorIsApplying())) {
            handleGorSolicitorFields(ocrFields, modifiedFields, bulkScanConfig);
        }
    }

    private void handleIHTFields(ExceptionRecordOCRFields ocrFields,
                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isFormVersion3AndSwitchDateValid(ocrFields)
                || isFormVersion2AndSwitchDateValid(ocrFields, exceptedEstateDateOfDeathChecker)
                || isFormVersion1Valid(ocrFields)) {

            setDefaultIHTValues(ocrFields, modifiedFields, bulkScanConfig);
            if (isIhtFormsNotCompleted(ocrFields)) {
                addModifiedField(modifiedFields, "iht400Completed", ocrFields.getIht400Completed());
                ocrFields.setIht400Completed("TRUE");
                log.info("Setting iht400Completed to {}", ocrFields.getIht400Completed());
                setFieldIfBlank(ocrFields::getIht400process, ocrFields::setIht400process,
                        "iht400process", "TRUE", modifiedFields);

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
        return "3".equals(ocrFields.getFormVersion()) && ("true".equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate())
                || "false".equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate()));
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
        return ("2".equals(ocrFields.getFormVersion()) && "True".equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate()))
                || ("3".equals(ocrFields.getFormVersion()) && "True".equalsIgnoreCase(ocrFields.getExceptedEstate()));
    }

    private boolean isIhtFormsNotCompleted(ExceptionRecordOCRFields ocrFields) {
        return "false".equalsIgnoreCase(ocrFields.getIht400421Completed()) && "false".equalsIgnoreCase(ocrFields
                .getIht207Completed()) && "false".equalsIgnoreCase(ocrFields
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
        } else if ("true".equalsIgnoreCase(fieldValue)) {
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

        if (isBlank(ocrFields.getSolsSolicitorFirmName())) {
            addModifiedField(modifiedFields, "solsSolicitorFirmName", ocrFields.getSolsSolicitorFirmName());
            ocrFields.setSolsSolicitorFirmName(bulkScanConfig.getName());
            log.info("Setting solicitor firm name to {}", ocrFields.getSolsSolicitorFirmName());
        }

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

        if (isBlank(ocrFields.getSolsSolicitorAddressLine1())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressLine1",
                    ocrFields.getSolsSolicitorAddressLine1());
            ocrFields.setSolsSolicitorAddressLine1(bulkScanConfig.getName());
            log.info("Setting solicitor address line 1 to {}", ocrFields.getSolsSolicitorAddressLine1());
        }

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

        if (isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressPostCode",
                    ocrFields.getSolsSolicitorAddressPostCode());
            ocrFields.setSolsSolicitorAddressPostCode(bulkScanConfig.getPostcode());
            log.info("Setting solicitor postcode to {}", ocrFields.getSolsSolicitorAddressPostCode());
        }

        if (isBlank(ocrFields.getSolsSolicitorEmail())) {
            addModifiedField(modifiedFields, "solsSolicitorEmail", ocrFields.getSolsSolicitorEmail());
            ocrFields.setSolsSolicitorEmail(bulkScanConfig.getEmail());
            log.info("Setting solicitor email to {}", ocrFields.getSolsSolicitorEmail());
        }

        if (isBlank(ocrFields.getSolsSolicitorPhoneNumber())) {
            addModifiedField(modifiedFields, "solsSolicitorPhoneNumber",
                    ocrFields.getSolsSolicitorPhoneNumber());
            ocrFields.setSolsSolicitorPhoneNumber(bulkScanConfig.getPhone());
            log.info("Setting solicitor phone to {}", ocrFields.getSolsSolicitorPhoneNumber());
        }
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
                .filter("TRUE"::equalsIgnoreCase)
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
