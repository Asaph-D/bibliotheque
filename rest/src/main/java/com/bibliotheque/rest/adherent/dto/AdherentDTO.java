package com.bibliotheque.rest.adherent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdherentDTO {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
}
