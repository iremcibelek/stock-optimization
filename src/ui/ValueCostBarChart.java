package ui;

import javax.swing.*;
import java.awt.*;

public class ValueCostBarChart extends JPanel {
    private long greedyValue, dpValue, greedyCost, dpCost;

    public void setValues(long gVal, long dVal, long gCost, long dCost) {
        this.greedyValue = gVal;
        this.dpValue = dVal;
        this.greedyCost = gCost;
        this.dpCost = dCost;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (greedyValue == 0 && dpValue == 0) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String msg = "Veri Yok";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (getWidth()-fm.stringWidth(msg))/2, getHeight()/2);
            return;
        }

        int w = getWidth();
        int h = getHeight();
        int bottomMargin = 50;
        int maxH = h - bottomMargin - 40;
        int barWidth = w / 6;

        long maxVal = Math.max(greedyValue, dpValue);
        if (maxVal == 0) maxVal = 1;

        // Renkler aynı
        Color colorGreedy = new Color(255, 204, 0); // Sarı
        Color colorDP = new Color(102, 0, 153);     // Mor

        int hG = (int) ((greedyValue * maxH) / maxVal);
        int hD = (int) ((dpValue * maxH) / maxVal);
        if (hG < 5 && greedyValue > 0) hG = 5;
        if (hD < 5 && dpValue > 0) hD = 5;

        // Greedy bar
        g.setColor(colorGreedy);
        g.fillRect(barWidth, h - bottomMargin - hG, barWidth, hG);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        String gTxt = "Greedy";
        String gVal = String.valueOf(greedyValue);
        g.drawString(gTxt, barWidth + (barWidth - fm.stringWidth(gTxt))/2, h - 25);
        g.drawString(gVal, barWidth + (barWidth - fm.stringWidth(gVal))/2, h - bottomMargin - hG - 5);

        // DP bar
        g.setColor(colorDP);
        g.fillRect(barWidth * 3, h - bottomMargin - hD, barWidth, hD);
        String dTxt = "DP";
        String dVal = String.valueOf(dpValue);
        g.drawString(dTxt, barWidth*3 + (barWidth - fm.stringWidth(dTxt))/2, h - 25);
        g.drawString(dVal, barWidth*3 + (barWidth - fm.stringWidth(dVal))/2, h - bottomMargin - hD - 5);

        g.setFont(new Font("Arial", Font.BOLD, 13));
        String title = "Toplam Kâr (Değer)";
        g.drawString(title, (w - fm.stringWidth(title))/2, 20);
    }
}