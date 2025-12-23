package ui;

import javax.swing.*;
import java.awt.*;

public class TimeBarChart extends JPanel {
    private double greedyTime, dpTime;

    public void setTimes(double gTime, double dTime) {
        this.greedyTime = gTime;
        this.dpTime = dTime;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (greedyTime == 0 && dpTime == 0) return;

        int w = getWidth();
        int h = getHeight();
        int bottomMargin = 50;
        int maxH = h - bottomMargin - 40;
        int barWidth = w / 6;

        double maxT = Math.max(greedyTime, dpTime);
        if (maxT == 0) maxT = 1;

        // Aynı renkler
        Color colorGreedy = new Color(255, 204, 0);
        Color colorDP = new Color(102, 0, 153);

        int hG = (int) ((greedyTime * maxH) / maxT);
        int hD = (int) ((dpTime * maxH) / maxT);
        if (hG < 5 && greedyTime > 0) hG = 5;
        if (hD < 5 && dpTime > 0) hD = 5;

        // Greedy
        g.setColor(colorGreedy);
        g.fillRect(barWidth, h - bottomMargin - hG, barWidth, hG);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        String gLbl = "Greedy";
        String gVal = String.format("%.3f ms", greedyTime);
        g.drawString(gLbl, barWidth + (barWidth - fm.stringWidth(gLbl))/2, h - 25);
        g.drawString(gVal, barWidth + (barWidth - fm.stringWidth(gVal))/2, h - bottomMargin - hG - 5);

        // DP
        g.setColor(colorDP);
        g.fillRect(barWidth * 3, h - bottomMargin - hD, barWidth, hD);
        String dLbl = "DP";
        String dVal = String.format("%.3f ms", dpTime);
        g.drawString(dLbl, barWidth*3 + (barWidth - fm.stringWidth(dLbl))/2, h - 25);
        g.drawString(dVal, barWidth*3 + (barWidth - fm.stringWidth(dVal))/2, h - bottomMargin - hD - 5);

        g.setFont(new Font("Arial", Font.BOLD, 13));
        String title = "Çalışma Süresi";
        g.drawString(title, (w - fm.stringWidth(title))/2, 20);
    }
}