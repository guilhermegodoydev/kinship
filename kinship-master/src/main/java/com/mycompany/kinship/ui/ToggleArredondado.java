/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kinship.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JToggleButton;

/**
 *
 * @author gui
 */
public class ToggleArredondado extends JToggleButton {
    public ToggleArredondado(String texto) {
        super(texto);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        g2.setColor(isSelected() ? Tema.LARANJA : Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.setColor(Tema.LARANJA); 
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }
}
