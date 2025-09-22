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
import uk.gov.hmcts.probate.service.template.pdf.caseextra.ProfitSharingCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.WillDateCaseExtra;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;

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
        caseDataMock = CaseData.builder().originalWillSignedDate(null)
                .codicilAddedDateList(null).build();
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(true);
        when(caseExtraDecorator.decorate(any(IhtEstateConfirmCaseExtra.class))).thenReturn("someJson");
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("someJson", actual);
    }

    @Test
    void shouldDecorateForWillSignedDate() {
        caseDataMock = CaseData.builder().originalWillSignedDate(LocalDate.of(2024, 12, 23))
                .codicilAddedDateList(null).build();
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
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
        List<CollectionMember<CodicilAddedDate>> date =
                List.of(new CollectionMember<>(CodicilAddedDate.builder().dateCodicilAdded(LocalDate
                        .of(2024, 12, 23)).build()));
        caseDataMock = CaseData.builder().originalWillSignedDate(null)
                .codicilAddedDateList(date).build();
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);

        when(localDateToWelshStringConverter.convert(date.getFirst().getValue()
                .getDateCodicilAdded())).thenReturn("23 Rhagfyr 2024");
        String caseExtraJson
                = "{\"showCodicilDate\" : \"Yes\",\"codicilSignedDateWelshFormatted\" : \"23 Rhagfyr 2024\"}";
        when(caseExtraDecorator.decorate(any(CodicilDateCaseExtra.class))).thenReturn(caseExtraJson);
        when(caseExtraDecorator.combineDecorations("", caseExtraJson)).thenReturn(caseExtraJson);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals(caseExtraJson, actual);
    }

    @Test
    void shouldDecorateForProfitSharingText() {
        caseDataMock = CaseData.builder().originalWillSignedDate(null)
                .codicilAddedDateList(null).solsWillType(GRANT_TYPE_PROBATE).whoSharesInCompanyProfits(List
                        .of("Partner","Member")).build();
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String caseExtraJson
                = "{\"welshSingularProfitSharingText\" : \"phartner ac aelod\",\"welshPluralProfitSharingText\" : "
                + "\"phartneriaid ac aelodau\"}";
        when(caseExtraDecorator.decorate(any(ProfitSharingCaseExtra.class))).thenReturn(caseExtraJson);
        when(caseExtraDecorator.combineDecorations("", caseExtraJson)).thenReturn(caseExtraJson);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals(caseExtraJson, actual);
    }

    @Test
    void shouldNotDecorate() {
        caseDataMock = CaseData.builder().originalWillSignedDate(null)
                .codicilAddedDateList(null).build();
        when(ihtEstateNotCompletedBusinessRule.isApplicable(caseDataMock)).thenReturn(false);
        String actual = solicitorLegalStatementPDFDecorator.decorate(caseDataMock);
        assertEquals("", actual);
    }
}
