package uk.gov.hmcts.probate.util;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("Utility Class");
    }

    public static String getStringFromFile(String fileName) throws IOException {
        File file = ResourceUtils.getFile(FileUtils.class.getResource("/" + fileName));
        return new String(Files.readAllBytes(file.toPath()));
    }
}
