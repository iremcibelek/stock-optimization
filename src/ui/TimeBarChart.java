package ui;

import javax.swing.*;
import java.awt.*;

public class TimeBarChart extends JPanel {

    private double gTime, dTime;

    public void setTimes(double g, double d) {
        gTime = g;
        dTime = d;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int leftMargin = 60;
        int bottomMargin = 60;
        int topMargin = 20;

        int chartHeight = getHeight() - topMargin - bottomMargin;

        // eksenler
        g2.setColor(Color.BLACK);

        // Y ekseni
        g2.drawLine(leftMargin, topMargin, leftMargin, getHeight() - bottomMargin);

        // X ekseni
        g2.drawLine(leftMargin,
                getHeight() - bottomMargin,
                getWidth() - 20,
                getHeight() - bottomMargin);

        g2.setColor(new Color(220, 220, 220));
        for (int i = 1; i <= 5; i++) {
            int y = topMargin + i * chartHeight / 5;
            g2.drawLine(leftMargin, y, getWidth() - 20, y);
        }

        if (gTime <= 0 && dTime <= 0) return;

        double max = Math.max(gTime, dTime);

        int barWidth = 50;

        int greedyX = leftMargin + 40;
        int dpX = leftMargin + 140;

        int greedyHeight = (int) (gTime / max * chartHeight);
        int dpHeight = (int) (dTime / max * chartHeight);

        int baseY = getHeight() - bottomMargin;

        // ===== GREEDY BAR =====
        g2.setColor(Color.BLUE);
        g2.fillRect(
                greedyX,
                baseY - greedyHeight,
                barWidth,
                greedyHeight
        );

        // ===== DP BAR =====
        g2.setColor(Color.RED);
        g2.fillRect(
                dpX,
                baseY - dpHeight,
                barWidth,
                dpHeight
        );

        // ===== VALUES ABOVE BARS =====
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));

        String gText = String.format("%.3f ms", gTime);
        String dText = String.format("%.3f ms", dTime);

        g2.drawString(
                gText,
                greedyX + barWidth / 2 - g2.getFontMetrics().stringWidth(gText) / 2,
                baseY - greedyHeight - 5
        );

        g2.drawString(
                dText,
                dpX + barWidth / 2 - g2.getFontMetrics().stringWidth(dText) / 2,
                baseY - dpHeight - 5
        );

        // ===== LABELS =====
        g2.drawString("Greedy", greedyX + 3, baseY + 15);
        g2.drawString("DP", dpX + 15, baseY + 15);

        // ===== GRAPH TITLE (BOTTOM CENTER) =====
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        String title = "Yürütme Süresi (ms)";

        int titleWidth = g2.getFontMetrics().stringWidth(title);
        int x = (getWidth() - titleWidth) / 2;
        int y = getHeight() - 15;

        g2.drawString(title, x, y);

    }
}

