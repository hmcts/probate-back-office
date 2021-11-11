package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
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
public class IhtEstateDefaulter {
    private final String SWITCH_DATE_FORMATTER_PATTERN = "yyyy-MM-dd";

    @Value("${iht-estate.switch-date}")
    private String ihtEstateSwitchDate;


    public void defaultPageFlowIhtSwitchDate(CaseData data,
                                             ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {

        LocalDate dod = data.getDeceasedDateOfDeath();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(SWITCH_DATE_FORMATTER_PATTERN);
        LocalDate switchDate = LocalDate.parse(ihtEstateSwitchDate, dateFormatter);
        responseCaseDataBuilder.dateOfDeathAfterEstateSwitch(dod.isAfter(switchDate) ? YES : NO);
    }
}
