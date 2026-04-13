package com.bibliotheque.rest.emprunt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.bibliotheque.rest.adherent.dto.AdherentDTO;
import com.bibliotheque.rest.livre.dto.LivreDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpruntDTO {
    private Long id;
    private LivreDTO livre;
    private AdherentDTO adherent;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
}
