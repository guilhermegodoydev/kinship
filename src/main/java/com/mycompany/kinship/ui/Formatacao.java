package com.mycompany.kinship.ui;

import com.mycompany.kinship.models.Comentario;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Formatacao {

    private Formatacao() {}

    public static String tempoRelativo(LocalDateTime data) {
        Duration d = Duration.between(data, LocalDateTime.now());
        if (d.toMinutes() < 1) return "agora";
        if (d.toMinutes() < 60) return "há " + d.toMinutes() + " min";
        if (d.toHours() < 24) return "há " + d.toHours() + "h";
        if (d.toDays() < 30) return "há " + d.toDays() + "d";
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String resumoComentario(Comentario c) {
        if (c == null) return null;
        String texto = c.getTexto().replace('\n', ' ');
        if (texto.length() > 80) texto = texto.substring(0, 77) + "...";
        int n = c.getTotalCurtidas();
        return c.getAutorNome() + ": \"" + texto + "\" (" + n
                + (n == 1 ? " curtida)" : " curtidas)");
    }
    
    public static String textoQuebrado(String texto, int largura) {
        String seguro = texto.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\n", "<br>");
        return "<html><body style='width:" + largura + "px'>" + seguro + "</body></html>";
    }

    public static String resumir(String texto, int max) {
        String linha = texto.replace('\n', ' ').trim();
        return linha.length() > max ? linha.substring(0, max - 3) + "..." : linha;
    }
}