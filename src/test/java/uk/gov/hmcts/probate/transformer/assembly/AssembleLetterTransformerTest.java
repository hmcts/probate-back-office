package uk.gov.hmcts.probate.transformer.assembly;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.Categories;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleCaseworker;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleEntitlement;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleForeignDomicile;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleFreeText;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleIHT;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleMissingInformation;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleWill;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AssembleLetterTransformerTest {
    @Mock
    private AssembleCaseworker assembleCaseworker;
    @Mock
    private AssembleFreeText assembleFreeText;
    @Mock
    private AssembleEntitlement assembleEntitlement;
    @Mock
    private AssembleIHT assembleIHT;
    @Mock
    private AssembleMissingInformation assembleMissingInformation;
    @Mock
    private AssembleForeignDomicile assembleForeignDomicile;
    @Mock
    private AssembleWill assembleWill;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    private AssembleLetterTransformer assembleLetterTransformer;

    @Before
    public void setUp() {
        assembleLetterTransformer = new AssembleLetterTransformer(assembleCaseworker,
                assembleFreeText, assembleEntitlement, assembleIHT, assembleMissingInformation, assembleForeignDomicile,
                assembleWill);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
    }

    @Test
    public void shouldSetupNoParagraphsFromMocks() {
        Categories categories = Categories.builder().build();
        when(caseDataMock.getCategories()).thenReturn(categories);
        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = ResponseCaseData.builder();

        assembleLetterTransformer.setupAllLetterParagraphDetails(caseDetailsMock, responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertThat(responseCaseData.getParagraphDetails().size(), equalTo(0));

    }

    @Test
    public void shouldSetupDefaultParagraphsFromMocks() {
        Categories categories = Categories.builder().build();
        when(caseDataMock.getCategories()).thenReturn(categories);

        List<ParagraphDetail> caseworkerTextList = Arrays.asList(ParagraphDetail.builder().code("someCode1").build());
        when(assembleCaseworker.caseworker(ArgumentMatchers.any(ParagraphCode.class), ArgumentMatchers.any(CaseData.class)))
                .thenReturn(caseworkerTextList);
        List<ParagraphDetail> freeTextList = Arrays.asList(ParagraphDetail.builder().code("someCode2").build());
        when(assembleFreeText.freeText(ArgumentMatchers.any(ParagraphCode.class), ArgumentMatchers.any(CaseData.class)))
                .thenReturn(freeTextList);

        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = ResponseCaseData.builder();

        assembleLetterTransformer.setupAllLetterParagraphDetails(caseDetailsMock, responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertThat(responseCaseData.getParagraphDetails().size(), equalTo(2));
        assertThat(responseCaseData.getParagraphDetails().get(0).getValue().getCode(), equalTo("someCode1"));
        assertThat(responseCaseData.getParagraphDetails().get(1).getValue().getCode(), equalTo("someCode2"));
    }

    @Test
    public void shouldSetupAllParagraphsFromMocks() {
        Categories categories = Mockito.mock(Categories.class);

        List<String> forDom = Arrays.asList("ForDomAffidavit");
        List<String> ent = Arrays.asList("EntExecNoAcc");
        List<String> iht = Arrays.asList("IHT205Miss");
        List<String> miss = Arrays.asList("MissInfoChangeApp");
        List<String> will = Arrays.asList("WillAnyOther");
        List selectedCats = Arrays.asList(forDom, ent, iht, miss, will);
        when(categories.getAllSelectedCategories()).thenReturn(selectedCats);
        when(caseDataMock.getCategories()).thenReturn(categories);

        List<ParagraphDetail> caseworkerTextList = Arrays.asList(ParagraphDetail.builder().code("someCode1").build());
        when(assembleCaseworker.caseworker(ArgumentMatchers.any(ParagraphCode.class), ArgumentMatchers.any(CaseData.class)))
                .thenReturn(caseworkerTextList);

        List<ParagraphDetail> freeTextList = Arrays.asList(ParagraphDetail.builder().code("someCode2").build());
        when(assembleFreeText.freeText(ArgumentMatchers.any(ParagraphCode.class), ArgumentMatchers.any(CaseData.class)))
                .thenReturn(freeTextList);

        List<ParagraphDetail> entTextList = Arrays.asList(ParagraphDetail.builder().code("someCode3").build());
        when(assembleEntitlement.executorNotAccountedFor(ArgumentMatchers.any(ParagraphCode.class), ArgumentMatchers.any(CaseData.class)))
                .thenReturn(entTextList);

        List<ParagraphDetail> ihtTextList = Arrays.asList(ParagraphDetail.builder().code("someCode4").build());
        when(assembleIHT.iht205Missing(ArgumentMatchers.any(ParagraphCode.class), ArgumentMatchers.any(CaseData.class)))
                .thenReturn(ihtTextList);

        List<ParagraphDetail> missTextList = Arrays.asList(ParagraphDetail.builder().code("someCode5").build());
        when(assembleMissingInformation.missingInfoChangeOfApplicant(ArgumentMatchers.any(ParagraphCode.class),
                ArgumentMatchers.any(CaseData.class)))
                .thenReturn(missTextList);

        List<ParagraphDetail> forDomTextList = Arrays.asList(ParagraphDetail.builder().code("someCode6").build());
        when(assembleForeignDomicile.affidavitOfLaw(ArgumentMatchers.any(ParagraphCode.class),
                ArgumentMatchers.any(CaseData.class)))
                .thenReturn(forDomTextList);

        List<ParagraphDetail> willTextList = Arrays.asList(ParagraphDetail.builder().code("someCode7").build());
        when(assembleWill.willAnyOther(ArgumentMatchers.any(ParagraphCode.class), ArgumentMatchers.any(CaseData.class)))
                .thenReturn(willTextList);

        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = ResponseCaseData.builder();

        assembleLetterTransformer.setupAllLetterParagraphDetails(caseDetailsMock, responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertThat(responseCaseData.getParagraphDetails().size(), equalTo(7));
        assertThat(responseCaseData.getParagraphDetails().get(0).getValue().getCode(), equalTo("someCode1"));
        assertThat(responseCaseData.getParagraphDetails().get(1).getValue().getCode(), equalTo("someCode6"));
        assertThat(responseCaseData.getParagraphDetails().get(2).getValue().getCode(), equalTo("someCode3"));
        assertThat(responseCaseData.getParagraphDetails().get(3).getValue().getCode(), equalTo("someCode4"));
        assertThat(responseCaseData.getParagraphDetails().get(4).getValue().getCode(), equalTo("someCode5"));
        assertThat(responseCaseData.getParagraphDetails().get(5).getValue().getCode(), equalTo("someCode7"));
        assertThat(responseCaseData.getParagraphDetails().get(6).getValue().getCode(), equalTo("someCode2"));

    }
}
