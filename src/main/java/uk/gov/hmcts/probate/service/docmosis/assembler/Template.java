package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Template {
    private final String value;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this.getClass() != obj.getClass())
            return false;

        Template other = (Template) obj;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + value.hashCode();
        return result;
    }

}
