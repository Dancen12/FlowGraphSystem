package graphapp.service;

import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.view.AppView;
import graphapp.view.parts.AppMenuBar;
import graphapp.view.parts.edition.FlowGraphEditionView;
import graphapp.view.parts.edition.parts.TableRow;

public class FlowNetworkEditService implements EditService {
    private FlowNetworkModel model;
    private AppView appView;

    public FlowNetworkEditService(FlowNetworkModel model, AppView appView) {
        this.model = model;
        this.appView = appView;
    }

    @Override
    public void edit() {
        FlowGraphEditionView editionView = appView.getFlowGraphEditionView();
        if (editionView.isEditingMode()) {
            readModelFromEditionView();
        } else {
            editionView.showEditionMode(model);
            enableOrDisableMenuButtons();
        }
    }

    private void enableOrDisableMenuButtons() {
        AppMenuBar menuBar = appView.getAppMenuBar();
        menuBar.enableOrDisable(AppMenuBar.REFRESH_BUTTON);
        menuBar.enableOrDisable(AppMenuBar.MIN_FLOW_BUTTON);
        menuBar.enableOrDisable(AppMenuBar.REMOVE_CYCLES_BUTTON);
        menuBar.enableOrDisable(AppMenuBar.ESTABLISH_FLOW_BUTTON);
        menuBar.enableOrDisable(AppMenuBar.MAX_FLOW_BUTTON);
        menuBar.enableOrDisable(AppMenuBar.LOAD_FROM_FILE_BUTTON);
        menuBar.enableOrDisable(AppMenuBar.SAVE_TO_FILE_BUTTON);

    }

    private void readModelFromEditionView() {

        FlowGraphEditionView editionView = appView.getFlowGraphEditionView();
        FlowNetworkModel tempModel = new FlowNetworkModel();

        for (TableRow row : editionView.getRows()) {
            tempModel.addVertex(row.getFrom());
            tempModel.addVertex(row.getTo());
            tempModel.addEdge(row.getFrom(), row.getTo(), new FlowEdge(row.getLowerBound(), row.getCurrentFlow(), row.getUpperBound()));
        }

        tempModel.setSource(editionView.getSource());
        tempModel.setSink(editionView.getSink());

        GraphModelValidator validator = new FlowNetworkModelValidator();
        if (!validator.isCorrectNetwork(tempModel)) {
            appView.showErrorMessage(validator.getErrors().toString());
            if (!tempModel.getAllVertices().isEmpty()) {
                tempModel.removeAll();
            }
        } else {
            editionView.hideEditionMode();
            enableOrDisableMenuButtons();
            model.setNetwork(tempModel.getNetwork());
            model.setSource(tempModel.getSource());
            model.setSink(tempModel.getSink());
            model.notifyListeners();
        }

    }
}
