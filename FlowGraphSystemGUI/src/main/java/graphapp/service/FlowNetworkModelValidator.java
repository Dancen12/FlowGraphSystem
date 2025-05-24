package graphapp.service;

import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.model.GraphModel;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowNetworkModelValidator implements GraphModelValidator {
    private List<String> errors;

    public FlowNetworkModelValidator() {
        errors = new ArrayList<>();
    }

    public boolean isCorrectNetwork(GraphModel graph) {
        errors.clear();

        if (graph instanceof FlowNetworkModel flowNetworkModel) {
            return hasValidSourceAndSink(flowNetworkModel)
                    && hasValidBounds(flowNetworkModel)
                    && hasNoSelfLoops(flowNetworkModel)
                    && areAllNodesReachableFromSource(flowNetworkModel)
                    && hasCorrectBalances(flowNetworkModel);
        } else {
            errors.add("Graph should be an instance of FlowNetworkModel");
            return false;
        }
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    private boolean hasValidSourceAndSink(FlowNetworkModel flowNetworkModel) {
        String source = flowNetworkModel.getSource();
        String sink = flowNetworkModel.getSink();

        if (source == null) {
            errors.add("Source should not be null");
            return false;
        }
        if (sink == null) {
            errors.add("Sink should not be null");
            return false;
        }
        if (source.equals(sink)) {
            errors.add("Source and Sink should not be the same");
            return false;
        }
        if (!flowNetworkModel.getNetwork().containsVertex(source)) {
            errors.add("Source vertex does not exist");
            return false;
        }
        if (!flowNetworkModel.getNetwork().containsVertex(sink)) {
            errors.add("Sink vertex does not exist");
            return false;
        }
        if (flowNetworkModel.getNetwork().inDegreeOf(source) != 0) {
            errors.add("Source in degree should be 0");
            return false;
        }
        if (flowNetworkModel.getNetwork().outDegreeOf(sink) != 0) {
            errors.add("Sink out degree should be 0");
            return false;
        }
        return true;
    }

    private boolean areAllNodesReachableFromSource(FlowNetworkModel flowNetworkModel) {
        Set<String> visited = new HashSet<>();
        BreadthFirstIterator<String, FlowEdge> bfs = new BreadthFirstIterator<>(flowNetworkModel.getNetwork(), flowNetworkModel.getSource());
        while (bfs.hasNext()) {
            visited.add(bfs.next());
        }
        if (visited.size() != flowNetworkModel.getAllVertices().size()) {
            errors.add("Not all vertices reachable from source");
            return false;
        }
        return true;
    }

    private boolean hasNoSelfLoops(FlowNetworkModel flowNetworkModel) {
        boolean hasNoSelfLoops = flowNetworkModel
                .getAllEdges()
                .stream()
                .noneMatch(edge -> flowNetworkModel.getEdgeSource(edge).equals(flowNetworkModel.getEdgeTarget(edge)));

        if (!hasNoSelfLoops) {
            errors.add("Self loops are not allowed");
            return false;
        }
        return true;
    }

    private boolean hasValidBounds(FlowNetworkModel flowNetworkModel) {
        boolean hasValidBounds = flowNetworkModel
                .getAllEdges()
                .stream()
                .allMatch(edge -> edge.getUpperBound() >= 0 && edge.getLowerBound() >= 0 && edge.getUpperBound() >= edge.getLowerBound());

        if (!hasValidBounds) {
            errors.add("Invalid bounds: Upper bound must be >= 0 and lower bound must be >= 0 and upper bound must be >= lower bound");
            return false;
        }
        return true;
    }

    private boolean hasCorrectBalances(FlowNetworkModel flowNetworkModel) {
        int balanceSum = 0;
        for (String vertex : flowNetworkModel.getAllVertices()) {

            for (FlowEdge edge : flowNetworkModel.getNetwork().outgoingEdgesOf(vertex)) {
                balanceSum -= edge.getLowerBound();
            }
            for (FlowEdge edge : flowNetworkModel.getNetwork().incomingEdgesOf(vertex)) {
                balanceSum += edge.getLowerBound();
            }
        }

        if (balanceSum != 0) {
            errors.add("Balances should be equal to zero");
            return false;
        }
        return true;
    }
}
