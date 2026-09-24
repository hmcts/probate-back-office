package uk.gov.hmcts.probate.service.wa;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class CreateTaskProcessorFactory {
    Map<String, CreateTaskProcessor> createTaskProcessors;

    public CreateTaskProcessorFactory(List<CreateTaskProcessor> createTaskProcessors) {
        this.createTaskProcessors = createTaskProcessors.stream()
                .collect(toMap(CreateTaskProcessor::getEventId, Function.identity()));
    }

    public Optional<CreateTaskProcessor> get(String eventId) {
        return Optional.ofNullable(createTaskProcessors.get(eventId));
    }
}
