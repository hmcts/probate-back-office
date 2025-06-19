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
import static uk.gov.hmcts.probate.model.Constants.TRUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.BILINGUAL_GRANT;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SPOUSE_OR_PARTNER;
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

        return modifiedFields;
    }

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
}
