package uk.gov.hmcts.probate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Service
@AllArgsConstructor
public class DateFormatterService {

    public String formatCaveatExpiryDate(LocalDate caveatExpiryDate) {
        if (caveatExpiryDate == null) {
            return null;
        }
        DateTimeFormatter caveatExpiryDateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        return addDayNumberSuffix(caveatExpiryDate.format(caveatExpiryDateFormatter));
    }

    private String addDayNumberSuffix(String formattedDate) {
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
    }


    public String formatDate(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMMM yyyy");
        try {
            Date date = originalFormat.parse(dateToConvert.toString());
            String formattedDate = targetFormat.format(date);
            return addDayNumberSuffix(formattedDate);
        } catch (ParseException ex) {
            ex.getMessage();
            return null;
        }
    }
}
