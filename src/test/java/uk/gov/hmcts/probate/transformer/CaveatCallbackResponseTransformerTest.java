package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.caveat.CavAddress;
import uk.gov.hmcts.probate.model.ccd.caveat.CavFullAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;

@RunWith(MockitoJUnitRunner.class)
public class CaveatCallbackResponseTransformerTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ApplicationType CAV_APPLICATION_TYPE = PERSONAL;
    private static final String CAV_REGISTRY_LOCATION = "Leeds";

    private static final String YES = "Yes";

    private static final String CAV_DECEASED_FORENAMES = "Forenames";
    private static final String CAV_DECEASED_SURNAME = "Surname";
    private static final LocalDate CAV_DECEASED_DOD = LocalDate.parse("2017-12-31", dateTimeFormatter);
    private static final LocalDate CAV_DECEASED_DOB = LocalDate.parse("2016-12-31", dateTimeFormatter);
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

    private static final String CAV_MESSAGE_CONTENT = "";

    @InjectMocks
    private CaveatCallbackResponseTransformer underTest;

    @Mock
    private CaveatCallbackRequest caveatCallbackRequestMock;

    @Mock
    private Document document;

    @Mock
    private CaveatDetails caveatDetailsMock;

    private CaveatData.CaveatDataBuilder caveatDataBuilder;

    @Before
    public void setup() {

        caveatDataBuilder = CaveatData.builder()
                .deceasedForenames(CAV_DECEASED_FORENAMES)
                .deceasedSurname(CAV_DECEASED_SURNAME)
                .deceasedDateOfDeath(CAV_DECEASED_DOD)
                .deceasedDateOfBirth(CAV_DECEASED_DOB)
                .deceasedAnyOtherNames(CAV_DECEASED_HAS_ALIAS)
                .deceasedFullAliasNameList(CAV_DECEASED_FULL_ALIAS_NAME_LIST)
                .deceasedAddress(CAV_DECEASED_ADDRESS)
                .deceasedForenames(CAV_CAVEATOR_FORENAMES)
                .caveatorSurname(CAV_CAVEATOR_SURNAME)
                .caveatorEmailAddress(CAV_CAVEATOR_EMAIL_ADDRESS)
                .caveatorAddress(CAV_CAVEATOR_ADDRESS)
                .expiryDate(CAV_EXPIRY_DATE)
                .messageContent(CAV_MESSAGE_CONTENT);

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
        caveatDataBuilder.deceasedFullAliasNameList(cavDeceasedFullAliasNameList);

        when(caveatCallbackRequestMock.getCaveatDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getCaveatData()).thenReturn(caveatDataBuilder.build());

        CaveatCallbackResponse caveatCallbackResponse = underTest.transform(caveatCallbackRequestMock);

        assertCommonDetails(caveatCallbackResponse);
        assertEquals(1, caveatCallbackResponse.getCaveatData().getDeceasedFullAliasNameList().size());
    }

    @Test
    public void shouldGetCaveatUploadedDocuments() {
        List<CollectionMember<UploadDocument>> documents = new ArrayList<>();
        documents.add(createUploadDocuments("0"));
        caveatDataBuilder.documentsUploaded(documents);

        when(caveatCallbackRequestMock.getCaveatDetails()).thenReturn(caveatDetailsMock);
        when(caveatDetailsMock.getCaveatData()).thenReturn(caveatDataBuilder.build());

        CaveatCallbackResponse caveatCallbackResponse = underTest.transform(caveatCallbackRequestMock);

        assertCommonDetails(caveatCallbackResponse);
        assertEquals(1, caveatCallbackResponse.getCaveatData().getDocumentsUploaded().size());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatExpiryDateChange() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.caveatRaised(caveatCallbackRequestMock);

        assertCommon(caveatCallbackResponse);

        assertEquals(CAV_FORMATTED_EXPIRY_DATE, caveatCallbackResponse.getCaveatData().getExpiryDate());
    }

    @Test
    public void shouldConvertRequestToDataBeanWithCaveatMessageContentChange() {
        CaveatCallbackResponse caveatCallbackResponse = underTest.generalMessage(caveatCallbackRequestMock, document);

        assertCommon(caveatCallbackResponse);

        assertTrue(caveatCallbackResponse.getCaveatData().getMessageContent().isEmpty());
    }

    private void assertCommon(CaveatCallbackResponse caveatCallbackResponse) {
        assertCommonDetails(caveatCallbackResponse);
        assertApplicationType(caveatCallbackResponse, CAV_APPLICATION_TYPE);
    }

    private void assertCommonDetails(CaveatCallbackResponse caveatCallbackResponse) {
        assertEquals(CAV_REGISTRY_LOCATION, caveatCallbackResponse.getCaveatData().getRegistryLocation());

        assertEquals(CAV_DECEASED_FORENAMES, caveatCallbackResponse.getCaveatData().getDeceasedForenames());
        assertEquals(CAV_DECEASED_SURNAME, caveatCallbackResponse.getCaveatData().getDeceasedSurname());
        assertEquals("2017-12-31", caveatCallbackResponse.getCaveatData().getDeceasedDateOfDeath());
        assertEquals("2016-12-31", caveatCallbackResponse.getCaveatData().getDeceasedDateOfBirth());
        assertEquals(CAV_DECEASED_HAS_ALIAS, caveatCallbackResponse.getCaveatData().getDeceasedAnyOtherNames());
        assertEquals(CAV_DECEASED_ADDRESS, caveatCallbackResponse.getCaveatData().getDeceasedAddress());

        assertEquals(CAV_CAVEATOR_FORENAMES, caveatCallbackResponse.getCaveatData().getCaveatorForenames());
        assertEquals(CAV_CAVEATOR_SURNAME, caveatCallbackResponse.getCaveatData().getCaveatorSurname());
        assertEquals(CAV_CAVEATOR_EMAIL_ADDRESS, caveatCallbackResponse.getCaveatData().getCaveatorEmailAddress());
        assertEquals(CAV_CAVEATOR_ADDRESS, caveatCallbackResponse.getCaveatData().getCaveatorAddress());
        assertEquals(CAV_MESSAGE_CONTENT, caveatCallbackResponse.getCaveatData().getMessageContent());
    }

    private void assertApplicationType(CaveatCallbackResponse caveatCallbackResponse, ApplicationType cavApplicationType) {
        assertEquals(cavApplicationType, caveatCallbackResponse.getCaveatData().getApplicationType());
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
