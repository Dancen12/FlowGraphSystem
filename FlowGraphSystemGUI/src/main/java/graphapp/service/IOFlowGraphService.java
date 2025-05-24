package graphapp.service;

import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.view.AppView;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOFlowGraphService implements IOGraphService {
    private FlowNetworkModel graphModel;
    private AppView appView;

    public IOFlowGraphService(FlowNetworkModel graphModel, AppView appView) {
        this.appView = appView;
        this.graphModel = graphModel;
    }

    @Override
    public void saveToFile(String filename) {
        File file = getFile(filename);
        if (file == null) {
            return;
        }


        int numberOfVertices = graphModel.getAllVertices().size();
        try {
            if (!file.exists()) {
                file.delete();
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(numberOfVertices + "\n");
            fileWriter.write(graphModel.getSource() + "\n");
            fileWriter.write(graphModel.getSink() + "\n");
            for (Object edge : graphModel.getAllEdges()) {
                fileWriter.write(edge.toString() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            Logger.getLogger(IOFlowGraphService.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void loadFromFile(String filename) {
        File file = getFile(filename);
        if (file == null || file.length() == 0) {
            return;
        }
        FlowNetworkModel tempModel = new FlowNetworkModel();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            int numberOfVertices = Integer.parseInt(bufferedReader.readLine());
            String source = bufferedReader.readLine().strip();
            String sink = bufferedReader.readLine().strip();
            tempModel.addVertex(source);
            tempModel.addVertex(sink);
            tempModel.setSource(source);
            tempModel.setSink(sink);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                String from = tokens[0];
                String to = tokens[1];
                int lowerBound = Integer.parseInt(tokens[2]);
                int currentFlow = Integer.parseInt(tokens[3]);
                int upperBound = Integer.parseInt(tokens[4]);
                tempModel.addVertex(from);
                tempModel.addVertex(to);
                tempModel.addEdge(from, to, new FlowEdge(lowerBound, currentFlow, upperBound));
            }
            GraphModelValidator validator = new FlowNetworkModelValidator();
            if (validator.isCorrectNetwork(tempModel)) {
                graphModel.removeAll();
                graphModel.setNetwork(tempModel.getNetwork());
                graphModel.setSource(tempModel.getSource());
                graphModel.setSink(tempModel.getSink());
            } else {
                appView.showErrorMessage(validator.getErrors().toString());
            }

            graphModel.notifyListeners();

        } catch (Exception e) {
            Logger.getLogger(IOFlowGraphService.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error while loading from file");
        }
        graphModel.notifyListeners();
    }

    private File getFile(String fileName) {
        if (fileName == null) {
            return appView.askAboutFile();
        }
        return new File(fileName);
    }
}
