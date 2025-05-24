package graphapp.model;

import java.util.Set;

public interface GraphModel<V, E> {
    void addVertex(V id);

    void addEdge(V from, V to, E edge);

    Set<E> getAllEdges();

    Set<V> getAllVertices();

    E getEdge(V from, V to);

    V getEdgeSource(E edge);

    V getEdgeTarget(E edge);

    boolean removeAll();

    boolean removeAllEdges();

    boolean removeAllVertices();

    void addListener(ModelListener listener);

    void notifyListeners();
}
