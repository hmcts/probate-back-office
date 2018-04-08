package uk.gov.hmcts.probate.util;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class TestUtils {

    public String getJsonFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources", fileName)));
    }

    public byte[] getFile(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get("src/test/resources", fileName));
    }

    public File getResourceFile(String fileName) throws IOException {
        return Paths.get("src/test/resources", fileName).toFile();
    }

    public Matcher<HttpServletRequest> requestHeaderMatcher(HttpServletRequest request, String header, String value) {
        return new CustomMatcher<HttpServletRequest>("match request header") {
            @Override
            public boolean matches(Object item) {
                return request.getHeader(header).equals(value);
            }
        };
    }

}
