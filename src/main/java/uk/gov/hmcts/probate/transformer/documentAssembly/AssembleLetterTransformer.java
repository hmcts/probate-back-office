package uk.gov.hmcts.probate.transformer.documentAssembly;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.Categories;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.CASEWORKER;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.ENT_EXEC_NOT_ACC;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.IHT_205_MISSING;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.IHT_AWAIT_IHT421;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.MISS_INFO_CHANGE_APP;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.MISS_INFO_DEATH_CERT;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.MISS_INFO_WILL;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.WILL_ANY_OTHER;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.WILL_PLIGHT;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.WILL_SEP_PAGES;
import static uk.gov.hmcts.probate.transformer.documentAssembly.ParagraphCode.WILL_STAPLE;

@Slf4j
@Component
public class AssembleLetterTransformer {
    private final Map<ParagraphCode, BiFunction<ParagraphCode, CaseData, ParagraphDetail>>
            paragraphCodeFunctions = ImmutableMap.<ParagraphCode, BiFunction<ParagraphCode, CaseData, ParagraphDetail>>builder()
            .put(ENT_EXEC_NOT_ACC, this::executorNotAccountedFor)
            .put(IHT_205_MISSING, this::iht205Missing)
            .put(IHT_AWAIT_IHT421, this::ihtAwait421)
            .put(MISS_INFO_WILL, this::missingInfoWill)
            .put(MISS_INFO_DEATH_CERT, this::missingInfoDeathCert)
            .put(MISS_INFO_CHANGE_APP, this::missingInfoChangeOfApplicant)
            .put(WILL_ANY_OTHER, this::willAnyOther)
            .put(WILL_PLIGHT, this::willPlight)
            .put(WILL_SEP_PAGES, this::willSeparatePages)
            .put(WILL_STAPLE, this::willStaple)
            .build();

    public void setupAllLetterParagraphDetails(@Valid CaseDetails caseDetails, ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder) {
        List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
        CaseData caseData = caseDetails.getData();
        Categories categories = caseData.getCategories();
        addParagraph(paragraphDetails, caseworker());
        addParagraphs(paragraphDetails, categories.getEntSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getIhtSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getMissInfoSelectedParagraphs(), caseData);
        addParagraphs(paragraphDetails, categories.getWillSelectedParagraphs(), caseData);

        categories.setParagraphDetails(paragraphDetails);
        responseCaseDataBuilder.categories(categories);
    }

    private ParagraphDetail caseworker() {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableText("Yes")
                .textLabel(CASEWORKER.getLabel())
                .code(CASEWORKER.getCode())
                .build();

        return paragraphDetail;
    }

    private ParagraphDetail willStaple(ParagraphCode paragraphCode, CaseData caseData) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableStatic("Yes")
                .staticLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .build();

        return paragraphDetail;
    }

    private ParagraphDetail iht205Missing(ParagraphCode paragraphCode, CaseData caseData) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableStatic("Yes")
                .staticLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .build();
        return paragraphDetail;
    }

    private ParagraphDetail ihtAwait421(ParagraphCode paragraphCode, CaseData caseData) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableStatic("Yes")
                .staticLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .build();

        return paragraphDetail;
    }

    private ParagraphDetail willSeparatePages(ParagraphCode paragraphCode, CaseData caseData) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableText("Yes")
                .textLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .build();

        return paragraphDetail;
    }

    private ParagraphDetail willPlight(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = create2ListItems("condition", "CONDITION REASON E.G.A TEAR",
                "staple", "STAPLE HOLES/ PUNCH HOLES");

        return createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    private ParagraphDetail willAnyOther(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = create2ListItems("completeLimit", "COMPLETE LIMITATION",
                "exemption", "EXEMPTION FROM THE WILL");

        return createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    private ParagraphDetail missingInfoDeathCert(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = create2ListItems("unclear", "THE ONE SUPPLIED IS UNCLEAR",
                "notSupplied", "ONE WAS NOT SUPPLIED");

        return createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    private ParagraphDetail missingInfoWill(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = create2ListItems("will", "WILL",
                "codicil", "CODICIL");
        return createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    private ParagraphDetail missingInfoChangeOfApplicant(ParagraphCode paragraphCode, CaseData caseData) {
        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableText("Yes")
                .textLabel(paragraphCode.getLabel())
                .textValue(caseData.getPrimaryApplicantFullName())
                .code(paragraphCode.getCode())
                .build();

        return paragraphDetail;
    }

    private List<DynamicListItem> create2ListItems(String item1Code, String item1Label, String item2Code, String item2Label) {
        List<DynamicListItem> listItems = new ArrayList<>();
        DynamicListItem item1 = DynamicListItem.builder()
                .code(item1Code)
                .label(item1Label)
                .build();
        listItems.add(item1);
        DynamicListItem item2 = DynamicListItem.builder()
                .code(item2Code)
                .label(item2Label)
                .build();
        listItems.add(item2);

        return listItems;
    }

    private ParagraphDetail executorNotAccountedFor(ParagraphCode paragraphCode, CaseData caseData) {
        List<DynamicListItem> listItems = new ArrayList<>();
        if (caseData.isPrimaryApplicantApplying()) {
            DynamicListItem primary = DynamicListItem.builder()
                    .code(caseData.getPrimaryApplicantForenames() + " " + caseData.getPrimaryApplicantSurname())
                    .label(caseData.getPrimaryApplicantForenames() + " " + caseData.getPrimaryApplicantSurname())
                    .build();
            listItems.add(primary);
        }
        if (caseData.getAdditionalExecutorsApplying() != null) {
            List<DynamicListItem> applying = caseData.getAdditionalExecutorsApplying()
                    .stream()
                    .map(CollectionMember::getValue)
                    .map(executor -> DynamicListItem.builder()
                            .code(executor.getApplyingExecutorName())
                            .label(executor.getApplyingExecutorName())
                            .build())
                    .collect(Collectors.toList());
            listItems.addAll(applying);
        }

        return createDynamicListParagraphDetail(paragraphCode, listItems);
    }

    private ParagraphDetail createDynamicListParagraphDetail(ParagraphCode paragraphCode, List<DynamicListItem> listItems) {
        DynamicList dynamicList = DynamicList.builder()
                .listItems(listItems)
                .value(DynamicListItem.builder().build())
                .build();

        ParagraphDetail paragraphDetail = ParagraphDetail.builder()
                .enableList("Yes")
                .dynamicList(dynamicList)
                .listLabel(paragraphCode.getLabel())
                .code(paragraphCode.getCode())
                .build();

        return paragraphDetail;
    }

    private void addParagraph(List<CollectionMember<ParagraphDetail>> paragraphDetails, ParagraphDetail paragraphDetail) {
        CollectionMember<ParagraphDetail> paragraphDetailCollectionMember = new CollectionMember<>(null, paragraphDetail);
        paragraphDetails.add(paragraphDetailCollectionMember);
    }

    private void addParagraphs(List<CollectionMember<ParagraphDetail>> paragraphDetails, List<String> selectedParagraphs, CaseData caseData) {
        if (selectedParagraphs == null) {
            return;
        }
        for (String selectedPara : selectedParagraphs) {
            Optional<ParagraphCode> paragraphCode = ParagraphCode.fromCode(selectedPara);
            ParagraphDetail paragraphDetail = paragraphCodeFunctions.get(paragraphCode.get()).apply(paragraphCode.get(), caseData);
            CollectionMember<ParagraphDetail> paragraphDetailCollectionMember = new CollectionMember<>(null, paragraphDetail);
            paragraphDetails.add(paragraphDetailCollectionMember);
        }
    }
}
