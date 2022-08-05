package com.company.webfluxone.models.dto;

import java.util.ArrayList;
import java.util.List;

public class ComentariosDTO {
    private List<String> comentarios;

    public ComentariosDTO() {
        this.comentarios = new ArrayList<>();
    }

    public void addComentario(String comentario) {
        this.comentarios.add(comentario);
    }

    @Override
    public String toString() {
        return "comentarios=" + comentarios;
    }
}
