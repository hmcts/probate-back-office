package uk.gov.hmcts.probate.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class TestUtils {

    public String getJsonFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources", fileName)));
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
