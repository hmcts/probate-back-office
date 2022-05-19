package uk.gov.hmcts.probate.util.diagrams;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiagramState {
    protected final String id;
    protected final String name;

    String asXml(int x, int y, int width, int height, String color){
        return "      <mxCell id=\"" + id + "\" value=\""
                + name + "\" style=\"rounded=1;whiteSpace=wrap;fillColor=#" + color + ";"
                + "html=1;fontFamily=Courier New;fontSize=10;glass=0;strokeWidth=3;shadow=0;\" vertex=\"1\" parent=\"1\">\n"
                + "          <mxGeometry x=\"" + x + "\" y=\"" + y
                + "\" width=\""+width+"\" height=\""+height+"\" as=\"geometry\" />\n"
                + "      </mxCell>\n";

    }
}
