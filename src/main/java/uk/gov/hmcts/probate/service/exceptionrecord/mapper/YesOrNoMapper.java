package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToYesOrNo;

@Slf4j
@Component
public class YesOrNoMapper {

    @SuppressWarnings("squid:S1168")
    @ToYesOrNo
    public String toYesOrNo(String booleanValue) {
        log.info("Beginning mapping for Yes or No value");

        if (booleanValue == null || booleanValue.isEmpty()) {
            return null;
        }

        boolean result = BooleanUtils.toBoolean(booleanValue);
        return (result ? Constants.YES : Constants.NO);
    }

}