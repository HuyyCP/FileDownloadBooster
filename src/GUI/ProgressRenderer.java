package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class ProgressRenderer extends JProgressBar implements TableCellRenderer {
    public ProgressRenderer(int min, int max) {
        super(min, max);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        setValue((int) value);
        return this;
    }
}

