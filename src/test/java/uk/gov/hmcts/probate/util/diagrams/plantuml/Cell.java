package uk.gov.hmcts.probate.util.diagrams.plantuml;

import lombok.Builder;
import lombok.Data;

public interface Cell {
    public static final int LIGHTEN_READONLY = 50;
    public static final int LIGHTEN_NO_ACCESS = 75;

    String getCrud();

    String getColor();

    default String getColorForCell() {
        String col = getColor();
        if (isReadonly()) {
            col = " %lighten('" + getColor() + "', " + LIGHTEN_READONLY + ")";
        } else if (isNoAccess()) {
            col = " %lighten('" + getColor() + "', " + LIGHTEN_NO_ACCESS + ")";
        }

        return col;
    }

    default boolean isReadonly() {
        return "R".equals(getCrud());
    }

    default boolean isNoAccess() {
        return "".equals(getCrud());
    }
}
