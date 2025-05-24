package graphapp.service;


import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.view.AppView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculateFlowService implements SubprocessTaskService {
    private FlowNetworkModel graphModel;
    private AppView appView;
    private static int taskID = 0;

    private final String EXE_PATH;

    public CalculateFlowService(FlowNetworkModel graphModel, AppView appView) {
        this.graphModel = graphModel;
        this.appView = appView;

        EXE_PATH = getPath();
    }

    @Override
    public void process(String... args) {
        GraphModelValidator validator = new FlowNetworkModelValidator();
        if (!validator.isCorrectNetwork(graphModel)) {
            Logger.getLogger(CalculateFlowService.class.getName()).log(Level.WARNING, validator.getErrors().toString());
            throw new RuntimeException(validator.getErrors().toString());
        }
        try {
            String tempFilename = String.format("FlowTemp%d.txt", taskID++);

            FlowNetworkModel tempModel = new FlowNetworkModel();
            Map<String, Integer> strIntVertices = new HashMap<>();
            Map<Integer, String> intStrVertices = new HashMap<>();
            connectStringVerticesWithIntVertices(strIntVertices, intStrVertices);
            convertEdges(strIntVertices, tempModel);
            tempModel.setSource(strIntVertices.get(graphModel.getSource()).toString());
            tempModel.setSink(strIntVertices.get(graphModel.getSink()).toString());

            IOGraphService ioGraphService = new IOFlowGraphService(tempModel, appView);
            ioGraphService.saveToFile(tempFilename);


            String mode = args[0];
            String source = tempModel.getSource();
            String sink = tempModel.getSink();
            ProcessBuilder processBuilder = new ProcessBuilder(EXE_PATH, getAbsolutePathToTempFile(tempFilename), mode, source, sink);

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                deleteTempFile(tempFilename);
                Logger.getLogger(CalculateFlowService.class.getName()).log(Level.SEVERE, "Error while calculating flow");
                throw new RuntimeException("Error while calculating flow");
            }
            ioGraphService.loadFromFile(tempFilename);
            deleteTempFile(tempFilename);
            updateFlowInGraphModel(tempModel, intStrVertices);

        } catch (IOException | InterruptedException e) {
            Logger.getLogger(CalculateFlowService.class.getName()).log(Level.SEVERE, null, e);
        }
        graphModel.notifyListeners();
    }

    private void updateFlowInGraphModel(FlowNetworkModel tempModel, Map<Integer, String> intStrVertices) {
        for (FlowEdge edge : tempModel.getAllEdges()) {
            int intSource = Integer.parseInt(tempModel.getEdgeSource(edge));
            int intTarget = Integer.parseInt(tempModel.getEdgeTarget(edge));
            String strSource = intStrVertices.get(intSource);
            String strTarget = intStrVertices.get(intTarget);
            graphModel.getEdge(strSource, strTarget).setCurrentFlow(edge.getCurrentFlow());
        }
    }

    private void convertEdges(Map<String, Integer> vertices, FlowNetworkModel tempModel) {
        vertices.values().forEach(vertex -> tempModel.addVertex(vertex.toString()));
        graphModel.getAllEdges().forEach(edge -> {
            FlowEdge newEdge = new FlowEdge(edge.getLowerBound(), edge.getCurrentFlow(), edge.getUpperBound());
            Integer i = vertices.get(graphModel.getEdgeSource(edge));
            Integer j = vertices.get(graphModel.getEdgeTarget(edge));
            if (i != null && j != null) {
                String source = i.toString();
                String target = j.toString();
                tempModel.addEdge(source, target, newEdge);
            }
        });
    }

    private String getPath() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("cppAppPaths.properties")) {
            if (in == null) {
                throw new RuntimeException("cppAppPaths.properties not found");
            }
            props.load(in);
        } catch (IOException e) {
            Logger.getLogger(CalculateFlowService.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return (String) props.get("CalculateFlowExe");
    }

    private String getAbsolutePathToTempFile(String fileName) {
        File file = new File(fileName);
        String absolutePath = file.getAbsolutePath();
        absolutePath = absolutePath.replace("\\", "/");
        return absolutePath;
    }

    private void deleteTempFile(String tempFilename) {
        File file = new File(tempFilename);
        if (file.delete()) {
            Logger.getLogger(CalculateFlowService.class.getName()).log(Level.INFO, "Temp file deleted: {0}", tempFilename);
        } else {
            Logger.getLogger(CalculateFlowService.class.getName()).log(Level.SEVERE, "Temp file could not be deleted: {0}", tempFilename);
        }
    }

    private void connectStringVerticesWithIntVertices(Map<String, Integer> strIntVertices, Map<Integer, String> intStrVertices) {
        Set<String> modelVertices = graphModel.getAllVertices();
        strIntVertices.put(graphModel.getSource(), 0);
        intStrVertices.put(0, graphModel.getSource());
        strIntVertices.put(graphModel.getSink(), modelVertices.size() - 1);
        intStrVertices.put(modelVertices.size() - 1, graphModel.getSink());
        final int[] newVertex = {1};
        modelVertices.forEach(vertex -> {
            if (!strIntVertices.containsKey(vertex) && !intStrVertices.containsKey(newVertex[0])) {
                strIntVertices.put(vertex, newVertex[0]);
                intStrVertices.put(newVertex[0], vertex);
                ++newVertex[0];
            }
        });
    }


}
