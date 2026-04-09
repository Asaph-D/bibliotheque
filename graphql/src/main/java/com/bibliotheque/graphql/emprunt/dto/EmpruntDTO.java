package com.bibliotheque.graphql.emprunt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bibliotheque.graphql.livre.dto.LivreDTO;
import com.bibliotheque.graphql.adherent.dto.AdherentDTO;
import java.time.LocalDate;

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
