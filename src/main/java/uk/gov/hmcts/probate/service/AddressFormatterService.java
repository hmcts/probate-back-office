package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.ProbateAddress;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Service
@Slf4j
@AllArgsConstructor
public class AddressFormatterService {

    public String formatAddress(ProbateAddress address) {
        String fullAddress = "";

        if (address != null) {
            fullAddress += checkEmpty(false, address.getProAddressLine1());
            fullAddress += checkEmpty(true, address.getProAddressLine2());
            fullAddress += checkEmpty(true, address.getProAddressLine3());
            fullAddress += checkEmpty(true, address.getProPostTown());
            fullAddress += checkEmpty(true, address.getProCounty());
            fullAddress += checkEmpty(true, address.getProPostCode());
            fullAddress += checkEmpty(true, address.getProCountry());
        }

        return fullAddress;
    }

    private String checkEmpty(boolean prependComma, String addressElement) {
        if (StringUtils.isEmpty(addressElement)) {
            return "";
        } else {
            return (prependComma ? ", " : "") + addressElement;
        }
    }
}
