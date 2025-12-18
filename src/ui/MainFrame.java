package ui;

import algorithm.DPSolver;
import algorithm.GreedySolver;
import model.Department;
import util.CSVReader;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    // UI Bileşenleri
    private JTextArea greedyArea;
    private JTextArea dpArea;
    private ValueCostBarChart valueCostChart;
    private TimeBarChart timeChart;

    // Detay pencereleri için veri tutucular
    private List<Department> lastGreedy;
    private List<Department> lastDP;

    public MainFrame() {
        setTitle("Algoritmik Stok Optimizasyonu (Greedy vs DP)");
        setSize(1150, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Fontlar
        Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font monoFont = new Font("Consolas", Font.PLAIN, 13);

        // ================= TOP PANEL (Girişler) =================
        JPanel top = new JPanel();
        top.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        top.setBackground(new Color(245, 245, 245));

        JLabel budgetLabel = new JLabel("Bütçe Doluluk Oranı (%):");
        budgetLabel.setFont(titleFont);

        // Bütçe seçenekleri
        Integer[] percents = {50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
        JComboBox<Integer> budgetBox = new JComboBox<>(percents);
        budgetBox.setSelectedIndex(4); // Varsayılan %70
        budgetBox.setFont(textFont);

        JButton runButton = new JButton("Algoritmaları Çalıştır");
        runButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        runButton.setBackground(new Color(0, 102, 204));
        runButton.setForeground(Color.WHITE);

        JButton greedyDetails = new JButton("Greedy için Seçilen Departmanlar");
        JButton dpDetails = new JButton("DP için Seçilen Departmanlar");

        top.add(budgetLabel);
        top.add(budgetBox);
        top.add(Box.createHorizontalStrut(20)); // Boşluk
        top.add(runButton);
        top.add(Box.createHorizontalStrut(20));
        top.add(greedyDetails);
        top.add(dpDetails);

        add(top, BorderLayout.NORTH);

        // ================= CENTER PANEL (Metin Çıktıları) =================
        greedyArea = new JTextArea();
        dpArea = new JTextArea();
        greedyArea.setFont(monoFont);
        dpArea.setFont(monoFont);
        greedyArea.setEditable(false);
        dpArea.setEditable(false);
        greedyArea.setMargin(new Insets(10, 10, 10, 10));
        dpArea.setMargin(new Insets(10, 10, 10, 10));

        // Kenarlıklar
        greedyArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE), "Greedy (Hızlı) Sonuçları"));
        dpArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.RED), "DP (Optimum) Sonuçları"));

        JScrollPane greedyScroll = new JScrollPane(greedyArea);
        JScrollPane dpScroll = new JScrollPane(dpArea);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, greedyScroll, dpScroll);
        centerSplit.setDividerLocation(550);
        centerSplit.setResizeWeight(0.5);
        add(centerSplit, BorderLayout.CENTER);

        // ================= BOTTOM PANEL (Grafikler) =================
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setPreferredSize(new Dimension(1100, 250));

        valueCostChart = new ValueCostBarChart();
        valueCostChart.setBorder(BorderFactory.createEtchedBorder());

        timeChart = new TimeBarChart();
        timeChart.setBorder(BorderFactory.createEtchedBorder());

        bottomPanel.add(valueCostChart);
        bottomPanel.add(timeChart);
        add(bottomPanel, BorderLayout.SOUTH);

        runButton.addActionListener(e -> {
            try {
                List<Department> departments = CSVReader.loadDepartments("data/cleanedWalmartTopStore.csv");

                if (departments == null || departments.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veri okunamadı! 'data/cleanedWalmartTopStore.csv' dosyasını kontrol edin.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Güvenlik Stoğu ve Bütçe Havuzu Hesabı
                int minStockQty = 2;
                long mandatoryCost = 0;
                long mandatoryValue = 0;
                long singleItemTotalCost = 0; // Algoritmanın seçebileceği havuz

                for (Department d : departments) {
                    mandatoryCost += (d.cost * minStockQty);
                    mandatoryValue += (d.value * minStockQty);
                    singleItemTotalCost += d.cost;
                }

                // Bütçe Havuzu: Zorunlu Stok + (Tüm ürünlerden 1 adet alacak para)
                long totalPoolCost = mandatoryCost + singleItemTotalCost;

                int percent = (Integer) budgetBox.getSelectedItem();

                long totalBudgetLong = (totalPoolCost * percent) / 100;
                int totalBudget = (int) totalBudgetLong;

                // Algoritmaya Kalan Bütçe
                int budgetForAlgorithm = (int) (totalBudget - mandatoryCost);

                if (budgetForAlgorithm < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Bütçe Yetersiz! (%"+percent+")\nZorunlu Stok İçin Gereken: " + mandatoryCost + "\nMevcut: " + totalBudget,
                            "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                // GREEDY calıstır
                long gStart = System.nanoTime();
                List<Department> greedyExtra = GreedySolver.solve(departments, budgetForAlgorithm);
                long gEnd = System.nanoTime();

                // DP calıstır
                long dStart = System.nanoTime();
                List<Department> dpExtra = DPSolver.solve(departments, budgetForAlgorithm);
                long dEnd = System.nanoTime();

                // Sonucları sakla
                lastGreedy = greedyExtra;
                lastDP = dpExtra;

                // Toplamları hesapla
                Result gRes = calculateTotal(greedyExtra, (int)mandatoryCost, (int)mandatoryValue);
                Result dRes = calculateTotal(dpExtra, (int)mandatoryCost, (int)mandatoryValue);

                double gTime = (gEnd - gStart) / 1_000_000.0;
                double dTime = (dEnd - dStart) / 1_000_000.0;

                int greedyAlgoSpent = gRes.extraCostOnly;
                int greedyUnused = budgetForAlgorithm - greedyAlgoSpent; // Greedy ne kadar kaldı?

                int dpAlgoSpent = dRes.extraCostOnly;
                int dpUnused = budgetForAlgorithm - dpAlgoSpent; // DP ne kadar kaldı?

                // --- GREEDY TEXT ---
                StringBuilder sbGreedy = new StringBuilder();
                sbGreedy.append("--- SONUÇLAR (%" + percent + ") ---\n");
                sbGreedy.append("Zorunlu Harcama: " + mandatoryCost + "\n");
                sbGreedy.append("Algoritmaya Ayrılan: " + budgetForAlgorithm + "\n");
                sbGreedy.append("--------------------------------\n");
                sbGreedy.append("ALGORİTMA: Greedy (Hızlı)\n");
                sbGreedy.append("Ekstra Seçilen: " + greedyExtra.size() + " ürün\n");
                sbGreedy.append("Algoritma Harcaması: " + greedyAlgoSpent + "\n");
                sbGreedy.append("Kalan (Artan) Para: " + greedyUnused + "\n");
                sbGreedy.append("--------------------------------\n");
                sbGreedy.append("SİSTEM GENELİ TOPLAM:\n");
                sbGreedy.append("TOPLAM HARCANAN: " + gRes.totalCost + "\n");
                sbGreedy.append("TOPLAM DEĞER: " + gRes.totalValue + "\n");
                sbGreedy.append("Süre: " + String.format("%.3f ms", gTime));

                greedyArea.setText(sbGreedy.toString());

                // --- DP TEXT ---
                StringBuilder sbDP = new StringBuilder();
                sbDP.append("--- SONUÇLAR (%" + percent + ") ---\n");
                sbDP.append("Zorunlu Harcama: " + mandatoryCost + "\n");
                sbDP.append("Algoritmaya Ayrılan: " + budgetForAlgorithm + "\n");
                sbDP.append("--------------------------------\n");
                sbDP.append("ALGORİTMA: DP (Optimum)\n");
                sbDP.append("Ekstra Seçilen: " + dpExtra.size() + " ürün\n");
                sbDP.append("Algoritma Harcaması: " + dpAlgoSpent + "\n");
                sbDP.append("Kalan (Artan) Para: " + dpUnused + "\n"); // <-- DOĞRU DEĞER
                sbDP.append("--------------------------------\n");
                sbDP.append("SİSTEM GENELİ TOPLAM:\n");
                sbDP.append("TOPLAM HARCANAN: " + dRes.totalCost + "\n");
                sbDP.append("TOPLAM DEĞER: " + dRes.totalValue + "\n");
                sbDP.append("Süre: " + String.format("%.3f ms", dTime));

                dpArea.setText(sbDP.toString());

                // Grafikleri Güncelle
                valueCostChart.setValues(gRes.totalValue, dRes.totalValue, gRes.totalCost, dRes.totalCost);
                timeChart.setTimes(gTime, dTime);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });

        // Detay Butonları
        greedyDetails.addActionListener(e -> showDetails(lastGreedy, "Greedy"));
        dpDetails.addActionListener(e -> showDetails(lastDP, "DP"));
    }

    private void showDetails(List<Department> list, String title) {
        if (list != null)
            new DepartmentTableDialog(this, title, list).setVisible(true);
        else
            JOptionPane.showMessageDialog(this, "Lütfen önce algoritmaları çalıştırın.");
    }

    private Result calculateTotal(List<Department> extras, int baseCost, int baseValue) {
        int cost = 0;
        int val = 0;
        for (Department d : extras) {
            cost += d.cost;
            val += d.value;
        }
        // Result: Toplam Cost, Toplam Value, Sadece Algoritma Cost
        return new Result(baseCost + cost, baseValue + val, cost);
    }

    private static class Result {
        int totalCost, totalValue, extraCostOnly;
        Result(int tc, int tv, int ec) {
            totalCost = tc;
            totalValue = tv;
            extraCostOnly = ec;
        }
    }
}