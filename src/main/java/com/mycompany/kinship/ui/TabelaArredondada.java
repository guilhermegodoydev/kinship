package com.mycompany.kinship.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class TabelaArredondada extends JScrollPane {
    private static final int RAIO_CANTO = 16;
    private static final Color FUNDO_HEADER = new Color(255, 235, 204); 
    private static final Color TEXTO_HEADER = new Color(80, 80, 80);

    public TabelaArredondada() {
        super();
        configurarEstiloBasico();
    }

    public TabelaArredondada(JTable tabela) {
        super(tabela);
        configurarEstiloBasico();
        if (tabela != null) {
            configurarTabela(tabela);
        }
    }

    @Override
    public void setViewportView(Component view) {
        super.setViewportView(view);
        if (view instanceof JTable) {
            configurarTabela((JTable) view);
        }
    }

    private void configurarEstiloBasico() {
        setOpaque(false);
        getViewport().setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        JPanel canto = new JPanel();
        canto.setBackground(FUNDO_HEADER);
        setCorner(JScrollPane.UPPER_RIGHT_CORNER, canto);
    }

    private void configurarTabela(JTable tabela) {
        tabela.setOpaque(false);
        tabela.setShowGrid(false);
        tabela.setBackground(Color.WHITE);
        
        JTableHeader cabecalho = tabela.getTableHeader();
        if (cabecalho != null) {
            cabecalho.setOpaque(false);
            cabecalho.setBackground(FUNDO_HEADER);
            
            cabecalho.setDefaultRenderer(new TableCellRenderer() {
                private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
                
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    
                    JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
                    
                    label.setOpaque(true);
                    label.setBackground(FUNDO_HEADER);
                    label.setForeground(TEXTO_HEADER);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    
                    label.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5)); 
                    
                    return label;
                }
            });
        }
        
        if (tabela.getDefaultRenderer(Object.class) instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) tabela.getDefaultRenderer(Object.class)).setOpaque(false);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), RAIO_CANTO, RAIO_CANTO);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RAIO_CANTO, RAIO_CANTO));
        super.paint(g2);
        g2.dispose();

        Graphics2D g3 = (Graphics2D) g.create();
        g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g3.setColor(Tema.LARANJA);
        g3.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RAIO_CANTO, RAIO_CANTO);
        g3.dispose();
    }
}