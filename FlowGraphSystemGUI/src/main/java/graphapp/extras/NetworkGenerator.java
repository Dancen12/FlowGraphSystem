package graphapp.extras;

import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.service.FlowNetworkModelValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkGenerator {

    public FlowNetworkModel generateNetwork(int numberOfNodes, DensityOfEdges edgeAlgorithm, int numberOfLevels) {
        if (!checkInput(numberOfNodes, numberOfLevels)) {
            throw new IllegalArgumentException("Wrong arguments");
        }

        FlowNetworkModel flowNetworkModel = new FlowNetworkModel();
        initModelVertices(numberOfNodes, flowNetworkModel);
        Map<Integer, List<String>> levels = setLevels(numberOfNodes, numberOfLevels, flowNetworkModel);
        addEdges(numberOfNodes, edgeAlgorithm, numberOfLevels, levels, flowNetworkModel);
        FlowNetworkModelValidator validator = new FlowNetworkModelValidator();
        if (validator.isCorrectNetwork(flowNetworkModel)) {
            return flowNetworkModel;
        } else {
            return null;
        }
    }

    /**
     * Liczba wierzcholkow i poziomow musi byc rowna co najmniej 3,
     * natomiast reszta z dzielenia wierzcholkow i poziomow bez s i t
     * musi wynosic 0
     *
     * @param numberOfNodes
     * @param numberOfLevels
     * @return boolean
     */
    private boolean checkInput(int numberOfNodes, int numberOfLevels) {
        int numberOfMiddleNodes = numberOfNodes - 2;
        int numberOfMiddleLevels = numberOfLevels - 2;
        return numberOfNodes >= 3 && numberOfLevels >= 3 && (numberOfMiddleNodes % numberOfMiddleLevels == 0);
    }

    private void addEdges(int numberOfNodes, DensityOfEdges edgeAlgorithm, int numberOfLevels, Map<Integer, List<String>> levels, FlowNetworkModel flowNetworkModel) {
        switch (edgeAlgorithm) {
            case OneToOneFromNextLevel:
                oneToOne(numberOfNodes, numberOfLevels, levels, flowNetworkModel);
                break;
            case OneToAllFromNextLevel:
                oneToAll(numberOfNodes, numberOfLevels, levels, flowNetworkModel);
                break;
        }
    }

    private Map<Integer, List<String>> setLevels(int numberOfNodes, int numberOfLevels, FlowNetworkModel flowNetworkModel) {
        Map<Integer, List<String>> levels = new HashMap<>();
        levels.put(0, List.of(flowNetworkModel.getSource()));
        levels.put(numberOfLevels - 1, List.of(flowNetworkModel.getSink()));

        List<String> middle = new ArrayList<>();
        for (int i = 1; i < numberOfNodes - 1; i++) {
            middle.add(String.valueOf(i));
        }

        int numberOfLevelsOnMiddle = numberOfLevels - 2;
        int id = 0;

        for (int i = 1; i <= numberOfLevelsOnMiddle; i++) {
            List<String> group = new ArrayList<>();
            while (group.size() < middle.size() / numberOfLevelsOnMiddle ||
                    (i <= middle.size() % numberOfLevelsOnMiddle && id < middle.size())) {
                group.add(middle.get(id++));
            }
            levels.put(i, group);
        }
        return levels;
    }

    private void initModelVertices(int numberOfNodes, FlowNetworkModel flowNetworkModel) {
        for (int i = 0; i < numberOfNodes; i++) {
            flowNetworkModel.addVertex(String.valueOf(i));
        }
        flowNetworkModel.setSource(String.valueOf(0));
        flowNetworkModel.setSink(String.valueOf(numberOfNodes - 1));
    }

    private void oneToAll(int numberOfNodes, int numberOfLevels, Map<Integer, List<String>> levels, FlowNetworkModel flowNetworkModel) {
        for (int l = 0; l < numberOfLevels; l++) {
            for (String v1 : levels.get(l)) {
                if (v1 == flowNetworkModel.getSink()) {
                    break;
                }
                for (String v2 : levels.get(l + 1)) {
                    flowNetworkModel.addEdge(v1, v2, new FlowEdge(1, 0, numberOfNodes - 1));
                }
            }
        }
    }

    private void oneToOne(int numberOfNodes, int numberOfLevels, Map<Integer, List<String>> levels, FlowNetworkModel flowNetworkModel) {
        List<String> levelOne = levels.get(1);
        for (int i = 0; i < levelOne.size(); i++) {
            flowNetworkModel.addEdge(flowNetworkModel.getSource(), levelOne.get(i), new FlowEdge(1, 0, numberOfNodes - 1));
        }
        for (int l = 1; l < numberOfLevels - 2; l++) {
            List<String> level = levels.get(l);
            List<String> nextLevel = levels.get(l + 1);
            for (int v = 0; v < level.size(); v++) {
                flowNetworkModel.addEdge(level.get(v), nextLevel.get(v), new FlowEdge(1, 0, numberOfNodes - 1));
            }
        }
        List<String> beforeSinkLevel = levels.get(numberOfLevels - 2);
        for (int i = 0; i < beforeSinkLevel.size(); i++) {
            flowNetworkModel.addEdge(beforeSinkLevel.get(i), flowNetworkModel.getSink(), new FlowEdge(1, 0, numberOfNodes - 1));
        }
    }

    public enum DensityOfEdges {
        OneToOneFromNextLevel,
        OneToAllFromNextLevel,
    }
}


