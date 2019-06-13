package uk.gov.hmcts.probate.service.docmosis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.HashMap;
import java.util.Map;

public class GenericMapperServiceTest {
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final long ID = 1234567891234567L;
    private GenericMapperService genericMapperService = new GenericMapperService(new RegistriesProperties());
    private CaseDetails caseDetails;
    private Map<String, Object> images = new HashMap<>();
    Registry registry = new Registry();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Registry> registries = new HashMap<>();

    @Before
    public void setup() {
        registry.setName("leeds");
        registry.setPhone("123456789");
        registries = mapper.convertValue(registry, Map.class);

        CaseData caseData = CaseData.builder()
                .registryLocation("leeds")
                .build();
        caseDetails = new CaseDetails(caseData, LAST_MODIFIED, ID);

        images.put("imageKey1", "imageValue1");
        images.put("imageKey2", "imageValue2");
        images.put("imageKey3", "imageValue3");
    }

    @Test
    public void test() {
        genericMapperService.caseDataWithImages(images, caseDetails);
    }
}
