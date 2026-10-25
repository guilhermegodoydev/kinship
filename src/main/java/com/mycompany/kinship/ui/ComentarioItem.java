package com.mycompany.kinship.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ComentarioItem extends JPanel {
    public static final int LARGURA_TEXTO = 160;

    private static final Color VERMELHO = new Color(224, 36, 94);
    private static final IconeCoracao CORACAO_VAZIO = new IconeCoracao(14, false, Tema.TEXTO_SUAVE);
    private static final IconeCoracao CORACAO_CHEIO = new IconeCoracao(14, true, VERMELHO);

    private final int codigoComentario;
    private final JLabel lblCoracao = new JLabel();
    private final JLabel lblTotal = new JLabel();
    private boolean ocupado = false;

    public ComentarioItem(int codigoComentario, String nickname, String tempo, String texto,
                          int curtidas, boolean curtido, Consumer<ComentarioItem> aoCurtir) {
        this.codigoComentario = codigoComentario;

        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);

        JPanel colunaAvatar = new JPanel(new BorderLayout());
        colunaAvatar.setOpaque(false);
        colunaAvatar.add(new AvatarCircular(32, nickname), BorderLayout.NORTH);
        add(colunaAvatar, BorderLayout.WEST);

        JLabel lblNick = new JLabel(nickname);
        lblNick.setFont(lblNick.getFont().deriveFont(Font.BOLD));
        JLabel lblTempo = new JLabel(tempo);
        lblTempo.setForeground(Tema.TEXTO_SUAVE);

        JPanel linha = new JPanel();
        linha.setLayout(new BoxLayout(linha, BoxLayout.X_AXIS));
        linha.setOpaque(false);
        linha.setAlignmentX(LEFT_ALIGNMENT);
        linha.add(lblNick);
        linha.add(Box.createHorizontalStrut(6));
        linha.add(lblTempo);

        JLabel lblTexto = new JLabel(Formatacao.textoQuebrado(texto, LARGURA_TEXTO));
        lblTexto.setVerticalAlignment(SwingConstants.TOP);
        lblTexto.setAlignmentX(LEFT_ALIGNMENT);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);
        centro.add(linha);
        centro.add(lblTexto);
        add(centro, BorderLayout.CENTER);

        lblCoracao.setAlignmentX(CENTER_ALIGNMENT);
        lblTotal.setAlignmentX(CENTER_ALIGNMENT);
        lblTotal.setFont(lblTotal.getFont().deriveFont(11f));
        lblCoracao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblTotal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel direita = new JPanel();
        direita.setLayout(new BoxLayout(direita, BoxLayout.Y_AXIS));
        direita.setOpaque(false);
        direita.add(lblCoracao);
        direita.add(lblTotal);

        JPanel colunaDireita = new JPanel(new BorderLayout());
        colunaDireita.setOpaque(false);
        colunaDireita.add(direita, BorderLayout.NORTH);
        add(colunaDireita, BorderLayout.EAST);

        MouseAdapter clique = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (aoCurtir != null && !ocupado) {
                    aoCurtir.accept(ComentarioItem.this);
                }
            }
        };
        lblCoracao.addMouseListener(clique);
        lblTotal.addMouseListener(clique);

        atualizarCurtida(curtido, curtidas);
    }

    public void atualizarCurtida(boolean curtido, int total) {
        lblCoracao.setIcon(curtido ? CORACAO_CHEIO : CORACAO_VAZIO);
        lblTotal.setText(String.valueOf(total));
        lblTotal.setForeground(curtido ? VERMELHO : Tema.TEXTO_SUAVE);
    }

    public void definirOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public int getCodigoComentario() {
        return codigoComentario;
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}