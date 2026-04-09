package com.bibliotheque.graphql.emprunt.repositories;

import com.bibliotheque.graphql.emprunt.entities.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByAdherentId(Long adherentId);
    
    List<Emprunt> findByLivreId(Long livreId);
    
    List<Emprunt> findByDateRetourEffectiveIsNull();
}
