package graphapp.service;

import graphapp.model.GraphModel;

import java.util.List;

public interface GraphModelValidator {
    boolean isCorrectNetwork(GraphModel graph);

    List<String> getErrors();
}
