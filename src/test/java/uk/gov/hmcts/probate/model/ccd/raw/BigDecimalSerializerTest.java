package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class BigDecimalSerializerTest {

    @Test
    void shouldSerialize() throws IOException {
        Writer jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        new BigDecimalSerializer().serialize(new BigDecimal("90000.00"), jsonGenerator, serializerProvider);;
        jsonGenerator.flush();
        assertThat(jsonWriter.toString(), is(equalTo("\"90000.00\"")));
    }
}
