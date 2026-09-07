package uk.gov.hmcts.probate.service.wa.search.parameter;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.service.wa.search.SearchOperator;

import java.io.IOException;

@JsonComponent
@SuppressWarnings({"PMD.LawOfDemeter"})
public class SearchRequestCustomDeserializer extends StdDeserializer<SearchParameter<?>> {

    private static final long serialVersionUID = -1895766495984179418L;

    private static final String ERROR_MESSAGE =
        "Each search_parameter element must have 'key', 'values' and 'operator' fields present and populated.";

    public SearchRequestCustomDeserializer() {
        this(null);
    }

    public SearchRequestCustomDeserializer(final Class<?> cls) {
        super(cls);
    }

    @Override
    public SearchParameter<?> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode searchNode = mapper.readTree(jsonParser);

        final JsonNode operatorNode = searchNode.get("operator");

        if (operatorNode == null) {
            throw new BadRequestException(ERROR_MESSAGE);
        }

        if (SearchOperator.BOOLEAN.getValue().equals(operatorNode.asText())) {
            return mapper.treeToValue(searchNode, SearchParameterBoolean.class);
        } else if (SearchOperator.IN.getValue().equals(operatorNode.asText())) {
            return mapper.treeToValue(searchNode, SearchParameterList.class);
        } else {
            throw new BadRequestException(ERROR_MESSAGE);
        }
    }
}
