package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEAT_FORENAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEAT_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOD;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_FORENAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.LEGAL_REPRESENTATIVE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.NOTIFIED_APPLICANTS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SPOUSE_OR_PARTNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRFieldModifierUtils {

    private final BulkScanConfig bulkScanConfig;
    private final PrimaryApplicantFieldsHandler primaryApplicantFieldsHandler;
    private final DeceasedFieldsHandler deceasedFieldsHandler;
    private final ExecutorsApplyingHandler executorsApplyingHandler;
    private final ExecutorsNotApplyingHandler executorsNotApplyingHandler;
    private final IHTFieldHandler ihtFieldHandler;
    private final SolicitorFieldHandler solicitorFieldHandler;
    private final CaveatSolicitorAddressHandler caveatSolicitorAddressHandler;
    private final CaveatCitizenAddressHandler caveatCitizenAddressHandler;


    public List<CollectionMember<ModifiedOCRField>> setDefaultGorValues(ExceptionRecordOCRFields ocrFields) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();

        handleSolicitorFields(ocrFields, modifiedFields);
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);
        executorsApplyingHandler.handleExecutorsApplyingFields(ocrFields, modifiedFields);
        executorsNotApplyingHandler.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);
        handleCommonFields(ocrFields, modifiedFields);

        return modifiedFields;
    }

    private void handleCommonFields(ExceptionRecordOCRFields ocrFields,
                                    List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(ocrFields::getSpouseOrPartner, ocrFields::setSpouseOrPartner,
                SPOUSE_OR_PARTNER, bulkScanConfig.getFieldsNotCompleted(), modifiedFields);
        setFieldIfBlank(ocrFields::getNotifiedApplicants, ocrFields::setNotifiedApplicants,
                NOTIFIED_APPLICANTS, bulkScanConfig.getFieldsNotCompleted(), modifiedFields);
    }

    private void handleSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(ocrFields::getSolsSolicitorIsApplying, ocrFields::setSolsSolicitorIsApplying,
                SOLICITOR_IS_APPLYING, bulkScanConfig.getSolicitorApplying(), modifiedFields);
        if (BooleanUtils.toBoolean(ocrFields.getSolsSolicitorIsApplying())) {
            solicitorFieldHandler.handleGorSolicitorFields(ocrFields, modifiedFields);
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
        handleCaveatCommonFields(exceptionRecordOCRFields, modifiedFields);

        if (BooleanUtils.toBoolean(exceptionRecordOCRFields.getLegalRepresentative())) {
            caveatSolicitorAddressHandler.handleCaveatSolicitorAddressFields(exceptionRecordOCRFields, modifiedFields);
        } else {
            caveatCitizenAddressHandler.handleCaveatCitizenAddressFields(exceptionRecordOCRFields, modifiedFields);
        }
        return modifiedFields;
    }

    private void handleCaveatCommonFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                          List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorForenames, exceptionRecordOCRFields::setCaveatorForenames,
                CAVEAT_FORENAMES, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorSurnames, exceptionRecordOCRFields::setCaveatorSurnames,
                CAVEAT_SURNAME, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getDeceasedForenames, exceptionRecordOCRFields::setDeceasedForenames,
                DECEASED_FORENAME, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getDeceasedSurname, exceptionRecordOCRFields::setDeceasedSurname,
                DECEASED_SURNAME, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getDeceasedDateOfDeath,
                exceptionRecordOCRFields::setDeceasedDateOfDeath, DECEASED_DOD,
                bulkScanConfig.getDob(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getLegalRepresentative,
                exceptionRecordOCRFields::setLegalRepresentative, LEGAL_REPRESENTATIVE,
                bulkScanConfig.getFieldsNotCompleted(), modifiedFields);
    }
}
