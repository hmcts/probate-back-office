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
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleFreeText;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleIHT;
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

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.Caeworker;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntExecNoAcc;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.FreeText;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT205Miss;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT421Await;
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
    private final AssembleWill assembleWill;

    private Map<ParagraphCode, BiFunction<ParagraphCode, CaseData, List<ParagraphDetail>>>
            paragraphCodeFunctions;

    private Map<ParagraphCode, BiFunction<ParagraphCode, CaseData, List<ParagraphDetail>>> getParagraphFunctions() {
        if (paragraphCodeFunctions == null) {
            paragraphCodeFunctions = ImmutableMap.<ParagraphCode, BiFunction<ParagraphCode, CaseData, List<ParagraphDetail>>>builder()
                    .put(FreeText, assembleFreeText::freeText)
                    .put(Caeworker, assembleCaseworker::caseworker)
                    .put(EntExecNoAcc, assembleEntitlement::executorNotAccountedFor)
                    .put(IHT205Miss, assembleIHT::iht205Missing)
                    .put(IHT421Await, assembleIHT::ihtAwait421)
                    .put(MissInfoWill, assembleMissingInformation::missingInfoWill)
                    .put(MissInfoDeathCert, assembleMissingInformation::missingInfoDeathCert)
                    .put(MissInfoChangeApp, assembleMissingInformation::missingInfoChangeOfApplicant)
                    .put(WillAnyOther, assembleWill::willAnyOther)
                    .put(WillPlight, assembleWill::willPlight)
                    .put(WillSepPages, assembleWill::willSeparatePages)
                    .put(WillStaple, assembleWill::willStaple)
                    .build();
        }

        return paragraphCodeFunctions;
    }

    public void setupAllLetterParagraphDetails(@Valid CaseDetails caseDetails,
                                               ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder) {
        CaseData caseData = caseDetails.getData();
        Categories categories = caseData.getCategories();
        List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
        addParagraphsForUsedFields(paragraphDetails, Caeworker.getParagraphFields(), caseData);
        addParagraphs(paragraphDetails, categories.getEntSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getIhtSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getMissInfoSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getWillSelectedParagraphs(), caseData);
        addParagraphsForUsedFields(paragraphDetails, FreeText.getParagraphFields(), caseData);

        responseCaseDataBuilder.categories(categories);
        responseCaseDataBuilder.paragraphDetails(paragraphDetails);
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
