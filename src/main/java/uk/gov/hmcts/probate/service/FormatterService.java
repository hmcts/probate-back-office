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
import java.util.Date;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Service
@Slf4j
@AllArgsConstructor
public class FormatterService {

    public String formatDate(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMMM yyyy");
        try {
            Date date = originalFormat.parse(dateToConvert.toString());
            String formattedDate = targetFormat.format(date);
            int day = Integer.parseInt(formattedDate.substring(0, 2));
            switch (day) {
                case 3:
                case 23:
                    return day + "rd " + formattedDate.substring(3);
                case 1:
                case 21:
                case 31:
                    return day + "st " + formattedDate.substring(3);
                case 2:
                case 22:
                    return day + "nd " + formattedDate.substring(3);
                default:
                    return day + "th " + formattedDate.substring(3);
            }
        } catch (ParseException ex) {
            ex.getMessage();
            return null;
        }
    }

    public String formatAddress(ProbateAddress address) {
        String fullAddress = "";

        if (address != null) {
            fullAddress += isEmpty(address.getProAddressLine1()) ? "" : address.getProAddressLine1() ;
            fullAddress += isEmpty(address.getProAddressLine2()) ? "" : ", " + address.getProAddressLine2();
            fullAddress += isEmpty(address.getProAddressLine3()) ? "" : ", " + address.getProAddressLine3();
            fullAddress += isEmpty(address.getProPostTown()) ? "" : ", " + address.getProPostTown();
            fullAddress += isEmpty(address.getProCounty()) ? "" : ", " + address.getProCounty();
            fullAddress += isEmpty(address.getProPostCode()) ? "" : ", " + address.getProPostCode();
            fullAddress += isEmpty(address.getProCountry()) ? "" : ", " + address.getProCountry();
        }

        return fullAddress;
    }
}
