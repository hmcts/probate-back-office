package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import io.micrometer.core.instrument.util.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.ExceptedEstateDateOfDeathChecker;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsApplying;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsNotApplying;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdoptiveRelatives;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToApplicationTypeGrantOfRepresentation;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAttorneyOnBehalfOfAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDeceasedHadLateSpouseOrCivilPartner;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToDefaultLocalDate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormCompletedOnline;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstate;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormEstateValuesCompleted;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTFormId;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTGrossValue;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToIHTNetValue;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToLong;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToMartialStatus;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPaperPaymentMethod;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPennies;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToPrimaryApplicantAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationship;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToRelationshipOther;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToSolicitorAddress;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToSolicitorWillType;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.probate.service.exceptionrecord.utils.OCRFieldExtractor;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.SolsPaymentMethods;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Mapper(componentModel = "spring",
        imports = {StringUtils.class, ApplicationType.class},
        uses = {BulkScanConfig.class,
                ApplicationTypeMapper.class,
                OCRFieldAddressMapper.class,
                OCRFieldAdditionalExecutorsApplyingMapper.class,
                OCRFieldAdditionalExecutorsNotApplyingMapper.class,
                OCRFieldDefaultLocalDateFieldMapper.class,
                OCRFieldYesOrNoMapper.class,
                OCRFieldMartialStatusMapper.class,
                OCRFieldAdoptiveRelativesMapper.class,
                OCRFieldIhtFormEstateMapper.class,
                OCRFieldIhtFormTypeMapper.class,
                OCRFieldIhtMoneyMapper.class,
                OCRFieldRelationshipMapper.class,
                OCRFieldPaymentMethodMapper.class,
                OCRFieldNumberMapper.class,
                OCRFieldIhtFormEstateValuesCompletedMapper.class,
                OCRFieldIhtFormCompletedOnlineMapper.class,
                OCRFieldDeceasedHadLateSpouseOrCivilPartnerMapper.class,
                OCRFieldSolicitorWillTypeMapper.class,
                OCRFieldIhtGrossValueMapper.class,
                OCRFieldIhtNetValueMapper.class
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExceptionRecordGrantOfRepresentationMapper {
    @Mapping(target = "extraCopiesOfGrant", expression = "java(OCRFieldNumberMapper.stringToLong(new String("
            + "\"extraCopiesOfGrant\"), ocrFields.getExtraCopiesOfGrant()))", qualifiedBy = {ToLong.class})
    @Mapping(target = "outsideUkGrantCopies", expression = "java(OCRFieldNumberMapper.stringToLong(new String("
            + "\"outsideUKGrantCopies\"), ocrFields.getOutsideUKGrantCopies()))", qualifiedBy = {ToLong.class})

    @Mapping(target = "applicationFeePaperForm", expression = "java(OCRFieldIhtMoneyMapper.poundsToPennies(new String("
            + "\"applicationFeePaperForm\"), ocrFields.getApplicationFeePaperForm()))", qualifiedBy = {ToPennies.class})
    @Mapping(target = "feeForCopiesPaperForm", expression = "java(OCRFieldIhtMoneyMapper.poundsToPennies(new String("
            + "\"feeForCopiesPaperForm\"), ocrFields.getFeeForCopiesPaperForm()))", qualifiedBy = {ToPennies.class})
    @Mapping(target = "totalFeePaperForm", expression = "java(OCRFieldIhtMoneyMapper.poundsToPennies(new String("
            + "\"totalFeePaperForm\"), ocrFields.getTotalFeePaperForm()))", qualifiedBy = {ToPennies.class})
    @Mapping(target = "paperPaymentMethod", source = "ocrFields.paperPaymentMethod", qualifiedBy = {
        ToPaperPaymentMethod.class})
    @Mapping(target = "paymentReferenceNumberPaperform", source = "ocrFields.paymentReferenceNumberPaperform")

    @Mapping(target = "primaryApplicantForenames", source = "ocrFields.primaryApplicantForenames")
    @Mapping(target = "primaryApplicantSurname", source = "ocrFields.primaryApplicantSurname")
    @Mapping(target = "primaryApplicantAddress", source = "ocrFields", qualifiedBy = {ToPrimaryApplicantAddress.class})
    @Mapping(target = "primaryApplicantPhoneNumber", source = "ocrFields.primaryApplicantPhoneNumber")
    @Mapping(target = "primaryApplicantEmailAddress", source = "ocrFields.primaryApplicantEmailAddress")
    @Mapping(target = "primaryApplicantSecondPhoneNumber", source = "ocrFields.primaryApplicantSecondPhoneNumber")
    @Mapping(target = "primaryApplicantHasAlias", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"primaryApplicantHasAlias\"), ocrFields.getPrimaryApplicantHasAlias()))",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "primaryApplicantAlias", source = "ocrFields.primaryApplicantAlias")

    @Mapping(target = "executorsApplying", source = "ocrFields", qualifiedBy = {ToAdditionalExecutorsApplying.class})

    @Mapping(target = "solsSolicitorIsApplying", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"solsSolicitorIsApplying\"), ocrFields.getSolsSolicitorIsApplying()))",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "solsSolicitorAddress", source = "ocrFields", qualifiedBy = {ToSolicitorAddress.class})
    @Mapping(target = "solsSolicitorFirmName", source = "ocrFields.solsSolicitorFirmName")
    @Mapping(target = "solsSolicitorAppReference", source = "ocrFields.solsSolicitorAppReference")
    @Mapping(target = "solsFeeAccountNumber", source = "ocrFields.solsFeeAccountNumber")
    @Mapping(target = "solsSolicitorEmail", source = "ocrFields.solsSolicitorEmail")
    @Mapping(target = "solsSolicitorPhoneNumber", source = "ocrFields.solsSolicitorPhoneNumber")

    @Mapping(target = "deceasedForenames", source = "ocrFields.deceasedForenames")
    @Mapping(target = "deceasedSurname", source = "ocrFields.deceasedSurname")
    @Mapping(target = "deceasedAddress", source = "ocrFields", qualifiedBy = {ToDeceasedAddress.class})
    @Mapping(target = "deceasedDateOfBirth", expression = "java(OCRFieldDefaultLocalDateFieldMapper"
            + ".toDefaultDateFieldMember(new String(\"deceasedDateOfBirth\"), ocrFields.getDeceasedDateOfBirth()))",
            qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedDateOfDeath", expression = "java(OCRFieldDefaultLocalDateFieldMapper"
            + ".toDefaultDateFieldMember(new String(\"deceasedDateOfDeath\"), ocrFields.getDeceasedDateOfDeath()))",
            qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "deceasedAnyOtherNames",
            expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"deceasedAnyOtherNames\"), "
                    + "ocrFields.getDeceasedAnyOtherNames()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedDomicileInEngWales", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"deceasedDomicileInEngWales\"), ocrFields.getDeceasedDomicileInEngWales()))", qualifiedBy = {
                ToYesOrNo.class})
    @Mapping(target = "deceasedMaritalStatus", source = "ocrFields.deceasedMartialStatus", qualifiedBy = {
        ToMartialStatus.class})

    @Mapping(target = "dateOfMarriageOrCP", expression = "java(OCRFieldDefaultLocalDateFieldMapper"
            + ".toDefaultDateFieldMember(new String(\"dateOfMarriageOrCP\"), ocrFields.getDateOfMarriageOrCP()))",
            qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "dateOfDivorcedCPJudicially", expression = "java(OCRFieldDefaultLocalDateFieldMapper"
            + ".toDefaultDateFieldMember(new String(\"dateOfDivorcedCPJudicially\"), "
            + "ocrFields.getDateOfDivorcedCPJudicially()))", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "courtOfDecree", source = "ocrFields.courtOfDecree")

    @Mapping(target = "foreignAsset", expression =
            "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"foreignAsset\"), ocrFields.getForeignAsset()))",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "foreignAssetEstateValue", expression = "java(OCRFieldIhtMoneyMapper.poundsToPennies(new String("
            + "\"foreignAssetEstateValue\"), ocrFields.getForeignAssetEstateValue()))", qualifiedBy = {ToPennies.class})

    @Mapping(target = "adopted", expression =
            "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"adopted\"), ocrFields.getAdopted()))",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "adoptiveRelatives", source = "ocrFields", qualifiedBy = {ToAdoptiveRelatives.class})
    @Mapping(target = "spouseOrPartner", expression =
            "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"spouseOrPartner\"), ocrFields.getSpouseOrPartner()))",
            qualifiedBy = {ToYesOrNo.class})

    // Following fields captured as text but used as booleans in orchestrator
    @Mapping(target = "childrenSurvived", ignore = true)
    @Mapping(target = "childrenUnderEighteenSurvivedText", expression = "java(ocrFields"
            + ".getChildrenUnderEighteenSurvived())")
    @Mapping(target = "childrenOverEighteenSurvivedText", expression = "java(ocrFields"
            + ".getChildrenOverEighteenSurvived())")
    @Mapping(target = "childrenDied", ignore = true)
    @Mapping(target = "childrenDiedUnderEighteenText", expression = "java(ocrFields.getChildrenDiedUnderEighteen())")
    @Mapping(target = "childrenDiedOverEighteenText", expression = "java(ocrFields.getChildrenDiedOverEighteen())")
    @Mapping(target = "grandChildrenSurvived", ignore = true)
    @Mapping(target = "grandChildrenSurvivedUnderEighteenText", expression = "java(ocrFields"
            + ".getGrandChildrenSurvivedUnderEighteen())")
    @Mapping(target = "grandChildrenSurvivedOverEighteenText", expression = "java(ocrFields"
            + ".getGrandChildrenSurvivedOverEighteen())")

    // Ignored fields defined in after mapping section.
    @Mapping(target = "parentsExistUnderEighteenSurvived", source = "ocrFields.parentsExistUnderEighteenSurvived")
    @Mapping(target = "parentsExistOverEighteenSurvived", source = "ocrFields.parentsExistOverEighteenSurvived")
    @Mapping(target = "wholeBloodSiblingsSurvivedUnderEighteen", source = "ocrFields"
            + ".wholeBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsSurvivedOverEighteen", source = "ocrFields"
            + ".wholeBloodSiblingsSurvivedOverEighteen")
    @Mapping(target = "wholeBloodSiblingsDiedUnderEighteen", source = "ocrFields.wholeBloodSiblingsDiedUnderEighteen")
    @Mapping(target = "wholeBloodSiblingsDiedOverEighteen", source = "ocrFields.wholeBloodSiblingsDiedOverEighteen")
    @Mapping(target = "wholeBloodNeicesAndNephewsUnderEighteen", source = "ocrFields"
            + ".wholeBloodNeicesAndNephewsUnderEighteen")
    @Mapping(target = "wholeBloodNeicesAndNephewsOverEighteen", source = "ocrFields"
            + ".wholeBloodNeicesAndNephewsOverEighteen")
    @Mapping(target = "halfBloodSiblingsSurvivedUnderEighteen", source = "ocrFields"
            + ".halfBloodSiblingsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodSiblingsSurvivedOverEighteen", source = "ocrFields"
            + ".halfBloodSiblingsSurvivedOverEighteen")
    @Mapping(target = "halfBloodSiblingsDiedUnderEighteen", source = "ocrFields.halfBloodSiblingsDiedUnderEighteen")
    @Mapping(target = "halfBloodSiblingsDiedOverEighteen", source = "ocrFields.halfBloodSiblingsDiedOverEighteen")
    @Mapping(target = "halfBloodNeicesAndNephewsUnderEighteen", source = "ocrFields"
            + ".halfBloodNeicesAndNephewsUnderEighteen")
    @Mapping(target = "halfBloodNeicesAndNephewsOverEighteen", source = "ocrFields"
            + ".halfBloodNeicesAndNephewsOverEighteen")
    @Mapping(target = "grandparentsDiedUnderEighteen", source = "ocrFields.grandparentsDiedUnderEighteen")
    @Mapping(target = "grandparentsDiedOverEighteen", source = "ocrFields.grandparentsDiedOverEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsSurvivedUnderEighteen", source = "ocrFields"
            + ".wholeBloodUnclesAndAuntsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsSurvivedOverEighteen", source = "ocrFields"
            + ".wholeBloodUnclesAndAuntsSurvivedOverEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsDiedUnderEighteen", source = "ocrFields"
            + ".wholeBloodUnclesAndAuntsDiedUnderEighteen")
    @Mapping(target = "wholeBloodUnclesAndAuntsDiedOverEighteen", source = "ocrFields"
            + ".wholeBloodUnclesAndAuntsDiedOverEighteen")
    @Mapping(target = "wholeBloodCousinsSurvivedUnderEighteen", source = "ocrFields"
            + ".wholeBloodCousinsSurvivedUnderEighteen")
    @Mapping(target = "wholeBloodCousinsSurvivedOverEighteen", source = "ocrFields"
            + ".wholeBloodCousinsSurvivedOverEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsSurvivedUnderEighteen", source = "ocrFields"
            + ".halfBloodUnclesAndAuntsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsSurvivedOverEighteen", source = "ocrFields"
            + ".halfBloodUnclesAndAuntsSurvivedOverEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsDiedUnderEighteen", source = "ocrFields"
            + ".halfBloodUnclesAndAuntsDiedUnderEighteen")
    @Mapping(target = "halfBloodUnclesAndAuntsDiedOverEighteen", source = "ocrFields"
            + ".halfBloodUnclesAndAuntsDiedOverEighteen")
    @Mapping(target = "halfBloodCousinsSurvivedUnderEighteen", source = "ocrFields"
            + ".halfBloodCousinsSurvivedUnderEighteen")
    @Mapping(target = "halfBloodCousinsSurvivedOverEighteen", source = "ocrFields.halfBloodCousinsSurvivedOverEighteen")
    @Mapping(target = "primaryApplicantRelationshipToDeceased",
            source = "ocrFields.primaryApplicantRelationshipToDeceased", qualifiedBy = {ToRelationship.class})
    @Mapping(target = "paRelationshipToDeceasedOther",
            source = "ocrFields.primaryApplicantRelationshipToDeceased", qualifiedBy = {ToRelationshipOther.class})
    @Mapping(target = "solsSOTName", source = "ocrFields.solsSolicitorRepresentativeName")
    @Mapping(target = "applyingAsAnAttorney", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"applyingAsAnAttorney\"), ocrFields.getApplyingAsAnAttorney()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "attorneyOnBehalfOfNameAndAddress", source = "ocrFields", qualifiedBy = {
        ToAttorneyOnBehalfOfAddress.class})
    @Mapping(target = "mentalCapacity", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"mentalCapacity\"), ocrFields.getMentalCapacity()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "courtOfProtection", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"courtOfProtection\"), ocrFields.getCourtOfProtection()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "epaOrLpa", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"epaOrLpa\"), "
            + "ocrFields.getEpaOrLpa()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "epaRegistered", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"epaRegistered\"), ocrFields.getEpaRegistered()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "domicilityCountry", source = "ocrFields.domicilityCountry")

    // domicilityIHTCert defined in after mapping section If domicilityEntrustingDocument or
    // domicilitySuccessionIHTCert is true
    @Mapping(target = "domicilityIHTCert", ignore = true)

    @Mapping(target = "executorsNotApplying", source = "ocrFields", qualifiedBy = {
        ToAdditionalExecutorsNotApplying.class})
    @Mapping(target = "willHasCodicils", expression =
            "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"willHasCodicils\"), ocrFields.getWillHasCodicils()))",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "deceasedMarriedAfterWillOrCodicilDate",
            expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"deceasedMarriedAfterWillOrCodicilDate\"), "
                    + "ocrFields.getDeceasedMarriedAfterWillOrCodicilDate()))",
            qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "languagePreferenceWelsh", expression =
            "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"bilingualGrantRequested\"), "
                    + "ocrFields.getBilingualGrantRequested()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "willDate", expression = "java(OCRFieldDefaultLocalDateFieldMapper.toDefaultDateFieldMember("
            + "new String(\"willDate\"), ocrFields.getWillDate()))", qualifiedBy = {ToDefaultLocalDate.class})
    @Mapping(target = "willsOutsideOfUK", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"willsOutsideOfUK\"), ocrFields.getWillsOutsideOfUK()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "willGiftUnderEighteen", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"willGiftUnderEighteen\"), ocrFields.getWillGiftUnderEighteen()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "notifiedApplicants", expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String("
            + "\"notifiedApplicants\"), ocrFields.getNotifiedApplicants()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtFormCompletedOnline", source = "ocrFields", qualifiedBy = {ToIHTFormCompletedOnline.class})
    @Mapping(target = "ihtReferenceNumber", source = "ocrFields.ihtReferenceNumber")
    @Mapping(target = "ihtFormId", source = "ocrFields", qualifiedBy = {ToIHTFormId.class})
    @Mapping(target = "ihtGrossValue", source = "ocrFields", qualifiedBy = {ToIHTGrossValue.class})
    @Mapping(target = "ihtNetValue", source = "ocrFields", qualifiedBy = {ToIHTNetValue.class})
    @Mapping(target = "paperForm", expression = "java(Boolean.TRUE)")
    @Mapping(target = "applicationType", source = "ocrFields", qualifiedBy = {
        ToApplicationTypeGrantOfRepresentation.class})
    @Mapping(target = "channelChoice", expression = "java(new String(\"BulkScan\"))")
    @Mapping(target = "ihtFormEstate", source = "ocrFields", qualifiedBy = {ToIHTFormEstate.class})
    @Mapping(target = "ihtEstateGrossValue", expression = "java(OCRFieldIhtMoneyMapper.poundsToPennies(new String("
            + "\"ihtEstateGrossValue\"), ocrFields.getIhtEstateGrossValue()))", qualifiedBy = {ToPennies.class})
    @Mapping(target = "ihtEstateNetValue", expression = "java(OCRFieldIhtMoneyMapper.poundsToPennies(new String("
            + "\"ihtEstateNetValue\"), ocrFields.getIhtEstateNetValue()))", qualifiedBy = {ToPennies.class})
    @Mapping(target = "ihtEstateNetQualifyingValue", expression = "java(OCRFieldIhtMoneyMapper.poundsToPennies("
            + "new String(\"ihtEstateNetQualifyingValue\"), ocrFields.getIhtEstateNetQualifyingValue()))",
                qualifiedBy = {ToPennies.class})
    @Mapping(target = "deceasedHadLateSpouseOrCivilPartner", source = "ocrFields", qualifiedBy = {
        ToDeceasedHadLateSpouseOrCivilPartner.class})
    @Mapping(target = "ihtUnusedAllowanceClaimed",
            expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"ihtUnusedAllowanceClaimed\"), "
                    + "ocrFields.getIhtUnusedAllowanceClaimed()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "ihtFormEstateValuesCompleted", source = "ocrFields",
            qualifiedBy = {ToIHTFormEstateValuesCompleted.class})
    @Mapping(target = "hmrcLetterId",
            expression = "java(OCRFieldYesOrNoMapper.toYesOrNo(new String(\"iht400process\"), "
                    + "ocrFields.getIht400process()))", qualifiedBy = {ToYesOrNo.class})
    @Mapping(target = "uniqueProbateCodeId", source = "ocrFields.ihtCode")
    @Mapping(target = "solsWillType", expression = "java(OCRFieldSolicitorWillTypeMapper.toSolicitorWillType("
            + "ocrFields.getSolsWillType(), grantType))",
            qualifiedBy = {ToSolicitorWillType.class})
    @Mapping(target = "solsWillTypeReason", source = "ocrFields.solsWillTypeReason")

    GrantOfRepresentationData toCcdData(ExceptionRecordOCRFields ocrFields, GrantType grantType);

    @BeforeMapping
    default void setDefaultIhtForm(ExceptionRecordOCRFields ocrFields,
                                   @MappingTarget GrantOfRepresentationData caseData,
                                   @Context BulkScanConfig bulkScanConfig, @Context ExceptedEstateDateOfDeathChecker
                                               exceptedEstateDateOfDeathChecker) {
        List<CollectionMember<ModifiedOCRField>> modifiedFields = new ArrayList<>();
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
            setDefaultValues(ocrFields, modifiedFields, bulkScanConfig);
            if (isIhtFormsNotCompleted(ocrFields)) {
                addModifiedField(modifiedFields, "iht400Completed", ocrFields.getIht400Completed());
                ocrFields.setIht400Completed("TRUE");
            }
        } else if (isFormVersion2AndSwitchDateValid(ocrFields, exceptedEstateDateOfDeathChecker)) {
            setDefaultValues(ocrFields, modifiedFields, bulkScanConfig);
        } else if (isFormVersion2Or3AndExceptedEstate(ocrFields)) {
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
        caseData.setModifiedOCRFieldList(modifiedFields);
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
                .getIht207Completed());
    }

    private void setDefaultValues(ExceptionRecordOCRFields ocrFields,
                                  List<CollectionMember<ModifiedOCRField>> modifiedList,
                                  BulkScanConfig bulkScanConfig) {
        checkAndSetField(ocrFields, modifiedList, "iht400421Completed", ocrFields.getIht400421Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, "iht207Completed", ocrFields.getIht207Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, "iht205Completed", ocrFields.getIht205Completed(),
                bulkScanConfig);
        checkAndSetField(ocrFields, modifiedList, "iht205completedOnline", ocrFields.getIht205Completed(),
                bulkScanConfig);
    }

    private void checkAndSetField(ExceptionRecordOCRFields ocrFields,
                                  List<CollectionMember<ModifiedOCRField>> modifiedList, String fieldName,
                                  String fieldValue, BulkScanConfig bulkScanConfig) {
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
                    };
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

    private void addModifiedField(List<CollectionMember<ModifiedOCRField>> modifiedList, String fieldName,
                                  String originalValue) {
        ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                .fieldName(fieldName)
                .originalValue(originalValue)
                .build();
        modifiedList.add(new CollectionMember<>(null, modifiedOCRField));
    }
    @AfterMapping
    default void clearSolsWillTypeAndReason(@MappingTarget GrantOfRepresentationData caseData) {
        // there should not be sols will type on citizen case
        if (caseData.getApplicationType() == ApplicationType.PERSONAL) {
            caseData.setSolsWillTypeReason(null);
            caseData.setSolsWillType(null);
        } else if (null == caseData.getSolsWillType()) {
            // there might not be a reason given but if there is not will type set then clear
            caseData.setSolsWillTypeReason(null);
        }
    }

    @AfterMapping
    default void setDomicilityIHTCert(@MappingTarget GrantOfRepresentationData caseData,
                                      ExceptionRecordOCRFields ocrField) {
        if (BooleanUtils.toBoolean(ocrField.getDomicilityEntrustingDocument())
                || BooleanUtils.toBoolean(ocrField.getDomicilitySuccessionIHTCert())) {
            caseData.setDomicilityIHTCert(Boolean.TRUE);
        }
    }

    @AfterMapping
    default void setSolsPaymentMethod(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS)
                && StringUtils.isNotBlank(caseData.getSolsFeeAccountNumber())) {
            caseData.setSolsPaymentMethods(SolsPaymentMethods.FEE_ACCOUNT);
        }
    }

    @AfterMapping
    default void setSolsSolicitorRepresentativeName(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if ((caseData.getApplicationType() == ApplicationType.SOLICITORS)
                && (StringUtils.isNotBlank(ocrField.getSolsSolicitorRepresentativeName()))) {
            String solicitorFullName = ocrField.getSolsSolicitorRepresentativeName();
            List<String> names = OCRFieldExtractor.splitFullname(solicitorFullName);
            if (names.size() > 2) {
                caseData.setSolsSOTSurname(names.get(names.size() - 1));
                caseData.setSolsSOTForenames(String.join(" ", names.subList(0, names.size() - 1)));
            } else if (names.size() == 1) {
                caseData.setSolsSOTSurname("");
                caseData.setSolsSOTForenames(names.get(0));
            } else {
                caseData.setSolsSOTSurname(names.get(1));
                caseData.setSolsSOTForenames(names.get(0));
            }
        }
    }

    @AfterMapping
    default void clearIhtFormOrReferenceIfNotSelected(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField) {
        if (caseData.getIhtFormCompletedOnline() != null && caseData.getIhtFormCompletedOnline()) {
            caseData.setIhtFormId(null);
        }
        if (caseData.getIhtFormCompletedOnline() != null && !caseData.getIhtFormCompletedOnline()) {
            caseData.setIhtReferenceNumber(null);
        }
    }

    @AfterMapping
    @SuppressWarnings("Duplicates")
    default void setDerivedFamilyBooleans(
            @MappingTarget GrantOfRepresentationData caseData, ExceptionRecordOCRFields ocrField, GrantType grantType) {
        if (greaterThenZero(ocrField.getChildrenUnderEighteenSurvived())
                || greaterThenZero(ocrField.getChildrenOverEighteenSurvived())) {
            caseData.setChildrenSurvived(Boolean.TRUE);
        } else {
            caseData.setChildrenOverEighteenSurvivedText(null);
            caseData.setChildrenUnderEighteenSurvivedText(null);
        }
        if (greaterThenZero(ocrField.getChildrenDiedUnderEighteen())
                || greaterThenZero(ocrField.getChildrenDiedOverEighteen())) {
            caseData.setChildrenDied(Boolean.TRUE);
        } else {
            caseData.setChildrenDiedOverEighteenText(null);
            caseData.setChildrenDiedUnderEighteenText(null);
        }
        if (greaterThenZero(ocrField.getGrandChildrenSurvivedUnderEighteen())
                || greaterThenZero(ocrField.getGrandChildrenSurvivedOverEighteen())) {
            caseData.setGrandChildrenSurvived(Boolean.TRUE);
        } else {
            caseData.setGrandChildrenSurvivedOverEighteenText(null);
            caseData.setGrandChildrenSurvivedUnderEighteenText(null);
        }
        if (grantType.equals(GrantType.INTESTACY)) {
            if (greaterThenZero(ocrField.getParentsExistUnderEighteenSurvived())
                    || greaterThenZero(ocrField.getParentsExistOverEighteenSurvived())) {
                caseData.setParentsExistSurvived(Boolean.TRUE);
            } else {
                caseData.setParentsExistOverEighteenSurvived(null);
                caseData.setParentsExistUnderEighteenSurvived(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodSiblingsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodSiblingsSurvivedOverEighteen())) {
                caseData.setWholeBloodSiblingsSurvived(Boolean.TRUE);
            } else {
                caseData.setWholeBloodSiblingsSurvivedOverEighteen(null);
                caseData.setWholeBloodSiblingsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodSiblingsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodSiblingsDiedOverEighteen())) {
                caseData.setWholeBloodSiblingsDied(Boolean.TRUE);
            } else {
                caseData.setWholeBloodSiblingsDiedOverEighteen(null);
                caseData.setWholeBloodSiblingsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodNeicesAndNephewsUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodNeicesAndNephewsOverEighteen())) {
                caseData.setWholeBloodNeicesAndNephews(Boolean.TRUE);
            } else {
                caseData.setWholeBloodNeicesAndNephewsOverEighteen(null);
                caseData.setWholeBloodNeicesAndNephewsUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodSiblingsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodSiblingsSurvivedOverEighteen())) {
                caseData.setHalfBloodSiblingsSurvived(Boolean.TRUE);
            } else {
                caseData.setHalfBloodSiblingsSurvivedOverEighteen(null);
                caseData.setHalfBloodSiblingsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodSiblingsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodSiblingsDiedOverEighteen())) {
                caseData.setHalfBloodSiblingsDied(Boolean.TRUE);
            } else {
                caseData.setHalfBloodSiblingsDiedOverEighteen(null);
                caseData.setHalfBloodSiblingsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodNeicesAndNephewsUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodNeicesAndNephewsOverEighteen())) {
                caseData.setHalfBloodNeicesAndNephews(Boolean.TRUE);
            } else {
                caseData.setHalfBloodNeicesAndNephewsOverEighteen(null);
                caseData.setHalfBloodNeicesAndNephewsUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getGrandparentsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getGrandparentsDiedOverEighteen())) {
                caseData.setGrandparentsDied(Boolean.TRUE);
            } else {
                caseData.setGrandparentsDiedOverEighteen(null);
                caseData.setGrandparentsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsSurvivedOverEighteen())) {
                caseData.setWholeBloodUnclesAndAuntsSurvived(Boolean.TRUE);
            } else {
                caseData.setWholeBloodUnclesAndAuntsSurvivedOverEighteen(null);
                caseData.setWholeBloodUnclesAndAuntsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodUnclesAndAuntsDiedOverEighteen())) {
                caseData.setWholeBloodUnclesAndAuntsDied(Boolean.TRUE);
            } else {
                caseData.setWholeBloodUnclesAndAuntsDiedOverEighteen(null);
                caseData.setWholeBloodUnclesAndAuntsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getWholeBloodCousinsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getWholeBloodCousinsSurvivedOverEighteen())) {
                caseData.setWholeBloodCousinsSurvived(Boolean.TRUE);
            } else {
                caseData.setWholeBloodCousinsSurvivedOverEighteen(null);
                caseData.setWholeBloodCousinsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsSurvivedOverEighteen())) {
                caseData.setHalfBloodUnclesAndAuntsSurvived(Boolean.TRUE);
            } else {
                caseData.setHalfBloodUnclesAndAuntsSurvivedOverEighteen(null);
                caseData.setHalfBloodUnclesAndAuntsSurvivedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsDiedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodUnclesAndAuntsDiedOverEighteen())) {
                caseData.setHalfBloodUnclesAndAuntsDied(Boolean.TRUE);
            } else {
                caseData.setHalfBloodUnclesAndAuntsDiedOverEighteen(null);
                caseData.setHalfBloodUnclesAndAuntsDiedUnderEighteen(null);
            }
            if (greaterThenZero(ocrField.getHalfBloodCousinsSurvivedUnderEighteen())
                    || greaterThenZero(ocrField.getHalfBloodCousinsSurvivedOverEighteen())) {
                caseData.setHalfBloodCousinsSurvived(Boolean.TRUE);
            } else {
                caseData.setHalfBloodCousinsSurvivedOverEighteen(null);
                caseData.setHalfBloodCousinsSurvivedUnderEighteen(null);
            }
        }
    }

    @AfterMapping
    default void setApplyingAsAnAttorneyBoolean(@MappingTarget GrantOfRepresentationData caseData,
                                                ExceptionRecordOCRFields ocrField) {
        if (caseData.getAttorneyOnBehalfOfNameAndAddress().size() > 0
                && StringUtils.isNotBlank(caseData.getAttorneyOnBehalfOfNameAndAddress().get(0).getValue().getName())) {
            caseData.setApplyingAsAnAttorney(Boolean.TRUE);
        } else {
            caseData.setApplyingAsAnAttorney(Boolean.FALSE);
        }
    }

    @AfterMapping
    default void setHandOffToLegacySiteBoolean(@MappingTarget GrantOfRepresentationData caseData,
                                               ExceptionRecordOCRFields ocrField) {
        if ("True".equalsIgnoreCase(ocrField.getSolsSolicitorIsApplying())) {
            caseData.setCaseHandedOffToLegacySite(Boolean.TRUE);
        }
    }

    @AfterMapping
    default void setNonRequiredEstateValuesToNull(@MappingTarget GrantOfRepresentationData caseData,
                                                  ExceptionRecordOCRFields ocrField) {

        String formVersion = ocrField.getFormVersion();

        if (("2".equals(formVersion) && "False".equalsIgnoreCase(ocrField.getDeceasedDiedOnAfterSwitchDate())
                || ("3".equals(formVersion)) && "False".equalsIgnoreCase(ocrField.getExceptedEstate()))) {

            caseData.setIhtEstateGrossValue(null);
            caseData.setIhtEstateNetValue(null);
            caseData.setIhtEstateNetQualifyingValue(null);
        }
    }

    default boolean greaterThenZero(String field) {
        Integer integerValue = 0;
        if (field != null) {
            try {
                integerValue = Integer.valueOf(field);
            } catch (Exception e) {
                // do nothing
            }
        }
        return BooleanUtils.toBoolean(integerValue);
    }
}
