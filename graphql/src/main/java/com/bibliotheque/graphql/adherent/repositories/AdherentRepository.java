package com.bibliotheque.graphql.adherent.repositories;

import com.bibliotheque.graphql.adherent.entities.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, Long> {
    Optional<Adherent> findByEmail(String email);
}
