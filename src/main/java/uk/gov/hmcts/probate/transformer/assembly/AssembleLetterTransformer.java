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
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleMissingInformation;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleWill;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.Caseworker;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntAttorney;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntDeathPa;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntExecNoAcc;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntFamTree;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntLeadingApp;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntNoTitle;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntPrejudiced;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntSubExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntWrongExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.ForDomAffidavit;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.ForDomInitialEnq;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.FreeText;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT205GrossEstateOver;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT205Miss;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT205NoAssets;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT217Miss;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT421Await;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHTIHT400;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapGen;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapInstitutedExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapMedical;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapOneExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoAwaitResponse;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoChangeApp;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoDeathCert;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoWill;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillAnyOther;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillPlight;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillSepPages;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillStaple;

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
                    .put(WillAnyOther, assembleWill::willAnyOther)
                    .put(WillPlight, assembleWill::willPlight)
                    .put(WillSepPages, assembleWill::willSeparatePages)
                    .put(WillStaple, assembleWill::willStaple)
                    .put(IncapGen, assembleIncapacity::incapacityGeneral)
                    .put(IncapOneExec, assembleIncapacity::incapacityOneExecutor)
                    .put(IncapInstitutedExec, assembleIncapacity::incapacityInstitutedExecutor)
                    .put(IncapMedical, assembleIncapacity::incapacityMedicalEvidence)
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
