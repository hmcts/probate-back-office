package uk.gov.hmcts.probate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public abstract class ComponentTestBase {

    @Autowired
    @Qualifier("validationMessageSource")
    private ResourceBundleMessageSource validationMessageSource;

    protected ResultMatcher matchErrorsSize(int expectedSize) {
        return jsonPath("$.errors", hasSize(expectedSize));
    }

    protected ResultMatcher matchFirstErrorMessageCode(String code) {
        return matchErrorMessageCode(0, code, null);
    }

    protected ResultMatcher matchFirstErrorMessageCode(List<String> code) {
        for (int i = 0; i < code.size(); i++) {
            return matchErrorMessageCode(i, code.get(i), null);
        }

        return null;
    }

    protected ResultMatcher matchErrorMessageCode(int errorNumber, String code, String[] args) {
        return jsonPath("$.errors[" + errorNumber + "]", is(validationMessageSource.getMessage(code, args, Locale.UK)));
    }
}
