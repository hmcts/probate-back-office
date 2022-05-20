package uk.gov.hmcts.probate.util.diagrams.diagramsnet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiagramState {
    private final String id;
    private final String name;
    private final int x;
    private final int y;

    private static final int w = 160;
    private static final int h = 60;
    private static final String XML = "      <mxCell id=\"{id}\" value=\"{name}\" style=\"rounded=1;" +
            "whiteSpace=wrap;fillColor=#00FFFF;html=1;fontFamily=Courier New;fontSize=10;glass=0;strokeWidth=3" +
            ";shadow=0;\" vertex=\"1\" parent=\"1\">\n" +
            "          <mxGeometry x=\"{x}\" y=\"{y}\" width=\""+w+"\" height=\""+h+"\" as=\"geometry\" />\n" +
            "      </mxCell>\n";


    String asXml(int x, int y, int width, int height, String color){
        return XML.replace("{id}", id)
                .replace("{name}", name)
                .replace("{x}", ""+x)
                .replace("{y}", ""+y);

//        return "      <mxCell id=\"" + id + "\" value=\""
//                + name + "\" style=\"rounded=1;whiteSpace=wrap;fillColor=#" + color + ";"
//                + "html=1;fontFamily=Courier New;fontSize=10;glass=0;strokeWidth=3;shadow=0;\" vertex=\"1\" parent=\"1\">\n"
//                + "          <mxGeometry x=\"" + x + "\" y=\"" + y
//                + "\" width=\""+width+"\" height=\""+height+"\" as=\"geometry\" />\n"
//                + "      </mxCell>\n";

    }

    public String getStateId() {
        return this.getId() + "State";
    }
}
