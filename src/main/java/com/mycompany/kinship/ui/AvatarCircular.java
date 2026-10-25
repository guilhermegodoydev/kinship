package com.mycompany.kinship.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class AvatarCircular extends JComponent {

    private final int tamanho;
    private final String inicial;
    private BufferedImage recorte;

    public AvatarCircular(int tamanho, String nome) {
        this.tamanho = tamanho;
        this.inicial = (nome == null || nome.isBlank()) ? "?" : nome.trim().substring(0, 1).toUpperCase();
        Dimension d = new Dimension(tamanho, tamanho);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    public void setImagem(BufferedImage original) {
        if (original == null) {
            recorte = null;
        } else {
            double escala = Math.max((double) tamanho / original.getWidth(),
                                     (double) tamanho / original.getHeight());
            int w = (int) Math.round(original.getWidth() * escala);
            int h = (int) Math.round(original.getHeight() * escala);
            recorte = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = recorte.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(original, (tamanho - w) / 2, (tamanho - h) / 2, w, h, null);
            g2.dispose();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape circulo = new Ellipse2D.Double(0, 0, tamanho, tamanho);

        if (recorte != null) {
            g2.setPaint(new TexturePaint(recorte, new Rectangle2D.Double(0, 0, tamanho, tamanho)));
            g2.fill(circulo);
        } else {
            g2.setColor(Tema.LARANJA);
            g2.fill(circulo);
            g2.setColor(new Color(90, 60, 30));
            g2.setFont(getFont().deriveFont(Font.BOLD, tamanho * 0.45f));
            FontMetrics fm = g2.getFontMetrics();
            int x = (tamanho - fm.stringWidth(inicial)) / 2;
            int y = (tamanho - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(inicial, x, y);
        }
        g2.dispose();
    }
}