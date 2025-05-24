package graphapp.controller;

import graphapp.service.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OneThreadTaskDispatcher implements TaskDispatcher {
    private Map<String, Runnable> tasks;
    private ExecutorService executor;
    private List<ExceptionHandler> exceptionHandlers;

    public OneThreadTaskDispatcher() {
        tasks = new HashMap<>();
        executor = Executors.newSingleThreadExecutor();
        exceptionHandlers = new ArrayList<>();
    }


    @Override
    public void registerTask(String taskName, Runnable task) {
        tasks.put(taskName, task);
    }

    @Override
    public void dispatchTask(String taskName) {
        if (tasks.containsKey(taskName)) {
            executor.execute(() -> {
                try {
                    tasks.get(taskName).run();
                } catch (Exception e) {
                    Logger.getLogger(OneThreadTaskDispatcher.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                    for (ExceptionHandler exceptionHandler : exceptionHandlers) {
                        exceptionHandler.handleException(e);
                    }
                }
            });
        } else {
            Logger.getLogger(OneThreadTaskDispatcher.class.getName()).log(Level.WARNING, "Task " + taskName + " not found");
        }

    }

    public void addExceptionHandler(ExceptionHandler exceptionHandler) {
        exceptionHandlers.add(exceptionHandler);
    }
}
