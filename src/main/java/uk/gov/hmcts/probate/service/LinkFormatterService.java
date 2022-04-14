package uk.gov.hmcts.probate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class LinkFormatterService {
    private static final String SEPARATOR = " ";
    private static final String KEY_BEFORE = "<BEFORE>";
    private static final String KEY_LINK_HREF = "<LINK_HREF>";
    private static final String KEY_LINK_TEXT = "<LINK_TEXT>";
    private static final String KEY_AFTER = "<AFTER>";
    private static final String LINK_WITH_TEXT = KEY_BEFORE 
        + "<a href=\"" + KEY_LINK_HREF + "\" target=\"_blank\">" + KEY_LINK_TEXT + "</a>" + KEY_AFTER;

    public String formatLink(String before, String hrefUrl, String linkText, String after) {
        String formatted = LINK_WITH_TEXT;
        formatted = formatted.replaceFirst(KEY_BEFORE, optionallyWithSeparator(before, false));
        formatted = formatted.replaceFirst(KEY_LINK_HREF, hrefUrl);
        formatted = formatted.replaceFirst(KEY_LINK_TEXT, linkText);
        formatted = formatted.replaceFirst(KEY_AFTER, optionallyWithSeparator(after, true));

        return formatted;
    }

    private String optionallyWithSeparator(String value, boolean beforeOrAfter) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return beforeOrAfter ? SEPARATOR + value : value + SEPARATOR; 
    }
}
