package uk.gov.hmcts.probate.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.wa.WaMapper;

import java.util.Base64;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskUtils {

    private final ObjectMapper objectMapper;

    public Optional<String> setTaskCompletion(
        String clientContext,
        CallbackRequest callbackRequest,
        Predicate<CallbackRequest> completeTask) {

        return getWaMapper(clientContext)
                .map(WaMapper::getClientContext)
                .filter(value -> nonNull(value.getUserTask()))
                .map(value ->
                        value.toBuilder()
                                .userTask(value.getUserTask().toBuilder()
                                        .completeTask(completeTask.test(callbackRequest))
                                        .build())
                                .build()).flatMap(updatedClientContext -> base64Encode(WaMapper.builder()
                        .clientContext(updatedClientContext)
                        .build()));
    }

    private  Optional<WaMapper> getWaMapper(String clientContext) {
        if (clientContext != null) {
            log.info("clientContext is present");
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(clientContext);
                String decodedString = new String(decodedBytes);
                return Optional.of(objectMapper.readValue(decodedString, WaMapper.class));
            } catch (Exception ex) {
                log.error("Exception while parsing the Client-Context {}", ex.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<String> base64Encode(WaMapper waMapper) {
        if (waMapper != null) {
            try {
                String clientContextToEncode = objectMapper.writeValueAsString(waMapper);
                return Optional.of(Base64.getEncoder().encodeToString(clientContextToEncode.getBytes()));
            } catch (JsonProcessingException e) {
                log.error("Exception while clientContext the Client-Context {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }
}
