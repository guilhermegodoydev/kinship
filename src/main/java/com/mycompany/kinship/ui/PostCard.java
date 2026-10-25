package com.mycompany.kinship.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Image;

public class PostCard extends JPanel {

    private static final Color TEXTO_SUAVE = new Color(150, 150, 150);
    private static final int LARGURA_TEXTO = 400;
    private static final ImageIcon ICONE_CURTIR = carregarIcone("/icons/curtir.png");
    private static final ImageIcon ICONE_COMENTAR = carregarIcone("/icons/comentar.png");
    public static final float ALINHAMENTO_X = Component.CENTER_ALIGNMENT;
    
    public PostCard(String autor, String tempo, String texto, int curtidas, int comentarios, String comentarioDestaque) {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setAlignmentX(ALINHAMENTO_X);

        add(criarTopo(autor, tempo), BorderLayout.NORTH);
        add(criarTexto(texto), BorderLayout.CENTER);
        add(criarRodape(curtidas, comentarios, comentarioDestaque), BorderLayout.SOUTH);
    }

    private static ImageIcon carregarIcone(String caminho) {
        Image img = new ImageIcon(PostCard.class.getResource(caminho))
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    private JPanel criarTopo(String autor, String tempo) {
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topo.setOpaque(false);
        JPanel avatar = new JPanel();
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setBackground(new Color(255, 203, 153));
        JLabel lblTempo = new JLabel(tempo);
        lblTempo.setForeground(TEXTO_SUAVE);
        topo.add(avatar);
        topo.add(new JLabel(autor));
        topo.add(lblTempo);
        return topo;
    }

    private JLabel criarTexto(String texto) {
        String seguro = texto.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\n", "<br>");
        JLabel lbl = new JLabel("<html><body style='width:" + LARGURA_TEXTO + "px'>"
                + seguro + "</body></html>");
        lbl.setVerticalAlignment(SwingConstants.TOP);
        return lbl;
    }

    private JPanel criarRodape(int curtidas, int comentarios, String destaque) {
        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setOpaque(false);

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        linha.setOpaque(false);
        linha.add(contador(ICONE_CURTIR, curtidas));
        linha.add(contador(ICONE_COMENTAR, comentarios));
        rodape.add(linha);

        if (destaque != null) {
            JLabel lbl = new JLabel(destaque);
            lbl.setForeground(TEXTO_SUAVE);
            lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            rodape.add(lbl);
        }
        return rodape;
    }

    private JLabel contador(ImageIcon icone, int valor) {
        JLabel lbl = new JLabel(String.valueOf(valor), icone, SwingConstants.LEFT);
        lbl.setIconTextGap(6);
        lbl.setForeground(Tema.TEXTO_SUAVE);
        return lbl;
    }
}