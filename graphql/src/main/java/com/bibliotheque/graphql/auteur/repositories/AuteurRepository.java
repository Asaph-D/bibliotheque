package com.bibliotheque.graphql.auteur.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.graphql.auteur.entities.Auteur;

@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {
    Optional<Auteur> findByNom(String nom);
    
    List<Auteur> findByNomContainingIgnoreCase(String nom);
}
