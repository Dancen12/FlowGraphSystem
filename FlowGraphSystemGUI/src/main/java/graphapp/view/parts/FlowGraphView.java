package graphapp.view.parts;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.model.GraphModel;
import graphapp.view.constants.FlowColors;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlowGraphView extends JPanel implements GraphView<String, FlowEdge> {

    private static final String VERTEX_STYLE = "shape=ellipse;perimeter=ellipsePerimeter;fillColor=%s;fontSize=16";
    private static final String EDGE_STYLE = "strokeColor=%s;fontSize=16;strokeWidth=2;";
    private static final double VERTEX_WIDTH = 30;
    private static final double VERTEX_HEIGHT = 30;


    public FlowGraphView() {
        setBorder(BorderFactory.createTitledBorder("Graph Visualization"));
        setLayout(new BorderLayout());
    }

    @Override
    public void draw(GraphModel<String, FlowEdge> model) {
        if (!(model instanceof FlowNetworkModel)) {
            return;
        }
        FlowNetworkModel network = (FlowNetworkModel) model;

        mxGraph drawingGraph = new mxGraph();
        Object parent = drawingGraph.getDefaultParent();

        drawingGraph.getModel().beginUpdate();
        try {
            Map<String, Object> vertexMap = new HashMap<>();

            for (String vertex : network.getAllVertices()) {
                String style = String.format(VERTEX_STYLE, getVertexColor(vertex, network));
                Object v = drawingGraph.insertVertex(parent, null, vertex, 0, 0, VERTEX_WIDTH, VERTEX_HEIGHT, style);
                vertexMap.put(vertex, v);
            }
            for (FlowEdge edge : network.getAllEdges()) {
                String source = network.getEdgeSource(edge);
                String target = network.getEdgeTarget(edge);
                String style = String.format(EDGE_STYLE, getEdgeColor(edge));
                String label = String.format("[%d,%d,%d]", edge.getLowerBound(), edge.getCurrentFlow(), edge.getUpperBound());

                drawingGraph.insertEdge(parent, null, label, vertexMap.get(source), vertexMap.get(target), style);
            }

        } catch (Exception e) {
            Logger.getLogger(FlowGraphView.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        } finally {
            drawingGraph.getModel().endUpdate();
        }

        mxHierarchicalLayout layout = new mxHierarchicalLayout(drawingGraph);
        layout.setOrientation(SwingConstants.WEST);
        layout.setIntraCellSpacing(100);
        layout.setInterRankCellSpacing(150);
        layout.execute(parent);


        mxGraphComponent graphComponent = new mxGraphComponent(drawingGraph);
        graphComponent.setConnectable(false);

        SwingUtilities.invokeLater(() -> refresh(graphComponent));
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    private void refresh(mxGraphComponent graphComponent) {
        removeAll();

        JScrollPane scrollPane = new JScrollPane(graphComponent);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private String getEdgeColor(FlowEdge edge) {
        if (edge.getCurrentFlow() == edge.getUpperBound()) {
            return FlowColors.FULL_FLOW_COLOR;
        } else if (edge.getCurrentFlow() == edge.getLowerBound()) {
            return FlowColors.MINIMUM_FLOW_COLOR;
        } else if (edge.getCurrentFlow() >= edge.getLowerBound() && edge.getCurrentFlow() <= edge.getUpperBound()) {
            return FlowColors.FEASIBLE_FLOW_COLOR;
        } else {
            return FlowColors.ERROR_FLOW_COLOR;
        }
    }

    private String getVertexColor(String vertex, FlowNetworkModel network) {
        if (network.getSource().equals(vertex)) {
            return FlowColors.SOURCE_VERTEX_COLOR;
        } else if (network.getSink().equals(vertex)) {
            return FlowColors.SINK_VERTEX_COLOR;
        } else {
            return FlowColors.VERTEX_COLOR;
        }
    }
}
