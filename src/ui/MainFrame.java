package ui;

import algorithm.DPSolver;
import algorithm.GreedySolver;
import model.Department;
import util.CSVReader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private JTextArea greedyArea;
    private JTextArea dpArea;
    private ValueCostBarChart valueCostChart;
    private TimeBarChart timeChart;
    private PieChartPanel pieChart;

    private List<Department> globalDepartments; // Ana veri listesi (Hafızada tutulur)
    private List<Department> lastMandatoryList;
    private List<Department> lastBaseList;
    private List<Department> lastGreedySelection;
    private List<Department> lastDPSelection;

    // Renkler
    public static final Color COL_GREEDY = new Color(255, 204, 0);
    public static final Color COL_DP = new Color(102, 0, 153);

    public MainFrame() {
        setTitle("Algoritmik Stok Optimizasyonu (Final)");
        setSize(1350, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Veriyi acılısta yukluyoruz
        globalDepartments = CSVReader.loadDepartments("data/cleanedWalmartTopStore.csv");
        if (globalDepartments == null) globalDepartments = new ArrayList<>();

        Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
        Font monoFont = new Font("Consolas", Font.PLAIN, 13);

        // Top Panel
        JPanel top = new JPanel();
        top.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        top.setBackground(new Color(245, 245, 245));

        JLabel budgetLabel = new JLabel("Bütçe Oranı (%):");
        budgetLabel.setFont(titleFont);

        Integer[] percents = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        JComboBox<Integer> budgetBox = new JComboBox<>(percents);
        budgetBox.setSelectedIndex(4);

        JButton stockManageButton = new JButton("Stok Durumu & Düzenle");
        stockManageButton.setBackground(new Color(255, 140, 0));
        stockManageButton.setForeground(Color.WHITE);
        stockManageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton runButton = new JButton("HESAPLA");
        runButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        runButton.setBackground(COL_DP);
        runButton.setForeground(Color.WHITE);

        JButton greedyDetails = new JButton("Greedy Detay");
        JButton dpDetails = new JButton("DP Detay");

        // Butonları ekle
        top.add(stockManageButton);
        top.add(Box.createHorizontalStrut(20));
        top.add(budgetLabel);
        top.add(budgetBox);
        top.add(Box.createHorizontalStrut(10));
        top.add(runButton);
        top.add(Box.createHorizontalStrut(20));
        top.add(greedyDetails);
        top.add(dpDetails);

        add(top, BorderLayout.NORTH);

        // Center Panel
        greedyArea = new JTextArea("Lütfen HESAPLA butonuna basın...");
        dpArea = new JTextArea("Lütfen HESAPLA butonuna basın...");
        greedyArea.setFont(monoFont);
        dpArea.setFont(monoFont);
        greedyArea.setEditable(false);
        dpArea.setEditable(false);
        greedyArea.setMargin(new Insets(10, 10, 10, 10));

        greedyArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COL_GREEDY, 2), "Greedy Raporu"));
        dpArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COL_DP, 2), "DP Raporu"));

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(greedyArea), new JScrollPane(dpArea));
        centerSplit.setDividerLocation(660);
        add(centerSplit, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setPreferredSize(new Dimension(1100, 280));

        valueCostChart = new ValueCostBarChart();
        valueCostChart.setBorder(BorderFactory.createEtchedBorder());
        timeChart = new TimeBarChart();
        timeChart.setBorder(BorderFactory.createEtchedBorder());
        pieChart = new PieChartPanel();
        pieChart.setBorder(BorderFactory.createEtchedBorder());

        // Acılıs temizligi
        valueCostChart.setValues(0, 0, 0, 0);
        timeChart.setTimes(0, 0);
        pieChart.setDualBudgetValues(0, 0, 0, 0, 0, 0);

        bottomPanel.add(valueCostChart);
        bottomPanel.add(timeChart);
        bottomPanel.add(pieChart);

        add(bottomPanel, BorderLayout.SOUTH);


        //  Stok yonetimi: mevcut listeyi kullanır
        stockManageButton.addActionListener(e -> {
            new StockManagerDialog(this, globalDepartments).setVisible(true);
        });

        //  Hesapla butonu: Mevcut listeyi kullanır (Dosyadan yüklemez)
        runButton.addActionListener(e -> {
            try {
                if (globalDepartments.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veri listesi boş!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                lastMandatoryList = new ArrayList<>();
                lastBaseList = new ArrayList<>();

                long staticTotalPoolCost = 0;
                for (Department d : globalDepartments) staticTotalPoolCost += (d.cost * 3L);

                int targetSafetyStock = 2;
                long requiredMandatoryCost = 0;
                for (Department d : globalDepartments) {
                    int needed = Math.max(0, targetSafetyStock - d.currentStock);
                    if (needed > 0) {
                        requiredMandatoryCost += (d.cost * needed);
                        for(int k=0; k<needed; k++) lastMandatoryList.add(d);
                    }
                }

                int percent = (Integer) budgetBox.getSelectedItem();
                long totalBudgetLong = (staticTotalPoolCost * percent) / 100;
                int totalBudget = (int) totalBudgetLong;

                List<Department> solverInputList = new ArrayList<>();
                int solverBudget = 0;
                boolean isEmergencyMode = false;

                long costPhase1_Mandatory = 0;
                long costPhase2_Base = 0;
                String basePackageStatus = "";

                if (totalBudget < requiredMandatoryCost) {
                    isEmergencyMode = true;
                    solverBudget = totalBudget;
                    lastMandatoryList.clear();
                    basePackageStatus = "İPTAL";
                    for (Department d : globalDepartments) {
                        int needed = Math.max(0, targetSafetyStock - d.currentStock);
                        for (int i = 0; i < needed; i++) solverInputList.add(d);
                    }
                    costPhase1_Mandatory = 0;
                } else {
                    isEmergencyMode = false;
                    costPhase1_Mandatory = requiredMandatoryCost;
                    int remainingBudget = (int) (totalBudget - requiredMandatoryCost);

                    List<Department> sortedForBase = new ArrayList<>(globalDepartments);
                    sortedForBase.sort((d1, d2) -> Double.compare((double)d2.value/d2.cost, (double)d1.value/d1.cost));

                    int itemsCount = 0;
                    for (Department d : sortedForBase) {
                        if (remainingBudget >= d.cost) {
                            remainingBudget -= d.cost;
                            costPhase2_Base += d.cost;
                            lastBaseList.add(d);
                            itemsCount++;
                        }
                    }

                    if (itemsCount == globalDepartments.size()) basePackageStatus = "ALINDI (Tam)";
                    else basePackageStatus = "KISMEN (" + itemsCount + "/" + globalDepartments.size() + ")";

                    int copiesToAdd = (itemsCount == globalDepartments.size()) ? 2 : 3;
                    for (Department d : globalDepartments) {
                        for (int i = 0; i < copiesToAdd; i++) solverInputList.add(d);
                    }
                    solverBudget = remainingBudget;
                }

                long gStart = System.nanoTime();
                lastGreedySelection = GreedySolver.solve(solverInputList, solverBudget);
                long gEnd = System.nanoTime();

                long dStart = System.nanoTime();
                lastDPSelection = DPSolver.solve(solverInputList, solverBudget);
                long dEnd = System.nanoTime();

                // Hesaplamalar
                long mandatoryValue = calculateListValue(lastMandatoryList);
                long baseValue = calculateListValue(lastBaseList);
                long commonValue = mandatoryValue + baseValue;

                long greedyAlgoCost = calculateListCost(lastGreedySelection);
                long totalGreedyCost = costPhase1_Mandatory + costPhase2_Base + greedyAlgoCost;
                long totalGreedyValue = commonValue + calculateListValue(lastGreedySelection);
                long greedyUnused = totalBudget - totalGreedyCost;

                long dpAlgoCost = calculateListCost(lastDPSelection);
                long totalDPCost = costPhase1_Mandatory + costPhase2_Base + dpAlgoCost;
                long totalDPValue = commonValue + calculateListValue(lastDPSelection);
                long dpUnused = totalBudget - totalDPCost;

                double gTime = (gEnd - gStart) / 1_000_000.0;
                double dTime = (dEnd - dStart) / 1_000_000.0;

                // Greedy Raporu
                StringBuilder sbG = new StringBuilder();
                sbG.append("=== BÜTÇE AKIŞI (Greedy) ===\n");
                sbG.append("BAŞLANGIÇ: " + totalBudget + "\n");
                sbG.append("-----------------------------\n");

                long currentBalanceG = totalBudget;
                if (isEmergencyMode) {
                    sbG.append("1. Zorunlu: BÜTÇE YETMEDİ\n   (Bakiye: " + currentBalanceG + ")\n");
                } else {
                    currentBalanceG -= costPhase1_Mandatory;
                    sbG.append("1. Zorunlu: -" + costPhase1_Mandatory + "\n");
                    sbG.append("   KALAN:    " + currentBalanceG + "\n");
                }

                if (!isEmergencyMode) {
                    currentBalanceG -= costPhase2_Base;
                    sbG.append("2. Temel Pkt: -" + costPhase2_Base + "\n");
                    sbG.append("   (" + basePackageStatus + ")\n");
                    sbG.append("   KALAN:    " + currentBalanceG + "\n");
                } else {
                    sbG.append("2. Temel Pkt: İPTAL\n");
                }

                currentBalanceG -= greedyAlgoCost;
                sbG.append("3. Greedy:   -" + greedyAlgoCost + " (" + lastGreedySelection.size() + " adet)\n");
                sbG.append("-----------------------------\n");
                sbG.append("SON BAKİYE: " + currentBalanceG + "\n\n");
                sbG.append("TOPLAM DEĞER: " + totalGreedyValue + "\n");
                sbG.append("Süre: " + String.format("%.3f ms", gTime));
                greedyArea.setText(sbG.toString());

                // DP raporu
                StringBuilder sbD = new StringBuilder();
                sbD.append("=== BÜTÇE AKIŞI (DP) ===\n");
                sbD.append("BAŞLANGIÇ: " + totalBudget + "\n");
                sbD.append("-----------------------------\n");

                long currentBalanceD = totalBudget;
                if (isEmergencyMode) {
                    sbD.append("1. Zorunlu: BÜTÇE YETMEDİ\n   (Bakiye: " + currentBalanceD + ")\n");
                } else {
                    currentBalanceD -= costPhase1_Mandatory;
                    sbD.append("1. Zorunlu: -" + costPhase1_Mandatory + "\n");
                    sbD.append("   KALAN:    " + currentBalanceD + "\n");
                }

                if (!isEmergencyMode) {
                    currentBalanceD -= costPhase2_Base;
                    sbD.append("2. Temel Pkt: -" + costPhase2_Base + "\n");
                    sbD.append("   (" + basePackageStatus + ")\n");
                    sbD.append("   KALAN:    " + currentBalanceD + "\n");
                } else {
                    sbD.append("2. Temel Pkt: İPTAL\n");
                }

                currentBalanceD -= dpAlgoCost;
                sbD.append("3. DP Seçimi: -" + dpAlgoCost + " (" + lastDPSelection.size() + " adet)\n");
                sbD.append("-----------------------------\n");
                sbD.append("SON BAKİYE: " + currentBalanceD + "\n\n");
                sbD.append("TOPLAM DEĞER: " + totalDPValue + "\n");
                sbD.append("Süre: " + String.format("%.3f ms", dTime));
                dpArea.setText(sbD.toString());

                // Grafikler
                valueCostChart.setValues(totalGreedyValue, totalDPValue, totalGreedyCost, totalDPCost);
                timeChart.setTimes(gTime, dTime);
                pieChart.setDualBudgetValues(
                        costPhase1_Mandatory,
                        costPhase2_Base,
                        greedyAlgoCost,
                        greedyUnused,
                        dpAlgoCost,
                        dpUnused
                );

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });

        greedyDetails.addActionListener(e -> showDetails(lastMandatoryList, lastBaseList, lastGreedySelection, "Greedy (Sarı)", true));
        dpDetails.addActionListener(e -> showDetails(lastMandatoryList, lastBaseList, lastDPSelection, "DP (Mor)", false));
    }

    private void showDetails(List<Department> mandatory, List<Department> base, List<Department> algo, String title, boolean isLeft) {
        if ((mandatory!=null && !mandatory.isEmpty()) || (base!=null && !base.isEmpty()) || (algo!=null && !algo.isEmpty())) {
            new DepartmentTableDialog(this, title, mandatory, base, algo, globalDepartments, isLeft).setVisible(true);
        } else JOptionPane.showMessageDialog(this, "Liste boş.");
    }
    private long calculateListCost(List<Department> list) { long c=0; if(list!=null) for(Department d:list)c+=d.cost; return c; }
    private long calculateListValue(List<Department> list) { long v=0; if(list!=null) for(Department d:list)v+=d.value; return v; }
}