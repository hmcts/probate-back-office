package uk.gov.hmcts.probate.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.BILINGUAL_GRANT;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SPOUSE_OR_PARTNER;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.THREE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.TWO;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.WILL_DATE;

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
    private static final String POSTCODE_REGEX_PATTERN = "^([A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}|GIR ?0A{2})$";

    public List<CollectionMember<ModifiedOCRField>> setDefaultGorValues(ExceptionRecordOCRFields ocrFields,
                                                                        GrantType grantType) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();

        handleSolicitorFields(ocrFields, modifiedFields);
        primaryApplicantFieldsHandler.handleGorPrimaryApplicantFields(ocrFields, modifiedFields);
        deceasedFieldsHandler.handleDeceasedFields(ocrFields, modifiedFields);
        ihtFieldHandler.handleIHTFields(ocrFields, modifiedFields);
        executorsApplyingHandler.handleExecutorsApplyingFields(ocrFields, modifiedFields);
        executorsNotApplyingHandler.handleExecutorsNotApplyingFields(ocrFields, modifiedFields);
        handleCommonFields(ocrFields, modifiedFields, grantType);
        handlePostCodeValidation(ocrFields, modifiedFields);

        return modifiedFields;
    }

    private void handlePostCodeValidation(ExceptionRecordOCRFields ocrFields,
                                          List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String defaultPostCode = bulkScanConfig.getPostcode();

        // Define the mapping once: Name -> Getter -> Setter
        List<PostCodeMapping> mappings = List.of(
                new PostCodeMapping(PRIMARY_APPLICANT_ADDRESS_POST_CODE,
                        ocrFields::getPrimaryApplicantAddressPostCode,
                        ocrFields::setPrimaryApplicantAddressPostCode),
                new PostCodeMapping(DECEASED_POST_CODE,
                        ocrFields::getDeceasedAddressPostCode,
                        ocrFields::setDeceasedAddressPostCode),
                new PostCodeMapping(EXECUTORS_APPLYING_0_ADDRESS_POST_CODE,
                        ocrFields::getExecutorsApplying0applyingExecutorAddressPostCode,
                        ocrFields::setExecutorsApplying0applyingExecutorAddressPostCode),
                new PostCodeMapping(EXECUTORS_APPLYING_1_ADDRESS_POST_CODE,
                        ocrFields::getExecutorsApplying1applyingExecutorAddressPostCode,
                        ocrFields::setExecutorsApplying1applyingExecutorAddressPostCode),
                new PostCodeMapping(EXECUTORS_APPLYING_2_ADDRESS_POST_CODE,
                        ocrFields::getExecutorsApplying2applyingExecutorAddressPostCode,
                        ocrFields::setExecutorsApplying2applyingExecutorAddressPostCode),
                new PostCodeMapping(SOLICITOR_ADDRESS_POST_CODE,
                        ocrFields::getSolsSolicitorAddressPostCode,
                        ocrFields::setSolsSolicitorAddressPostCode)
        );

        for (PostCodeMapping mapping : mappings) {
            String currentPostCode = mapping.getter.get();
            if (isNotBlank(currentPostCode) && !currentPostCode.matches(POSTCODE_REGEX_PATTERN)) {
                log.info("Set invalid postcode {} from {} to {}", mapping.fieldName, currentPostCode, defaultPostCode);
                mapping.setter.accept(defaultPostCode);
                addModifiedField(modifiedFields, mapping.fieldName, currentPostCode);
            }
        }
    }

    private record PostCodeMapping(String fieldName, Supplier<String> getter, Consumer<String> setter) {}

    private void handleCommonFields(ExceptionRecordOCRFields ocrFields,
                                    List<CollectionMember<ModifiedOCRField>> modifiedFields, GrantType grantType) {
        setFieldIfBlank(ocrFields::getSpouseOrPartner, ocrFields::setSpouseOrPartner,
                SPOUSE_OR_PARTNER, bulkScanConfig.getFieldsNotCompleted(), modifiedFields);
        setFieldIfBlank(ocrFields::getBilingualGrantRequested, ocrFields::setBilingualGrantRequested,
                BILINGUAL_GRANT, bulkScanConfig.getFieldsNotCompleted(), modifiedFields);
        if (GrantType.GRANT_OF_PROBATE.equals(grantType)) {
            setFieldIfBlank(ocrFields::getWillDate, ocrFields::setWillDate,
                    WILL_DATE, bulkScanConfig.getDob(), modifiedFields);
        }
    }

    private void handleSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isNotBlank(ocrFields.getSolsSolicitorRepresentativeName()) || isNotBlank(ocrFields
                .getSolsSolicitorFirmName())) {
            setFieldIfBlank(ocrFields::getSolsSolicitorIsApplying, ocrFields::setSolsSolicitorIsApplying,
                    SOLICITOR_IS_APPLYING, bulkScanConfig.getSolsSolicitorIsApplying(), modifiedFields);
        } else {
            setFieldIfBlank(ocrFields::getSolsSolicitorIsApplying, ocrFields::setSolsSolicitorIsApplying,
                    SOLICITOR_IS_APPLYING, bulkScanConfig.getSolicitorNotApplying(), modifiedFields);
        }

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
                        TRUE.equalsIgnoreCase(ocrFields.getIht400Completed()),
                        TRUE.equalsIgnoreCase(ocrFields.getIht400421Completed()),
                        TRUE.equalsIgnoreCase(ocrFields.getIht207Completed()),
                        TRUE.equalsIgnoreCase(ocrFields.getIht205Completed()),
                        (isFormVersion2Valid(ocrFields)
                                && TRUE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate()))
                                || (isFormVersion3Valid(ocrFields)
                                && TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate()))
                )
                .filter(Boolean::booleanValue)
                .count();

        if (ihtFormCount > 1) {
            warnings.add(new CollectionMember<>(null,
                    "More than one IHT form is marked as TRUE. Only one form should be selected as TRUE."));
        }
        return warnings;
    }

    public boolean isFormVersion2Valid(ExceptionRecordOCRFields ocrFields) {
        return TWO.equals(ocrFields.getFormVersion());
    }

    public boolean isFormVersion3Valid(ExceptionRecordOCRFields ocrFields) {
        return THREE.equals(ocrFields.getFormVersion());
    }

    private void setFieldIfBlank(Supplier<String> getter, Consumer<String> setter, String fieldName,
                                 String defaultValue, List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (isBlank(getter.get())) {
            addModifiedField(modifiedFields, fieldName, getter.get());
            setter.accept(defaultValue);
            log.info("Setting {} to {}", fieldName, defaultValue);
        }
    }
}
