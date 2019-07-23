package org.gaozou.roy.quick;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class ProgressBarCellRenderer extends javax.swing.JProgressBar implements javax.swing.table.TableCellRenderer {

    /** Creates a new instance of ProgressBarCellRenderer */
    public ProgressBarCellRenderer() {
    }

    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        int current = 0;

        try {
            current = ((Integer) value).intValue();
        }
        catch (Exception e) {
            TextCellRenderer tcr = new TextCellRenderer( javax.swing.JLabel.CENTER );

            return tcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        setMinimum( 0 );
        setValue( current );
        setMaximum( 100 );

        setStringPainted( true );

        return this;
    }

    public void validate() {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)  {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    }

    public boolean isOpaque() {
        return true;
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void repaint(java.awt.Rectangle r) {
    }

    public void revalidate() {
    }

}