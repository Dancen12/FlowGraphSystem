package graphapp.controller;

public interface TaskDispatcher {
    void registerTask(String taskName, Runnable task);

    void dispatchTask(String taskName);
}
