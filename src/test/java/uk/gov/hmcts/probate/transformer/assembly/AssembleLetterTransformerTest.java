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
    AssembleIncapacity assembleIncapacity;
    @Mock
    AssembleLifeAndMinorityInterest assembleLifeAndMinorityInterest;
    @Mock
    AssembleSOTIncomplete assembleSOTIncomplete;
    @Mock
    AssembleWitness assembleWitness;
    @Mock
    AssembleSolicitorGeneral assembleSolicitorGeneral;
    @Mock
    AssembleSolicitorCert assembleSolicitorCert;
    @Mock
    AssembleSolicitorAffidavit assembleSolicitorAffidavit;
    @Mock
    AssembleSolicitorRedeclaration assembleSolicitorRedeclaration;

    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    private AssembleLetterTransformer assembleLetterTransformer;

    @Before
    public void setUp() {
        assembleLetterTransformer = new AssembleLetterTransformer(assembleCaseworker,
                assembleFreeText, assembleEntitlement, assembleIHT, assembleMissingInformation, assembleForeignDomicile,
                assembleWill, assembleIncapacity, assembleLifeAndMinorityInterest, assembleSOTIncomplete,
                assembleWitness, assembleSolicitorGeneral, assembleSolicitorCert, assembleSolicitorAffidavit,
                assembleSolicitorRedeclaration);

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
        List<String> incap = Arrays.asList("IncapGen");
        List<String> admonLife = Arrays.asList("LMAdmonLife");
        List<String> sotPa1pQ2 = Arrays.asList("SotPa1pQ2");
        List<String> witConsent = Arrays.asList("WitConsent");
        List<String> powAtt = Arrays.asList("GenPowerOfAttorney");
        List<String> certAlias = Arrays.asList("Certsalias");
        List<String> affidAliasAffidInt = Arrays.asList("AffidAliasAffidInt");
        List<String> redecClearing = Arrays.asList("RedecClearing");

        List selectedCats = Arrays.asList(forDom, ent, iht, miss, will, incap, admonLife, sotPa1pQ2, witConsent, powAtt,
            certAlias, affidAliasAffidInt, redecClearing);
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

        List<ParagraphDetail> incapGenTextList = Arrays.asList(ParagraphDetail.builder().code("someCode8").build());
        when(assembleIncapacity.incapacityGeneral(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(incapGenTextList);

        List<ParagraphDetail> lifeMinTextList = Arrays.asList(ParagraphDetail.builder().code("someCode9").build());
        when(assembleLifeAndMinorityInterest.admonWillLife(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(lifeMinTextList);

        List<ParagraphDetail> sotPa1pQ2TextList = Arrays.asList(ParagraphDetail.builder().code("someCode10").build());
        when(assembleSOTIncomplete.sotPa1pQ2(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(sotPa1pQ2TextList);

        List<ParagraphDetail> witConsentTextList = Arrays.asList(ParagraphDetail.builder().code("someCode11").build());
        when(assembleWitness.witnessConsent(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(witConsentTextList);

        List<ParagraphDetail> powAttTextList = Arrays.asList(ParagraphDetail.builder().code("someCode12").build());
        when(assembleSolicitorGeneral.solsGenPowerAttorney(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(powAttTextList);

        List<ParagraphDetail> solCertAliasTextList = Arrays.asList(ParagraphDetail.builder().code("someCode13").build());
        when(assembleSolicitorCert.solsCertAlias(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(solCertAliasTextList);

        List<ParagraphDetail> affidAliasAffidIntTextList =
            Arrays.asList(ParagraphDetail.builder().code("someCode14").build());
        when(assembleSolicitorAffidavit.solsAffidAlias(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(affidAliasAffidIntTextList);

        List<ParagraphDetail> redecClearingTextList =
            Arrays.asList(ParagraphDetail.builder().code("someCode15").build());
        when(assembleSolicitorRedeclaration.solsRedecClearing(ArgumentMatchers.any(ParagraphCode.class),
            ArgumentMatchers.any(CaseData.class)))
            .thenReturn(redecClearingTextList);

        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = ResponseCaseData.builder();

        assembleLetterTransformer.setupAllLetterParagraphDetails(caseDetailsMock, responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertThat(responseCaseData.getParagraphDetails().size(), equalTo(15));
        int i = 0;

        assertDetail(responseCaseData, i++, "someCode1");
        assertDetail(responseCaseData, i++, "someCode6");
        assertDetail(responseCaseData, i++, "someCode3");
        assertDetail(responseCaseData, i++, "someCode4");
        assertDetail(responseCaseData, i++, "someCode5");
        assertDetail(responseCaseData, i++, "someCode7");
        assertDetail(responseCaseData, i++, "someCode8");
        assertDetail(responseCaseData, i++, "someCode9");
        assertDetail(responseCaseData, i++, "someCode10");
        assertDetail(responseCaseData, i++, "someCode11");
        assertDetail(responseCaseData, i++, "someCode12");
        assertDetail(responseCaseData, i++, "someCode13");
        assertDetail(responseCaseData, i++, "someCode14");
        assertDetail(responseCaseData, i++, "someCode15");
        assertDetail(responseCaseData, i++, "someCode2");

    }

    private void assertDetail(ResponseCaseData responseCaseData, int i, String code) {
        assertThat(responseCaseData.getParagraphDetails().get(i).getValue().getCode(), equalTo(code));
    }
}
