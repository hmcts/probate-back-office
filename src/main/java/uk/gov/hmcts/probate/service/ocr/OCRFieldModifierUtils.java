package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRFieldModifierUtils {

    private final BulkScanConfig bulkScanConfig;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;
    public static final String IHT_FORM_NOT_REQUIRED = "exceptedEstate";
    public static final String IHT400_COMPLETED = "iht400completed";
    public static final String IHT_400_PROCESS = "iht400process";
    public static final String IHT400421_COMPLETED = "iht400421completed";
    public static final String IHT207_COMPLETED = "iht207completed";
    public static final String IHT205_COMPLETED = "iht205completed";

    public List<CollectionMember<ModifiedOCRField>> setDefaultValues(ExceptionRecordOCRFields ocrFields) {

        List<CollectionMember<ModifiedOCRField>> modifiedFields = new
                ArrayList<>();
        if (!isBlank(ocrFields.getDeceasedDateOfDeath()) && isBlank(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate",
                    ocrFields.getDeceasedDiedOnAfterSwitchDate());
            if (exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())) {
                log.info("Setting deceasedDiedOnAfterSwitchDate to TRUE");
                ocrFields.setDeceasedDiedOnAfterSwitchDate("TRUE");
                log.info("Setting deceasedDiedOnAfterSwitchDate {}", ocrFields.getDeceasedDiedOnAfterSwitchDate());
            } else {
                log.info("Setting deceasedDiedOnAfterSwitchDate to FALSE");
                ocrFields.setDeceasedDiedOnAfterSwitchDate("FALSE");
                log.info("Setting deceasedDiedOnAfterSwitchDate {}", ocrFields.getDeceasedDiedOnAfterSwitchDate());
            }
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
            }
            if (isBlank(ocrFields.getIhtEstateNetValue())) {
                addModifiedField(modifiedFields, "ihtEstateNetValue", ocrFields.getIhtEstateNetValue());
                ocrFields.setIhtEstateNetValue(bulkScanConfig.getGrossNetValue());
            }
            if (isBlank(ocrFields.getIhtEstateNetQualifyingValue())) {
                addModifiedField(modifiedFields, "ihtEstateNetQualifyingValue", ocrFields
                        .getIhtEstateNetQualifyingValue());
                ocrFields.setIhtEstateNetQualifyingValue(bulkScanConfig.getGrossNetValue());
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

    public List<String> checkWarnings(ExceptionRecordOCRFields ocrFields) {
        List<String> warnings = new ArrayList<>();
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
            warnings.add("More than one IHT form is marked as TRUE. Only one form should be selected as TRUE.");
        }
        return warnings;
    }
}
