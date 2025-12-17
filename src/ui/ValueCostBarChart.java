package ui;

import javax.swing.*;
import java.awt.*;

public class ValueCostBarChart extends JPanel {

    private double gValue, dValue;

    public void setValues(double gv, double dv, double gc, double dc) {
        gValue = gv;
        dValue = dv;
//        gCost = gc;
//        dCost = dc;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int leftMargin = 60;
        int rightMargin = 20;
        int topMargin = 20;
        int bottomMargin = 60;

        int chartHeight = getHeight() - topMargin - bottomMargin;
        int baseY = getHeight() - bottomMargin;

        // ===== AXES =====
        g2.setColor(Color.BLACK);
        g2.drawLine(leftMargin, topMargin, leftMargin, baseY);
        g2.drawLine(leftMargin, baseY, getWidth() - rightMargin, baseY);

        // ===== GRID =====
        g2.setColor(new Color(220, 220, 220));
        for (int i = 1; i <= 5; i++) {
            int y = topMargin + i * chartHeight / 5;
            g2.drawLine(leftMargin, y, getWidth() - rightMargin, y);
        }

        drawPair(g2, 120, chartHeight, baseY, gValue, dValue, "Toplam Değer");
        //drawPair(g2, 380, chartHeight, baseY, gCost, dCost, "Used Budget");
    }

    private void drawPair(Graphics2D g, int x, int chartHeight, int baseY,
                          double gv, double dv, String label) {

        double max = Math.max(gv, dv);
        if (max == 0) return;

        int barWidth = 50;
        int gap = 30;

        int gh = (int) (gv / max * chartHeight);
        int dh = (int) (dv / max * chartHeight);

        int gX = x;
        int dX = x + barWidth + gap;

        // Greedy
        g.setColor(Color.BLUE);
        g.fillRect(gX, baseY - gh, barWidth, gh);

        // DP
        g.setColor(Color.RED);
        g.fillRect(dX, baseY - dh, barWidth, dh);

        // Values above bars
        g.setColor(Color.BLACK);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));

        String gvText = String.format("%.2f", gv);
        String dvText = String.format("%.2f", dv);

        g.drawString(
                gvText,
                gX + barWidth / 2 - g.getFontMetrics().stringWidth(gvText) / 2,
                baseY - gh - 5
        );

        g.drawString(
                dvText,
                dX + barWidth / 2 - g.getFontMetrics().stringWidth(dvText) / 2,
                baseY - dh - 5
        );

        // Labels
        g.drawString("Greedy", gX + 3, baseY + 15);
        g.drawString("DP", dX + 15, baseY + 15);

        // Category label
        g.drawString(label, x + 15, getHeight() - 10);
    }
}