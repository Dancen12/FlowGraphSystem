package graphapp.service;

import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.view.AppView;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoveCyclesService implements SubprocessTaskService {

    private FlowNetworkModel graphModel;
    private AppView appView;
    private final String EXE_PATH;

    public RemoveCyclesService(FlowNetworkModel graphModel, AppView appView) {
        this.graphModel = graphModel;
        this.appView = appView;
        EXE_PATH = getPath();
    }

    @Override
    public void process(String... args) {

        Map<String, Integer> strToIntVertices = mapGraphModelToIntVertices();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(graphModel.getAllVertices().size()).append(" ").append(graphModel.getAllEdges().size()).append("\n");
        graphModel.getAllEdges().forEach(e -> {
            String[] tokens = e.toString().split(" ");
            String source = strToIntVertices.get(tokens[0]).toString();
            String target = strToIntVertices.get(tokens[1]).toString();
            stringBuilder.append(source).append(" ").append(target).append("\n");
        });

        Map<Integer, String> intToStrMap = createInverseMap(strToIntVertices);

        String argsForProcess = stringBuilder.toString();

        try {
            ProcessBuilder pb = new ProcessBuilder(EXE_PATH);
            pb.redirectErrorStream(true);


            Process process = pb.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bufferedWriter.write(argsForProcess);
            bufferedWriter.flush();
            int exitCode = process.waitFor();
            processOutput(bufferedReader, intToStrMap);

            if (exitCode != 0) {
                throw new RuntimeException("GraphCycleReducer exit with code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            Logger.getLogger(RemoveCyclesService.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        appView.getGraphView().draw(graphModel);

    }

    private void processOutput(BufferedReader bufferedReader, Map<Integer, String> intToStrMap) throws IOException {

        List<String[]> rawEdges = new ArrayList<>();
        Set<String> used = new LinkedHashSet<>();
        String line;
        int highestPathId = 0;
        while ((line = bufferedReader.readLine()) != null && !line.equals("G3:"))
            ;
        while ((line = bufferedReader.readLine()) != null && !line.equals("v components:")) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(":");
            String from = parts[0].trim();
            if (from.matches("^p\\d+")) {
                int pathId = Integer.parseInt(from.replaceAll("^p", ""));
                if (pathId > highestPathId) {
                    highestPathId = pathId;
                }
            }
            if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                for (String to : parts[1].trim().split("\\s+")) {
                    rawEdges.add(new String[]{from, to});
                    used.add(from);
                    used.add(to);
                }
            }
        }
        int n = used.size();

        graphModel.removeAll();

        int defaultLowerBound = 1;
        int defaultUpperBound = n - 1;
        int defaultFlow = 0;
        Set<String> seen = new HashSet<>();
        for (String[] rawEdge : rawEdges) {
            String from = rawEdge[0];
            String to = rawEdge[1];
            String key = from + " " + to;
            if (seen.add(key)) {
                graphModel.addVertex(from);
                graphModel.addVertex(to);
                graphModel.addEdge(from, to, new FlowEdge(defaultLowerBound, defaultFlow, defaultUpperBound));
            }
        }
        graphModel.setSource("s");
        graphModel.setSink("t");

        StringBuilder messageWithVComponents = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null && !line.equals("Paths:")) {
            if (line.charAt(0) == 'v') {
                messageWithVComponents.append(line.trim()).append("\n");
            }
        }

        appView.showMessage("V components", messageWithVComponents.toString());

        StringBuilder messageWithPaths = new StringBuilder();
        int numberOfPaths = 0;
        while ((line = bufferedReader.readLine()) != null && numberOfPaths <= highestPathId) {
            StringBuilder messageLine = new StringBuilder(String.format("p%d: ", numberOfPaths));
            String[] pathVertices = line.split(" ");
            for (int i = 0; i < pathVertices.length; i++) {
                int vertex = Integer.parseInt(pathVertices[i]);
                if (i != 0) {
                    messageLine.append("-> ");
                }
                messageLine.append(intToStrMap.get(vertex));
            }
            messageWithPaths.append(messageLine).append("\n");
            numberOfPaths++;
        }

        appView.showMessage("Paths", messageWithPaths.toString());

    }

    private String getPath() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("cppAppPaths.properties")) {
            if (in == null) {
                throw new RuntimeException("cppAppPaths.properties not found");
            }
            props.load(in);
        } catch (IOException e) {
            Logger.getLogger(RemoveCyclesService.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return (String) props.get("GraphCycleReducerExe");
    }

    private Map<String, Integer> mapGraphModelToIntVertices() {
        Set<String> modelVertices = graphModel.getAllVertices();
        Map<String, Integer> convertedVertices = new HashMap<>();
        convertedVertices.put(graphModel.getSource(), 0);
        convertedVertices.put(graphModel.getSink(), modelVertices.size() - 1);
        final int[] newVertex = {1};
        for (String vertex : modelVertices) {
            if (!convertedVertices.containsKey(vertex)) {
                convertedVertices.put(vertex, newVertex[0]);
                ++newVertex[0];
            }
        }
        return convertedVertices;
    }

    private Map<Integer, String> createInverseMap(Map<String, Integer> map) {
        Map<Integer, String> inverseMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            inverseMap.put(entry.getValue(), entry.getKey());
        }
        return inverseMap;
    }

}