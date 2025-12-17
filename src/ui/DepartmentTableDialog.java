package ui;

import model.Department;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DepartmentTableDialog extends JDialog {

    public DepartmentTableDialog(JFrame parent, String title, List<Department> depts) {
        super(parent, title, true);
        setSize(300, 400);
        setLocationRelativeTo(parent);

        String[] cols = {"Department ID", "Cost", "Value"};
        Object[][] data = new Object[depts.size()][3];

        for (int i = 0; i < depts.size(); i++) {
            data[i][0] = depts.get(i).id;
            data[i][1] = depts.get(i).cost;
            data[i][2] = depts.get(i).value;
        }

        JTable table = new JTable(data, cols);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(22);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setAutoCreateRowSorter(true);

        add(new JScrollPane(table));
    }
}
