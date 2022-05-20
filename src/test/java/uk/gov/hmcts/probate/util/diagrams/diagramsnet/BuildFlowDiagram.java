package uk.gov.hmcts.probate.util.diagrams.diagramsnet;

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
    //use https://app.diagrams.net/
    private TextFileBuilderService textFileBuilderService = new TextFileBuilderService();

    private static final String CASE_TYPE_PREFIX = "CCD_Probate_";
    private static final String BASE_DIR = "ccdImports/configFiles/" + CASE_TYPE_PREFIX;
    private static final String CASE_TYPE_GRANT = "Backoffice";
    private static final String CASE_TYPE_CAVEAT = "Caveat";
    private static final String ROLE_CW = "caseworker-probate-issuer";

    private String filteredByRole = ROLE_CW;
    private boolean showCallbacks = false;
    private int width = 1000;
    private int height = 1000;

    public static void main(String[] args) throws IOException {
        new BuildFlowDiagram().generateAll();
    }

    private void generateAll() throws IOException {
        generate(CASE_TYPE_GRANT);
        //generate("Caveat");
    }

    private void generate(String caseType) throws IOException {
        List<DiagramState> allStates = getAllStates(caseType);
        List<DiagramEvent> allEvents = getAllEvents(caseType, allStates);
        addEventAuths(caseType, allEvents);


        DiagramState startState = DiagramState.builder()
                .id("begin")
                .name("BEGIN")
                .build();

        DiagramState endState = DiagramState.builder()
                .id("end")
                .name("END")
                .build();

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<mxfile host=\"app.diagrams.net\" modified=\"2021-07-03T10:40:14.316Z\" agent=\"5.0 (X11)\" "
                + "version=\"14.8.4\" etag=\"NBv04A9aWNXBj8fG8eNX\">\n"
                + "  <diagram id=\"XHciW1gV2JFlq7YNA-pa\">\n"
                + "    <mxGraphModel dx=\"" + width + "\" dy=\""+height+"\" grid=\"1\" gridSize=\"10\" guides=\"1\" tooltips=\"1\" "
                + "connect=\"1\" arrows=\"1\" fold=\"1\" page=\"1\" pageScale=\"1\" pageWidth=\""+width+"\" "
                + "pageHeight=\""+height+"\" math=\"0\" shadow=\"0\">\n"
                + "      <root>\n"
                + "        <mxCell id=\"0\" />\n"
                + "        <mxCell id=\"1\" parent=\"0\" />\n"
                + startState.asXml(width/2, 10, 50, 50, "FF0000");

        String footer = "      </root>\n"
                + "    </mxGraphModel>\n"
                + "  </diagram>\n"
                + "</mxfile>\n";

        List<String> allRows = new ArrayList<>();
        allRows.add(header);
        int i = 0;
        int startCount = 0;
        int x = 100;
        int y = 100;
        int dx = 120 + 20;
        int dy = 40 + 20;
        List<DiagramState> usedStates = new ArrayList<>();
        for (DiagramEvent event : allEvents) {
            if (event.getPre() == null && event.getPost() != null) {
                if (!usedStates.contains(event.getPost())) {
                    allRows.add(event.getPost().asXml(50 + (startCount * dx * 2), 100 + (startCount * dy/2),
                            160, 60, "00FFFF"));
                    usedStates.add(event.getPost());
                    startCount++;
                    y = y + 20;
                }
            } else if (event.getPre() != null && event.getPost() == null) {
                if (!usedStates.contains(event.getPre())) {
                    allRows.add(event.getPre().asXml(x, y, 120, 40, "00FFAA"));
                    usedStates.add(event.getPre());
                    x = x + dx;
                    y = y + dy;
                }
            } else if (event.getPre() != null && event.getPost() != null) {
                if (!usedStates.contains(event.getPre())) {
                    allRows.add(event.getPre().asXml(x, y, 120, 40,"00FF00"));
                    usedStates.add(event.getPre());
                    x = x + dx;
                    if (x > 1000) {
                        x = dx;
                        y = y + dy;
                    }
                }
                if (!usedStates.contains(event.getPost())) {
                    allRows.add(event.getPost().asXml(x, y, 120, 40, "00FF00"));
                    usedStates.add(event.getPost());
                    x = x + dx;
                    y = y + dy;
                }
            }

            allRows.add(event.getEventToStateArrows(i, showCallbacks));
            i++;
        }
        allRows.add(footer);
        textFileBuilderService.createFile(allRows, ",", CASE_TYPE_PREFIX + caseType
                + "_" + filteredByRole + "_flow.xml");
    }

    private List<DiagramEvent> getAllEvents(String caseType, List<DiagramState> allStates) throws IOException {
        List<DiagramEvent> allEvents = new ArrayList<>();
        String caseEvent = getStringFromFile(BASE_DIR + caseType + "/CaseEvent.json");
        Map<String, Object>[] caseEvents = new ObjectMapper().readValue(caseEvent, HashMap[].class);

        for (Map<String, Object> eventMap : caseEvents) {
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

            DiagramState preDiagramState = getState(allStates, pre);
            DiagramState postDiagramState = getState(allStates, post);

            DiagramEvent diagramEvent = DiagramEvent.builder()
                    .id(id)
                    .name(name)
                    .pre(preDiagramState)
                    .post(postDiagramState)
                    .start(start)
                    .about(about)
                    .submitted(submitted)
                    .build();
            allEvents.add(diagramEvent);
        }
        return allEvents;
    }

    private List<DiagramState> getAllStates(String caseType) throws IOException {
        List<DiagramState> allDiagramStates = new ArrayList<>();
        String caseSates = getStringFromFile(BASE_DIR + caseType + "/State.json");
        Map<String, Object>[] states = new ObjectMapper().readValue(caseSates, HashMap[].class);
        for (Map<String, Object> state : states) {
            allDiagramStates.add(DiagramState.builder()
                    .id(state.get("ID").toString())
                    .name(state.get("Name").toString())
                    .x(getPosition(state, 0))
                    .y(getPosition(state, 1))
                    .build());
        }

        allDiagramStates.add(DiagramState.builder()
                .id("*")
                .name("ALL")
                .build());
        return allDiagramStates;
    }

    private int getPosition(Map<String, Object> state, int ind) {
        if (state.get("Position") == null) {
            return -1;
        }
        return Integer.valueOf(state.get("Position").toString().split(",")[ind]);
    }

    private void addEventAuths(String caseType, List<DiagramEvent> allEvents) throws IOException {
        List<DiagramEvent> filteredEvents = new ArrayList<>();
        String authEvents = getStringFromFile(BASE_DIR + caseType + "/AuthorisationCaseEvent.json");
        Map<String, Object>[] auths = new ObjectMapper().readValue(authEvents, HashMap[].class);
        for (Map<String, Object> state : auths) {
            String eventId = state.get("CaseEventID").toString();
            String userRole = state.get("UserRole").toString();
            String crud = state.get("CRUD").toString();
            if (filteredByRole.equals(userRole)) {
                DiagramEvent de = getEventById(allEvents, eventId);
                filteredEvents.add(de);
            }
        }

        allEvents.retainAll(filteredEvents);
    }

    private DiagramEvent getEventById(List<DiagramEvent> allEvents, String eventId) {
        for (DiagramEvent de : allEvents) {
            if (de.getId().equals(eventId)) {
                return de;
            }
        }
        return null;
    }

    private DiagramState getState(List<DiagramState> allDiagramStates, String find) {
        for (DiagramState diagramState : allDiagramStates) {
            if (diagramState.getId().equalsIgnoreCase(find)) {
                return diagramState;
            }
        }
        return null;
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

}
