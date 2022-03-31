package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LinkFormatterServiceTest {

    @InjectMocks
    private LinkFormatterService underTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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