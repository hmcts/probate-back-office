package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@RequiredArgsConstructor
@Service
@Slf4j
public class IhtEstateDefaulter {
    private static final String SWITCH_DATE_FORMATTER_PATTERN = "yyyy-MM-dd";

    @Value("${iht-estate.switch-date}")
    private String ihtEstateSwitchDate;


    public void defaultPageFlowIhtSwitchDate(CaseData data,
                                             ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {

        LocalDate dod = data.getDeceasedDateOfDeath();
        log.info("caseData {}", data);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(SWITCH_DATE_FORMATTER_PATTERN);
        LocalDate switchDate = LocalDate.parse(ihtEstateSwitchDate, dateFormatter);
        if (dod.isBefore(switchDate)) {
            log.info("Putting form estate to null");
            responseCaseDataBuilder.ihtFormEstate(null);
        }
        else {
            log.info("Putting form id to null");
            responseCaseDataBuilder.ihtFormId(null);
        }
        responseCaseDataBuilder.dateOfDeathAfterEstateSwitch(!dod.isBefore(switchDate) ? YES : NO);
        log.info("response {}", responseCaseDataBuilder);
    }
}
