package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PieChartPanel extends JPanel {
    private double greedyValue;
    private double dpValue;

    public PieChartPanel() {
    }

    public void setValues(double g, double d) {
        this.greedyValue = g;
        this.dpValue = d;
        this.repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Değerler 0 ise çizme
        if (this.greedyValue != 0.0 || this.dpValue != 0.0) {
            Graphics2D g2 = (Graphics2D)g;

            // Kenar yumuşatma (Anti-aliasing)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(this.getWidth(), this.getHeight()) - 40;
            int x = (this.getWidth() - size) / 2;
            int y = (this.getHeight() - size) / 2;

            double total = this.greedyValue + this.dpValue;

            // Açıları hesapla
            double greedyAngle = (this.greedyValue / total) * 360.0;
            double dpAngle = 360.0 - greedyAngle; // Kalanı DP olsun (tam 360 tamamlasın)

            // 1. Dilim: Greedy (Mavi)
            g2.setColor(Color.BLUE);
            g2.fillArc(x, y, size, size, 0, (int)Math.ceil(greedyAngle));

            // 2. Dilim: DP (Kırmızı)
            g2.setColor(Color.RED);
            // Greedy'nin bittiği yerden başla
            g2.fillArc(x, y, size, size, (int)Math.ceil(greedyAngle), (int)Math.ceil(dpAngle));

            // Etiketleri Çiz
            // Greedy Label
            this.drawLabel(g2, "Greedy", this.greedyValue, (this.greedyValue / total) * 100.0, greedyAngle / 2.0, x, y, size);

            // DP Label
            this.drawLabel(g2, "DP", this.dpValue, (this.dpValue / total) * 100.0, greedyAngle + (dpAngle / 2.0), x, y, size);

            this.drawLegend(g2);
        }
    }

    private void drawLabel(Graphics2D g2, String name, double value, double percent, double angle, int x, int y, int size) {
        double rad = Math.toRadians(angle);
        int cx = x + size / 2;
        int cy = y + size / 2;

        // Yazının merkeze uzaklığı
        int tx = (int)((double)cx + Math.cos(rad) * (double)size * 0.35);
        int ty = (int)((double)cy - Math.sin(rad) * (double)size * 0.35); // Y koordinatı ters (yukarı negatif)

        g2.setColor(Color.WHITE); // Yazı rengi beyaz olsun, pasta üstünde daha iyi okunur
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // İsim
        g2.drawString(name, tx - 20, ty - 5);

        // --- DEĞİŞİKLİK BURADA ---
        // Yüzdeyi virgülden sonra 3 basamak göster (%.3f)
        // Böylece %49.999 ile %50.001 arasındaki farkı görebilirsin.
        g2.drawString(String.format("(%.3f%%)", percent), tx - 20, ty + 10);
    }

    private void drawLegend(Graphics2D g2) {
        int x = 10;
        int y = 10;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Greedy Legend
        g2.setColor(Color.BLUE);
        g2.fillRect(x, y, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("Greedy", x + 15, y + 10);

        // DP Legend
        g2.setColor(Color.RED);
        g2.fillRect(x, y + 20, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("DP", x + 15, y + 30);
    }
}