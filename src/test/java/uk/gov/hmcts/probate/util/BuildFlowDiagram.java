package uk.gov.hmcts.probate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.service.filebuilder.TextFileBuilderService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BuildFlowDiagram {
    private TextFileBuilderService textFileBuilderService = new TextFileBuilderService();

    private static final String CASE_TYPE_PREFIX = "CCD_Probate_";
    private static final String BASE_DIR = "ccdImports/configFiles/" + CASE_TYPE_PREFIX;
    private static final String CASE_TYPE_GRANT = "Backoffice";
    private static final String CASE_TYPE_CAVEAT = "Caveat";
    private static final String[] EVENT_COLORS = {"000000", "FF0000", "00FF00", "0000FF", "B5739D", "7EA6E0", "67AB9F",
        "97D077", "FFD966", "EA6B66"};
    private boolean showCallbacks = false;

    public static void main(String[] args) throws IOException {
        new BuildFlowDiagram().generateAll();
    }

    private void generateAll() throws IOException {
        generate(CASE_TYPE_GRANT);
        //generate("Caveat");
    }

    private void generate(String caseType) throws IOException {
        List<State> allStates = getAllStates(caseType);
        String caseEvent = getStringFromFile(BASE_DIR + caseType + "/CaseEvent.json");
        Map<String, Object>[] responses = new ObjectMapper().readValue(caseEvent, HashMap[].class);
        List<Event> allEvents = new ArrayList<>();
        for (Map<String, Object> eventMap : responses) {
            String id = "";
            String name = "";
            String pre = "";
            String post = "";
            String start = "";
            String about = "";
            String submitted = "";
            for (String key : eventMap.keySet()) {
                switch (key) {
                    case "ID":
                        id = eventMap.get(key).toString();
                        break;
                    case "Name":
                        name = eventMap.get(key).toString();
                        break;
                    case "PreConditionState(s)":
                        pre = eventMap.get(key).toString();
                        break;
                    case "PostConditionState":
                        post = eventMap.get(key).toString();
                        break;
                    case "CallBackURLAboutToStartEvent":
                        start = eventMap.get(key).toString();
                        break;
                    case "CallBackURLAboutToSubmitEvent":
                        about = eventMap.get(key).toString();
                        break;
                    case "CallBackURLSubmittedEvent":
                        submitted = eventMap.get(key).toString();
                        break;
                }

            }

            State preState = getState(allStates, pre);
            State postState = getState(allStates, post);

            Event event = new Event(id, name, preState, postState, start, about, submitted);
            allEvents.add(event);
        }

        String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<mxfile host=\"app.diagrams.net\" modified=\"2021-07-03T10:40:14.316Z\" agent=\"5.0 (X11)\" "
                + "version=\"14.8.4\" etag=\"NBv04A9aWNXBj8fG8eNX\">\n"
                + "  <diagram id=\"XHciW1gV2JFlq7YNA-pa\">\n"
                + "    <mxGraphModel dx=\"1422\" dy=\"791\" grid=\"1\" gridSize=\"10\" guides=\"1\" tooltips=\"1\" "
                + "connect=\"1\" arrows=\"1\" fold=\"1\" page=\"1\" pageScale=\"1\" pageWidth=\"827\" "
                + "pageHeight=\"1169\" math=\"0\" shadow=\"0\">\n"
                + "      <root>\n"
                + "        <mxCell id=\"0\" />\n"
                + "        <mxCell id=\"1\" parent=\"0\" />\n";

        String end = "      </root>\n"
                + "    </mxGraphModel>\n"
                + "  </diagram>\n"
                + "</mxfile>\n";

        List<String> allRows = new ArrayList<>();
        allRows.add(start);
        int i = 0;
        int startCount = 0;
        int x = 100;
        int y = 100;
        int dx = 120 + 20;
        int dy = 40 + 20;
        List<State> usedStates = new ArrayList<>();
        for (Event event : allEvents) {
            if (event.pre == null && event.post != null) {
                if (!usedStates.contains(event.post)) {
                    allRows.add(event.getStateCell(event.post, 100 + (startCount * dx), 100 + (startCount * dy),
                            "00FF00"));
                    usedStates.add(event.post);
                    x = 100 + dx + (startCount * dx);
                    y = 100 + dy + (startCount * dy);
                    startCount++;
                }
            } else if (event.pre != null && event.post == null) {
                if (!usedStates.contains(event.pre)) {
                    allRows.add(event.getStateCell(event.pre, x, y, "FF0000"));
                    usedStates.add(event.pre);
                    x = x + dx;
                    y = y + dy;
                }
            } else if (event.pre != null && event.post != null) {
                if (!usedStates.contains(event.pre)) {
                    allRows.add(event.getStateCell(event.pre, x, y));
                    usedStates.add(event.pre);
                    x = x + dx;
                    y = y + dy;
                }
                if (!usedStates.contains(event.post)) {
                    allRows.add(event.getStateCell(event.post, x, y));
                    usedStates.add(event.post);
                    x = x + dx;
                    y = y + dy;
                }
            }

            allRows.add(event.getEventToStateArrows(i));
            i++;
        }
        allRows.add(end);
        textFileBuilderService.createFile(allRows, ",", CASE_TYPE_PREFIX + caseType
                + "-flow.xml");
    }

    private List<State> getAllStates(String caseType) throws IOException {
        List<State> allStates = new ArrayList<>();
        String caseSates = getStringFromFile(BASE_DIR + caseType + "/State.json");
        Map<String, Object>[] states = new ObjectMapper().readValue(caseSates, HashMap[].class);
        int num = 2;
        for (Map<String, Object> state : states) {
            allStates.add(new State(state.get("ID").toString(), state.get("Name").toString()));
            num++;
        }

        allStates.add(new State("*", "ALL"));
        return allStates;
    }

    private State getState(List<State> allStates, String find) {
        for (State state : allStates) {
            if (state.id.equalsIgnoreCase(find)) {
                return state;
            }
        }
        return null;
    }

    private class Event {

        private final String id;
        private final String name;
        private final State pre;
        private final State post;
        private final String start;
        private final String about;
        private final String submitted;

        public Event(String id, String name, State pre, State post, String start, String about, String submited) {
            this.id = id;
            this.name = name;
            this.pre = pre;
            this.post = post;
            this.start = start;
            this.about = about;
            this.submitted = submited;
        }

        private String getStateCell(State state, int x, int y) {
            return getStateCell(state, x, y, "E6FFCC");
        }

        private String getStateCell(State state, int x, int y, String col) {
            String cellStr = "        <mxCell id=\"" + state.id + "\" value=\"" + state.name + "\" style=\"rounded=1;"
                    + "whiteSpace=wrap;fillColor=#" + col + ";"
                    + "html=1;fontFamily=Courier New;fontSize=12;glass=0;strokeWidth=3;shadow=0;\" vertex=\"1\" "
                    + "parent=\"1\">\n          <mxGeometry x=\"" + x + "\" y=\"" + y + "\" width=\"120\" "
                    + " height=\"40\" as=\"geometry\" />\n        </mxCell>\n";


            return cellStr;
        }

        private String getEventToStateArrows(int c) {
            String source = pre == null ? "" : "source=\"" + pre.id + "\"";
            String target = post == null ? "" : "target=\"" + post.id + "\"";
            String color = EVENT_COLORS[c % (EVENT_COLORS.length - 1)];
            String callbacks = showCallbacks ? "["
                    + (start == null ? "" : start) + ":"
                    + (about == null ? "" : about) + ":"
                    + (submitted == null ? "" : submitted) + "]" : "";
            callbacks = removeUri(callbacks);
            String nameAndCallbacks = name + callbacks;
            String arrowStr = "\n        <mxCell id=\"" + id + "\" value=\"" + nameAndCallbacks + "\" "
                    + "style=\"curved=1;html=1;jettySize=auto;orthogonalLoop=1;"
                    + "fontFamily=Courier New;fontSize=14;fontColor=#" + color + ";"
                    + "endArrow=block;endFill=0;endSize=8;strokeWidth=3;shadow=0;"
                    + "edgeStyle=orthogonalEdgeStyle;strokeColor=#" + color + ";html=1;"
                    + "labelBackgroundColor=none;labelPosition=right;verticalLabelPosition=middle;align=left;"
                    + "verticalAlign=bottom;\""
                    + " edge=\"1\" "
                    + "" + source + " " + target + " parent=\"1\">\n"
                    + "<mxGeometry relative=\"1\" as=\"geometry\" />\n</mxCell>\n";

            return arrowStr;
        }

        private String removeUri(String callback) {
            String toRemove = "http://\\$\\{CCD_DEF_CASE_SERVICE_BASE_URL\\}";
            return callback.replaceAll(toRemove, "");
        }
    }

    public String getStringFromFile(String path) throws IOException {
        String fileAsString = "";
        try (InputStream in = new FileInputStream(path);
             BufferedReader r = new BufferedReader(
                     new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String str = null;
            StringBuilder sb = new StringBuilder(8192);
            while ((str = r.readLine()) != null) {
                sb.append(str);
            }
            fileAsString = sb.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileAsString;
    }

    private class State {

        private final String id;
        private final String name;

        private State(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
