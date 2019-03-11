package uk.gov.hmcts.probate.service.filebuilder;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IronMountainFileServiceTest {

    private IronMountainFileService ironmountainFileService = new IronMountainFileService(new TextFileBuilderService());
    private CaseData.CaseDataBuilder caseData;
    private CaseDetails createdCase;
    private CaseData builtData;
    private CollectionMember<Document> document;
    private static final String FILE_NAME = "testFile.txt";
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @Before
    public void setup() {
        document = new CollectionMember<>(Document
                .builder().documentDateAdded(LocalDate.of(2019, 02, 18)).documentType(DocumentType.DIGITAL_GRANT)
                .build());

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
                .deceasedDateOfDeath(LocalDate.of(1990, 01, 01))
                .deceasedDateOfBirth(LocalDate.of(2015, 01, 01))
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
                .ihtGrossValue(new BigDecimal(new BigInteger("88"), 0))
                .ihtNetValue(new BigDecimal(new BigInteger("77"), 0))
                .caseType("gop")
                .registryLocation("Oxford")
                .applicationType(ApplicationType.PERSONAL);
    }

    @Test
    public void testIronMountainFileBuilt() throws IOException {
        builtData = caseData.build();
        builtData.getProbateDocumentsGenerated().add(document);
        createdCase = new CaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(createdCase, FILE_NAME)).readLine(),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainFilePopulated.txt")));
    }

    @Test
    public void testFileIsBuildWithEmptyOptionalValues() throws IOException {
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
        builtData.getProbateDocumentsGenerated().add(document);
        createdCase = new CaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(createdCase, FILE_NAME)).readLine(),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainFileEmptyOptionals.txt")));
    }

    @Test
    public void testPrimaryApplicantAsNoChangesGrantee() throws IOException {
        caseData.primaryApplicantIsApplying("No");
        builtData = caseData.build();
        builtData.getProbateDocumentsGenerated().add(document);
        createdCase = new CaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(createdCase, FILE_NAME)).readLine(),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainPrimaryApplicantNo.txt")));
    }

    @Test
    public void testSolicitorApplicationTypeDisplaysSolicitorInformation() throws IOException {
        caseData.applicationType(ApplicationType.SOLICITOR);
        builtData = caseData.build();
        builtData.getProbateDocumentsGenerated().add(document);
        createdCase = new CaseDetails(builtData, LAST_MODIFIED, 1234567890876L);
        assertThat(createFile(ironmountainFileService.createIronMountainFile(createdCase, FILE_NAME)).readLine(),
                is(FileUtils.getStringFromFile("expectedGeneratedFiles/ironMountainSolicitor.txt")));
    }

    private BufferedReader createFile(File file) throws FileNotFoundException {
        file.deleteOnExit();
        FileReader fileReader = new FileReader(file);
        return new BufferedReader(fileReader);
    }
}