package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class LinkFormatterServiceTest {

    @InjectMocks
    private LinkFormatterService underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldFillLink() {
        assertEquals("before <a href=\"HREF\" target=\"_blank\">LINK</a> after", underTest.formatLink("before", "HREF",
            "LINK", "after"));
    }

    @Test
    public void shouldFillLinkNoBefore() {
        assertEquals("<a href=\"HREF\" target=\"_blank\">LINK</a> after", underTest.formatLink("", "HREF", "LINK",
            "after"));
    }

    @Test
    public void shouldFillLinkNoAfter() {
        assertEquals("before <a href=\"HREF\" target=\"_blank\">LINK</a>", underTest.formatLink("before", "HREF",
            "LINK", ""));
    }

    @Test
    public void shouldFillLinkNoBeforeOrAfter() {
        assertEquals("<a href=\"HREF\" target=\"_blank\">LINK</a>", underTest.formatLink("", "HREF", "LINK", ""));
    }
}
