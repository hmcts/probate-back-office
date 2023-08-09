package uk.gov.hmcts.probate.service;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmailWithFileServiceTest {

    @Autowired
    EmailWithFileService emailWithFileService;


    /**
     * Method under test: {@link EmailWithFileService#emailFile(File)}.
     */
    @Test
    void testEmailFileFail() {
        assertFalse(
            emailWithFileService.emailFile(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile()));
        assertFalse(emailWithFileService.emailFile(Paths.get(System.getProperty("java.io.tmpdir"), "").toFile()));
    }
}