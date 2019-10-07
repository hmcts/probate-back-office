package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class Categories {

    private final List<String> selectedCategories = new ArrayList();
    private final List<String> entSelectedParagraphs = new ArrayList();
    private final List<String> ihtSelectedParagraphs = new ArrayList();
    private final List<String> missInfoSelectedParagraphs = new ArrayList();
    private final List<String> willSelectedParagraphs = new ArrayList();
}
