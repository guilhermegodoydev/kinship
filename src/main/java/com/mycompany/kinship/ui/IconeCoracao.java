package com.mycompany.kinship.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.Icon;

public class IconeCoracao implements Icon {

    private static final Path2D FORMA = criarForma();

    private final int tamanho;
    private final boolean preenchido;
    private final Color cor;

    public IconeCoracao(int tamanho, boolean preenchido, Color cor) {
        this.tamanho = tamanho;
        this.preenchido = preenchido;
        this.cor = cor;
    }

    private static Path2D criarForma() {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(12, 21.35);
        p.lineTo(10.55, 20.03);
        p.curveTo(5.4, 15.36, 2, 12.28, 2, 8.5);
        p.curveTo(2, 5.42, 4.42, 3, 7.5, 3);
        p.curveTo(9.24, 3, 10.91, 3.81, 12, 5.09);
        p.curveTo(13.09, 3.81, 14.76, 3, 16.5, 3);
        p.curveTo(19.58, 3, 22, 5.42, 22, 8.5);
        p.curveTo(22, 12.28, 18.6, 15.36, 13.45, 20.04);
        p.lineTo(12, 21.35);
        p.closePath();
        return p;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double escala = tamanho / 24.0;
            g2.scale(escala, escala);
            g2.setColor(cor);
            if (preenchido) {
                g2.fill(FORMA);
            } else {
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(FORMA);
            }
        } finally {
            g2.dispose();
        }
    }

    @Override public int getIconWidth() { return tamanho; }
    @Override public int getIconHeight() { return tamanho; }
}