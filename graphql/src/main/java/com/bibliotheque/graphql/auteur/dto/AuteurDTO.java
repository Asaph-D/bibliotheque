package com.bibliotheque.graphql.auteur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuteurDTO {
    private Long id;
    private String nom;
    private String nationalite;
}
