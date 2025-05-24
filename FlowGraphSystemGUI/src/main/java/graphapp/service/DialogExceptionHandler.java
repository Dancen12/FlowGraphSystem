package graphapp.service;

import javax.swing.*;

public class DialogExceptionHandler implements ExceptionHandler {

    @Override
    public void handleException(Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
