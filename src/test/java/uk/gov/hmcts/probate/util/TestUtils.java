package uk.gov.hmcts.probate.util;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class TestUtils {

    public String getJsonFromFile(String fileName) throws IOException {
        File file = ResourceUtils.getFile(this.getClass().getResource("/" + fileName));

        return new String(Files.readAllBytes(file.toPath()));
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
