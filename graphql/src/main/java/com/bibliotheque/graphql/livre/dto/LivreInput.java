package com.bibliotheque.graphql.livre.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivreInput {
    private String titre;
    private String isbn;
    private Genre genre;
    private Long auteurId;
    private Integer anneePublication;
}
