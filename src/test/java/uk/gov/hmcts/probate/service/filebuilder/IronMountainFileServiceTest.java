package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


class IronMountainFileServiceTest {

    private IronMountainFileService ironmountainFileService = new IronMountainFileService(new TextFileBuilderService());
    private ImmutableList.Builder<ReturnedCaseDetails> caseList = new ImmutableList.Builder<>();
    private CaseData.CaseDataBuilder caseData;
    private CaseData.CaseDataBuilder caseData2;
    private ReturnedCaseDetails createdCase;
    private CaseData builtData;
    private static final String FILE_NAME = "testFile.txt";
    private static final LocalDateTime LAST_MODIFIED = LocalDateTime.now(ZoneOffset.UTC).minusYears(2);



    @BeforeEach
    public void setup() {
        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        caseData = CaseData.builder()
                .deceasedForenames("Nigel")
                .deceasedSurname("Deadsoul")
                .deceasedDateOfDeath(LocalDate.of(2015, 1, 1))
                .deceasedDateOfBirth(LocalDate.of(1990, 1, 1))
                .deceasedAddress(SolsAddress.builder().addressLine1("123 Dead street").addressLine3("The lane")
                        .postCode("AB5 6CD").build())
                .boDeceasedTitle("Mr")
                .primaryApplicantIsApplying("Yes")
                .primaryApplicantForenames("Tim")
                .primaryApplicantSurname("Timson")
                .primaryApplicantAddress(SolsAddress.builder().addressLine1("321 Fake street").postCode("AB1 2CD")
                        .build())
                .additionalExecutorsApplying(additionalExecutors)
                .solsSolicitorFirmName("Solicitors R us")
                .solsSolicitorAddress(SolsAddress.builder().addressLine1("999 solicitor street").build())
                .ihtGrossValue(new BigDecimal(new BigInteger("8899"), 0))
                .ihtNetValue(new BigDecimal(new BigInteger("7787"), 0))
                .caseType("gop")
                .registryLocation("Oxford")
                .grantIssuedDate("2019-02-18")
                .solsSOTName("John Thesolicitor")
                .applicationType(ApplicationType.PERSONAL);

        caseData2 = CaseData.builder()
                .deceasedForenames("Nigel")
                .deceasedSurname("Deadsoul")
                .deceasedDateOfDeath(LocalDate.of(2015, 1, 1))
                .deceasedDateOfBirth(LocalDate.of(1990, 1, 1))
                .deceasedAddress(SolsAddress.builder().addressLine1("123 Dead street\nThe lane").build())
                .boDeceasedTitle("Mr")
                .primaryApplicantIsApplying("Yes")
                .primaryApplicantForenames("Tim")
                .primaryApplicantSurname("Timson")
                .primaryApplicantAddress(SolsAddress.builder().addressLine1("321 Fake street\nLS2 3FD").build())
                .additionalExecutorsApplying(additionalExecutors)
                .solsSolicitorFirmName("Solicitors R us")
                .solsSolicitorAddress(SolsAddress.builder().addressLine1("999 solicitor street\nLondon \nLS1 2SA")
                        .build())
                .ihtGrossValue(new BigDecimal(new BigInteger("8800"), 0))
                .ihtNetValue(new BigDecimal(new BigInteger("7700"), 0))
                .caseType("gop")
                .registryLocation("Oxford")
                .grantIssuedDate("2019-02-18")
                .applicationType(ApplicationType.PERSONAL);
    }

    @Test
    void testIronMountainFileBuilt() throws IOException {
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainFilePopulated.txt")));
    }

    @Test
    void testIronMountainFileBuiltWithEmptyIHTValues() throws IOException {
        caseData.ihtGrossValue(null);
        caseData.ihtNetValue(null);
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainFilePopulatedZeroIHTs.txt")));
    }

    @Test
    void testFileIsBuildWithEmptyOptionalValues() throws IOException {
        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        caseData.boDeceasedTitle("")
                .deceasedAddress(SolsAddress.builder().build())
                .primaryApplicantAddress(SolsAddress.builder().build())
                .boDeceasedTitle("")
                .primaryApplicantAddress(SolsAddress.builder().build())
                .additionalExecutorsApplying(additionalExecutors);
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainFileEmptyOptionals.txt")));
    }

    @Test
    void testPrimaryApplicantAsNoChangesGrantee() throws IOException {
        caseData.primaryApplicantIsApplying("No");
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainPrimaryApplicantNo.txt")));
    }

    @Test
    void testSolicitorApplicationTypeDisplaysSolicitorInformation() throws IOException {
        caseData.applicationType(ApplicationType.SOLICITOR);
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainSolicitor.txt")));
    }

    @Test
    void testAdColligendaBonaCaseType() throws IOException {
        caseData.caseType("adColligendaBona");
        caseData.applicationType(ApplicationType.SOLICITOR);
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainAdColligendBona.txt")));
    }

    @Test
    void testCarriageReturnInAddressIsReplacedWithSpace() throws IOException {
        builtData = caseData2.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainOneAddressLine.txt")));
    }

    @Test
    void testRegistryLocationCtscMapped() throws IOException {
        caseData.registryLocation("ctsc");
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainFileCtsc.txt")));
    }

    @Test
    void testSolicitorAsGranteeWhenNoExecutorsAndPrimaryApplicantNotApplying() throws IOException {
        caseData.applicationType(ApplicationType.SOLICITOR);
        caseData.primaryApplicantIsApplying("No");
        caseData.additionalExecutorsApplying(null);
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainSolAsPrimary.txt")));
    }

    @Test
    void testAddExceptionForIncorrectCaseData() throws IOException {
        caseData2.applicationType(null);
        builtData = caseData2.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
            is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainExceptionCase.txt")));
    }

    @Test
    void testGetApplyingExecutorNameWhenExecutorNameNotPopulated() throws IOException {
        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorFirstName("Bob")
                        .applyingExecutorLastName("Smith")
                        .applyingExecutorAddress(SolsAddress.builder().build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        caseData.boDeceasedTitle("")
                .deceasedAddress(SolsAddress.builder().build())
                .primaryApplicantAddress(SolsAddress.builder().build())
                .boDeceasedTitle("")
                .primaryApplicantAddress(SolsAddress.builder().build())
                .additionalExecutorsApplying(additionalExecutors);
        builtData = caseData.build();
        createdCase = new ReturnedCaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        caseList.add(createdCase);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(caseList.build(), FILE_NAME)),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainFileEmptyOptionals.txt")));
    }

    private String createFile(File file) throws IOException {
        file.deleteOnExit();
        return new String(Files.readAllBytes(Paths.get(file.getName())), StandardCharsets.UTF_8);
    }
}
