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

        if (!isBlank(ocrFields.getDeceasedDateOfDeath()) && isBlank(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate",
                    ocrFields.getDeceasedDiedOnAfterSwitchDate());
            if (exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())) {
                ocrFields.setDeceasedDiedOnAfterSwitchDate("TRUE");
                log.info("Setting deceasedDiedOnAfterSwitchDate to {}", ocrFields.getDeceasedDiedOnAfterSwitchDate());
            } else {
                ocrFields.setDeceasedDiedOnAfterSwitchDate("FALSE");
                log.info("Setting deceasedDiedOnAfterSwitchDate to {}", ocrFields.getDeceasedDiedOnAfterSwitchDate());
            }
        }

        if (isBlank(ocrFields.getPrimaryApplicantForenames())) {
            addModifiedField(modifiedFields, "primaryApplicantForenames",
                    ocrFields.getPrimaryApplicantForenames());
            ocrFields.setPrimaryApplicantForenames(bulkScanConfig.getName());
            log.info("Setting primary applicant forename(s) to {}", ocrFields.getPrimaryApplicantForenames());
        }

        if (isBlank(ocrFields.getPrimaryApplicantSurname())) {
            addModifiedField(modifiedFields, "primaryApplicantSurname",
                    ocrFields.getPrimaryApplicantSurname());
            ocrFields.setPrimaryApplicantSurname(bulkScanConfig.getName());
            log.info("Setting primary applicant surname to {}", ocrFields.getPrimaryApplicantSurname());
        }

        if (isBlank(ocrFields.getPrimaryApplicantAddressLine1())) {
            addModifiedField(modifiedFields, "primaryApplicantAddressLine1",
                    ocrFields.getPrimaryApplicantAddressLine1());
            ocrFields.setPrimaryApplicantAddressLine1(bulkScanConfig.getAddressLine());
            log.info("Setting primary applicant address line 1 to {}", ocrFields.getPrimaryApplicantAddressLine1());
        }

        if (isBlank(ocrFields.getPrimaryApplicantAddressPostCode())) {
            addModifiedField(modifiedFields, "primaryApplicantAddressPostCode",
                    ocrFields.getPrimaryApplicantAddressPostCode());
            ocrFields.setPrimaryApplicantAddressPostCode(bulkScanConfig.getPostcode());
            log.info("Setting primary applicant postcode to {}", ocrFields.getPrimaryApplicantAddressPostCode());
        }

        if (isBlank(ocrFields.getDeceasedSurname())) {
            addModifiedField(modifiedFields, "deceasedSurname", ocrFields.getDeceasedSurname());
            ocrFields.setDeceasedSurname(bulkScanConfig.getName());
            log.info("Setting deceased surname to {}", ocrFields.getDeceasedSurname());
        }

        if (isBlank(ocrFields.getDeceasedAddressLine1())) {
            addModifiedField(modifiedFields, "deceasedAddressLine1", ocrFields.getDeceasedAddressLine1());
            ocrFields.setDeceasedAddressLine1(bulkScanConfig.getName());
            log.info("Setting deceased address line 1 to {}", ocrFields.getDeceasedAddressLine1());
        }

        if (isBlank(ocrFields.getDeceasedAddressPostCode())) {
            addModifiedField(modifiedFields, "deceasedAddressPostCode",
                    ocrFields.getDeceasedAddressPostCode());
            ocrFields.setDeceasedAddressPostCode(bulkScanConfig.getPostcode());
            log.info("Setting deceased postcode to {}", ocrFields.getDeceasedAddressPostCode());
        }

        if (isBlank(ocrFields.getDeceasedDateOfBirth())) {
            addModifiedField(modifiedFields, "deceasedDateOfBirth", ocrFields.getDeceasedDateOfBirth());
            ocrFields.setDeceasedDateOfBirth(bulkScanConfig.getDob());
            log.info("Setting deceased date of birth to {}", ocrFields.getDeceasedDateOfBirth());
        }

        if (isBlank(ocrFields.getDeceasedAnyOtherNames())) {
            addModifiedField(modifiedFields, "deceasedAnyOtherNames",
                    ocrFields.getDeceasedAnyOtherNames());
            ocrFields.setDeceasedAnyOtherNames("FALSE");
            log.info("Setting deceased any other names to {}", ocrFields.getDeceasedAnyOtherNames());
        }

        if (isBlank(ocrFields.getDeceasedDomicileInEngWales())) {
            addModifiedField(modifiedFields, "deceasedDomicileInEngWales",
                    ocrFields.getDeceasedDomicileInEngWales());
            ocrFields.setDeceasedDomicileInEngWales("TRUE");
            log.info("Setting deceased domiciled in Eng/Wales to {}", ocrFields.getDeceasedDomicileInEngWales());
        }

        if (isBlank(ocrFields.getDeceasedForenames())) {
            addModifiedField(modifiedFields, "deceasedForenames", ocrFields.getDeceasedForenames());
            ocrFields.setDeceasedForenames(bulkScanConfig.getName());
            log.info("Setting deceased forename(s) to {}", ocrFields.getDeceasedForenames());
        }

        if (isBlank(ocrFields.getDeceasedSurname())) {
            addModifiedField(modifiedFields, "deceasedSurname", ocrFields.getDeceasedSurname());
            ocrFields.setDeceasedSurname(bulkScanConfig.getName());
            log.info("Setting deceased surname to {}", ocrFields.getDeceasedSurname());
        }

        if (BooleanUtils.toBoolean(ocrFields.getSolsSolicitorIsApplying())) {
            handleGorSolicitorFields(ocrFields, modifiedFields, bulkScanConfig);
        }

        if (isFormVersion3AndSwitchDateValid(ocrFields)) {
            setDefaultIHTValues(ocrFields, modifiedFields, bulkScanConfig);
            if (isIhtFormsNotCompleted(ocrFields)) {
                addModifiedField(modifiedFields, "iht400Completed", ocrFields.getIht400Completed());
                ocrFields.setIht400Completed("TRUE");
                log.info("Setting iht400Completed to {}", ocrFields.getIht400Completed());
                if (isBlank(ocrFields.getIht400process())) {
                    addModifiedField(modifiedFields, "iht400process", ocrFields.getIht400process());
                    ocrFields.setIht400process("TRUE");
                    log.info("Setting iht400process to {}", ocrFields.getIht400process());
                }
                if (isBlank(ocrFields.getProbateGrossValueIht400())) {
                    addModifiedField(modifiedFields, "probateGrossValueIht400",
                            ocrFields.getProbateGrossValueIht400());
                    ocrFields.setProbateGrossValueIht400(bulkScanConfig.getGrossNetValue());
                    log.info("Setting probateGrossValueIht400 to {}", ocrFields.getProbateGrossValueIht400());
                }
                if (isBlank(ocrFields.getProbateNetValueIht400())) {
                    addModifiedField(modifiedFields, "probateNetValueIht400",
                            ocrFields.getProbateNetValueIht400());
                    ocrFields.setProbateNetValueIht400(bulkScanConfig.getGrossNetValue());
                    log.info("Setting probateNetValueIht400 to {}", ocrFields.getProbateNetValueIht400());
                }
            }
        }
        if (isFormVersion2AndSwitchDateValid(ocrFields, exceptedEstateDateOfDeathChecker)) {
            setDefaultIHTValues(ocrFields, modifiedFields, bulkScanConfig);
        }
        if (isFormVersion2Or3AndExceptedEstate(ocrFields)) {
            if (isBlank(ocrFields.getIhtEstateGrossValue())) {
                addModifiedField(modifiedFields, "ihtEstateGrossValue", ocrFields.getIhtEstateGrossValue());
                ocrFields.setIhtEstateGrossValue(bulkScanConfig.getGrossNetValue());
                log.info("Setting ihtEstateGrossValue to {}", ocrFields.getIhtEstateGrossValue());
            }
            if (isBlank(ocrFields.getIhtEstateNetValue())) {
                addModifiedField(modifiedFields, "ihtEstateNetValue", ocrFields.getIhtEstateNetValue());
                ocrFields.setIhtEstateNetValue(bulkScanConfig.getGrossNetValue());
                log.info("Setting ihtEstateNetValue to {}", ocrFields.getIhtEstateNetValue());
            }
            if (isBlank(ocrFields.getIhtEstateNetQualifyingValue())) {
                addModifiedField(modifiedFields, "ihtEstateNetQualifyingValue", ocrFields
                        .getIhtEstateNetQualifyingValue());
                ocrFields.setIhtEstateNetQualifyingValue(bulkScanConfig.getGrossNetValue());
                log.info("Setting ihtEstateNetQualifyingValue to {}", ocrFields.getIhtEstateNetQualifyingValue());
            }
        }
        return modifiedFields;
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
                    if (isBlank(ocrFields.getIht421grossValue())) {
                        addModifiedField(modifiedList, "iht421grossValue", ocrFields.getIht421grossValue());
                        ocrFields.setIht421grossValue(bulkScanConfig.getGrossNetValue());
                        log.info("Setting iht421grossValue to {}", ocrFields.getIht421grossValue());
                    }
                    if (isBlank(ocrFields.getIht421netValue())) {
                        addModifiedField(modifiedList, "iht421netValue", ocrFields.getIht421netValue());
                        ocrFields.setIht421netValue(bulkScanConfig.getGrossNetValue());
                        log.info("Setting iht421netValue to {}", ocrFields.getIht421netValue());
                    }
                    break;
                case "iht207Completed":
                    if (isBlank(ocrFields.getIht207grossValue())) {
                        addModifiedField(modifiedList, "iht207grossValue", ocrFields.getIht207grossValue());
                        ocrFields.setIht207grossValue(bulkScanConfig.getGrossNetValue());
                        log.info("Setting iht207grossValue to {}", ocrFields.getIht207grossValue());
                    }
                    if (isBlank(ocrFields.getIht207netValue())) {
                        addModifiedField(modifiedList, "iht207netValue", ocrFields.getIht207netValue());
                        ocrFields.setIht207netValue(bulkScanConfig.getGrossNetValue());
                        log.info("Setting iht207netValue to {}", ocrFields.getIht207netValue());
                    }
                    break;
                case "iht205Completed":
                    if (isBlank(ocrFields.getIhtGrossValue205())) {
                        addModifiedField(modifiedList, "ihtGrossValue205", ocrFields.getIhtGrossValue205());
                        ocrFields.setIhtGrossValue205(bulkScanConfig.getGrossNetValue());
                        log.info("Setting ihtGrossValue205 to {}", ocrFields.getIhtGrossValue205());
                    }
                    if (isBlank(ocrFields.getIhtNetValue205())) {
                        addModifiedField(modifiedList, "ihtNetValue205", ocrFields.getIhtNetValue205());
                        ocrFields.setIhtNetValue205(bulkScanConfig.getGrossNetValue());
                        log.info("Setting ihtNetValue205 to {}", ocrFields.getIhtNetValue205());
                    }
                    break;
                case "iht205completedOnline":
                    if (isBlank(ocrFields.getIhtReferenceNumber())) {
                        addModifiedField(modifiedList, "ihtReferenceNumber", ocrFields
                                .getIhtReferenceNumber());
                        ocrFields.setIhtReferenceNumber("1234");
                    }
                    if (isBlank(ocrFields.getIhtGrossValue205())) {
                        addModifiedField(modifiedList, "ihtGrossValue205", ocrFields.getIhtGrossValue205());
                        ocrFields.setIhtGrossValue205(bulkScanConfig.getGrossNetValue());
                    }
                    if (isBlank(ocrFields.getIhtNetValue205())) {
                        addModifiedField(modifiedList, "ihtNetValue205", ocrFields.getIhtNetValue205());
                        ocrFields.setIhtNetValue205(bulkScanConfig.getGrossNetValue());
                    }
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
        //not needed -Iswarya as we are setting in OCRToCCDMandatoryField
        /*if (isBlank(ocrFields.getSolsSolicitorIsApplying())
                && isNotBlank(ocrFields.getSolsSolicitorRepresentativeName())
                && isNotBlank(ocrFields.getSolsSolicitorEmail())) {
            addModifiedField(modifiedFields, "solsSolicitorIsApplying",
                    ocrFields.getSolsSolicitorIsApplying());
            ocrFields.setSolsSolicitorIsApplying("TRUE");
            log.info("Setting solicitor is applying to TRUE");
        }*/
        //something is wrong here- Iswarya
        if (isBlank(ocrFields.getSolsSolicitorRepresentativeName())) {
            addModifiedField(modifiedFields, "solsSolicitorRepresentativeName",
                    ocrFields.getSolsSolicitorFirmName());
            if (isNotBlank(ocrFields.getSolsSolicitorFirmName())) {
                ocrFields.setSolsSolicitorRepresentativeName(ocrFields.getSolsSolicitorFirmName());
                log.info("Setting solicitor representative name to {}", ocrFields.getSolsSolicitorFirmName());
            }
        }

        if (isBlank(ocrFields.getSolsSolicitorFirmName())) {
            addModifiedField(modifiedFields, "solsSolicitorFirmName", ocrFields.getSolsSolicitorFirmName());
            ocrFields.setSolsSolicitorFirmName(bulkScanConfig.getName());
            log.info("Setting solicitor firm name to {}", ocrFields.getSolsSolicitorFirmName());
        }

        //Not sure why deceased surname is needed for solicitor app reference -Iswarya
        if (isBlank(ocrFields.getSolsSolicitorAppReference())) {
            addModifiedField(modifiedFields, "solsSolicitorAppReference",
                    ocrFields.getSolsSolicitorAppReference());
            if (isNotBlank(ocrFields.getDeceasedSurname())) {
                ocrFields.setSolsSolicitorAppReference(ocrFields.getDeceasedSurname());
                log.info("Setting legal representative to deceased surname {}",
                        ocrFields.getSolsSolicitorAppReference());
            }
        }

        //isNotBlank will not come into play here - Iswarya
        // TODO - Populate fields from postcode
        if (isBlank(ocrFields.getSolsSolicitorAddressLine1())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressLine1",
                    ocrFields.getSolsSolicitorAddressLine1());
            if (isNotBlank(ocrFields.getSolsSolicitorAddressLine1())) {
                // Add auto population from postcode code here
                ocrFields.setSolsSolicitorAddressLine1(bulkScanConfig.getAddressLine());
                log.info("Setting solicitor firm address line 1 to {}", ocrFields.getSolsSolicitorAddressLine1());
            } else {
                ocrFields.setSolsSolicitorAddressLine1(bulkScanConfig.getName());
                log.info("Setting solicitor firm address line 1 to {}", ocrFields.getSolsSolicitorAddressLine1());
            }
        }

        // TODO - If addressLine1 and Postcode are blank, what should this be? (Awaiting Response from Operations)
        if (isBlank(ocrFields.getSolsSolicitorAddressLine2())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressLine2",
                    ocrFields.getSolsSolicitorAddressLine2());
            if (isBlank(ocrFields.getSolsSolicitorAddressLine1())
                    && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
                ocrFields.setSolsSolicitorAddressLine2(bulkScanConfig.getName());
                log.info("Setting solicitor address line 2 to {}", ocrFields.getSolsSolicitorAddressLine2());
            }
        }

        // TODO - If addressLine1 and Postcode are blank, what should this be? (Awaiting Response from Operations)
        if (isBlank(ocrFields.getSolsSolicitorAddressTown())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressTown",
                    ocrFields.getSolsSolicitorAddressTown());
            if (isBlank(ocrFields.getSolsSolicitorAddressLine1())
                    && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
                ocrFields.setSolsSolicitorAddressTown(bulkScanConfig.getName());
                log.info("Setting solicitor town or city to {}", ocrFields.getSolsSolicitorAddressTown());
            }
        }

        if (isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressPostCode",
                    ocrFields.getSolsSolicitorAddressPostCode());
            ocrFields.setSolsSolicitorAddressPostCode(bulkScanConfig.getPostcode());
            log.info("Setting solicitor postcode to {}", ocrFields.getSolsSolicitorAddressPostCode());
        }

        if (isBlank(ocrFields.getSolsSolicitorEmail())) {
            addModifiedField(modifiedFields, "solsSolicitorEmail",
                    ocrFields.getSolsSolicitorEmail());
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

    public List<CollectionMember<ModifiedOCRField>> setDefaultCaveatValues(ExceptionRecordOCRFields
                                                                                   exceptionRecordOCRFields) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();

        if (isBlank(exceptionRecordOCRFields.getCaveatorForenames())) {
            addModifiedField(modifiedFields, "caveatorForenames",
                    exceptionRecordOCRFields.getCaveatorForenames());
            exceptionRecordOCRFields.setCaveatorForenames(bulkScanConfig.getName());
            log.info("Setting caveator forename(s) to {}", exceptionRecordOCRFields.getCaveatorForenames());
        }

        if (isBlank(exceptionRecordOCRFields.getCaveatorSurnames())) {
            addModifiedField(modifiedFields, "caveatorSurnames",
                    exceptionRecordOCRFields.getCaveatorSurnames());
            exceptionRecordOCRFields.setCaveatorSurnames(bulkScanConfig.getName());
            log.info("Setting caveator surname to {}", exceptionRecordOCRFields.getCaveatorSurnames());
        }

        if (isBlank(exceptionRecordOCRFields.getDeceasedForenames())) {
            addModifiedField(modifiedFields, "deceasedForenames",
                    exceptionRecordOCRFields.getDeceasedForenames());
            exceptionRecordOCRFields.setCaveatorSurnames(bulkScanConfig.getName());
            log.info("Setting caveat deceased forename to {}", exceptionRecordOCRFields.getDeceasedForenames());
        }

        if (isBlank(exceptionRecordOCRFields.getDeceasedSurname())) {
            addModifiedField(modifiedFields, "deceasedSurname",
                    exceptionRecordOCRFields.getDeceasedSurname());
            exceptionRecordOCRFields.setCaveatorSurnames(bulkScanConfig.getName());
            log.info("Setting Caveat Deceased surname to {}", exceptionRecordOCRFields.getDeceasedSurname());
        }

        if (isBlank(exceptionRecordOCRFields.getDeceasedDateOfDeath())) {
            addModifiedField(modifiedFields, "deceasedDateOfDeath",
                    exceptionRecordOCRFields.getDeceasedDateOfDeath());
            exceptionRecordOCRFields.setDeceasedDateOfBirth(bulkScanConfig.getDob());
            log.info("Setting deceased date of death to {}", exceptionRecordOCRFields.getDeceasedDateOfDeath());
        }

        if (BooleanUtils.toBoolean(exceptionRecordOCRFields.getLegalRepresentative())) {
            handleCaveatSolicitorAddressFields(exceptionRecordOCRFields, modifiedFields);
        } else {
            handleCaveatCitizenAddressFields(exceptionRecordOCRFields, modifiedFields);
        }
        return modifiedFields;
    }

    private void handleCaveatSolicitorAddressFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(exceptionRecordOCRFields.getSolsSolicitorAddressLine1())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressLine1",
                    exceptionRecordOCRFields.getSolsSolicitorAddressLine1());
            exceptionRecordOCRFields.setSolsSolicitorAddressLine1(bulkScanConfig.getName());
            log.info("Setting solicitor firm address line 1 to {}",
                    exceptionRecordOCRFields.getSolsSolicitorAddressLine1());
        }
        if (isBlank(exceptionRecordOCRFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, "solsSolicitorAddressPostCode",
                    exceptionRecordOCRFields.getSolsSolicitorAddressPostCode());
            exceptionRecordOCRFields.setSolsSolicitorAddressPostCode(bulkScanConfig.getPostcode());
            log.info("Setting solicitor postcode to {}", exceptionRecordOCRFields.getSolsSolicitorAddressPostCode());
        }
        if (isBlank(exceptionRecordOCRFields.getSolsSolicitorFirmName())) {
            addModifiedField(modifiedFields, "solsSolicitorFirmName",
                    exceptionRecordOCRFields.getSolsSolicitorFirmName());
            exceptionRecordOCRFields.setSolsSolicitorFirmName(bulkScanConfig.getName());
            log.info("Setting solicitor firm name to {}", exceptionRecordOCRFields.getSolsSolicitorFirmName());
        }
    }

    private void handleCaveatCitizenAddressFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                      List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(exceptionRecordOCRFields.getCaveatorAddressLine1())) {
            addModifiedField(modifiedFields, "caveatorAddressLine1",
                    exceptionRecordOCRFields.getCaveatorAddressLine1());
            exceptionRecordOCRFields.setCaveatorAddressLine1(bulkScanConfig.getName());
            log.info("Setting caveator address line 1 to {}", exceptionRecordOCRFields.getCaveatorAddressLine1());
        }
        if (isBlank(exceptionRecordOCRFields.getCaveatorAddressPostCode())) {
            addModifiedField(modifiedFields, "caveatorAddressPostCode",
                    exceptionRecordOCRFields.getCaveatorAddressPostCode());
            exceptionRecordOCRFields.setCaveatorAddressPostCode(bulkScanConfig.getPostcode());
            log.info("Setting caveator address postcode to {}", exceptionRecordOCRFields.getCaveatorAddressPostCode());
        }
    }

}
