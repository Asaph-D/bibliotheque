package com.bibliotheque.graphql.livre.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.graphql.livre.entities.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
    Optional<Livre> findByIsbn(String isbn);
    
    List<Livre> findByTitreContainingIgnoreCase(String titre);
    
    List<Livre> findByGenre(String genre);
    
    List<Livre> findByDisponible(Boolean disponible);
}
