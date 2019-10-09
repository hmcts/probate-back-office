package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToMartialStatus;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;
import uk.gov.hmcts.reform.probate.model.cases.MaritalStatus;

@Slf4j
@Component
public class OCRFieldMartialStatusMapper {

    private static final String NEVER_MARRIED = "neverMarried";
    private static final String WIDOWED = "widowed";
    private static final String MARRIED_CIVIL_PARTNERSHIP = "marriedCivilPartnership";
    private static final String DIVORCED_CIVIL_PARTNERSHIP = "divorcedCivilPartnership";
    private static final String JUDICIALLY = "judicially";

    @SuppressWarnings("squid:S1168")
    @ToMartialStatus
    public MaritalStatus toMartialStatus(String martialStatusValue) throws OCRMappingException {
        log.info("Beginning mapping for Martial Status value: {}", martialStatusValue);

        if (martialStatusValue == null || martialStatusValue.isEmpty()) {
            return null;
        } else {
            switch (martialStatusValue.trim()) {
                case NEVER_MARRIED:
                    return MaritalStatus.NEVER_MARRIED;
                case WIDOWED:
                    return MaritalStatus.WIDOWED;
                case MARRIED_CIVIL_PARTNERSHIP:
                    return MaritalStatus.MARRIED;
                case DIVORCED_CIVIL_PARTNERSHIP:
                    return MaritalStatus.DIVORCED;
                case JUDICIALLY:
                    return MaritalStatus.JUDICIALLY_SEPARATED;
                default: {
                    String errorMessage = "Martial Status field '" + martialStatusValue + "' could not be mapped to a case";
                    log.error(errorMessage);
                    throw new OCRMappingException(errorMessage);
                }
            }
        }
    }

}