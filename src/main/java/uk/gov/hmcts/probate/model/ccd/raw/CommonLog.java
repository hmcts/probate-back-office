package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface CommonLog {
    String getRelevantFields(ObjectMapper objMap) throws JsonProcessingException;
}
