package ui;

import model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DepartmentTableDialog extends JDialog {

    public DepartmentTableDialog(JFrame parent, String title,
                                 List<Department> mandatoryList,  //  Zorunlu
                                 List<Department> baseList,       //  Temel Paket
                                 List<Department> algoList,       //  Algoritma
                                 List<Department> globalDepartments,
                                 boolean isLeftPosition) {
        super(parent, title, false);
        setSize(900, 550); //
        setLayout(new BorderLayout());

        // pencere konumu
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int parentX = parent.getX();
        int parentY = parent.getY();

        if (isLeftPosition) {
            int x = Math.max(0, parentX - this.getWidth() - 10);
            setLocation(x, parentY);
        } else {
            int x = Math.min(screenSize.width - this.getWidth(), parentX + parent.getWidth() + 10);
            setLocation(x, parentY);
        }


        // Depo stokları
        Map<Integer, Integer> initialStockMap = new HashMap<>();
        Map<Integer, Department> refDeptMap = new HashMap<>();
        for (Department d : globalDepartments) {
            initialStockMap.put(d.id, d.currentStock);
            refDeptMap.put(d.id, d);
        }

        // Listeleri Say
        Map<Integer, Integer> mandatoryCountMap = countItems(mandatoryList);
        Map<Integer, Integer> baseCountMap = countItems(baseList);
        Map<Integer, Integer> algoCountMap = countItems(algoList);

        // Tum etkilenen ID'leri birlestir
        Set<Integer> allInvolvedIds = new HashSet<>();
        allInvolvedIds.addAll(mandatoryCountMap.keySet());
        allInvolvedIds.addAll(baseCountMap.keySet());
        allInvolvedIds.addAll(algoCountMap.keySet());


        // Tablo olusturma
        String[] columnNames = {"ID", "Maliyet", "Değer", "Depodaki", "Zorunlu", "Temel Paket", "Algoritma", "SON STOK"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) { return Integer.class; }
        };

        for (Integer id : allInvolvedIds) {
            Department d = refDeptMap.get(id);
            int initial = initialStockMap.getOrDefault(id, 0);

            int cMandatory = mandatoryCountMap.getOrDefault(id, 0);
            int cBase = baseCountMap.getOrDefault(id, 0);
            int cAlgo = algoCountMap.getOrDefault(id, 0);

            int totalStock = initial + cMandatory + cBase + cAlgo;

            model.addRow(new Object[]{
                    d.id,
                    d.cost,
                    d.value,
                    initial,
                    cMandatory, // Yeni sutun
                    cBase,      // Yeni sutun
                    cAlgo,      // Yeni sutun
                    totalStock
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Sutun genislik ayarları
        table.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        table.getColumnModel().getColumn(4).setPreferredWidth(70); // Zorunlu
        table.getColumnModel().getColumn(5).setPreferredWidth(80); // Temel
        table.getColumnModel().getColumn(6).setPreferredWidth(80); // Algo
        table.getColumnModel().getColumn(7).setPreferredWidth(80); // Toplam

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Alt bilgi
        int sumMandatory = mandatoryList != null ? mandatoryList.size() : 0;
        int sumBase = baseList != null ? baseList.size() : 0;
        int sumAlgo = algoList != null ? algoList.size() : 0;

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel summaryLabel = new JLabel("Zorunlu: " + sumMandatory + " | Temel: " + sumBase + " | Algoritma: " + sumAlgo + " adet   ");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bottomPanel.add(summaryLabel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Listeyi map'e ceviren yardımcı metot
    private Map<Integer, Integer> countItems(List<Department> list) {
        Map<Integer, Integer> map = new HashMap<>();
        if (list != null) {
            for (Department d : list) {
                map.put(d.id, map.getOrDefault(d.id, 0) + 1);
            }
        }
        return map;
    }
}