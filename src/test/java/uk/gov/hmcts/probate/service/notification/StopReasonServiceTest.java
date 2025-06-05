package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.config.notifications.NotificationStop;
import uk.gov.hmcts.probate.config.notifications.StopReasonCode;
import uk.gov.hmcts.probate.model.LanguagePreference;

import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopReasonServiceTest {

    @Mock
    private NotificationStop notificationStop;

    @Mock
    private StopReasonCode stopReasonCodes;

    @InjectMocks
    private StopReasonService stopReasonService;

    @Test
    void returnsCorrectDescriptionForValidStopReasonCode() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));
        when(stopReasonCodes.getDeceasedAddressMissing()).thenReturn("Deceased address missing");

        String result = stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "DeceasedAddressMissing");

        assertEquals("Deceased address missing", result);
    }

    @Test
    void returnsStopReasonCodeWhenCodeIsNotMapped() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        String result = stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "UnknownCode");

        assertEquals("UnknownCode", result);
    }

    @Test
    void throwsExceptionWhenLanguagePreferenceIsNotMapped() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.WELSH, stopReasonCodes));

        assertThrows(NullPointerException.class, () ->
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "DeceasedAddressMissing"));
    }

    @Test
    void returnsMappedDescriptionForKnownStopReasonCode() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));
        when(stopReasonCodes.getDocumentsRequired()).thenReturn("Documents required");

        String result = stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "DocumentsRequired");

        assertEquals("Documents required", result);
    }

}