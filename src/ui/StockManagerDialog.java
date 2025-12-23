package ui;

import model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockManagerDialog extends JDialog {

    public StockManagerDialog(JFrame parent, List<Department> departments) {
        super(parent, "Mevcut Stok Yönetimi", true);
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Tablo baslıkları
        String[] columnNames = {"Dept ID", "Maliyet", "Mevcut Stok (Düzenle)"};

        // Tablo modeli
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // sadece stok sutunu degistirilebilir
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
        };

        // Verileri yukle
        for (Department d : departments) {
            model.addRow(new Object[]{d.id, d.cost, d.currentStock});
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        //  Baska yere tıklayınca duzenlemeyi bitir
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Stok degisimini listeye isle
        model.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 2 && row >= 0) {
                Object val = model.getValueAt(row, 2);
                // Null kontrolü
                int newStock = (val instanceof Integer) ? (Integer) val : Integer.parseInt(val.toString());

                // Orijinal listeyi guncelle
                departments.get(row).currentStock = newStock;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Kaydet ve Kapat");
        closeBtn.setBackground(new Color(0, 153, 76));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        closeBtn.addActionListener(e -> {
            // Eger hala yazıyorsa islemi zorla bitir
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            dispose();
        });

        add(closeBtn, BorderLayout.SOUTH);
    }
}