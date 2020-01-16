package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class HmrcFileServiceTest {
    private FileExtractDateFormatter fileExtractDateFormatter = Mockito.mock(FileExtractDateFormatter.class);
    private HmrcFileService hmrcFileService = new HmrcFileService(new TextFileBuilderService(), fileExtractDateFormatter);

    private ImmutableList.Builder<ReturnedCaseDetails> caseList = new ImmutableList.Builder<>();
    private CaseData.CaseDataBuilder caseDataSolictor;
    private CaseData.CaseDataBuilder caseDataPersonal;
    private CaseData.CaseDataBuilder caseDataCarriageReturns;
    private ReturnedCaseDetails createdCase;
    private CaseData builtData;
    private static final String FILE_DATE = "20190101";
    private static final String FILE_NAME = "1_20190101.dat";
    private static final String[] LAST_MODIFIED = {"2019", "3", "3", "0", "0", "0", "0"};

    @Before
    public void setup() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
            new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                    .addressLine3("North West East Field")
                    .postCode("AB2 3CD")
                    .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        List<CollectionMember<AliasName>> deceasedAliasNames = Arrays.asList(
            new CollectionMember(null, AliasName.builder().solsAliasname("PETER PIPER KRENT").build()),
            new CollectionMember(null, AliasName.builder().solsAliasname("PETE KRENT").build())
        );
        LocalDate dod = LocalDate.of(2018, 8, 17);
        LocalDate dob = LocalDate.of(1940, 10, 20);
        String grantIssuedDate = "2018-10-24";
        caseDataSolictor = CaseData.builder()
            .deceasedForenames("PETAR")
            .deceasedSurname("KRNETA")
            .deceasedDateOfDeath(dod)
            .deceasedDateOfBirth(dob)
            .deceasedAddress(SolsAddress.builder().addressLine1("7 Pevensey Avenue")
                .addressLine3("Leicester")
                .postCode("LE5 6XQ").build())
            .boDeceasedTitle("MR")
            .solsDeceasedAliasNamesList(deceasedAliasNames)
            .primaryApplicantIsApplying("Yes")
            .primaryApplicantForenames("ANDJELKA")
            .primaryApplicantSurname("KOMODROMOS")
            .primaryApplicantAddress(SolsAddress.builder()
                .addressLine1("37 Otter Lane")
                .addressLine2("Mountsorrel")
                .addressLine3("Loughborough")
                .county("Leicestershire")
                .postCode("LE12 7GL")
                .build())
            .additionalExecutorsApplying(additionalExecutors)
            .solsSolicitorFirmName("BRAY & BRAY")
            .solsSolicitorAddress(SolsAddress.builder()
                .addressLine1("Spa Place")
                .addressLine2("36-42 Humberstone Road")
                .postTown("Leicester")
                .postCode("LE5 0AE")
                .build())
            .ihtFormId("IHT205")
            .ihtGrossValue(new BigDecimal(new BigInteger("32500000"), 0))
            .ihtNetValue(new BigDecimal(new BigInteger("28700000"), 0))
            .caseType("gop")
            .registryLocation("Liverpool")
            .grantIssuedDate(grantIssuedDate)
            .solsSOTName("John The solicitor")
            .applicationType(ApplicationType.SOLICITOR);

        caseDataPersonal = CaseData.builder()
            .deceasedForenames("PETAR")
            .deceasedSurname("KRNETA")
            .deceasedDateOfDeath(dod)
            .deceasedDateOfBirth(dob)
            .deceasedAddress(SolsAddress.builder()
                .addressLine1("7 Pevensey Avenue")
                .addressLine3("Leicester")
                .postCode("LE5 6XQ").build())
            .boDeceasedTitle("MR")
            .solsDeceasedAliasNamesList(deceasedAliasNames)
            .primaryApplicantIsApplying("Yes")
            .primaryApplicantForenames("ANDJELKA")
            .primaryApplicantSurname("KOMODROMOS")
            .primaryApplicantAddress(SolsAddress.builder()
                .addressLine1("37 Otter Lane")
                .addressLine2("Mountsorrel")
                .addressLine3("Loughborough")
                .county("Leicestershire")
                .postCode("LE12 7GL")
                .build())
            .additionalExecutorsApplying(additionalExecutors)
            .ihtFormId("IHT205")
            .ihtGrossValue(new BigDecimal(new BigInteger("32500000"), 0))
            .ihtNetValue(new BigDecimal(new BigInteger("28700000"), 0))
            .caseType("gop")
            .registryLocation("Liverpool")
            .grantIssuedDate(grantIssuedDate)
            .solsSOTName("John The personal")
            .applicationType(ApplicationType.PERSONAL);

        caseDataCarriageReturns = CaseData.builder()
            .deceasedForenames("PETAR")
            .deceasedSurname("KRNETA")
            .deceasedDateOfDeath(dod)
            .deceasedDateOfBirth(dob)
            .deceasedAddress(SolsAddress.builder()
                .addressLine1("7 Pevensey Avenue\nBelper")
                .addressLine3("Leicester")
                .postCode("LE5 6XQ").build())
            .boDeceasedTitle("MR")
            .solsDeceasedAliasNamesList(deceasedAliasNames)
            .primaryApplicantIsApplying("Yes")
            .primaryApplicantForenames("ANDJELKA")
            .primaryApplicantSurname("KOMODROMOS")
            .primaryApplicantAddress(SolsAddress.builder()
                .addressLine1("37 Otter Lane\nStreet")
                .addressLine2("Mountsorrel")
                .addressLine3("Loughborough")
                .county("Leicestershire")
                .postCode("LE12 7GL")
                .build())
            .additionalExecutorsApplying(additionalExecutors)
            .ihtFormId("IHT205")
            .ihtGrossValue(new BigDecimal(new BigInteger("32500000"), 0))
            .ihtNetValue(new BigDecimal(new BigInteger("28700000"), 0))
            .caseType("gop")
            .registryLocation("Liverpool")
            .grantIssuedDate(grantIssuedDate)
            .solsSOTName("John The personal")
            .applicationType(ApplicationType.PERSONAL);

        when(fileExtractDateFormatter.formatDataDate(dod)).thenReturn("17-AUG-2018");
        when(fileExtractDateFormatter.formatDataDate(dob)).thenReturn("20-OCT-1940");
        when(fileExtractDateFormatter.formatDataDate(LocalDate.parse(grantIssuedDate))).thenReturn("24-OCT-2018");
        when(fileExtractDateFormatter.formatFileDate()).thenReturn(FILE_DATE);
    }

    @Test
    public void testHmrcFileBuiltForSolicitor() throws IOException {
        builtData = caseDataSolictor.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1111222233334444L);
        caseList.add(createdCase);
        assertThat(createFile(hmrcFileService.createHmrcFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/hmrcSolicitor.txt")));
    }

    @Test
    public void testHmrcFileBuiltForPersonal() throws IOException {
        builtData = caseDataPersonal.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 2222333344445555L);
        caseList.add(createdCase);
        assertThat(createFile(hmrcFileService.createHmrcFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/hmrcPersonal.txt")));
    }

    @Test(expected = ClientException.class)
    public void testHmrcFileIHTErrorForPersonal() {
        builtData = caseDataPersonal.ihtFormId("notAnIHTValue").build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 2222333344445555L);
        caseList.add(createdCase);
        hmrcFileService.createHmrcFile(caseList.build(), FILE_NAME);
    }

    @Test
    public void testHmrcFileBuiltForMultiples() throws IOException {
        builtData = caseDataPersonal.build();
        caseList.add(new ReturnedCaseDetails(builtData, LAST_MODIFIED, 2222333344445555L));
        builtData = caseDataSolictor.build();
        caseList.add(new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1111222233334444L));
        assertThat(createFile(hmrcFileService.createHmrcFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/hmrcMultipleCases.txt")));
    }

    @Test
    public void testFileIsBuiltWithEmptyOptionalValues() throws IOException {
        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
            new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                .applyingExecutorAddress(SolsAddress.builder().build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        caseDataSolictor.deceasedAddress(SolsAddress.builder().build())
            .primaryApplicantAddress(SolsAddress.builder().build())
            .boDeceasedTitle("")
            .additionalExecutorsApplying(additionalExecutors);
        builtData = caseDataSolictor.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 3333444455556666L);
        caseList.add(createdCase);
        assertThat(createFile(hmrcFileService.createHmrcFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/hmrcEmptyOptionals.txt")));
    }

    @Test
    public void testPrimaryApplicantAsNoChangesGrantee() throws IOException {
        caseDataPersonal.primaryApplicantIsApplying("No");
        builtData = caseDataPersonal.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 4444555566667777L);
        caseList.add(createdCase);
        assertThat(createFile(hmrcFileService.createHmrcFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/hmrcPrimaryApplicantNo.txt")));
    }

    @Test
    public void testCarriageReturnInAddressIsReplacedWithSpace() throws IOException {
        builtData = caseDataCarriageReturns.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 5555666677778888L);
        caseList.add(createdCase);
        assertThat(createFile(hmrcFileService.createHmrcFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/hmrcPersonalReplaced.txt")));
    }

    private String createFile(File file) throws IOException {
        file.deleteOnExit();
        return new String(Files.readAllBytes(Paths.get(file.getName())), StandardCharsets.UTF_8);
    }
}