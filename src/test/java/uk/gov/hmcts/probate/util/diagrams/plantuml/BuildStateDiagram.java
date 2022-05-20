package uk.gov.hmcts.probate.util.diagrams.plantuml;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.service.filebuilder.TextFileBuilderService;

import java.io.BufferedReader;
import java.io.File;
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
public class BuildStateDiagram {
    //use https://plantuml.com/
    private TextFileBuilderService textFileBuilderService = new TextFileBuilderService();

    private static final String CASE_TYPE_PREFIX = "CCD_Probate_";
    private static final String BASE_DIR = "ccdImports/configFiles/" + CASE_TYPE_PREFIX;
    private static final String CASE_TYPE_GRANT = "Backoffice";
    private static final String CASE_TYPE_CAVEAT = "Caveat";
    private static final String ROLE_CW = "caseworker-probate-issuer";
    private static final String ROLE_PP = "caseworker-probate-solicitor";
    private static final String COLOR_STATE = " #bfbfff";
    private static final String COLOR_EVENT = " #ff8080";
    private static final String ARROW = " --> ";
    private static final String ARROW_TERMINATOR = " --> ";
    private static final String CR = " \n";

    private boolean filteredByRole = true;
    private String filteredByRoleName = ROLE_PP;
    private boolean showCallbacks = true;

    public static void main(String[] args) throws IOException {
        new BuildStateDiagram().generateAll();
    }

    private void generateAll() throws IOException {
        generate(CASE_TYPE_GRANT);
        //generate("Caveat");
    }

    private void generate(String caseType) throws IOException {
        List<PlantUmlState> allStates = getAllStates(caseType);
        List<PlantUmlEvent> allEvents = getAllEvents(caseType, allStates);
        //addEventAuths(caseType, allEvents);


        String header = "@startuml" + CR;
        String keyState = "state STATE" + COLOR_STATE + CR;
        String noteState = "note left of STATE : States" + CR;
        String keyEvent = "state EVENT" + COLOR_EVENT + CR;
        String noteEvent = "note left of EVENT : Events" + CR;

        String footer = "@enduml";

        List<String> allRows = new ArrayList<>();
        allRows.add(header);
        allRows.add(keyState);
        allRows.add(noteState);
        allRows.add(keyEvent);
        allRows.add(noteEvent);
        allRows.add(CR);

        for (PlantUmlState state : allStates) {
            String id = state.getStateId();
            String stateName = separateWithCRs(state.getName());

            String stateRow = "state " + id + " as \"" + stateName + "\" " + COLOR_STATE + CR;
            allRows.add(stateRow);

        }
        for (PlantUmlEvent event : allEvents) {
            String id = event.getEventId();
            String eventName = separateWithCRs(event.getName());

            String eventRow = "state " + id + " as \"" + eventName + "\"" + COLOR_EVENT + CR;
            allRows.add(eventRow);
            List<String> cbRows = getAllCallbackRows(event);
            allRows.addAll(cbRows);
        }
        allRows.add(CR);
        allRows.add(CR);
        allRows.add(CR);
        for (PlantUmlEvent event : allEvents) {
            String eventId = event.getEventId();
            if (event.getPre() == null && event.getPost() == null) {
                String preEventRow = "[*] " + ARROW_TERMINATOR + eventId + " \n";
                String postEventRow = eventId + ARROW_TERMINATOR + "[*] \n";

                allRows.add(preEventRow);
                allRows.add(postEventRow);
            } else if (event.getPre() == null && event.getPost() != null) {
                String preEventRow = "[*] " + ARROW_TERMINATOR + eventId + CR;
                allRows.add(preEventRow);

                String post = event.getPost().getStateId();
                String eventPostRow = eventId + ARROW + post + CR;
                allRows.add(eventPostRow);

            } else if (event.getPre() != null && event.getPost() != null) {
                String pre = event.getPre().getStateId();
                String post = event.getPost().getStateId();

                String preEventRow = pre + ARROW + eventId + CR;
                String eventPostRow = eventId + ARROW + post + CR;
                allRows.add(preEventRow);
                allRows.add(eventPostRow);

            }
            allRows.add(CR);
        }

        allRows.add(footer);
        textFileBuilderService.createFile(allRows, ",", CASE_TYPE_PREFIX + caseType
                + "_" + filteredByRoleName + "_state.txt");
        File source = new File("");
    }

    private List<String> getAllCallbackRows(PlantUmlEvent event) {
        List<String> all = new ArrayList<>();
        if (showCallbacks) {
            String start = addEventCallback(event, event.getStart(), "1 - ");
            if (start != null) {
                all.add(start);
            }
            String about = addEventCallback(event, event.getAbout(), "2 - ");
            if (about != null) {
                all.add(about);
            }
            String sub = addEventCallback(event, event.getSubmitted(), "3 - ");
            if (sub != null) {
                all.add(sub);
            }
        }
        return all;
    }

    private String addEventCallback(PlantUmlEvent event, String cb, String prefix) {
        return cb.isBlank() ? null : event.getEventId() + " : " + prefix + separateWithCRs(removeUri(cb)) + CR;
    }

    private String removeUri(String callback) {
        String toRemove = "http://\\$\\{CCD_DEF_CASE_SERVICE_BASE_URL\\}";
        return callback.replaceAll(toRemove, "");
    }

    private List<PlantUmlEvent> getAllEvents(String caseType, List<PlantUmlState> allStates) throws IOException {
        String caseEvent = getStringFromFile(BASE_DIR + caseType + "/CaseEvent.json");
        Map<String, Object>[] caseEvents = new ObjectMapper().readValue(caseEvent, HashMap[].class);

        List<PlantUmlEvent> allEvents = new ArrayList<>();
        for (Map<String, Object> eventMap : caseEvents) {
            String id = "";
            String name = "";
            String description = "";
            String pre = "";
            String post = "";
            String start = "";
            String about = "";
            String submitted = "";
            boolean showSummary = false;
            for (String key : eventMap.keySet()) {
                switch (key) {
                    case "ID":
                        id = eventMap.get(key).toString();
                        break;
                    case "Name":
                        name = eventMap.get(key).toString();
                        break;
                    case "Description":
                        description = eventMap.get(key).toString();
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
                    case "ShowSummary":
                        showSummary = Boolean.valueOf(eventMap.get(key).toString());
                        break;
                }

            }

            PlantUmlState preDiagramState = getState(allStates, pre);
            PlantUmlState postDiagramState = getState(allStates, post);

            PlantUmlEvent plantUmlEvent = PlantUmlEvent.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .pre(preDiagramState)
                    .post(postDiagramState)
                    .start(start)
                    .about(about)
                    .submitted(submitted)
                    .showSummary(showSummary)
                    .build();
            allEvents.add(plantUmlEvent);
        }


        List<PlantUmlEvent> allAuthdEvents = new ArrayList<>();
        String allAuthEvents = getStringFromFile(BASE_DIR + caseType + "/AuthorisationCaseEvent.json");
        Map<String, Object>[] authEvents = new ObjectMapper().readValue(allAuthEvents, HashMap[].class);
        for (Map<String, Object> authEvent : authEvents) {
            String userRole = authEvent.get("UserRole").toString();
            if (!filteredByRole || filteredByRoleName.equals(userRole)) {
                allAuthdEvents.add(allEvents.stream().filter(e -> e.getId().equals(authEvent.get("CaseEventID"))).findFirst().get());
            }
        }
        return allAuthdEvents;
    }

    private List<PlantUmlState> getAllStates(String caseType) throws IOException {
        String caseSates = getStringFromFile(BASE_DIR + caseType + "/State.json");
        Map<String, Object>[] allStatesMap = new ObjectMapper().readValue(caseSates, HashMap[].class);

        List<PlantUmlState> allStates = new ArrayList<>();
        for (Map<String, Object> statesMap : allStatesMap) {
            String id = "";
            String name = "";
            String description = "";
            for (String key : statesMap.keySet()) {
                switch (key) {
                    case "ID":
                        id = statesMap.get(key).toString();
                        break;
                    case "Name":
                        name = statesMap.get(key).toString();
                        break;
                    case "Description":
                        description = statesMap.get(key).toString();
                        break;
                }

            }

            PlantUmlState plantUmlState = PlantUmlState.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .build();
            allStates.add(plantUmlState);
        }


        List<PlantUmlState> allAuthdStates = new ArrayList<>();
        String allAuthSates = getStringFromFile(BASE_DIR + caseType + "/AuthorisationCaseState.json");
        Map<String, Object>[] authStates = new ObjectMapper().readValue(allAuthSates, HashMap[].class);
        for (Map<String, Object> state : authStates) {
            String userRole = state.get("UserRole").toString();
            if (!filteredByRole || filteredByRoleName.equals(userRole)) {
                PlantUmlState thisState = allStates.stream().filter(s -> s.getId().equals(state.get("CaseStateID"))).findFirst().get();
                if (thisState != null) {
                    allAuthdStates.add(PlantUmlState.builder()
                            .id(thisState.getId())
                            .name(thisState.getName())
                            .description(thisState.getDescription())
                            .build());
                }
            }
        }

        return allAuthdStates;
    }

    private PlantUmlState getState(List<PlantUmlState> allDiagramStates, String find) {
        for (PlantUmlState PlantUmlState : allDiagramStates) {
            if (PlantUmlState.getId().equalsIgnoreCase(find)) {
                return PlantUmlState;
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

    private String separateWithCRs(String name) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (char c : name.toCharArray()) {
            if (c == " ".toCharArray()[0]) {
                builder.append("\\n");
                i = 0;
            } else if (i > 30) {
                builder.append("\\n");
                builder.append(c);
                i = 0;
            } else {
                builder.append(c);
            }
            i++;
        }
        return builder.toString();
    }

}
