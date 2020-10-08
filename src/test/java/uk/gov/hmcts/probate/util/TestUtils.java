package uk.gov.hmcts.probate.util;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class TestUtils {

    public String getStringFromFile(String fileName) throws IOException {
        File file = ResourceUtils.getFile(this.getClass().getResource("/" + fileName));

        return new String(Files.readAllBytes(file.toPath()));
    }

    public String stripSuperfluousChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.replaceAll("\r", "");
    }
}
