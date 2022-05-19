package uk.gov.hmcts.probate.util.diagrams;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiagramEvent {

    private static final String[] EVENT_COLORS = {"000000", "FF0000", "00FF00", "0000FF", "B5739D", "7EA6E0", "67AB9F",
            "97D077", "FFD966", "EA6B66"};

    private final String id;
    private final String name;
    private final DiagramState pre;
    private final DiagramState post;
    private final String start;
    private final String about;
    private final String submitted;

    protected String getEventToStateArrows(int c, boolean showCallbacks) {
        String source = pre == null ? "source=\"begin\"" : "source=\"" + pre.id + "\"";
        String target = post == null ? "" : "target=\"" + post.id + "\"";
        String color = EVENT_COLORS[c % (EVENT_COLORS.length - 1)];
        String callbacks = showCallbacks ? "["
                + (start == null ? "" : start) + ":"
                + (about == null ? "" : about) + ":"
                + (submitted == null ? "" : submitted) + "]" : "";
        callbacks = removeUri(callbacks);
        String nameAndCallbacks = name + callbacks;

        String eventStr = "";
        if (pre != null && post != null && pre.id != null && post.id != null && pre.id.equals(post.id)) {
            eventStr = "\n        <mxCell id=\"" + id + "\" value=\"" + nameAndCallbacks + "\" "
                    + "style=\"curved=1;html=1;jettySize=auto;orthogonalLoop=1;"
                    + "fontFamily=Courier New;fontSize=14;fontColor=#" + color + ";"
                    + "endArrow=block;endFill=0;endSize=8;strokeWidth=3;shadow=0;"
                    + "edgeStyle=orthogonalEdgeStyle;strokeColor=#" + color + ";html=1;"
                    + "labelBackgroundColor=none;labelPosition=right;verticalLabelPosition=middle;align=left;"
                    + "verticalAlign=bottom;\""
                    + " edge=\"1\" "
                    + "" + source + " " + target + " parent=\"1\">\n"
                    + "          <mxGeometry relative=\"1\" as=\"geometry\" />\n</mxCell>\n";
        } else {
            eventStr = "      <mxCell id=\""+id+"\" value=\""+nameAndCallbacks+"\" style=\"shape=flexArrow;endArrow=classic;html=1;"
                    + "rounded=0;endWidth=78;endSize=14;width=50;startSize=14;\" edge=\"1\" parent=\"1\" "
                    + source+" "+target+">\n"
                    + "        <mxGeometry width=\"50\" height=\"50\" relative=\"1\" as=\"geometry\">\n"
                    + "          <mxPoint x=\"100\" y=\"100\" as=\"sourcePoint\"/>\n"
                    + "          <mxPoint x=\"200\" y=\"100\" as=\"targetPoint\"/>\n"
                    + "        </mxGeometry>"
                    + "      </mxCell>";

        }
        return eventStr;
    }

    private String removeUri(String callback) {
        String toRemove = "http://\\$\\{CCD_DEF_CASE_SERVICE_BASE_URL\\}";
        return callback.replaceAll(toRemove, "");
    }
}
