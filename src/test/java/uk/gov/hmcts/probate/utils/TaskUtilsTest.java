package uk.gov.hmcts.probate.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.wa.WaMapper;

import java.io.IOException;
import java.util.Optional;

import static java.util.Base64.getDecoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskUtilsTest {
    @Spy
    private ObjectMapper mapper;
    private TaskUtils taskUtils;
    @Mock
    private CallbackRequest callbackRequest;
    @Mock
    private JsonProcessingException jsonProcessingException;

    private static final String CLIENT_CONTEXT = """
        {
          "client_context": {
            "user_task": {
              "complete_task" : true
            }
          }
        }
        """;

    private static final String CLIENT_CONTEXT_WITH_LANGUAGE = """
        {
           "client_context": {
             "user_language": {
               "language": "en"
             }
           }
        }
        """;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        taskUtils = new TaskUtils(mapper);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testWhenClientContextSetTaskCompletionFlag(boolean completeTask) throws IOException {
        WaMapper waMapper = mapper.readValue(CLIENT_CONTEXT, WaMapper.class);
        Optional<String> encodedString = taskUtils.base64Encode(waMapper);
        assertThat(encodedString).isNotEmpty();

        Optional<String> encodedClientContext = taskUtils.setTaskCompletion(encodedString.get(),
                callbackRequest,
                data -> completeTask);

        assertThat(encodedClientContext).isNotEmpty();
        byte[] decodeClientContext = getDecoder().decode(encodedClientContext.get());
        WaMapper updatedWaMapper = mapper.readValue(decodeClientContext, WaMapper.class);
        assertThat(updatedWaMapper.getClientContext().getUserTask().isCompleteTask())
                .isEqualTo(completeTask);

    }

    @Test
    void testWhenClientContextNotPresent() {
        assertThat(taskUtils.setTaskCompletion(null, callbackRequest, data -> false))
                .isEmpty();
    }

    @Test
    void testWhenClientContextDoesNotContainTask() throws JsonProcessingException {
        WaMapper waMapper = mapper.readValue(CLIENT_CONTEXT_WITH_LANGUAGE, WaMapper.class);
        Optional<String> encodedString = taskUtils.base64Encode(waMapper);
        assertThat(encodedString).isNotEmpty();

        assertThat(taskUtils.setTaskCompletion(encodedString.get(), callbackRequest, data -> false))
                .isEmpty();
    }

    @Test
    void testWhenClientContextIsMalformed() {
        String malformedClientContext = "not-a-valid-base64-string";
        assertThat(taskUtils.setTaskCompletion(malformedClientContext, callbackRequest, data -> false))
                .isEmpty();
    }

    @Test
    void testWhenValidBase64Encode() {
        WaMapper waMapper = WaMapper.builder().build();
        Optional<String> encodedString = taskUtils.base64Encode(waMapper);
        assertThat(encodedString).isNotEmpty();
    }

    @Test
    void testWhenErrorBase64Encode() throws JsonProcessingException {
        WaMapper waMapper = WaMapper.builder().build();
        when(mapper.writeValueAsString(waMapper))
                .thenThrow(jsonProcessingException);

        assertThatThrownBy(() ->
                taskUtils.base64Encode(waMapper)
        ).isInstanceOf(RuntimeException.class);
    }
}