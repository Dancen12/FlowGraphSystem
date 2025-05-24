package graphapp.view.parts;

import graphapp.model.GraphModel;

import javax.swing.*;

public interface GraphView<V, E> {
    void draw(GraphModel<V, E> model);

    JComponent getComponent();
}
