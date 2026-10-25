package com.mycompany.kinship.services;

import com.mycompany.kinship.models.Post;
import java.util.List;

public record ResultadoOrdenacao(List<Post> posts, long nanos, long comparacoes, long trocas) {}