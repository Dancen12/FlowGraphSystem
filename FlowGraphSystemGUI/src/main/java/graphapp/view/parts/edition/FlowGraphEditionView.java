package graphapp.view.parts.edition;

import graphapp.model.FlowEdge;
import graphapp.model.FlowNetworkModel;
import graphapp.view.constants.ButtonsLabel;
import graphapp.view.parts.edition.parts.GraphTableModel;
import graphapp.view.parts.edition.parts.TableRow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowGraphEditionView extends JPanel {

    private JScrollPane scrollPane;
    private GraphTableModel tableModel;
    private Map<String, JButton> buttons = new HashMap<>();
    private JTable table;
    private JTextField sourceField;
    private JTextField sinkField;

    private static final Dimension BUTTON_PREFERRED_SIZE = new Dimension(120, 40);
    public static final String ADD_EDGE_BUTTON = "ADD_EDGE_BUTTON";
    public static final String REMOVE_EDGE_BUTTON = "REMOVE_EDGE_BUTTON";

    private boolean editing = false;

    public FlowGraphEditionView() {
        createLayout();
    }

    private void createLayout() {
        setBorder(BorderFactory.createTitledBorder("Graph Edition"));
        setLayout(new BorderLayout());
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(createButtonsPanel());
        northPanel.add(createSourceAndSinkPanel());
        add(northPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        createTable();
        scrollPane.add(table);
        add(scrollPane, BorderLayout.CENTER);


    }

    private void createTable() {
        tableModel = new GraphTableModel();
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultCellEditor editor = (DefaultCellEditor) table.getDefaultEditor(String.class);
        JTextField textField = (JTextField) editor.getComponent();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateModel() {
                int row = table.getEditingRow();
                int col = table.getEditingColumn();
                if (row >= 0 && col >= 0) {
                    String newValue = textField.getText();
                    if (newValue != null && newValue.length() > 0) {
                        table.getModel().setValueAt(newValue, row, col);
                    }
                }
            }

            public void insertUpdate(DocumentEvent e) {
                updateModel();
            }

            public void removeUpdate(DocumentEvent e) {
                updateModel();
            }

            public void changedUpdate(DocumentEvent e) {
                updateModel();
            }
        });
    }

    private JPanel createSourceAndSinkPanel() {
        sourceField = new JTextField();
        sourceField.setPreferredSize(BUTTON_PREFERRED_SIZE);
        sourceField.setBorder(BorderFactory.createTitledBorder("Source"));
        sourceField.setEnabled(false);

        sinkField = new JTextField();
        sinkField.setPreferredSize(BUTTON_PREFERRED_SIZE);
        sinkField.setBorder(BorderFactory.createTitledBorder("Sink"));
        sinkField.setEnabled(false);

        JPanel sourceSinkPanel = new JPanel();
        sourceSinkPanel.setLayout(new GridLayout(1, 2));
        sourceSinkPanel.add(sourceField);
        sourceSinkPanel.add(sinkField);

        return sourceSinkPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(createButton(ADD_EDGE_BUTTON, ButtonsLabel.ADD_EDGE_BUTTON_LABEL));
        buttonPanel.add(createButton(REMOVE_EDGE_BUTTON, ButtonsLabel.REMOVE_EDGE_BUTTON_LABEL));

        buttons.get(ADD_EDGE_BUTTON).addActionListener(e -> {
            tableModel.addRow("-", "-", 0, 0, 0);
            tableModel.fireTableRowsInserted(tableModel.getRowCount() - 1, tableModel.getRowCount() - 1);
        });

        buttons.get(REMOVE_EDGE_BUTTON).addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                int selectedRow = table.getSelectedRow();
                tableModel.removeRow(selectedRow);
                tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
            }
        });

        return buttonPanel;
    }

    private JButton createButton(String button, String name) {
        JButton b = new JButton(name);
        b.setPreferredSize(BUTTON_PREFERRED_SIZE);
        buttons.put(button, b);
        b.setEnabled(false);
        return b;
    }

    public void showEditionMode(FlowNetworkModel graphModel) {
        buttons.get(ADD_EDGE_BUTTON).setEnabled(true);
        buttons.get(REMOVE_EDGE_BUTTON).setEnabled(true);
        sourceField.setEnabled(true);
        sinkField.setEnabled(true);
        tableModel.clear();
        for (FlowEdge edge : graphModel.getAllEdges()) {
            String source = graphModel.getEdgeSource(edge);
            String target = graphModel.getEdgeTarget(edge);
            int lowerBound = edge.getLowerBound();
            int currentFlow = edge.getCurrentFlow();
            int upperBound = edge.getUpperBound();
            tableModel.addRow(source, target, lowerBound, currentFlow, upperBound);
        }
        sourceField.setText(graphModel.getSource());
        sinkField.setText(graphModel.getSink());
        tableModel.fireTableDataChanged();
        editing = true;
        refreshView();
    }

    public void hideEditionMode() {
        SwingUtilities.invokeLater(() -> {
            clearAll();
            buttons.get(ADD_EDGE_BUTTON).setEnabled(false);
            buttons.get(REMOVE_EDGE_BUTTON).setEnabled(false);
            sourceField.setEnabled(false);
            sinkField.setEnabled(false);
            scrollPane.setViewportView(null);
            repaint();
            revalidate();
        });
        editing = false;
    }

    private void refreshView() {
        SwingUtilities.invokeLater(() -> {
            scrollPane.setViewportView(table);
            repaint();
            revalidate();
        });
    }

    public boolean isEditingMode() {
        return editing;
    }

    public List<TableRow> getRows() {
        return tableModel.getAllRows();
    }

    public String getSource() {
        return sourceField.getText();
    }

    public String getSink() {
        return sinkField.getText();
    }

    private void clearAll() {
        tableModel.clear();
        tableModel.fireTableStructureChanged();
        sourceField.setText("");
        sinkField.setText("");
    }
}
