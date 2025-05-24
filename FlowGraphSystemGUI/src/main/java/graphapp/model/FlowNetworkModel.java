package graphapp.model;

import lombok.Getter;
import lombok.Setter;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowNetworkModel implements GraphModel<String, FlowEdge> {
    @Getter
    @Setter
    private DefaultDirectedGraph<String, FlowEdge> network;
    private final List<ModelListener> listeners;
    @Getter
    @Setter
    private String source;
    @Getter
    @Setter
    private String sink;

    public FlowNetworkModel() {
        network = new DefaultDirectedGraph<>(FlowEdge.class);
        listeners = new ArrayList<>();
    }

    @Override
    public void addVertex(String id) {
        network.addVertex(id);
    }

    @Override
    public void addEdge(String from, String to, FlowEdge edge) {
        network.addEdge(from, to, edge);
    }

    @Override
    public Set<FlowEdge> getAllEdges() {
        return network.edgeSet();
    }

    @Override
    public Set<String> getAllVertices() {
        return network.vertexSet();
    }

    @Override
    public FlowEdge getEdge(String from, String to) {
        return network.getEdge(from, to);
    }

    @Override
    public String getEdgeSource(FlowEdge edge) {
        return network.getEdgeSource(edge);
    }

    @Override
    public String getEdgeTarget(FlowEdge edge) {
        return network.getEdgeTarget(edge);
    }

    @Override
    public boolean removeAll() {
        removeAllEdges();
        removeAllVertices();
        return network.vertexSet().isEmpty() && network.edgeSet().isEmpty();
    }

    @Override
    public boolean removeAllEdges() {
        synchronized (network) {
            Set<FlowEdge> edgeSet = new HashSet<>(network.edgeSet());
            network.removeAllEdges(edgeSet);
        }
        return network.edgeSet().isEmpty();
    }

    @Override
    public boolean removeAllVertices() {
        synchronized (network) {
            Set<String> vertexSet = new HashSet<>(network.vertexSet());
            network.removeAllVertices(vertexSet);
        }
        return network.vertexSet().isEmpty();

    }

    public void addListener(ModelListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners() {
        for (ModelListener listener : listeners) {
            listener.proceedModelChange(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(network.vertexSet().size()).append("\n");
        for (FlowEdge edge : network.edgeSet()) {
            sb.append(edge.toString()).append("\n");
        }
        return sb.toString();
    }
}
