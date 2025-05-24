package graphapp.controller;

import graphapp.model.FlowNetworkModel;
import graphapp.model.GraphModel;
import graphapp.model.ModelListener;
import graphapp.service.*;
import graphapp.service.constants.FlowAppModes;
import graphapp.view.AppView;
import graphapp.view.ViewListener;
import graphapp.view.constants.ViewMessages;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements ViewListener, ModelListener {
    private GraphModel graphModel;
    private AppView appView;
    private OneThreadTaskDispatcher taskDispatcher;
    private ViewService viewService;
    private IOGraphService ioGraphService;
    private SubprocessTaskService removeCyclesService;
    private SubprocessTaskService flowService;
    private EditService editService;

    public MainController(GraphModel graphModel, AppView appView) {
        this.graphModel = graphModel;
        this.appView = appView;
        viewService = new FlowGraphViewService(graphModel, appView);


        if (graphModel instanceof FlowNetworkModel flowModel) {
            editService = new FlowNetworkEditService(flowModel, appView);
            removeCyclesService = new RemoveCyclesService(flowModel, appView);
            flowService = new CalculateFlowService(flowModel, appView);
            ioGraphService = new IOFlowGraphService(flowModel, appView);
        }


        appView.addListener(this);
        graphModel.addListener(this);
        taskDispatcher = new OneThreadTaskDispatcher();
        taskDispatcher.addExceptionHandler(new DialogExceptionHandler());
        setUpServices();
    }

    @Override
    public void proceedModelChange(GraphModel model) {
        viewService.proceedViewMessage(ViewMessages.REFRESH);
    }

    @Override
    public void proceedViewMessage(String message) {
        try {
            taskDispatcher.dispatchTask(message);
        } catch (Exception e) {

            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            appView.showErrorMessage(e.getMessage());
        }

    }

    private void setUpServices() {

        taskDispatcher.registerTask(ViewMessages.REFRESH, () -> viewService.proceedViewMessage(ViewMessages.REFRESH));
        taskDispatcher.registerTask(ViewMessages.REMOVE_CYCLES, removeCyclesService::process);

        if (graphModel instanceof FlowNetworkModel) {
            taskDispatcher.registerTask(ViewMessages.LOAD_FROM_FILE, () -> ioGraphService.loadFromFile(null));
            taskDispatcher.registerTask(ViewMessages.SAVE_TO_FILE, () -> ioGraphService.saveToFile(null));
            taskDispatcher.registerTask(ViewMessages.CALCULATE_MIN_FLOW, () -> flowService.process(FlowAppModes.MIN_FLOW));
            taskDispatcher.registerTask(ViewMessages.CALCULATE_MAX_FLOW, () -> flowService.process(FlowAppModes.MAX_FLOW));
            taskDispatcher.registerTask(ViewMessages.ESTABLISH_FLOW, () -> flowService.process(FlowAppModes.ESTIMATE_FLOW));
            taskDispatcher.registerTask(ViewMessages.START_STOP_EDITION, () -> editService.edit());
        }
    }
}
