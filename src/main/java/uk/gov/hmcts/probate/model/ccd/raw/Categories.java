package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Categories {

    private final List<String> selectedCategories = new ArrayList();
    private final List<String> entSelectedParagraphs = new ArrayList();
    private final List<String> ihtSelectedParagraphs = new ArrayList();
    private final List<String> missInfoSelectedParagraphs = new ArrayList();
    private final List<String> willSelectedParagraphs = new ArrayList();
    private final List<String> incapacitySelectedParagraphs = new ArrayList();
    private final List<String> forDomSelectedParagraphs = new ArrayList();

    public List<List<String>> getAllSelectedCategories() {
        return Arrays.asList(entSelectedParagraphs, ihtSelectedParagraphs, missInfoSelectedParagraphs,
                willSelectedParagraphs, forDomSelectedParagraphs);
    }
}
