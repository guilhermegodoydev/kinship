
package com.mycompany.kinship.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AreaTextoArredondada extends PainelArredondado {

    private final JTextArea campoTexto;
    private final JScrollPane scroll;

    public AreaTextoArredondada(int linhas, int colunas) {
        super();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        campoTexto = new JTextArea(linhas, colunas);
        campoTexto.setLineWrap(true);
        campoTexto.setWrapStyleWord(true);
        campoTexto.setOpaque(false);
        campoTexto.setForeground(Tema.TEXTO);
        campoTexto.setCaretColor(Tema.TEXTO);
        campoTexto.setMargin(new Insets(8, 10, 8, 10));

        scroll = new JScrollPane(campoTexto);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        add(scroll, BorderLayout.CENTER);
    }

    public String getText() {
        return campoTexto.getText();
    }

    public void setText(String texto) {
        campoTexto.setText(texto);
    }

    public JTextArea getCampoTexto() {
        return campoTexto;
    }

    public void definirTamanho(int largura, int altura) {
        Dimension tamanho = new Dimension(largura, altura);
        setPreferredSize(tamanho);
        setMinimumSize(tamanho);
        setMaximumSize(tamanho);
    }
}
