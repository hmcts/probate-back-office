package uk.gov.hmcts.probate.model.ccd.tasklist;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class Alert {
    private final String inset;
    private final String body;
    private List<String> list;
    private final String date;

    public Alert withList(List<String> list) {
        this.setList(list);
        return this;
    }

    public Optional<List<String>> getList() {
        return Optional.ofNullable(list);
    }

    public static Alert alert(String inset, String body, String date) {
        return Alert.builder()
                .inset(inset)
                .body(body)
                .date(date)
                .build();
    }
}
