package uk.gov.hmcts.probate.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.probateman.Caveat;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "spring.jpa.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto: update",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {CaveatRepositoryTest.RepositoryTestConfiguration.class})
@AutoConfigureMockMvc
public class CaveatRepositoryTest {
    @Autowired
    private CaveatRepository caveatRepository;

    @Test
    public void shouldUpdateCaveat() {
        Caveat caveat = new Caveat();
        caveat.setId(1L);
        caveat.setDnmInd("N");
        Caveat savedCaveat = caveatRepository.save(caveat);
        assertEquals("N", savedCaveat.getDnmInd());

        savedCaveat.setDnmInd("Y");
        Caveat updatedCaveat = caveatRepository.save(savedCaveat);
        assertEquals("Y", updatedCaveat.getDnmInd());
    }

    @TestConfiguration
    @EnableWebSecurity
    @ComponentScan("uk.gov.hmcts.probate")
    static class RepositoryTestConfiguration {
    }
}
