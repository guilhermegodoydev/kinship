package com.mycompany.kinship;

import com.mycompany.kinship.models.Post;
import com.mycompany.kinship.ui.Tema;
import com.mycompany.kinship.ui.ToggleArredondado;
import com.mycompany.kinship.ui.PostCard;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;
import com.mycompany.kinship.models.Comentario;
import com.mycompany.kinship.daos.ComentarioDAO;
import com.mycompany.kinship.ui.Formatacao;
import com.mycompany.kinship.algoritmos.Algoritmo;
import com.mycompany.kinship.services.ComentarioService;
import com.mycompany.kinship.services.Criterio;
import com.mycompany.kinship.services.CurtidaService;
import com.mycompany.kinship.services.FeedService;
import com.mycompany.kinship.services.ResultadoOrdenacao;
import com.mycompany.kinship.services.Sessao;
import com.mycompany.kinship.services.ValidacaoException;
import com.mycompany.kinship.ui.ComentarioItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Locale;
import javax.swing.Box;
import java.text.NumberFormat;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JTextField;
import com.mycompany.kinship.ui.CampoTextoArredondado;
import com.mycompany.kinship.ui.BotaoArredondado;

public class TelaFeed extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaFeed.class.getName());

    private static final int TAMANHO_PAGINA = 10;
    private static final int LIMITE_EXIBICAO = 200;
    private final ComentarioDAO comentarioDAO = new ComentarioDAO();
    private int geracaoAtual = 0;
    private List<Post> listaOrdenada = List.of();
    private int proximoIndice = 0;
    private boolean carregando = false;
    private static final Locale BR = Locale.of("pt", "BR");
    private final FeedService feedService = new FeedService();
    private List<Post> postsOriginais = List.of();
    private int totalNoBanco = 0;
    private JToggleButton[] controles;
    private final CurtidaService curtidaService = new CurtidaService();
    private Map<Integer, Post> postsPorCodigo = Map.of();
    private record DadosPagina(Map<Integer, Comentario> destaques, Set<Integer> curtidos) {}
    private final ComentarioService comentarioService = new ComentarioService();
    private final PostCard.Acoes acoesDosCards = new PostCard.Acoes(this::aoClicarEmCurtir, this::aoClicarEmComentar);
    private JPanel painelComentarios;
    private Post postAberto = null;
    private PostCard cardAberto = null;
    private int deslocamentoComentarios = 0;
    private boolean carregandoComentarios = false;
    private boolean temMaisComentarios = false;
    private int geracaoComentarios = 0;
    private static final int LARGURA_MENU_COMENTARIOS = 340;
    
    public TelaFeed() {
        initComponents();
        configurarFeed();
        configurarComentarios();
        configurarSelecao();
        carregarFeed();
    }

    record OpcaoQuantidade(int valor, boolean todos) {
        @Override public String toString() {
            String n = NumberFormat.getIntegerInstance(Locale.of("pt", "BR")).format(valor);
            return todos ? "Todos (" + n + ")" : n;
        }
    }
    
    private void configurarSelecao() {
        toggleMaisCurtidas.setActionCommand("CURTIDAS");
        toggleMaisComentado.setActionCommand("COMENTARIOS");
        toggleRecentes.setActionCommand("DATA");
        toggleBubbleSort.setActionCommand("BUBBLE_SORT");
        toggleInsertionSort.setActionCommand("INSERTION_SORT");
        toggleMergeSort.setActionCommand("MERGE_SORT");
        toggleQuickSort.setActionCommand("QUICK_SORT");

        controles = new JToggleButton[]{toggleMaisCurtidas, toggleMaisComentado, toggleRecentes,
                toggleBubbleSort, toggleInsertionSort, toggleMergeSort, toggleQuickSort};

        ActionListener aoMudar = e -> ordenarEExibir();
        for (JToggleButton b : controles) {
            b.addActionListener(aoMudar);
        }

        toggleMaisCurtidas.setSelected(true);   // critério padrão
        toggleMergeSort.setSelected(true);      // algoritmo padrão
    }

    private void definirControlesHabilitados(boolean habilitado) {
        for (JToggleButton b : controles) {
            b.setEnabled(habilitado);
        }
    }

    private void carregarFeed() {
        definirControlesHabilitados(false);
        lblOrdenando.setText("Carregando posts...");

        new SwingWorker<FeedService.Carga, Void>() {
            @Override
            protected FeedService.Carga doInBackground() {
                return feedService.carregar();
            }

            @Override
            protected void done() {
                try {
                    FeedService.Carga carga = get();
                    postsOriginais = carga.posts();
                    postsPorCodigo = postsOriginais.stream().collect(Collectors.toMap(Post::getCodigo, p -> p));
                    totalNoBanco = carga.totalNoBanco();
                    ordenarEExibir();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    lblOrdenando.setText("Não foi possível carregar o feed.");
                }
            }
        }.execute();
    }

    private void ordenarEExibir() {
        if (postsOriginais.isEmpty()) {
            listaOrdenada = List.of();
            mostrarPrimeiraPagina();
            lblOrdenando.setText("Nenhum post cadastrado ainda.");
            definirControlesHabilitados(true);
            return;
        }

        Criterio criterio = Criterio.valueOf(btnGrupoFiltros.getSelection().getActionCommand());
        Algoritmo algoritmo = Algoritmo.valueOf(btnGrupoAlgoritmos.getSelection().getActionCommand());

        definirControlesHabilitados(false);
        lblOrdenando.setText("Ordenando...");

        new SwingWorker<ResultadoOrdenacao, Void>() {
            @Override
            protected ResultadoOrdenacao doInBackground() {
                return feedService.ordenar(postsOriginais, criterio, algoritmo);
            }

            @Override
            protected void done() {
                try {
                    ResultadoOrdenacao r = get();
                    listaOrdenada = r.posts();

                    lblOrdenando.setText(String.format(BR, "%,d de %,d posts ordenados",
                            postsOriginais.size(), totalNoBanco));
                    lblTempo.setText(String.format(BR, "Tempo: %.2f ms", r.nanos() / 1_000_000.0));
                    lblComparacoes.setText(String.format(BR, "Comparações: %,d", r.comparacoes()));
                    lblTrocas.setText(String.format(BR, "Trocas: %,d", r.trocas()));

                    mostrarPrimeiraPagina();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    lblOrdenando.setText("Erro ao ordenar os posts.");
                } finally {
                    definirControlesHabilitados(true);
                }
            }
        }.execute();
    }
    
    private void configurarFeed() {
        painelPosts.setPreferredSize(null);
        painelPosts.setMinimumSize(null);
        painelPosts.setMaximumSize(null);

        JScrollBar barra = scrollPostagens.getVerticalScrollBar();
        barra.setUnitIncrement(24);
        barra.addAdjustmentListener(e -> {
            if (barra.getValue() + barra.getVisibleAmount() >= barra.getMaximum() - 150) {
                carregarProximaPagina();
            }
        });
        
        painelPosts.setOpaque(true);
        painelPosts.setBackground(Tema.FUNDO);
        scrollPostagens.getViewport().setOpaque(true);
        scrollPostagens.getViewport().setBackground(Tema.FUNDO);
    }
    
    private void mostrarPrimeiraPagina() {
        geracaoAtual++;
        carregando = false;
        proximoIndice = 0;
        painelPosts.removeAll();
        painelPosts.revalidate();
        painelPosts.repaint();
        scrollPostagens.getVerticalScrollBar().setValue(0);
        carregarProximaPagina();
    }
    
    private void carregarProximaPagina() {
        if (carregando || proximoIndice >= listaOrdenada.size()
                || proximoIndice >= LIMITE_EXIBICAO) return;
        carregando = true;

        final int geracao = geracaoAtual;
        final int fim = Math.min(
                Math.min(proximoIndice + TAMANHO_PAGINA, listaOrdenada.size()),
                LIMITE_EXIBICAO);
        final List<Post> fatia = new ArrayList<>(listaOrdenada.subList(proximoIndice, fim));

        new SwingWorker<DadosPagina, Void>() {
             @Override
            protected DadosPagina doInBackground() throws Exception {
                List<Integer> codigos = fatia.stream().map(Post::getCodigo).toList();
                Map<Integer, Comentario> destaques = comentarioDAO.buscarDestaquePorPosts(codigos);
                Set<Integer> curtidos = curtidaService.postsCurtidos(codigoUsuarioLogado(), codigos);
                return new DadosPagina(destaques, curtidos);
            }

            @Override
            protected void done() {
                if (geracao != geracaoAtual) return;

                DadosPagina dados;
                try {
                    dados = get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    dados = new DadosPagina(Map.of(), Set.of());
                }

                for (Post p : fatia) {
                    painelPosts.add(new PostCard(
                            p.getCodigo(),
                            p.getAutorNome(),
                            Formatacao.tempoRelativo(p.getDataPublicacao()),
                            p.getConteudo(),
                            p.getTotalCurtidas(),
                            p.getTotalComentarios(),
                            Formatacao.resumoComentario(dados.destaques().get(p.getCodigo())),
                            dados.curtidos().contains(p.getCodigo()),
                            acoesDosCards));
                    painelPosts.add(Box.createVerticalStrut(12));
                }
                
                proximoIndice = fim;
                boolean chegouAoFim = proximoIndice >= Math.min(listaOrdenada.size(), LIMITE_EXIBICAO);
                if (chegouAoFim) {
                    String texto = listaOrdenada.size() > LIMITE_EXIBICAO
                            ? String.format(BR, "Mostrando os primeiros %,d de %,d posts ordenados",
                                    LIMITE_EXIBICAO, listaOrdenada.size())
                            : "Você chegou ao fim do feed";
                    JLabel lblFim = new JLabel(texto, SwingConstants.CENTER);
                    lblFim.setForeground(Tema.TEXTO_SUAVE);
                    lblFim.setAlignmentX(PostCard.ALINHAMENTO_X);
                    lblFim.setBorder(BorderFactory.createEmptyBorder(8, 0, 24, 0));
                    painelPosts.add(lblFim);
                }
                painelPosts.revalidate();
                painelPosts.repaint();

                SwingUtilities.invokeLater(() -> {
                    carregando = false;
                    JViewport vp = scrollPostagens.getViewport();
                    if (vp.getViewSize().height <= vp.getHeight()) {
                        carregarProximaPagina();
                    }
                });
            }
        }.execute();
    }
    
    private void configurarComentarios() {
        Color fundo = Tema.FUNDO;

        Dimension atual = containerPainelComentarios.getPreferredSize();
        containerPainelComentarios.setPreferredSize(new Dimension(LARGURA_MENU_COMENTARIOS, atual.height));
        containerPainelComentarios.setMinimumSize(new Dimension(LARGURA_MENU_COMENTARIOS, 0));
        containerPainelComentarios.setMaximumSize(new Dimension(LARGURA_MENU_COMENTARIOS, Integer.MAX_VALUE));
        
        painelComentarios = new JPanel();
        painelComentarios.setLayout(new BoxLayout(painelComentarios, BoxLayout.Y_AXIS));
        painelComentarios.setBackground(fundo);
        painelComentarios.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        scrollComentarios.setViewportView(painelComentarios);
        scrollComentarios.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollComentarios.setBorder(null);
        scrollComentarios.getViewport().setOpaque(true);
        scrollComentarios.getViewport().setBackground(fundo);

        JScrollBar barra = scrollComentarios.getVerticalScrollBar();
        barra.setUnitIncrement(16);
        barra.addAdjustmentListener(e -> {
            if (barra.getValue() + barra.getVisibleAmount() >= barra.getMaximum() - 80) {
                carregarProximosComentarios();
            }
        });

        lblErroComentario.setForeground(new Color(192, 57, 43));
        lblErroComentario.setFont(lblErroComentario.getFont().deriveFont(Font.BOLD, 12f));
        lblErroComentario.setText(" ");

        btnEnviarComentario.addActionListener(e -> enviarComentario());
        txtComentario.addActionListener(e -> enviarComentario());

        mostrarSemSelecao();
        scrollComentarios.setPreferredSize(new Dimension(340, 300));
        
        JPanel linhaCampo = new JPanel(new BorderLayout(8, 0));
        linhaCampo.setOpaque(false);
        linhaCampo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaCampo.add(txtComentario, BorderLayout.CENTER);
        linhaCampo.add(btnEnviarComentario, BorderLayout.EAST);
        txtComentario.setHorizontalAlignment(JTextField.LEFT);

        painelRodapeComentarios.removeAll();
        painelRodapeComentarios.setLayout(new BoxLayout(painelRodapeComentarios, BoxLayout.Y_AXIS));
        lblErroComentario.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelRodapeComentarios.add(linhaCampo);
        painelRodapeComentarios.add(lblErroComentario);

        linhaCampo.setMaximumSize(new Dimension(Integer.MAX_VALUE, linhaCampo.getPreferredSize().height));
    }
    
    private void mostrarErroComentario(String mensagem) {
        lblErroComentario.setText(mensagem == null || mensagem.isBlank()
                ? " "
                : Formatacao.textoQuebrado(mensagem, LARGURA_MENU_COMENTARIOS - 32));
    }

    private void atualizarMenuLateral() {
        Container pai = containerScroll.getParent();
        if (pai != null) {
            pai.revalidate();
            pai.repaint();
        }
    }

    private void mostrarSemSelecao() {
        geracaoComentarios++;
        carregandoComentarios = false;
        postAberto = null;
        cardAberto = null;

        lblNomePost.setText("Nenhum post selecionado");
        lblDescricaoPost.setVisible(false);
        containerScroll.setVisible(false);
        painelRodapeComentarios.setVisible(false);
        atualizarMenuLateral();
    }

    private void aoClicarEmComentar(PostCard card) {
        Post post = postsPorCodigo.get(card.getCodigoPost());
        if (post == null) return;

        cardAberto = card;

        if (postAberto == post) {
            txtComentario.requestFocusInWindow();
            return;
        }

        postAberto = post;
        lblNomePost.setText("Comentários");
        lblDescricaoPost.setText(Formatacao.textoQuebrado(
            "Post de " + post.getAutorNome() + ": " + Formatacao.resumir(post.getConteudo(), 70),
            LARGURA_MENU_COMENTARIOS - 32));
        lblDescricaoPost.setVisible(true);
        containerScroll.setVisible(true);
        painelRodapeComentarios.setVisible(true);
        lblErroComentario.setText(" ");
        txtComentario.setText("");
        atualizarMenuLateral();

        recarregarComentarios();
        txtComentario.requestFocusInWindow();
    }

    private void recarregarComentarios() {
        geracaoComentarios++;
        carregandoComentarios = false;
        deslocamentoComentarios = 0;
        temMaisComentarios = true;
        painelComentarios.removeAll();
        painelComentarios.revalidate();
        painelComentarios.repaint();
        scrollComentarios.getVerticalScrollBar().setValue(0);
        carregarProximosComentarios();
    }

    private void carregarProximosComentarios() {
        if (postAberto == null || carregandoComentarios || !temMaisComentarios) return;
        carregandoComentarios = true;

        final int geracao = geracaoComentarios;
        final int codigoPost = postAberto.getCodigo();
        final int deslocamento = deslocamentoComentarios;
        final int usuario = codigoUsuarioLogado();

        new SwingWorker<ComentarioService.Pagina, Void>() {
            @Override
            protected ComentarioService.Pagina doInBackground() {
                return comentarioService.listarPagina(codigoPost, usuario, deslocamento);
            }

            @Override
            protected void done() {
                if (geracao != geracaoComentarios) return;

                try {
                    ComentarioService.Pagina pagina = get();

                    for (Comentario c : pagina.itens()) {
                        painelComentarios.add(new ComentarioItem(
                                c.getCodigo(),
                                c.getAutorNickname(),
                                Formatacao.tempoRelativo(c.getDataHora()),
                                c.getTexto(),
                                c.getTotalCurtidas(),
                                pagina.curtidos().contains(c.getCodigo()),
                                TelaFeed.this::aoClicarEmCurtirComentario));
                        painelComentarios.add(Box.createVerticalStrut(8));
                    }
                    deslocamentoComentarios += pagina.itens().size();
                    temMaisComentarios = pagina.temMais();

                    if (deslocamentoComentarios == 0) {
                        JLabel vazio = new JLabel("Sem comentários ainda. Seja o primeiro!");
                        vazio.setForeground(Tema.TEXTO_SUAVE);
                        vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
                        vazio.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
                        painelComentarios.add(vazio);
                    }

                    painelComentarios.revalidate();
                    painelComentarios.repaint();

                    SwingUtilities.invokeLater(() -> {
                        carregandoComentarios = false;
                        JViewport vp = scrollComentarios.getViewport();
                        if (temMaisComentarios && vp.getViewSize().height <= vp.getHeight()) {
                            carregarProximosComentarios();
                        }
                    });
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    carregandoComentarios = false;
                }
            }
        }.execute();
    }

    private void enviarComentario() {
        if (postAberto == null) return;

        final Post post = postAberto;
        final PostCard card = cardAberto;
        final int usuario = codigoUsuarioLogado();
        final String texto = txtComentario.getText();

        mostrarErroComentario("...");
        if (usuario == 0) {
            mostrarErroComentario("Faça login para comentar");
            return;
        }

        btnEnviarComentario.setEnabled(false);

        new SwingWorker<Comentario, Void>() {
            @Override
            protected Comentario doInBackground() throws Exception {
                return comentarioService.comentar(post.getCodigo(), usuario, texto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    txtComentario.setText("");
                    post.setTotalComentarios(post.getTotalComentarios() + 1);
                    if (card != null) {
                        card.atualizarComentarios(post.getTotalComentarios());
                    }
                    if (postAberto == post) {
                        recarregarComentarios();   // o novo comentário aparece no topo
                    }
                } catch (ExecutionException ex) {
                    if (ex.getCause() instanceof ValidacaoException ve) {
                        mostrarErroComentario("Comentário inválido");
                    } else {
                        ex.printStackTrace();
                        mostrarErroComentario("Não foi possível enviar. Tente novamente.");
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    btnEnviarComentario.setEnabled(true);
                }
            }
        }.execute();
    }

    private void aoClicarEmCurtirComentario(ComentarioItem item) {
        int usuario = codigoUsuarioLogado();
        if (usuario == 0) return;

        item.definirOcupado(true);

        new SwingWorker<CurtidaService.Resultado, Void>() {
            @Override
            protected CurtidaService.Resultado doInBackground() {
                return curtidaService.alternarComentario(item.getCodigoComentario(), usuario);
            }

            @Override
            protected void done() {
                try {
                    CurtidaService.Resultado r = get();
                    item.atualizarCurtida(r.curtido(), r.total());
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(TelaFeed.this,
                            "Não foi possível curtir o comentário. Tente novamente.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                } finally {
                    item.definirOcupado(false);
                }
            }
        }.execute();
    }
    
    private int codigoUsuarioLogado() {
        return Sessao.estaLogado() ? Sessao.getUsuarioLogado().getCodigo() : 0;
    }

    private void aoClicarEmCurtir(PostCard card) {
        int usuario = codigoUsuarioLogado();
        if (usuario == 0) return;

        card.definirOcupado(true);

        new SwingWorker<CurtidaService.Resultado, Void>() {
            @Override
            protected CurtidaService.Resultado doInBackground() {
                return curtidaService.alternar(card.getCodigoPost(), usuario);
            }

            @Override
            protected void done() {
                try {
                    CurtidaService.Resultado r = get();
                    card.atualizarCurtida(r.curtido(), r.total());

                    Post post = postsPorCodigo.get(card.getCodigoPost());
                    if (post != null) {
                        post.setTotalCurtidas(r.total());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(TelaFeed.this,
                            "Não foi possível curtir o post. Tente novamente.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                } finally {
                    card.definirOcupado(false);
                }
            }
        }.execute();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnGrupoFiltros = new javax.swing.ButtonGroup();
        btnGrupoAlgoritmos = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblPerfil = new javax.swing.JLabel();
        lblBenchmark = new javax.swing.JLabel();
        lblSair = new javax.swing.JLabel();
        panelContainerMenuLateral = new javax.swing.JPanel();
        panelMenuLateral = new javax.swing.JPanel();
        panelBotoesAlgoritmos = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 10), new java.awt.Dimension(5, 10));
        toggleBubbleSort = new ToggleArredondado("Bubble Sort")
        ;
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 10), new java.awt.Dimension(5, 10));
        toggleInsertionSort = new ToggleArredondado("Insertion Sort");
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 10), new java.awt.Dimension(5, 10));
        toggleMergeSort = new ToggleArredondado("Merge Sort");
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 10), new java.awt.Dimension(5, 10));
        toggleQuickSort = new ToggleArredondado("Quick Sort");
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 15), new java.awt.Dimension(5, 15));
        panelBotoesOrdenar = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 10), new java.awt.Dimension(5, 10));
        toggleMaisCurtidas = new ToggleArredondado("Mais Curtidas");
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 10), new java.awt.Dimension(5, 10));
        toggleMaisComentado = new ToggleArredondado("Mais recomendado");
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 10), new java.awt.Dimension(5, 10));
        toggleRecentes = new ToggleArredondado("Recentes");
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 15), new java.awt.Dimension(5, 15));
        panelLabelsResultados = new javax.swing.JPanel();
        lblTrocas = new javax.swing.JLabel();
        lblComparacoes = new javax.swing.JLabel();
        lblTempo = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        lblOrdenando = new javax.swing.JLabel();
        scrollPostagens = new javax.swing.JScrollPane();
        painelPosts = new javax.swing.JPanel();
        containerPainelComentarios = new javax.swing.JPanel();
        painelComentario = new javax.swing.JPanel();
        lblNomePost = new javax.swing.JLabel();
        lblDescricaoPost = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        containerScroll = new javax.swing.JPanel();
        scrollComentarios = new javax.swing.JScrollPane();
        painelRodapeComentarios = new javax.swing.JPanel();
        txtComentario = new CampoTextoArredondado();
        lblErroComentario = new javax.swing.JLabel();
        btnEnviarComentario = new BotaoArredondado("Publicar");
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Kinship - Feed");
        setExtendedState(6);
        setMinimumSize(new java.awt.Dimension(700, 530));
        setResizable(false);
        setSize(new java.awt.Dimension(700, 530));

        jPanel1.setBackground(Tema.FUNDO);
        jPanel1.setForeground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panelHeader.setBackground(new java.awt.Color(255, 204, 153));

        jPanel3.setBackground(new java.awt.Color(255, 204, 153));

        jLabel1.setForeground(Tema.LARANJA);
        jLabel1.setText("Feed");

        lblPerfil.setForeground(Tema.TEXTO);
        lblPerfil.setText("Perfil");
        lblPerfil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPerfilMouseClicked(evt);
            }
        });

        lblBenchmark.setForeground(Tema.TEXTO);
        lblBenchmark.setText("Benchmark");
        lblBenchmark.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblBenchmarkMouseClicked(evt);
            }
        });

        lblSair.setForeground(Tema.TEXTO);
        lblSair.setText("Sair");
        lblSair.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSairMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(lblPerfil)
                .addGap(18, 18, 18)
                .addComponent(lblBenchmark)
                .addGap(18, 18, 18)
                .addComponent(lblSair)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblPerfil)
                    .addComponent(lblBenchmark)
                    .addComponent(lblSair)))
        );

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(panelHeader, gridBagConstraints);

        panelContainerMenuLateral.setOpaque(false);
        panelContainerMenuLateral.setPreferredSize(new java.awt.Dimension(150, 338));
        panelContainerMenuLateral.setLayout(new java.awt.BorderLayout());

        panelMenuLateral.setBackground(Tema.FUNDO);
        panelMenuLateral.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 15));
        panelMenuLateral.setOpaque(false);
        panelMenuLateral.setLayout(new javax.swing.BoxLayout(panelMenuLateral, javax.swing.BoxLayout.Y_AXIS));

        panelBotoesAlgoritmos.setOpaque(false);
        panelBotoesAlgoritmos.setLayout(new javax.swing.BoxLayout(panelBotoesAlgoritmos, javax.swing.BoxLayout.Y_AXIS));

        jLabel5.setForeground(Tema.TEXTO);
        jLabel5.setText("Selecione o algoritmo:");
        panelBotoesAlgoritmos.add(jLabel5);
        panelBotoesAlgoritmos.add(filler7);

        btnGrupoAlgoritmos.add(toggleBubbleSort);
        toggleBubbleSort.setText("Bubble Sort");
        panelBotoesAlgoritmos.add(toggleBubbleSort);
        panelBotoesAlgoritmos.add(filler4);

        btnGrupoAlgoritmos.add(toggleInsertionSort);
        toggleInsertionSort.setText("Insertion Sort");
        panelBotoesAlgoritmos.add(toggleInsertionSort);
        panelBotoesAlgoritmos.add(filler6);

        btnGrupoAlgoritmos.add(toggleMergeSort);
        toggleMergeSort.setText("Merge Sort");
        panelBotoesAlgoritmos.add(toggleMergeSort);
        panelBotoesAlgoritmos.add(filler5);

        btnGrupoAlgoritmos.add(toggleQuickSort);
        toggleQuickSort.setText("Quick Sort");
        panelBotoesAlgoritmos.add(toggleQuickSort);

        panelMenuLateral.add(panelBotoesAlgoritmos);
        panelMenuLateral.add(filler9);

        panelBotoesOrdenar.setBackground(Tema.FUNDO);
        panelBotoesOrdenar.setOpaque(false);
        panelBotoesOrdenar.setLayout(new javax.swing.BoxLayout(panelBotoesOrdenar, javax.swing.BoxLayout.Y_AXIS));

        jLabel7.setForeground(Tema.TEXTO);
        jLabel7.setText("Ordenar por:");
        panelBotoesOrdenar.add(jLabel7);
        panelBotoesOrdenar.add(filler1);

        btnGrupoFiltros.add(toggleMaisCurtidas);
        toggleMaisCurtidas.setText("Mais Curtidas");
        panelBotoesOrdenar.add(toggleMaisCurtidas);
        panelBotoesOrdenar.add(filler2);

        btnGrupoFiltros.add(toggleMaisComentado);
        toggleMaisComentado.setText("Mais comentando");
        panelBotoesOrdenar.add(toggleMaisComentado);
        panelBotoesOrdenar.add(filler3);

        btnGrupoFiltros.add(toggleRecentes);
        toggleRecentes.setText("Recentes");
        panelBotoesOrdenar.add(toggleRecentes);

        panelMenuLateral.add(panelBotoesOrdenar);
        panelMenuLateral.add(filler8);

        panelLabelsResultados.setBackground(Tema.FUNDO);
        panelLabelsResultados.setOpaque(false);
        panelLabelsResultados.setLayout(new javax.swing.BoxLayout(panelLabelsResultados, javax.swing.BoxLayout.Y_AXIS));

        lblTrocas.setForeground(Tema.TEXTO_SUAVE);
        lblTrocas.setText("Trocas: 10.453");
        panelLabelsResultados.add(lblTrocas);

        lblComparacoes.setForeground(Tema.TEXTO_SUAVE);
        lblComparacoes.setText("Comparações: 124.380");
        panelLabelsResultados.add(lblComparacoes);

        lblTempo.setForeground(Tema.TEXTO_SUAVE);
        lblTempo.setText("Tempo: 38ms");
        panelLabelsResultados.add(lblTempo);

        panelMenuLateral.add(panelLabelsResultados);

        panelContainerMenuLateral.add(panelMenuLateral, java.awt.BorderLayout.WEST);

        jSeparator1.setForeground(new java.awt.Color(153, 153, 153));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        panelContainerMenuLateral.add(jSeparator1, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(panelContainerMenuLateral, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout(10, 0));

        lblOrdenando.setForeground(Tema.TEXTO);
        lblOrdenando.setText("50 posts ordenados por mais curtidos");
        lblOrdenando.setToolTipText("");
        jPanel2.add(lblOrdenando, java.awt.BorderLayout.NORTH);

        scrollPostagens.setBackground(Tema.FUNDO);
        scrollPostagens.setBorder(null);
        scrollPostagens.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPostagens.setToolTipText("");
        scrollPostagens.setOpaque(false);

        painelPosts.setBackground(Tema.FUNDO);
        painelPosts.setMaximumSize(new java.awt.Dimension(480, 32767));
        painelPosts.setMinimumSize(new java.awt.Dimension(480, 150));
        painelPosts.setOpaque(false);
        painelPosts.setPreferredSize(new java.awt.Dimension(480, 150));
        painelPosts.setLayout(new javax.swing.BoxLayout(painelPosts, javax.swing.BoxLayout.Y_AXIS));
        scrollPostagens.setViewportView(painelPosts);

        jPanel2.add(scrollPostagens, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel2, gridBagConstraints);

        containerPainelComentarios.setOpaque(false);
        containerPainelComentarios.setLayout(new java.awt.BorderLayout());

        painelComentario.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 0, 0));
        painelComentario.setOpaque(false);
        painelComentario.setLayout(new javax.swing.BoxLayout(painelComentario, javax.swing.BoxLayout.Y_AXIS));

        lblNomePost.setText("Nome do post");
        painelComentario.add(lblNomePost);

        lblDescricaoPost.setText("Descricao");
        painelComentario.add(lblDescricaoPost);
        painelComentario.add(jSeparator3);

        containerScroll.setOpaque(false);
        containerScroll.setPreferredSize(new java.awt.Dimension(340, 386));

        scrollComentarios.setOpaque(false);

        javax.swing.GroupLayout containerScrollLayout = new javax.swing.GroupLayout(containerScroll);
        containerScroll.setLayout(containerScrollLayout);
        containerScrollLayout.setHorizontalGroup(
            containerScrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollComentarios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
        );
        containerScrollLayout.setVerticalGroup(
            containerScrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollComentarios, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
        );

        painelComentario.add(containerScroll);

        painelRodapeComentarios.setOpaque(false);
        painelRodapeComentarios.setLayout(new javax.swing.BoxLayout(painelRodapeComentarios, javax.swing.BoxLayout.Y_AXIS));

        txtComentario.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtComentario.setToolTipText("");
        txtComentario.addActionListener(this::txtComentarioActionPerformed);
        painelRodapeComentarios.add(txtComentario);

        lblErroComentario.setPreferredSize(new java.awt.Dimension(71, 16));
        painelRodapeComentarios.add(lblErroComentario);

        btnEnviarComentario.setText("Publicar");
        btnEnviarComentario.setPreferredSize(new java.awt.Dimension(71, 23));
        painelRodapeComentarios.add(btnEnviarComentario);

        painelComentario.add(painelRodapeComentarios);

        containerPainelComentarios.add(painelComentario, java.awt.BorderLayout.CENTER);

        jSeparator2.setForeground(new java.awt.Color(153, 153, 153));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        containerPainelComentarios.add(jSeparator2, java.awt.BorderLayout.WEST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(containerPainelComentarios, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(716, 539));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblSairMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSairMouseClicked
        TelaLogin telaLogin = new TelaLogin();
        telaLogin.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblSairMouseClicked

    private void lblPerfilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPerfilMouseClicked
        TelaPerfil telaPerfil = new TelaPerfil();
        telaPerfil.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblPerfilMouseClicked

    private void lblBenchmarkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBenchmarkMouseClicked
       TelaBenchmark telaBench = new TelaBenchmark();
       telaBench.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_lblBenchmarkMouseClicked

    private void txtComentarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtComentarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComentarioActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaFeed().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviarComentario;
    private javax.swing.ButtonGroup btnGrupoAlgoritmos;
    private javax.swing.ButtonGroup btnGrupoFiltros;
    private javax.swing.JPanel containerPainelComentarios;
    private javax.swing.JPanel containerScroll;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblBenchmark;
    private javax.swing.JLabel lblComparacoes;
    private javax.swing.JLabel lblDescricaoPost;
    private javax.swing.JLabel lblErroComentario;
    private javax.swing.JLabel lblNomePost;
    private javax.swing.JLabel lblOrdenando;
    private javax.swing.JLabel lblPerfil;
    private javax.swing.JLabel lblSair;
    private javax.swing.JLabel lblTempo;
    private javax.swing.JLabel lblTrocas;
    private javax.swing.JPanel painelComentario;
    private javax.swing.JPanel painelPosts;
    private javax.swing.JPanel painelRodapeComentarios;
    private javax.swing.JPanel panelBotoesAlgoritmos;
    private javax.swing.JPanel panelBotoesOrdenar;
    private javax.swing.JPanel panelContainerMenuLateral;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelLabelsResultados;
    private javax.swing.JPanel panelMenuLateral;
    private javax.swing.JScrollPane scrollComentarios;
    private javax.swing.JScrollPane scrollPostagens;
    private javax.swing.JToggleButton toggleBubbleSort;
    private javax.swing.JToggleButton toggleInsertionSort;
    private javax.swing.JToggleButton toggleMaisComentado;
    private javax.swing.JToggleButton toggleMaisCurtidas;
    private javax.swing.JToggleButton toggleMergeSort;
    private javax.swing.JToggleButton toggleQuickSort;
    private javax.swing.JToggleButton toggleRecentes;
    private javax.swing.JTextField txtComentario;
    // End of variables declaration//GEN-END:variables
}
