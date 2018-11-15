package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.CavFullAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;

@RunWith(MockitoJUnitRunner.class)
public class CaveatCallbackResponseTransformerTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String YES = "Yes";

    private static final String CAV_DECEASED_FORENAMES = "Forenames";
    private static final String CAV_DECEASED_SURNAME = "Surname";
    private static final LocalDate CAV_DECEASED_DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final String CAV_DECEASED_HAS_ALIAS = YES;
    private static final String CAV_DECEASED_FULL_ALIAS_NAME = "AliasFN AliasSN";
    private static final List<CollectionMember<CavFullAliasName>> CAV_DECEASED_FULL_ALIAS_NAME_LIST = emptyList();
    private static final CavAddress CAV_DECEASED_ADDRESS = Mockito.mock(CavAddress.class);

    private static final String CAV_CAVEATOR_FORENAMES = "Forenames";
    private static final String CAV_CAVEATOR_SURNAME = "Surname";
    private static final String CAV_CAVEATOR_EMAIL_ADDRESS = "cav@email.com";
    private static final CavAddress CAV_CAVEATOR_ADDRESS = Mockito.mock(CavAddress.class);

    private static final LocalDate CAV_EXPIRY_DATE = LocalDate.now().plusMonths(CAVEAT_LIFESPAN);
    private static final String CAV_FORMATTED_EXPIRY_DATE = dateTimeFormatter.format(CAV_EXPIRY_DATE);

    @InjectMocks
    private CaveatCallbackResponseTransformer underTest;

    @Mock
    private CaveatCallbackRequest caveatCallbackRequestMock;

    @Mock
    private CaveatDetails caveatDetailsMock;

    private CaveatData.CaveatDataBuilder caveatDataBuilder;

    @Before
    public void setup() {

        caveatDataBuilder = CaveatData.builder()
                .cavDeceasedForenames(CAV_DECEASED_FORENAMES)
                .cavDeceasedSurname(CAV_DECEASED_SURNAME)
                .cavDeceasedDateOfDeath(CAV_DECEASED_DOD)
                .cavDeceasedAnyOtherNames(CAV_DECEASED_HAS_ALIAS)
                .cavDeceasedFullAliasNameList(CAV_DECEASED_FULL_ALIAS_NAME_LIST)
                .cavDeceasedAddress(CAV_DECEASED_ADDRESS)
                .cavCaveatorForenames(CAV_CAVEATOR_FORENAMES)
                .cavCaveatorSurname(CAV_CAVEATOR_SURNAME)
                .cavCaveatorEmailAddress(CAV_CAVEATOR_EMAIL_ADDRESS)
                .cavCaveatorAddress(CAV_CAVEATOR_ADDRESS)
                .cavExpiryDate(CAV_EXPIRY_DATE);

        when(caveatCallbackRequestMock.getCaveatDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getCaveatData()).thenReturn(caveatDataBuilder.build());
    }

    @Test
    public void shouldTransformCaveatCallbackRequestToCaveatCallbackResponse() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.transform(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);
    }

    @Test
    public void shouldTransformCaseForCaveatWithDeceasedAliasNames() {
        List<CollectionMember<CavFullAliasName>> cavDeceasedFullAliasNameList = new ArrayList<>();

        CavFullAliasName an11 = CavFullAliasName.builder().fullAliasName(CAV_DECEASED_FULL_ALIAS_NAME).build();
        CollectionMember<CavFullAliasName> an1 = new CollectionMember<>("0", an11);
        cavDeceasedFullAliasNameList.add(an1);
        caveatDataBuilder.cavDeceasedFullAliasNameList(cavDeceasedFullAliasNameList);

        when(caveatCallbackRequestMock.getCaveatDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getCaveatData()).thenReturn(caveatDataBuilder.build());

        CaveatCallbackResponse caveatCallbackResponse = underTest.transform(caveatCallbackRequestMock);

        assertCommonDetails(caveatCallbackResponse);
        assertEquals(1, caveatCallbackResponse.getCaveatData().getCavDeceasedFullAliasNameList().size());
    }

    @Test
    public void shouldGetCaveatUploadedDocuments() {
        List<CollectionMember<UploadDocument>> documents = new ArrayList<>();
        documents.add(createUploadDocuments("0"));
        caveatDataBuilder.cavDocumentsUploaded(documents);

        when(caveatCallbackRequestMock.getCaveatDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getCaveatData()).thenReturn(caveatDataBuilder.build());

        CaveatCallbackResponse caveatCallbackResponse = underTest.transform(caveatCallbackRequestMock);

        assertCommonDetails(caveatCallbackResponse);
        assertEquals(1, caveatCallbackResponse.getCaveatData().getCavDocumentsUploaded().size());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatExpiryDateChange() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatRaised(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);

        assertEquals(CAV_FORMATTED_EXPIRY_DATE, caveatCallbackResponse.getCaveatData().getCavExpiryDate());
    }

    private void assertCommon(CaveatCallbackResponse caveatCallbackResponse) {
        assertCommonDetails(caveatCallbackResponse);
    }

    private void assertCommonDetails(CaveatCallbackResponse caveatCallbackResponse) {

        assertEquals(CAV_DECEASED_FORENAMES, caveatCallbackResponse.getCaveatData().getCavDeceasedForenames());
        assertEquals(CAV_DECEASED_SURNAME, caveatCallbackResponse.getCaveatData().getCavDeceasedSurname());
        assertEquals("2017-12-31", caveatCallbackResponse.getCaveatData().getCavDeceasedDateOfDeath());
        assertEquals(CAV_DECEASED_HAS_ALIAS, caveatCallbackResponse.getCaveatData().getCavDeceasedAnyOtherNames());
        assertEquals(CAV_DECEASED_ADDRESS, caveatCallbackResponse.getCaveatData().getCavDeceasedAddress());

        assertEquals(CAV_CAVEATOR_FORENAMES, caveatCallbackResponse.getCaveatData().getCavCaveatorForenames());
        assertEquals(CAV_CAVEATOR_SURNAME, caveatCallbackResponse.getCaveatData().getCavCaveatorSurname());
        assertEquals(CAV_CAVEATOR_EMAIL_ADDRESS, caveatCallbackResponse.getCaveatData().getCavCaveatorEmailAddress());
        assertEquals(CAV_CAVEATOR_ADDRESS, caveatCallbackResponse.getCaveatData().getCavCaveatorAddress());
    }

    private CollectionMember<UploadDocument> createUploadDocuments(String id) {
        DocumentLink docLink = DocumentLink.builder()
                .documentBinaryUrl("")
                .documentFilename("")
                .documentUrl("")
                .build();

        UploadDocument doc = UploadDocument.builder()
                .comment("comment")
                .documentLink(docLink)
                .documentType(DocumentType.IHT).build();
        return new CollectionMember<>(id, doc);
    }
}
