package uk.gov.hmcts.probate.service.evidencemanagement.builder;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DocumentManagementURIBuilderTest {

    private static final String URL = "URL";
    private static final String HOST = "HOST";
    private static final String ID = "ID";

    private DocumentManagementURIBuilder documentManagementURIBuilder;

    @Before
    public void setUp() {
        documentManagementURIBuilder = new DocumentManagementURIBuilder(HOST, URL);
    }

    @Test
    public void shouldBuildUrl() {
        assertThat(documentManagementURIBuilder.buildUrl(), is(HOST + URL));
    }

    @Test
    public void shouldBuildUrlWithId() {
        assertThat(documentManagementURIBuilder.buildUrl(ID), is(HOST + URL + "/" + ID));
    }
}
