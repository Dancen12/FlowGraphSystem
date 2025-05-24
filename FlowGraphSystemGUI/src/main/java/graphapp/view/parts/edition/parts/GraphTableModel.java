package graphapp.view.parts.edition.parts;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphTableModel extends AbstractTableModel {
    private List<TableRow> data = new ArrayList<>();
    private String[] columnNames = {"From", "To", "Lower bound", "Current flow", "Upper bound"};
    private Class<?>[] types = {String.class, String.class, String.class, String.class, String.class};
    public static final int FROM_ID = 0;
    public static final int TO_ID = 1;
    public static final int LOWER_BOUND_ID = 2;
    public static final int CURRENT_FLOW_ID = 3;
    public static final int UPPER_BOUND_ID = 4;

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {

            case FROM_ID:
                return data.get(rowIndex).getFrom();
            case TO_ID:
                return data.get(rowIndex).getTo();
            case LOWER_BOUND_ID:
                return data.get(rowIndex).getLowerBound();
            case CURRENT_FLOW_ID:
                return data.get(rowIndex).getCurrentFlow();
            case UPPER_BOUND_ID:
                return data.get(rowIndex).getUpperBound();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex >= LOWER_BOUND_ID && columnIndex < columnNames.length) {
            try {
                int intValue = Integer.parseInt(aValue.toString());
                switch (columnIndex) {
                    case LOWER_BOUND_ID:
                        data.get(rowIndex).setLowerBound(intValue);
                        return;
                    case CURRENT_FLOW_ID:
                        data.get(rowIndex).setCurrentFlow(intValue);
                        return;
                    case UPPER_BOUND_ID:
                        data.get(rowIndex).setUpperBound(intValue);
                }
            } catch (Exception e) {
                Logger.getLogger(GraphTableModel.class.getName()).log(Level.WARNING, e.getMessage());
            }

        } else if (columnIndex >= FROM_ID && columnIndex < LOWER_BOUND_ID) {
            switch (columnIndex) {
                case FROM_ID:
                    data.get(rowIndex).setFrom(aValue.toString());
                    return;
                case TO_ID:
                    data.get(rowIndex).setTo(aValue.toString());
            }

        }
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void clear() {
        data.clear();
    }

    public void addRow(String from, String to, int lowerBound, int currentFlow, int upperBound) {
        TableRow row = new TableRow(from, to, lowerBound, currentFlow, upperBound);
        data.add(row);
    }

    public void removeRow(int rowIndex) {
        data.remove(rowIndex);
    }

    public List<TableRow> getAllRows() {
        return data;
    }
}
