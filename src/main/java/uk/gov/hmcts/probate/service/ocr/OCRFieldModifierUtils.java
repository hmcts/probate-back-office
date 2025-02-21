package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class OCRFieldModifierUtils {

    private final BulkScanConfig bulkScanConfig;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    public List<CollectionMember<ModifiedOCRField>> setDefaultValues(ExceptionRecordOCRFields ocrFields) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new
                ArrayList<>();
        if (!isBlank(ocrFields.getDeceasedDateOfDeath()) && isBlank(ocrFields.getDeceasedDiedOnAfterSwitchDate())) {
            addModifiedField(modifiedFields, "deceasedDiedOnAfterSwitchDate",
                    ocrFields.getDeceasedDiedOnAfterSwitchDate());
            if (exceptedEstateDateOfDeathChecker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath())) {
                ocrFields.setDeceasedDiedOnAfterSwitchDate("TRUE");
            } else {
                ocrFields.setDeceasedDiedOnAfterSwitchDate("FALSE");
            }
        }
        if (isFormVersion3AndSwitchDateValid(ocrFields)) {
            setDefaultIHTValues(ocrFields, modifiedFields, bulkScanConfig);
            if (isIhtFormsNotCompleted(ocrFields)) {
                addModifiedField(modifiedFields, "iht400Completed", ocrFields.getIht400Completed());
                ocrFields.setIht400Completed("TRUE");
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
                    break;
                case "iht207Completed":
                    ocrFields.setIht207Completed(bulkScanConfig.getIhtForm());
                    break;
                case "iht205Completed":
                    ocrFields.setIht205Completed(bulkScanConfig.getIhtForm());
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
                    }
                    if (isBlank(ocrFields.getIht421netValue())) {
                        addModifiedField(modifiedList, "iht421netValue", ocrFields.getIht421netValue());
                        ocrFields.setIht421netValue(bulkScanConfig.getGrossNetValue());
                    }
                    break;
                case "iht207Completed":
                    if (isBlank(ocrFields.getIht207grossValue())) {
                        addModifiedField(modifiedList, "iht207grossValue", ocrFields.getIht207grossValue());
                        ocrFields.setIht207grossValue(bulkScanConfig.getGrossNetValue());
                    }
                    if (isBlank(ocrFields.getIht207netValue())) {
                        addModifiedField(modifiedList, "iht207netValue", ocrFields.getIht207netValue());
                        ocrFields.setIht207netValue(bulkScanConfig.getGrossNetValue());
                    }
                    break;
                case "iht205Completed":
                    if (isBlank(ocrFields.getIhtGrossValue205())) {
                        addModifiedField(modifiedList, "ihtGrossValue205", ocrFields.getIhtGrossValue205());
                        ocrFields.setIhtGrossValue205(bulkScanConfig.getGrossNetValue());
                    }
                    if (isBlank(ocrFields.getIhtNetValue205())) {
                        addModifiedField(modifiedList, "ihtNetValue205", ocrFields.getIhtNetValue205());
                        ocrFields.setIhtNetValue205(bulkScanConfig.getGrossNetValue());
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
}
