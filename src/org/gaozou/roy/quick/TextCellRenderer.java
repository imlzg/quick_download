package org.gaozou.roy.quick;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class TextCellRenderer extends DefaultTableCellRenderer {
    int textAlignment = JLabel.LEFT;

    public TextCellRenderer() {}
    public TextCellRenderer(int align) {
        this.textAlignment = align;
    }

    public void setTextAlignment(int align) {
        this.textAlignment = align;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(textAlignment);
        return label;
    }
}
