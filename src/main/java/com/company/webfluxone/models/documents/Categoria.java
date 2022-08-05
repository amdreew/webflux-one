package com.company.webfluxone.models.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "categorias")
public class Categoria {
    @Id
    @NotEmpty
    private String id;
    private String nombre;
}
