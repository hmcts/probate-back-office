package uk.gov.hmcts.probate.transformer.assembly;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.Categories;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleCaseworker;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleEntitlement;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleForeignDomicile;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleFreeText;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleIHT;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleIncapacity;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleLifeAndMinorityInterest;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleMissingInformation;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSOTIncomplete;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorAffidavit;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorCert;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorGeneral;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorRedeclaration;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleWill;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleWitness;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleLetterTransformer {
    private final AssembleCaseworker assembleCaseworker;
    private final AssembleFreeText assembleFreeText;
    private final AssembleEntitlement assembleEntitlement;
    private final AssembleIHT assembleIHT;
    private final AssembleMissingInformation assembleMissingInformation;
    private final AssembleForeignDomicile assembleForeignDomicile;
    private final AssembleWill assembleWill;
    private final AssembleIncapacity assembleIncapacity;
    private final AssembleLifeAndMinorityInterest assembleLifeAndMinorityInterest;
    private final AssembleSOTIncomplete assembleSOTIncomplete;
    private final AssembleWitness assembleWitness;
    private final AssembleSolicitorGeneral assembleSolicitorGeneral;
    private final AssembleSolicitorCert assembleSolicitorCert;
    private final AssembleSolicitorAffidavit assembleSolicitorAffidavit;
    private final AssembleSolicitorRedeclaration assembleSolicitorRedeclaration;

    private Map<ParagraphCode, BiFunction<ParagraphCode, CaseData, List<ParagraphDetail>>>
            paragraphCodeFunctions;

    private Map<ParagraphCode, BiFunction<ParagraphCode, CaseData, List<ParagraphDetail>>> getParagraphFunctions() {
        if (paragraphCodeFunctions == null) {
            paragraphCodeFunctions = ImmutableMap.<ParagraphCode, BiFunction<ParagraphCode, CaseData,
                    List<ParagraphDetail>>>builder()
                    .put(FreeText, assembleFreeText::freeText)
                    .put(Caseworker, assembleCaseworker::caseworker)
                    .put(ForDomAffidavit, assembleForeignDomicile::affidavitOfLaw)
                    .put(ForDomInitialEnq, assembleForeignDomicile::initialEnquiry)
                    .put(EntExecNoAcc, assembleEntitlement::executorNotAccountedFor)
                    .put(EntAttorney, assembleEntitlement::entitlementAttorneyAndExec)
                    .put(EntLeadingApp, assembleEntitlement::entitlementLeadingGrantApplication)
                    .put(EntNoTitle, assembleEntitlement::entitlementNoTitle)
                    //  .put(EntTwoApps, assembleEntitlement::entitlementNoTitle)
                    .put(EntFamTree, assembleEntitlement::entitlementFamilyTree)
                    .put(EntDeathPa, assembleEntitlement::entitlementConfirmDeath)
                    .put(EntSubExec, assembleEntitlement::entitlementSubstitutedExec)
                    .put(EntPrejudiced, assembleEntitlement::entitlementPrejudice)
                    .put(EntWrongExec, assembleEntitlement::entitlementWrongExec)
                    .put(IHT205Miss, assembleIHT::iht205Missing)
                    .put(IHT421Await, assembleIHT::ihtAwait421)
                    .put(IHT205NoAssets, assembleIHT::ihtNoAssets)
                    .put(IHT205GrossEstateOver, assembleIHT::ihtGrossEstate)
                    .put(IHT217Miss, assembleIHT::iht217Missing)
                    .put(IHTIHT400, assembleIHT::iht400)
                    .put(MissInfoWill, assembleMissingInformation::missingInfoWill)
                    .put(MissInfoDeathCert, assembleMissingInformation::missingInfoDeathCert)
                    .put(MissInfoChangeApp, assembleMissingInformation::missingInfoChangeOfApplicant)
                    .put(MissInfoAwaitResponse, assembleMissingInformation::missingInfoDateOfRequest)
                    .put(MissInfoAlias, assembleMissingInformation::missingInfoAlias)
                    .put(MissInfoRenunWill, assembleMissingInformation::missingInfoRenunWill)
                    .put(MissInfoGrantReq, assembleMissingInformation::missingInfoGrantReq)
                    .put(WillAnyOther, assembleWill::willAnyOther)
                    .put(WillPlight, assembleWill::willPlight)
                    .put(WillSepPages, assembleWill::willSeparatePages)
                    .put(WillStaple, assembleWill::willStaple)
                    .put(WillRevoked, assembleWill::willRevoked)
                    .put(WillLost, assembleWill::willLost)
                    .put(WillList, assembleWill::willList)
                    .put(WillFiat, assembleWill::willFiat)
                    .put(IncapGen, assembleIncapacity::incapacityGeneral)
                    .put(IncapOneExec, assembleIncapacity::incapacityOneExecutor)
                    .put(IncapInstitutedExec, assembleIncapacity::incapacityInstitutedExecutor)
                    .put(IncapMedical, assembleIncapacity::incapacityMedicalEvidence)
                    .put(IntLifeAndMin, assembleLifeAndMinorityInterest::intestacyLifeandMinority)
                    .put(IntLife, assembleLifeAndMinorityInterest::intestacyLife)
                    .put(IntMinor, assembleLifeAndMinorityInterest::intestacyMinority)
                    .put(AdmonLife, assembleLifeAndMinorityInterest::admonWillLife)
                    .put(AdmonMinor, assembleLifeAndMinorityInterest::admonWillMinority)
                    .put(IntParental, assembleLifeAndMinorityInterest::intestacyParentalResponsibility)
                    .put(SotPa1pRedec, assembleSOTIncomplete::sotPa1pRedec)
                    .put(SotPa1aRedec, assembleSOTIncomplete::sotPa1aRedec)
                    .put(SotNotSigned, assembleSOTIncomplete::sotNotSigned)
                    .put(SotPa1pQ2, assembleSOTIncomplete::sotPa1pQ2)
                    .put(SotPa1pQ3, assembleSOTIncomplete::sotPa1pQ3)
                    .put(SotPa1pQ4, assembleSOTIncomplete::sotPa1pQ4)
                    .put(SotPa1pQ5, assembleSOTIncomplete::sotPa1pQ5)
                    .put(SotPa1pQ6, assembleSOTIncomplete::sotPa1pQ6)
                    .put(SotPa1pQ7, assembleSOTIncomplete::sotPa1pQ7)
                    .put(SotPa1aQ2, assembleSOTIncomplete::sotPa1aQ2)
                    .put(SotPa1aQ3, assembleSOTIncomplete::sotPa1aQ3)
                    .put(SotPa1aQ4, assembleSOTIncomplete::sotPa1aQ4)
                    .put(SotPa1aQ5, assembleSOTIncomplete::sotPa1aQ5)
                    .put(SotPa1aQ6, assembleSOTIncomplete::sotPa1aQ6)
                    .put(WitnessConsent, assembleWitness::witnessConsent)
                    .put(WitnessDate, assembleWitness::witnessDate)
                    .put(WitnessExecution, assembleWitness::witnessExecution)
                    .put(WitnessSignature, assembleWitness::witnessSignature)
                    .put(solsGenPowerAttorney, assembleSolicitorGeneral::solsGenPowerAttorney)
                    .put(solsGenVoid, assembleSolicitorGeneral::solsGenVoidForUncertainity)
                    .put(solsGenAuthorityPartners, assembleSolicitorGeneral::solsGneralAuthorityPartners)
                    .put(solsCertOtherWill, assembleSolicitorCert::solsCertOtherWill)
                    .put(solsCertAlias, assembleSolicitorCert::solsCertAlias)
                    .put(solsCertDeceasedAdd, assembleSolicitorCert::solsCertDeceasedAddress)
                    .put(solsCertDeponentAdd, assembleSolicitorCert::solsCertDeponentsAddress)
                    .put(solsCertDivorce, assembleSolicitorCert::solsCertDivorce)
                    .put(solsCertDivorceDissolve, assembleSolicitorCert::solsCertDivorceDissolved)
                    .put(solsCertDOB, assembleSolicitorCert::solsCertDob)
                    .put(solsCertDOD, assembleSolicitorCert::solsCertDod)
                    .put(solsCertEpaLpa, assembleSolicitorCert::solsCertEpaLpa)
                    .put(solsCertExecNotAcc, assembleSolicitorCert::solsCertExecNotAccounted)
                    .put(solsCertFirmSucc, assembleSolicitorCert::solsCertFirmSuccceeded)
                    .put(solsCertExecName, assembleSolicitorCert::solsCertExecName)
                    .put(solsCertLifeMinority, assembleSolicitorCert::solsCertLifeMinority)
                    .put(solsCertPartnersDod, assembleSolicitorCert::solsCertPartnersDod)
                    .put(solsCertPlight, assembleSolicitorCert::solsCertPlightCondition)
                    .put(solsCertPowerReserved, assembleSolicitorCert::solsCertPowerReserved)
                    .put(solsCertSettledLand, assembleSolicitorCert::solsCertSettledLand)
                    .put(solsCertSpouse, assembleSolicitorCert::solsCertSpouse)
                    .put(solsCertSurvivalExec, assembleSolicitorCert::solsCertSurvivalExec)
                    .put(solsCertTrustCorp, assembleSolicitorCert::solsCertTrustCorp)
                    .put(solsCertWillSepPages, assembleSolicitorCert::solsCertWillSeparatePages)
                    .put(solsAffidAliasInt, assembleSolicitorAffidavit::solsAffidAliasIntestacy)
                    .put(solsAffidAlias, assembleSolicitorAffidavit::solsAffidAlias)
                    .put(solsAffidExec, assembleSolicitorAffidavit::solsAffidExec)
                    .put(solsAffidHandwriting, assembleSolicitorAffidavit::solsAffidHandwriting)
                    .put(solsAffidIdentity, assembleSolicitorAffidavit::solsAffidIdentity)
                    .put(solsAffidKnowledge, assembleSolicitorAffidavit::solsAffidKnowledge)
                    .put(solsAffidAlterations, assembleSolicitorAffidavit::solsAffidAlterations)
                    .put(solsAffidDate, assembleSolicitorAffidavit::solsAffidDate)
                    .put(solsAffidSearch, assembleSolicitorAffidavit::solsAffidSearch)
                    .put(solsAffidRecital, assembleSolicitorAffidavit::solsAffidMisRecital)
                    .put(solsRedecCodicil, assembleSolicitorRedeclaration::solsRedecCodicil)
                    .put(solsRedecSotSigned, assembleSolicitorRedeclaration::solsRedecSotSigned)
                    .put(solsRedecDomicile, assembleSolicitorRedeclaration::solsRedecDomicile)
                    .put(solsRedecIntForDom, assembleSolicitorRedeclaration::solsRedecIntestacyForeignDomicile)
                    .put(soplsRedecWillsForDom, assembleSolicitorRedeclaration::solsRedecWillsForeignDomicile)
                    .put(solsRedecMinority, assembleSolicitorRedeclaration::solsRedecMinorityInterest)
                    .put(solsRedecNetEstate, assembleSolicitorRedeclaration::solsRedecNetEstate)
                    .put(solsRedecTitle, assembleSolicitorRedeclaration::solsRedecTitle)
                    .put(solsRedecClearing, assembleSolicitorRedeclaration::solsRedecClearing)
                    .build();
        }

        return paragraphCodeFunctions;
    }

    public void setupAllLetterParagraphDetails(@Valid CaseDetails caseDetails,
                                               ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder) {
        CaseData caseData = caseDetails.getData();
        Categories categories = caseData.getCategories();
        List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
        addParagraphsForUsedFields(paragraphDetails, Caseworker.getParagraphFields(), caseData);
        addAllCategoryParagraphs(paragraphDetails, categories.getAllSelectedCategories(), caseData);
        addParagraphsForUsedFields(paragraphDetails, FreeText.getParagraphFields(), caseData);

        responseCaseDataBuilder.categories(categories);
        responseCaseDataBuilder.paragraphDetails(paragraphDetails);
    }

    private void addAllCategoryParagraphs(List<CollectionMember<ParagraphDetail>> paragraphDetails,
                                          List<List<String>> allCategories, CaseData caseData) {
        for (List<String> categories : allCategories) {
            addParagraphs(paragraphDetails, categories, caseData);
        }
    }

    private void addParagraphsForUsedFields(List<CollectionMember<ParagraphDetail>> allParagraphDetails,
                                            List<ParagraphField> paragraphFields, CaseData caseData) {
        for (ParagraphField paragraphField : paragraphFields) {
            Optional<ParagraphCode> paragraphCode = ParagraphCode.fromFieldCode(paragraphField.getFieldCode());
            if (paragraphCode.isPresent()) {
                addAllParagraphDetails(allParagraphDetails, paragraphCode.get(), caseData);
            }
        }

    }

    private void addAllParagraphDetails(List<CollectionMember<ParagraphDetail>> allParagraphDetails,
                                        ParagraphCode paragraphCode, CaseData caseData) {
        List<ParagraphDetail> paragraphDetails = getParagraphFunctions().get(paragraphCode).apply(paragraphCode, caseData);
        for (ParagraphDetail paragraphDetail : paragraphDetails) {
            CollectionMember<ParagraphDetail> paragraphDetailCollectionMember = new CollectionMember<>(null, paragraphDetail);
            allParagraphDetails.add(paragraphDetailCollectionMember);
        }
    }

    private void addParagraphs(List<CollectionMember<ParagraphDetail>> allParagraphDetails,
                               List<String> selectedParagraphs, CaseData caseData) {
        if (selectedParagraphs == null) {
            return;
        }
        for (String selectedPara : selectedParagraphs) {
            Optional<ParagraphCode> paragraphCode = ParagraphCode.fromFieldCode(selectedPara);
            if (paragraphCode.isPresent()) {
                addAllParagraphDetails(allParagraphDetails, paragraphCode.get(), caseData);
            }
        }
    }
}
