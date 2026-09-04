package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.model.DisposedCaseSearchType;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@Jacksonized
@EqualsAndHashCode
@Data
@Slf4j
public class DisposedCaseSearch {
    private final String searchType;
    private final String ccdId;
    private final LocalDate decDeathDate;
    private final Integer decDeathDateRange;
    private final String decForenames;
    private final String decSurname;
}
