package uk.gov.hmcts.probate.service.wa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskProcessorFactoryTest {

    @Mock
    private CreateTaskProcessor processor1;

    @Mock
    private CreateTaskProcessor processor2;

    private CreateTaskProcessorFactory factory;

    @BeforeEach
    void setUp() {
        when(processor1.getEventId())
                .thenReturn("event-1");

        when(processor2.getEventId())
                .thenReturn("event-2");

        factory = new CreateTaskProcessorFactory(
                List.of(processor1, processor2)
        );
    }

    @Test
    void shouldReturnProcessorForEventId() {
        Optional<CreateTaskProcessor> result = factory.get("event-1");

        assertThat(result)
                .isPresent()
                .contains(processor1);
    }

    @Test
    void shouldReturnEmptyWhenEventIdDoesNotExist() {
        Optional<CreateTaskProcessor> result = factory.get("unknown-event");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnCorrectProcessorForEachEventId() {
        assertThat(factory.get("event-1"))
                .contains(processor1);

        assertThat(factory.get("event-2"))
                .contains(processor2);
    }
}