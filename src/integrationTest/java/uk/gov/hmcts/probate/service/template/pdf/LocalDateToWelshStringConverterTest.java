package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LocalDateToWelshStringConverterTest {

    @Autowired
    private LocalDateToWelshStringConverter localDateToWelshStringConverter;

    @Test
    void convertDateInWelsh() {
        LocalDate localDate = LocalDate.of(2019, 12, 23);
        final String dateInWelsh = localDateToWelshStringConverter.convert(localDate);
        assertEquals("23 Rhagfyr 2019", dateInWelsh);
    }
}
