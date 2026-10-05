package com.mycompany.kinship.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CampoTextoArredondado extends JTextField {
    public CampoTextoArredondado() {
        setOpaque(false);
        setBorder(new EmptyBorder(6, 12, 6, 12));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }
 
            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.setColor(Tema.LARANJA);
        g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();

        super.paintComponent(g);
    }
}