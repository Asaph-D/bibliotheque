package com.bibliotheque.graphql.livre.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bibliotheque.graphql.auteur.dto.AuteurDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivreDTO {
    private Long id;
    private String titre;
    private String isbn;
    private Genre genre;
    private Integer anneePublication;
    private Boolean disponible;
    private AuteurDTO auteur;
}
