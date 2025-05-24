package graphapp.view.parts;

import graphapp.view.constants.ButtonsLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class AppMenuBar extends JMenuBar {
    public static final String EDIT_BUTTON = "EDIT_BUTTON";
    public static final String REFRESH_BUTTON = "REFRESH_BUTTON";
    public static final String MIN_FLOW_BUTTON = "MIN_FLOW_BUTTON";
    public static final String LOAD_FROM_FILE_BUTTON = "LOAD_FROM_FILE_BUTTON";
    public static final String SAVE_TO_FILE_BUTTON = "SAVE_TO_FILE_BUTTON";
    public static final String REMOVE_CYCLES_BUTTON = "REMOVE_CYCLES_BUTTON";
    public static final String MAX_FLOW_BUTTON = "MAX_FLOW_BUTTON";
    public static final String ESTABLISH_FLOW_BUTTON = "ESTABLISH_FLOW_BUTTON";

    private Map<String, JButton> buttons;

    private static final Dimension BUTTON_SIZE = new Dimension(50, 50);

    public AppMenuBar() {
        createButtons();
        addButtonsToMenu();
        setBorder(BorderFactory.createTitledBorder("Menu"));
        setPreferredSize(new Dimension(BUTTON_SIZE.width * buttons.size(), BUTTON_SIZE.height * 2));
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void createButtons() {
        buttons = new HashMap<>();

        buttons.put(EDIT_BUTTON, createButton(ButtonsLabel.EDIT_BUTTON_LABEL));
        buttons.put(REFRESH_BUTTON, createButton(ButtonsLabel.REFRESH_BUTTON_LABEL));
        buttons.put(MIN_FLOW_BUTTON, createButton(ButtonsLabel.MIN_FLOW_BUTTON_LABEL));
        buttons.put(LOAD_FROM_FILE_BUTTON, createButton(ButtonsLabel.LOAD_FROM_FILE_BUTTON_LABEL));
        buttons.put(SAVE_TO_FILE_BUTTON, createButton(ButtonsLabel.SAVE_TO_FILE_BUTTON_LABEL));
        buttons.put(REMOVE_CYCLES_BUTTON, createButton(ButtonsLabel.REMOVE_CYCLES_BUTTON_LABEL));
        buttons.put(MAX_FLOW_BUTTON, createButton(ButtonsLabel.MAX_FLOW_BUTTON_LABEL));
        buttons.put(ESTABLISH_FLOW_BUTTON, createButton(ButtonsLabel.ESTABLISH_FLOW_BUTTON_LABEL));
    }

    private JButton createButton(String buttonName) {
        JButton button = new JButton(buttonName);
        button.setPreferredSize(BUTTON_SIZE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        return button;
    }

    private void addButtonsToMenu() {
        setLayout(new GridLayout(2, 4));
        for (JButton button : buttons.values()) {
            add(button);
        }
    }

    public void addButtonListener(String buttonName, ActionListener actionListener) {
        buttons.get(buttonName).addActionListener(actionListener);
    }

    public void enableOrDisable(String buttonName) {
        JButton button = buttons.get(buttonName);
        button.setEnabled(!button.isEnabled());
    }

}
