package com.bibliotheque.rest.livre.entities;

import com.bibliotheque.rest.author.entities.Auteur;
import com.bibliotheque.rest.livre.dto.Genre;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "livres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class Livre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(unique = true, nullable = false)
    private String isbn;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;
    
    @Column(name = "annee_publication")
    private Integer anneePublication;
    
    @Column(nullable = false)
    private Boolean disponible = true;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Auteur auteur;
    
    public Long getAuteurId() {
        return auteur != null ? auteur.getId() : null;
    }
}

