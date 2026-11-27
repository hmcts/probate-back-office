package uk.gov.hmcts.probate.service.wa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.probate.model.wa.WaMapper;
import uk.gov.hmcts.probate.utils.TaskUtils;


public final class WaTestUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TaskUtils TASK_UTILS = new TaskUtils(OBJECT_MAPPER);

    private WaTestUtils() {
    }

    public static String encodedClientContext(String clientContext)
            throws JsonProcessingException {

        WaMapper waMapper = OBJECT_MAPPER.readValue(
                clientContext,
                WaMapper.class
        );

        return TASK_UTILS.base64Encode(waMapper)
                .orElse("");
    }
}