package graphapp;

import graphapp.controller.MainController;
import graphapp.model.FlowNetworkModel;
import graphapp.model.GraphModel;
import graphapp.view.AppView;

public class GraphApp {
    private GraphModel model;
    private AppView graphView;
    private MainController mainController;

    public static void main(String[] args) {
        demo();
    }

    private static void demo() {
        new GraphApp().createMVC();

    }

    private void createMVC() {
        model = new FlowNetworkModel();
        graphView = new AppView();
        mainController = new MainController(model, graphView);
    }
}
