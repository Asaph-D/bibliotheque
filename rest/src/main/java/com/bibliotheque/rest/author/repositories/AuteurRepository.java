package com.bibliotheque.rest.author.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.rest.author.entities.Auteur;


@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {
    Optional<Auteur> findByNom(String nom);
    
    List<Auteur> findByNomContainingIgnoreCase(String nom);
}

