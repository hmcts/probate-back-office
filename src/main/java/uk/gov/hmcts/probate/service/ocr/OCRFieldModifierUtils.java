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
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEAT_FORENAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEAT_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.CAVEATOR_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_ANY_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOB;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOD;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_DOMICILE_IN_ENG_WALES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_FORENAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.DECEASED_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_LINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_0_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_1_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTORS_APPLYING_2_OTHER_NAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_0_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_1_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.EXECUTOR_NOT_APPLYING_2_REASON;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT205;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT207;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT400;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.FORM_IHT400421;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400421;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_205_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_207;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_207_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_207_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_421_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_421_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_400_PROCESS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_ESTATE_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_ESTATE_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_ESTATE_NET_QUALIFYING_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_FORM_ID;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_FORM_COMPLETED_ONLINE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_GROSS_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_GROSS_VALUE_EXCEPTED_ESTATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_NET_VALUE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_NET_VALUE_EXCEPTED_ESTATE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.IHT_REFERENCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.NOTIFIED_APPLICANTS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_FORENAMES;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_HAS_ALIAS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_SURNAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ALIAS;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.PRIMARY_APPLICANT_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_LINE1;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_LINE2;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_POST_CODE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_ADDRESS_TOWN;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_APP_REFERENCE;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_EMAIL;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_FIRM_NAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_IS_APPLYING;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_PHONE_NUMBER;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SOLICITOR_REPRESENTATIVE_NAME;
import static uk.gov.hmcts.probate.model.DummyValuesConstants.SPOUSE_OR_PARTNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRFieldModifierUtils {

    private final BulkScanConfig bulkScanConfig;
    private final ExceptedEstateDateOfDeathChecker exceptedEstateDateOfDeathChecker;

    public List<CollectionMember<ModifiedOCRField>> setDefaultGorValues(ExceptionRecordOCRFields ocrFields) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();

        handleSolicitorFields(ocrFields, modifiedFields);
        handlePrimaryApplicantFields(ocrFields, modifiedFields);
        handleDeceasedFields(ocrFields, modifiedFields);
        handleIHTFields(ocrFields, modifiedFields);
        handleExecutorsNotApplyingFields(ocrFields, modifiedFields);
        handleExecutorsApplyingFields(ocrFields, modifiedFields);
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

    private boolean isValidIhtFormId(String ihtFormId) {
        return Stream.of(FORM_IHT205, FORM_IHT207, FORM_IHT400421, FORM_IHT400)
                .anyMatch(form -> form.equalsIgnoreCase(ihtFormId));
    }

    private void handleSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(ocrFields::getSolsSolicitorIsApplying, ocrFields::setSolsSolicitorIsApplying,
                SOLICITOR_IS_APPLYING, bulkScanConfig.getSolicitorApplying(), modifiedFields);
        if (BooleanUtils.toBoolean(ocrFields.getSolsSolicitorIsApplying())) {
            handleGorSolicitorFields(ocrFields, modifiedFields, bulkScanConfig);
        }
    }

    private void handleGorSolicitorFields(ExceptionRecordOCRFields ocrFields,
                                          List<CollectionMember<ModifiedOCRField>> modifiedFields,
                                          BulkScanConfig bulkScanConfig) {
        if (isBlank(ocrFields.getSolsSolicitorRepresentativeName())) {
            addModifiedField(modifiedFields, SOLICITOR_REPRESENTATIVE_NAME,
                    ocrFields.getSolsSolicitorRepresentativeName());
            if (isNotBlank(ocrFields.getSolsSolicitorFirmName())) {
                ocrFields.setSolsSolicitorRepresentativeName(ocrFields.getSolsSolicitorFirmName());
                log.info("Setting solicitor representative name to {}", ocrFields.getSolsSolicitorFirmName());
            } else {
                ocrFields.setSolsSolicitorRepresentativeName(bulkScanConfig.getName());
                log.info("Setting solicitor representative to {}", bulkScanConfig.getName());
            }
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorFirmName, ocrFields::setSolsSolicitorFirmName,
                SOLICITOR_FIRM_NAME, bulkScanConfig.getName(), modifiedFields);

        if (isBlank(ocrFields.getSolsSolicitorAppReference())) {
            addModifiedField(modifiedFields, SOLICITOR_APP_REFERENCE,
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
                SOLICITOR_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);

        if (isBlank(ocrFields.getSolsSolicitorAddressLine2()) && isBlank(ocrFields.getSolsSolicitorAddressLine1())
                && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, SOLICITOR_ADDRESS_LINE2,
                    ocrFields.getSolsSolicitorAddressLine2());
            ocrFields.setSolsSolicitorAddressLine2(bulkScanConfig.getName());
            log.info("Setting solicitor address line 2 to {}", ocrFields.getSolsSolicitorAddressLine2());
        }

        if (isBlank(ocrFields.getSolsSolicitorAddressTown()) && isBlank(ocrFields.getSolsSolicitorAddressLine1())
                && isBlank(ocrFields.getSolsSolicitorAddressPostCode())) {
            addModifiedField(modifiedFields, SOLICITOR_ADDRESS_TOWN,
                    ocrFields.getSolsSolicitorAddressTown());
            ocrFields.setSolsSolicitorAddressTown(bulkScanConfig.getName());
            log.info("Setting solicitor town or city to {}", ocrFields.getSolsSolicitorAddressTown());
        }

        setFieldIfBlank(ocrFields::getSolsSolicitorAddressPostCode, ocrFields::setSolsSolicitorAddressPostCode,
                SOLICITOR_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(ocrFields::getSolsSolicitorEmail, ocrFields::setSolsSolicitorEmail,
                SOLICITOR_EMAIL, bulkScanConfig.getEmail(), modifiedFields);

        setFieldIfBlank(ocrFields::getSolsSolicitorPhoneNumber, ocrFields::setSolsSolicitorPhoneNumber,
                SOLICITOR_PHONE_NUMBER, bulkScanConfig.getPhone(), modifiedFields);
    }


    private void handlePrimaryApplicantFields(ExceptionRecordOCRFields ocrFields,
                                              List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (!BooleanUtils.toBoolean(ocrFields.getSolsSolicitorIsApplying())) {
            handleGorPrimaryApplicantFields(ocrFields, modifiedFields, bulkScanConfig);
        }
    }

    private void handleGorPrimaryApplicantFields(ExceptionRecordOCRFields ocrFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields,
                                                 BulkScanConfig bulkScanConfig) {
        setFieldIfBlank(ocrFields::getPrimaryApplicantForenames, ocrFields::setPrimaryApplicantForenames,
                PRIMARY_APPLICANT_FORENAMES, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantSurname, ocrFields::setPrimaryApplicantSurname,
                PRIMARY_APPLICANT_SURNAME, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantAddressLine1, ocrFields::setPrimaryApplicantAddressLine1,
                PRIMARY_APPLICANT_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);
        setFieldIfBlank(ocrFields::getPrimaryApplicantAddressPostCode, ocrFields::setPrimaryApplicantAddressPostCode,
                PRIMARY_APPLICANT_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(ocrFields::getPrimaryApplicantHasAlias, ocrFields::setPrimaryApplicantHasAlias,
                PRIMARY_APPLICANT_HAS_ALIAS, bulkScanConfig.getPrimaryApplicantHasAlias(), modifiedFields);

        if (TRUE.equalsIgnoreCase(ocrFields.getPrimaryApplicantHasAlias()) && isBlank(
                ocrFields.getPrimaryApplicantAlias())) {
            addModifiedField(modifiedFields, PRIMARY_APPLICANT_ALIAS, ocrFields
                    .getPrimaryApplicantAlias());
            ocrFields.setPrimaryApplicantAlias(bulkScanConfig.getNames());
        }
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

    private void handleExecutorsApplyingFields(ExceptionRecordOCRFields ocrFields,
                                               List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (!isBlank(ocrFields.getExecutorsApplying0applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying0applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying0applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, EXECUTORS_APPLYING_0_OTHER_NAMES, ocrFields
                        .getExecutorsApplying0applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying0applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressLine1,
                    EXECUTORS_APPLYING_0_ADDRESS_LINE, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressTown,
                    EXECUTORS_APPLYING_0_ADDRESS_TOWN, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying0applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying0applyingExecutorAddressPostCode,
                    EXECUTORS_APPLYING_0_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
        if (!isBlank(ocrFields.getExecutorsApplying1applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying1applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying1applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, EXECUTORS_APPLYING_1_OTHER_NAMES, ocrFields
                        .getExecutorsApplying1applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying1applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressLine1,
                    EXECUTORS_APPLYING_1_ADDRESS_LINE, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressTown,
                    EXECUTORS_APPLYING_1_ADDRESS_TOWN, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying1applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying1applyingExecutorAddressPostCode,
                    EXECUTORS_APPLYING_1_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
        if (!isBlank(ocrFields.getExecutorsApplying2applyingExecutorName())) {
            if (TRUE.equalsIgnoreCase(ocrFields.getExecutorsApplying2applyingExecutorDifferentNameToWill()) && isBlank(
                    ocrFields.getExecutorsApplying2applyingExecutorOtherNames())) {
                addModifiedField(modifiedFields, EXECUTORS_APPLYING_2_OTHER_NAMES, ocrFields
                        .getExecutorsApplying2applyingExecutorOtherNames());
                ocrFields.setExecutorsApplying2applyingExecutorOtherNames(bulkScanConfig.getNames());
            }
            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressLine1,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressLine1,
                    EXECUTORS_APPLYING_2_ADDRESS_LINE, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressTown,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressTown,
                    EXECUTORS_APPLYING_2_ADDRESS_TOWN, bulkScanConfig.getName(),
                    modifiedFields);

            setFieldIfBlank(ocrFields::getExecutorsApplying2applyingExecutorAddressPostCode,
                    ocrFields::setExecutorsApplying2applyingExecutorAddressPostCode,
                    EXECUTORS_APPLYING_2_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(),
                    modifiedFields);
        }
    }

    public void handleExecutorsNotApplyingFields(ExceptionRecordOCRFields ocrFields,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        if (!isBlank(ocrFields.getExecutorsNotApplying0notApplyingExecutorName()) && isBlank(
                ocrFields.getExecutorsNotApplying0notApplyingExecutorReason())) {
            addModifiedField(modifiedFields, EXECUTOR_NOT_APPLYING_0_REASON, ocrFields
                    .getExecutorsNotApplying0notApplyingExecutorReason());
            ocrFields.setExecutorsNotApplying0notApplyingExecutorReason(bulkScanConfig.getExecutorsNotApplyingReason());
        }

        if (!isBlank(ocrFields.getExecutorsNotApplying1notApplyingExecutorName()) && isBlank(
                ocrFields.getExecutorsNotApplying1notApplyingExecutorReason())) {
            addModifiedField(modifiedFields, EXECUTOR_NOT_APPLYING_1_REASON, ocrFields
                    .getExecutorsNotApplying1notApplyingExecutorReason());
            ocrFields.setExecutorsNotApplying1notApplyingExecutorReason(bulkScanConfig.getExecutorsNotApplyingReason());
        }

        if (!isBlank(ocrFields.getExecutorsNotApplying2notApplyingExecutorName()) && isBlank(
                ocrFields.getExecutorsNotApplying2notApplyingExecutorReason())) {
            addModifiedField(modifiedFields, EXECUTOR_NOT_APPLYING_2_REASON, ocrFields
                    .getExecutorsNotApplying2notApplyingExecutorReason());
            ocrFields.setExecutorsNotApplying2notApplyingExecutorReason(bulkScanConfig.getExecutorsNotApplyingReason());
        }
    }

    private void setDefaultValues(ExceptionRecordOCRFields ocrFields,
                                  List<CollectionMember<ModifiedOCRField>> modifiedFields,
                                  String switchDateValue, String dateOfDeathValue) {
        addModifiedField(modifiedFields, DECEASED_DIED_ON_OR_AFTER_SWITCH_DATE, ocrFields
                .getDeceasedDiedOnAfterSwitchDate());
        ocrFields.setDeceasedDiedOnAfterSwitchDate(switchDateValue);

        addModifiedField(modifiedFields, DECEASED_DOD, ocrFields.getDeceasedDateOfDeath());
        ocrFields.setDeceasedDateOfDeath(dateOfDeathValue);

        log.info("Setting deceasedDiedOnAfterSwitchDate to {}", switchDateValue);
    }

    private void handleIHTFields(ExceptionRecordOCRFields ocrFields,
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

    private boolean isFormVersionValid(ExceptionRecordOCRFields ocrFields) {
        return isFormVersion3AndSwitchDateValid(ocrFields)
                || isFormVersion2AndSwitchDateValid(ocrFields, exceptedEstateDateOfDeathChecker);
    }

    private void setEstateValues(ExceptionRecordOCRFields ocrFields,
                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
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
            } else if (isValidIhtFormId(ihtFormId)) {
                setFieldIfBlank(ocrFields::getIhtGrossValue, ocrFields::setIhtGrossValue,
                        IHT_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
                setFieldIfBlank(ocrFields::getIhtNetValue, ocrFields::setIhtNetValue,
                        IHT_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedFields);
            }
        }
    }

    private boolean isFormVersion3AndSwitchDateValid(ExceptionRecordOCRFields ocrFields) {
        return isFormVersion3Valid(ocrFields) && (TRUE.equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate())
                || FALSE.equalsIgnoreCase(ocrFields.getDeceasedDiedOnAfterSwitchDate()));
    }

    private boolean isFormVersion2AndSwitchDateValid(ExceptionRecordOCRFields ocrFields,
                                                     ExceptedEstateDateOfDeathChecker checker) {
        return isFormVersion2Valid(ocrFields) && (checker.isOnOrAfterSwitchDate(ocrFields
                .getDeceasedDateOfDeath()) || !checker.isOnOrAfterSwitchDate(ocrFields.getDeceasedDateOfDeath()));
    }

    private boolean isFormVersion1Valid(ExceptionRecordOCRFields ocrFields) {
        return "1".equals(ocrFields.getFormVersion());
    }

    private boolean isFormVersion2Valid(ExceptionRecordOCRFields ocrFields) {
        return "2".equals(ocrFields.getFormVersion());
    }

    private boolean isFormVersion3Valid(ExceptionRecordOCRFields ocrFields) {
        return "3".equals(ocrFields.getFormVersion());
    }

    private boolean isFormVersion2Or3AndExceptedEstate(ExceptionRecordOCRFields ocrFields) {
        return (isFormVersion2Valid(ocrFields) && TRUE.equalsIgnoreCase(ocrFields
                .getDeceasedDiedOnAfterSwitchDate()))
                || (isFormVersion3Valid(ocrFields) && TRUE.equalsIgnoreCase(ocrFields.getExceptedEstate()));
    }

    private boolean isIhtFormsNotCompleted(ExceptionRecordOCRFields ocrFields) {
        return FALSE.equalsIgnoreCase(ocrFields.getIht400421Completed()) && FALSE.equalsIgnoreCase(ocrFields
                .getIht207Completed()) && FALSE.equalsIgnoreCase(ocrFields
                .getIht205Completed());
    }

    private void setDefaultIHTValues(ExceptionRecordOCRFields ocrFields,
                                  List<uk.gov.hmcts.reform.probate.model.cases.CollectionMember<ModifiedOCRField>>
                                          modifiedList, BulkScanConfig bulkScanConfig) {
        checkAndSetField(ocrFields, modifiedList, IHT_400421, ocrFields.getIht400421Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, IHT_207, ocrFields.getIht207Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, IHT_205, ocrFields.getIht205Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, IHT_205_COMPLETED_ONLINE, ocrFields
                        .getIht205completedOnline(), bulkScanConfig);
    }

    private void checkAndSetField(ExceptionRecordOCRFields ocrFields,
                                  List<CollectionMember<ModifiedOCRField>> modifiedList,
                                  String fieldName, String fieldValue,
                                  BulkScanConfig bulkScanConfig) {
        if (isBlank(fieldValue)) {
            addModifiedField(modifiedList, fieldName, fieldValue);
            setFieldValue(ocrFields, fieldName, bulkScanConfig.getIhtForm());
        } else if (TRUE.equalsIgnoreCase(fieldValue)) {
            if (isFormVersion3Valid(ocrFields)) {
                setFormVersion3Fields(ocrFields, modifiedList, fieldName, bulkScanConfig);
            } else if (isFormVersion2Valid(ocrFields)) {
                setFormVersion2Fields(ocrFields, modifiedList, bulkScanConfig);
            }
        }
    }

    private void setFieldValue(ExceptionRecordOCRFields ocrFields, String fieldName, String value) {
        switch (fieldName) {
            case IHT_400421:
                ocrFields.setIht400421Completed(value);
                log.info("Setting iht400421Completed to {}", value);
                break;
            case IHT_207:
                ocrFields.setIht207Completed(value);
                log.info("Setting iht207Completed to {}", value);
                break;
            case IHT_205:
                ocrFields.setIht205Completed(value);
                log.info("Setting iht205Completed to {}", value);
                break;
            case IHT_205_COMPLETED_ONLINE:
                ocrFields.setIht205completedOnline(value);
                break;
        }
    }

    private void setFormVersion3Fields(ExceptionRecordOCRFields ocrFields,
                                       List<CollectionMember<ModifiedOCRField>> modifiedList,
                                       String fieldName, BulkScanConfig bulkScanConfig) {
        switch (fieldName) {
            case IHT_400421:
                setFieldIfBlank(ocrFields::getIht421grossValue, ocrFields::setIht421grossValue,
                        IHT_421_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIht421netValue, ocrFields::setIht421netValue,
                        IHT_421_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                break;
            case IHT_207:
                setFieldIfBlank(ocrFields::getIht207grossValue, ocrFields::setIht207grossValue,
                        IHT_207_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIht207netValue, ocrFields::setIht207netValue,
                        IHT_207_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                break;
            case IHT_205:
                setFieldIfBlank(ocrFields::getIhtGrossValue205, ocrFields::setIhtGrossValue205,
                        IHT_205_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIhtNetValue205, ocrFields::setIhtNetValue205,
                        IHT_205_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                break;
            case IHT_205_COMPLETED_ONLINE:
                setFieldIfBlank(ocrFields::getIhtReferenceNumber, ocrFields::setIhtReferenceNumber,
                        IHT_REFERENCE, "1234", modifiedList);
                setFieldIfBlank(ocrFields::getIhtGrossValue205, ocrFields::setIhtGrossValue205,
                        IHT_205_GROSS_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                setFieldIfBlank(ocrFields::getIhtNetValue205, ocrFields::setIhtNetValue205,
                        IHT_207_NET_VALUE, bulkScanConfig.getGrossNetValue(), modifiedList);
                break;
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
                SOLICITOR_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorAddressPostCode,
                exceptionRecordOCRFields::setSolsSolicitorAddressPostCode,
                SOLICITOR_ADDRESS_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getSolsSolicitorFirmName,
                exceptionRecordOCRFields::setSolsSolicitorFirmName,
                SOLICITOR_FIRM_NAME, bulkScanConfig.getName(), modifiedFields);
    }

    private void handleCaveatCitizenAddressFields(ExceptionRecordOCRFields exceptionRecordOCRFields,
                                      List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorAddressLine1,
                exceptionRecordOCRFields::setCaveatorAddressLine1,
                CAVEATOR_ADDRESS_LINE1, bulkScanConfig.getName(), modifiedFields);

        setFieldIfBlank(exceptionRecordOCRFields::getCaveatorAddressPostCode,
                exceptionRecordOCRFields::setCaveatorAddressPostCode,
                CAVEATOR_POST_CODE, bulkScanConfig.getPostcode(), modifiedFields);
    }

}
