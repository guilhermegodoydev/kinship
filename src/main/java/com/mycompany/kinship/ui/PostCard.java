package com.mycompany.kinship.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class PostCard extends PainelArredondado {

    private static final Color TEXTO_SUAVE = new Color(150, 150, 150);
    private static final int LARGURA_TEXTO = 400;
    private static final ImageIcon ICONE_COMENTAR = carregarIcone("/icons/comentar.png");
    public static final float ALINHAMENTO_X = Component.CENTER_ALIGNMENT;
    private static final Color VERMELHO_CURTIDA = new Color(224, 36, 94);
    private static final IconeCoracao CORACAO_VAZIO = new IconeCoracao(16, false, Tema.TEXTO_SUAVE);
    private static final IconeCoracao CORACAO_CHEIO = new IconeCoracao(16, true, VERMELHO_CURTIDA);

    private final int codigoPost;
    private final JLabel lblCurtidas = new JLabel();
    private boolean ocupado = false;
    
    private final JLabel lblComentarios = new JLabel();

    public PostCard(int codigoPost, String autor, String tempo, String texto,
                int curtidas, int comentarios, String comentarioDestaque,
                boolean curtido, Acoes acoes) 
    {
        this.codigoPost = codigoPost;
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setAlignmentX(ALINHAMENTO_X);

        add(criarTopo(autor, tempo), BorderLayout.NORTH);
        add(criarTexto(texto), BorderLayout.CENTER);
        add(criarRodape(curtidas, comentarios, comentarioDestaque, curtido, acoes), BorderLayout.SOUTH);
    }

    private JLabel criarComentarios(int total, Acoes acoes) {
        lblComentarios.setIcon(ICONE_COMENTAR);
        lblComentarios.setIconTextGap(6);
        lblComentarios.setForeground(Tema.TEXTO_SUAVE);
        lblComentarios.setText(String.valueOf(total));
        lblComentarios.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblComentarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (acoes != null && acoes.comentar() != null) {
                    acoes.comentar().accept(PostCard.this);
                }
            }
        });
        return lblComentarios;
    }

    public void atualizarComentarios(int total) {
        lblComentarios.setText(String.valueOf(total));
    }

    public record Acoes(Consumer<PostCard> curtir, Consumer<PostCard> comentar) {}
    
    private static ImageIcon carregarIcone(String caminho) {
        Image img = new ImageIcon(PostCard.class.getResource(caminho))
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    private JPanel criarTopo(String autor, String tempo) {
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topo.setOpaque(false);
        
        AvatarCircular avatar = new AvatarCircular(36, autor);
        
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

    private JPanel criarRodape(int curtidas, int comentarios, String destaque,
                           boolean curtido, Acoes acoes) 
    {
        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setOpaque(false);

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        linha.setOpaque(false);
        linha.add(criarComentarios(comentarios, acoes));
        linha.add(criarCurtidas(curtidas, curtido, acoes));
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
    
    private JLabel criarCurtidas(int total, boolean curtido, Acoes acoes) {
        lblCurtidas.setIconTextGap(6);
        lblCurtidas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCurtidas.addMouseListener(new MouseAdapter() {
        @Override
            public void mouseClicked(MouseEvent e) {
                if (acoes != null && acoes.curtir() != null && !ocupado) {
                    acoes.curtir().accept(PostCard.this);
                }
            }
        });
        atualizarCurtida(curtido, total);
        return lblCurtidas;
    }

    public void atualizarCurtida(boolean curtido, int total) {
        lblCurtidas.setIcon(curtido ? CORACAO_CHEIO : CORACAO_VAZIO);
        lblCurtidas.setForeground(curtido ? VERMELHO_CURTIDA : Tema.TEXTO_SUAVE);
        lblCurtidas.setText(String.valueOf(total));
    }

    public void definirOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public int getCodigoPost() {
        return codigoPost;
    }
}