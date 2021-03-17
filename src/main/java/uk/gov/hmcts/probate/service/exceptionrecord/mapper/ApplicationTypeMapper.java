package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToApplicationTypeCaveat;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToApplicationTypeGrantOfRepresentation;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;

@Slf4j
@Component
public class ApplicationTypeMapper {

    @SuppressWarnings("squid:S1168")
    @ToApplicationTypeGrantOfRepresentation
    public ApplicationType toApplicationTypeGrantOfRepresentation(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Application Type (Grant of Representation)");
        if (StringUtils.isNotBlank(ocrFields.getSolsSolicitorIsApplying() )
                && BooleanUtils.toBoolean(ocrFields.getSolsSolicitorIsApplying())) {
            log.info("Found solicitor details returning ApplicationType.SOLICITORS");
            return ApplicationType.SOLICITORS;
        }

        log.info("No solicitor details found returning ApplicationType.PERSONAL");
        return ApplicationType.PERSONAL;
    }

    @SuppressWarnings("squid:S1168")
    @ToApplicationTypeCaveat
    public ApplicationType toApplicationTypeCaveat(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Application Type (Caveat)");
        if (StringUtils.isNotBlank(ocrFields.getSolsSolicitorFirmName() )
                || StringUtils.isNotBlank(ocrFields.getSolsSolicitorRepresentativeName())) {
            log.info("Found solicitor details returning ApplicationType.SOLICITORS");
            return ApplicationType.SOLICITORS;
        }

        log.info("No solicitor details found returning ApplicationType.PERSONAL");
        return ApplicationType.PERSONAL;
    }
}