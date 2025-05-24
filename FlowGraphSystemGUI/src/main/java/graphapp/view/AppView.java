package graphapp.view;

import graphapp.view.constants.ViewMessages;
import graphapp.view.parts.AppMenuBar;
import graphapp.view.parts.FlowGraphView;
import graphapp.view.parts.GraphView;
import graphapp.view.parts.edition.FlowGraphEditionView;
import lombok.Getter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AppView extends JFrame {
    @Getter
    private AppMenuBar appMenuBar;
    @Getter
    private FlowGraphEditionView flowGraphEditionView;
    @Getter
    private GraphView graphView;

    private List<ViewListener> listeners;

    public AppView() {
        listeners = new ArrayList<>();
        createLayout();
        setUpMenuListeners();
    }

    private void createLayout() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Graph Manager");
        setSize(1500, 800);
        createComponents();
        packComponentsToPanels();
        setVisible(true);
    }

    private void packComponentsToPanels() {
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(appMenuBar, BorderLayout.NORTH);
        eastPanel.add(flowGraphEditionView, BorderLayout.CENTER);

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        westPanel.add(graphView.getComponent(), BorderLayout.CENTER);

        add(westPanel, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);
    }

    private void createComponents() {
        appMenuBar = new AppMenuBar();
        flowGraphEditionView = new FlowGraphEditionView();
        graphView = new FlowGraphView();
    }

    private void setUpMenuListeners() {
        appMenuBar.addButtonListener(AppMenuBar.EDIT_BUTTON, e -> notifyListeners(ViewMessages.START_STOP_EDITION));
        appMenuBar.addButtonListener(AppMenuBar.REMOVE_CYCLES_BUTTON, e -> notifyListeners(ViewMessages.REMOVE_CYCLES));
        appMenuBar.addButtonListener(AppMenuBar.LOAD_FROM_FILE_BUTTON, e -> notifyListeners(ViewMessages.LOAD_FROM_FILE));
        appMenuBar.addButtonListener(AppMenuBar.SAVE_TO_FILE_BUTTON, e -> notifyListeners(ViewMessages.SAVE_TO_FILE));
        appMenuBar.addButtonListener(AppMenuBar.REFRESH_BUTTON, e -> notifyListeners(ViewMessages.REFRESH));
        appMenuBar.addButtonListener(AppMenuBar.MIN_FLOW_BUTTON, e -> notifyListeners(ViewMessages.CALCULATE_MIN_FLOW));
        appMenuBar.addButtonListener(AppMenuBar.MAX_FLOW_BUTTON, e -> notifyListeners(ViewMessages.CALCULATE_MAX_FLOW));
        appMenuBar.addButtonListener(AppMenuBar.ESTABLISH_FLOW_BUTTON, e -> notifyListeners(ViewMessages.ESTABLISH_FLOW));
    }


    public void addListener(ViewListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(String message) {
        Logger.getLogger(AppView.class.getName()).log(Level.INFO, "Notify: " + message);
        for (ViewListener listener : listeners) {
            listener.proceedViewMessage(message);
        }
    }

    public File askAboutFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Pliki tekstowe (*.txt)");
        chooser.showOpenDialog(this);

        return chooser.getSelectedFile();
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showMessage(String title, String message) {
        JDialog messageDialog = new JDialog();
        messageDialog.setSize(200, 200);
        messageDialog.setTitle(title);
        messageDialog.setAlwaysOnTop(true);
        messageDialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(message);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        messageDialog.add(scrollPane);
        messageDialog.setVisible(true);
    }
}
