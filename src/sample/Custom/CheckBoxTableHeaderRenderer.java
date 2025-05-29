package sample.Custom;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author nabil
 */
public class CheckBoxTableHeaderRenderer extends JCheckBox implements TableCellRenderer {

    private final JTable table;
    private final int column;

    public CheckBoxTableHeaderRenderer(JTable table, int column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int il) {
        return this;
    }
}
