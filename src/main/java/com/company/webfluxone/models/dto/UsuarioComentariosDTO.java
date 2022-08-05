package com.company.webfluxone.models.dto;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class UsuarioComentariosDTO {
    private UsuarioDTO usuario;

    private ComentariosDTO comentarios;
}
