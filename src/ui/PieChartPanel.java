package ui;

import javax.swing.*;
import java.awt.*;

public class PieChartPanel extends JPanel {

    private long gMandatory, gBase, gAlgo, gUnused;
    private long dMandatory, dBase, dAlgo, dUnused;

    public void setDualBudgetValues(long mandatory, long base,
                                    long gAlgo, long gUnused,
                                    long dAlgo, long dUnused) {
        this.gMandatory = mandatory; this.dMandatory = mandatory;
        this.gBase = base;           this.dBase = base;
        this.gAlgo = gAlgo;          this.gUnused = gUnused;
        this.dAlgo = dAlgo;          this.dUnused = dUnused;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        long totalG = gMandatory + gBase + gAlgo + gUnused;

        if (totalG <= 0) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            String msg = "Hesaplama Bekleniyor...";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (getWidth()-fm.stringWidth(msg))/2, getHeight()/2);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int chartW = w / 2;
        int diameter = Math.min(chartW, h) - 60;

        // Sol: greedy
        int x1 = (chartW - diameter) / 2;
        int y = (h - diameter) / 2 + 10;
        drawPie(g2, x1, y, diameter, gMandatory, gBase, gAlgo, gUnused, totalG, "Greedy Bütçe", true);

        // Sag: DP
        int x2 = chartW + (chartW - diameter) / 2;
        drawPie(g2, x2, y, diameter, dMandatory, dBase, dAlgo, dUnused, totalG, "DP Bütçe", false);

        drawCommonLegend(g2, h);
    }

    private void drawPie(Graphics2D g2, int x, int y, int d, long mand, long base, long algo, long unused, long total, String title, boolean isGreedy) {
        // Acı hesabı (min gorunurluk var)
        int aMand = calculateAngle(mand, total);
        int aBase = calculateAngle(base, total);
        int aAlgo = calculateAngle(algo, total);

        // Kalan para acısı
        int aUnused = 360 - aMand - aBase - aAlgo;
        // Matematiksel hatadan dolayı eksiye duserse sıfırla
        if (aUnused < 0) aUnused = 0;

        int current = 90; // baslangıc acısı

        // Renkler
        Color cMand = new Color(255, 69, 0);   // Kırmızı
        Color cBase = new Color(0, 70, 140);   // Koyu Mavi
        Color cAlgo = isGreedy ? new Color(255, 204, 0) : new Color(102, 0, 153); // Sarı veya Mor
        Color cUnused = Color.LIGHT_GRAY;

        current = drawSlice(g2, x, y, d, current, aMand, cMand, total, mand);
        current = drawSlice(g2, x, y, d, current, aBase, cBase, total, base);
        current = drawSlice(g2, x, y, d, current, aAlgo, cAlgo, total, algo);
        current = drawSlice(g2, x, y, d, current, aUnused, cUnused, total, unused);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, x + (d - fm.stringWidth(title))/2, y - 10);
    }

    // Eğer deger > 0 ise en az 2 derece
    private int calculateAngle(long value, long total) {
        if (value == 0) return 0;
        int angle = (int) (360.0 * value / total);
        if (angle == 0) return 2; // Cok kucukse en az 2 (gorunurluk icin)
        return angle;
    }

    private int drawSlice(Graphics2D g2, int x, int y, int d, int start, int arc, Color c, long total, long val) {
        if (arc <= 0) return start;
        g2.setColor(c);
        g2.fillArc(x, y, d, d, start, arc);

        // Yuzde yazdirma (sadece 3ten buyuk ise yaz)
        if (val > 0) {
            double p = (double) val / total * 100.0;
            if (p > 3.0) {
                double rad = Math.toRadians(start + arc / 2.0);
                int r = d / 2;
                int tx = (int) ((x + r) + (r * 0.6) * Math.cos(rad));
                int ty = (int) ((y + r) - (r * 0.6) * Math.sin(rad));

                g2.setColor(Color.WHITE);
                if (c == Color.LIGHT_GRAY || c == new Color(255, 204, 0)) g2.setColor(Color.BLACK);

                g2.setFont(new Font("Arial", Font.BOLD, 10));
                String s = String.format("%.0f%%", p);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, tx - fm.stringWidth(s)/2, ty + 4);
            }
        }
        return start + arc;
    }

    private void drawCommonLegend(Graphics2D g2, int h) {
        int y = h - 30;
        int startX = getWidth()/2 - 160;

        drawLegendItem(g2, startX, y, new Color(255, 69, 0), "Zorunlu");
        drawLegendItem(g2, startX + 70, y, new Color(0, 70, 140), "Temel Pkt");
        drawLegendItem(g2, startX + 150, y, new Color(255, 204, 0), "Greedy");
        drawLegendItem(g2, startX + 220, y, new Color(102, 0, 153), "DP");
        drawLegendItem(g2, startX + 270, y, Color.LIGHT_GRAY, "Artan");
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color c, String text) {
        g2.setColor(c);
        g2.fillRect(x, y, 10, 10);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString(text, x + 13, y + 9);
    }
}