package com.bibliotheque.rest.author.entities;

import java.util.List;

import com.bibliotheque.rest.livre.entities.Livre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auteurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auteur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column
    private String nationalite;
    
    @OneToMany(mappedBy = "auteur", fetch = FetchType.LAZY)
    private List<Livre> livres;
}
