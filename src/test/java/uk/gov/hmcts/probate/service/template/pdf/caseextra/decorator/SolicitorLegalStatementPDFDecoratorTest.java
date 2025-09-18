package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.CodicilDateCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.WillDateCaseExtra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SolicitorLegalStatementPDFDecoratorTest {

    @InjectMocks
    private SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator;

    @Mock
    private CaseExtraDecorator caseExtraDecorator;
    @Mock
    private IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;
    @Mock
    private LocalDateToWelshStringConverter localDateToWelshStringConverter;
    private CaseData caseDataMock;

    @Test
    void shouldDecorateForIhtEstateNotCompleted() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(caseExtraDecorator.decorate(any(IhtEstateConfirmCaseExtra.class))).thenReturn("someJson");
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }

    @Test
    void shouldDecorateForWillSignedDate() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        caseDataMock = CaseData.builder().originalWillSignedDate(LocalDate.of(2024, 12, 23))
                .codicilAddedDateList(null).build();
        when(localDateToWelshStringConverter.convert(caseDataMock.getOriginalWillSignedDate()))
                .thenReturn("23 Rhagfyr 2024");
        String caseExtraJson
                = "{\"showWillDate\" : \"Yes\",\"originalWillSignedDateWelshFormatted\" : \"23 Rhagfyr 2024\"}";
        when(caseExtraDecorator.decorate(any(WillDateCaseExtra.class))).thenReturn(caseExtraJson);
        when(caseExtraDecorator.combineDecorations("", caseExtraJson)).thenReturn(caseExtraJson);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals(caseExtraJson, actual);
    }

    @Test
    void shouldDecorateForCodicilSignedDate() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        List<CollectionMember<CodicilAddedDate>> VALID_ADDED_CODICIL_DATES =
                List.of(new CollectionMember<>(CodicilAddedDate.builder().dateCodicilAdded(LocalDate
                        .of(2024, 12, 23)).build()));
        caseDataMock = CaseData.builder().originalWillSignedDate(null)
                .codicilAddedDateList(VALID_ADDED_CODICIL_DATES).build();
        when(localDateToWelshStringConverter.convert(VALID_ADDED_CODICIL_DATES.getFirst().getValue()
                .getDateCodicilAdded())).thenReturn("23 Rhagfyr 2024");
        String caseExtraJson
                = "{\"showCodicilDate\" : \"Yes\",\"codicilSignedDateWelshFormatted\" : \"23 Rhagfyr 2024\"}";
        when(caseExtraDecorator.decorate(any(CodicilDateCaseExtra.class))).thenReturn(caseExtraJson);
        when(caseExtraDecorator.combineDecorations("", caseExtraJson)).thenReturn(caseExtraJson);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals(caseExtraJson, actual);
    }

    @Test
    void shouldNotDecorate() {
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("", actual);
    }
}
