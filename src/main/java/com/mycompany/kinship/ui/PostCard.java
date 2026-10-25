
package com.mycompany.kinship.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
    private final JLabel lblComentarios = new JLabel();
    private final JLabel lblTexto = new JLabel();
    private boolean ocupado = false;

    public PostCard(int codigoPost, String autor, String tempo, String texto,
                    int curtidas, int comentarios, String comentarioDestaque,
                    boolean curtido, Acoes acoes) {
        this(codigoPost, autor, tempo, texto, curtidas, comentarios,
                comentarioDestaque, curtido, acoes, null);
    }

    public PostCard(int codigoPost, String autor, String tempo, String texto,
                    int curtidas, int comentarios, String comentarioDestaque,
                    boolean curtido, Acoes acoes, AcoesPerfil acoesPerfil) {
        this.codigoPost = codigoPost;

        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setAlignmentX(ALINHAMENTO_X);

        add(criarTopo(autor, tempo), BorderLayout.NORTH);
        add(criarTexto(texto), BorderLayout.CENTER);
        add(criarRodape(curtidas, comentarios, comentarioDestaque,
                curtido, acoes, acoesPerfil), BorderLayout.SOUTH);
    }

    public record Acoes(Consumer<PostCard> curtir, Consumer<PostCard> comentar) {}

    public record AcoesPerfil(
            Consumer<PostCard> editar,
            Consumer<PostCard> excluir
    ) {}

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
        lblTexto.setVerticalAlignment(SwingConstants.TOP);
        lblTexto.setText(formatarTexto(texto));
        return lblTexto;
    }

    private String formatarTexto(String texto) {
        String seguro = texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");

        return "<html><body style='width:" + LARGURA_TEXTO + "px'>"
                + seguro + "</body></html>";
    }

    public void atualizarTexto(String texto) {
        lblTexto.setText(formatarTexto(texto));
        revalidate();
        repaint();
    }

    private JPanel criarRodape(int curtidas, int comentarios, String destaque,
                               boolean curtido, Acoes acoes,
                               AcoesPerfil acoesPerfil) {
        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setOpaque(false);

        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        linha.setOpaque(false);
        linha.add(criarComentarios(comentarios, acoes));
        linha.add(criarCurtidas(curtidas, curtido, acoes));
        rodape.add(linha);

        if (destaque != null && !destaque.isEmpty()) {
            JLabel lbl = new JLabel(destaque);
            lbl.setForeground(TEXTO_SUAVE);
            lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            rodape.add(lbl);
        }

        if (acoesPerfil != null
                && (acoesPerfil.editar() != null || acoesPerfil.excluir() != null)) {
            JPanel linhaAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            linhaAcoes.setOpaque(false);

            if (acoesPerfil.editar() != null) {
                JButton btnEditar = criarBotaoAcao("Editar");
                btnEditar.addActionListener(e -> {
                    if (!ocupado) {
                        acoesPerfil.editar().accept(this);
                    }
                });
                linhaAcoes.add(btnEditar);
            }

            if (acoesPerfil.excluir() != null) {
                JButton btnExcluir = criarBotaoAcao("Excluir");
                btnExcluir.setForeground(new Color(190, 45, 45));
                btnExcluir.addActionListener(e -> {
                    if (!ocupado) {
                        acoesPerfil.excluir().accept(this);
                    }
                });
                linhaAcoes.add(btnExcluir);
            }

            rodape.add(linhaAcoes);
        }

        return rodape;
    }

    private JButton criarBotaoAcao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        botao.setContentAreaFilled(false);
        return botao;
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