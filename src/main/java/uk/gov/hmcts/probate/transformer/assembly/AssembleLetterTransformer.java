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
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleIHT;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleMissingInformation;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleWill;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.CASEWORKER;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.ENT_EXEC_NOT_ACC;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT_205_MISSING;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT_AWAIT_IHT421;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MISS_INFO_CHANGE_APP;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MISS_INFO_DEATH_CERT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MISS_INFO_WILL;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WILL_ANY_OTHER;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WILL_PLIGHT;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WILL_SEP_PAGES;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WILL_STAPLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleLetterTransformer {
    private final AssembleCaseworker assembleCaseworker;
    private final AssembleEntitlement assembleEntitlement;
    private final AssembleIHT assembleIHT;
    private final AssembleMissingInformation assembleMissingInformation;
    private final AssembleWill assembleWill;

    private Map<ParagraphCode, BiFunction<ParagraphCode, CaseData, ParagraphDetail>>
            paragraphCodeFunctions;

    private Map<ParagraphCode, BiFunction<ParagraphCode, CaseData, ParagraphDetail>> getParagraphFunctions() {
        if (paragraphCodeFunctions == null) {
            paragraphCodeFunctions = ImmutableMap.<ParagraphCode, BiFunction<ParagraphCode, CaseData, ParagraphDetail>>builder()
                    .put(CASEWORKER, assembleCaseworker::caseworker)
                    .put(ENT_EXEC_NOT_ACC, assembleEntitlement::executorNotAccountedFor)
                    .put(IHT_205_MISSING, assembleIHT::iht205Missing)
                    .put(IHT_AWAIT_IHT421, assembleIHT::ihtAwait421)
                    .put(MISS_INFO_WILL, assembleMissingInformation::missingInfoWill)
                    .put(MISS_INFO_DEATH_CERT, assembleMissingInformation::missingInfoDeathCert)
                    .put(MISS_INFO_CHANGE_APP, assembleMissingInformation::missingInfoChangeOfApplicant)
                    .put(WILL_ANY_OTHER, assembleWill::willAnyOther)
                    .put(WILL_PLIGHT, assembleWill::willPlight)
                    .put(WILL_SEP_PAGES, assembleWill::willSeparatePages)
                    .put(WILL_STAPLE, assembleWill::willStaple)
                    .build();
        }

        return paragraphCodeFunctions;
    }

    public void setupAllLetterParagraphDetails(@Valid CaseDetails caseDetails,
                                               ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder) {
        CaseData caseData = caseDetails.getData();
        Categories categories = caseData.getCategories();
        List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
        addParagraphs(paragraphDetails, Arrays.asList(CASEWORKER.getCode()), caseData);
        addParagraphs(paragraphDetails, categories.getEntSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getIhtSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getMissInfoSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getWillSelectedParagraphs(), caseData);

        responseCaseDataBuilder.categories(categories);
        responseCaseDataBuilder.paragraphDetails(paragraphDetails);
    }

    private void addParagraphs(List<CollectionMember<ParagraphDetail>> paragraphDetails,
                               List<String> selectedParagraphs, CaseData caseData) {
        if (selectedParagraphs == null) {
            return;
        }
        for (String selectedPara : selectedParagraphs) {
            Optional<ParagraphCode> paragraphCode = ParagraphCode.fromCode(selectedPara);
            ParagraphDetail paragraphDetail = getParagraphFunctions().get(paragraphCode.get()).apply(paragraphCode.get(), caseData);
            CollectionMember<ParagraphDetail> paragraphDetailCollectionMember = new CollectionMember<>(null, paragraphDetail);
            paragraphDetails.add(paragraphDetailCollectionMember);
        }
    }
}
