package graphapp.model;

import lombok.Getter;
import org.jgrapht.graph.DefaultEdge;

@Getter
public class FlowEdge extends DefaultEdge {
    private int lowerBound;
    private int currentFlow;
    private int upperBound;

    public FlowEdge(int lowerBound, int currentFlow, int upperBound) {
        this.currentFlow = currentFlow;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public void setCurrentFlow(int flow) {
        if (flow < lowerBound && flow > upperBound) {
            throw new IllegalArgumentException("Flow out of bounds");
        }
        this.currentFlow = flow;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d %d %d", getSource(), getTarget(), lowerBound, currentFlow, upperBound);
    }
}