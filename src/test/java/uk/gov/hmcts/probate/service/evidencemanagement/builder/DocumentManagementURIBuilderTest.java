package uk.gov.hmcts.probate.service.evidencemanagement.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DocumentManagementURIBuilderTest {

    private static final String URL = "URL";
    private static final String HOST = "HOST";
    private static final String ID = "ID";

    private DocumentManagementURIBuilder documentManagementURIBuilder;

    @BeforeEach
    public void setUp() {
        documentManagementURIBuilder = new DocumentManagementURIBuilder(HOST, URL);
    }

    @Test
    void shouldBuildUrl() {
        assertThat(documentManagementURIBuilder.buildUrl(), is(HOST + URL));
    }

    @Test
    void shouldBuildUrlWithId() {
        assertThat(documentManagementURIBuilder.buildUrl(ID), is(HOST + URL + "/" + ID));
    }
}
