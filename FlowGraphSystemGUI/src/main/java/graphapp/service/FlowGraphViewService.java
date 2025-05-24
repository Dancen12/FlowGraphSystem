package graphapp.service;

import graphapp.model.GraphModel;
import graphapp.view.AppView;
import graphapp.view.constants.ViewMessages;

import javax.swing.*;

public class FlowGraphViewService implements ViewService {
    private AppView appView;
    private GraphModel graphModel;

    public FlowGraphViewService(GraphModel graphModel,AppView appView) {
        this.appView = appView;
        this.graphModel = graphModel;
    }

    @Override
    public void proceedViewMessage(String message) {
        if (message.equals(ViewMessages.REFRESH)) {
            refreshView();
        } else {
            throw new IllegalArgumentException("Invalid message received: " + message);
        }
    }

    private void refreshView() {
        SwingUtilities.invokeLater(() -> appView.getGraphView().draw(graphModel));
    }

}
