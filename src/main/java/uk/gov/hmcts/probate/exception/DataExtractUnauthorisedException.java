package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataExtractUnauthorisedException extends RuntimeException {
    public static final String DATA_EXTRACT_NOT_AUTHORISED_MESSAGE = "Data Extract has not been authorised";
    
    public DataExtractUnauthorisedException() {
        super(DATA_EXTRACT_NOT_AUTHORISED_MESSAGE);
    }
}
