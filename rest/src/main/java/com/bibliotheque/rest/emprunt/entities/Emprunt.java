package com.bibliotheque.rest.emprunt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.bibliotheque.rest.adherent.entities.Adherent;
import com.bibliotheque.rest.livre.entities.Livre;

@Entity
@Table(name = "emprunts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emprunt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adherent_id", nullable = false)
    private Adherent adherent;
    
    @Column(name = "date_emprunt", nullable = false)
    private LocalDate dateEmprunt;
    
    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDate dateRetourPrevue;
    
    @Column(name = "date_retour_effective")
    private LocalDate dateRetourEffective;
}
