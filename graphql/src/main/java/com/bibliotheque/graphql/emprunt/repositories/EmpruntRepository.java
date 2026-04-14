package com.bibliotheque.graphql.emprunt.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.graphql.emprunt.entities.Emprunt;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByAdherentId(Long adherentId);
    
    List<Emprunt> findByLivreId(Long livreId);
    
    List<Emprunt> findByDateRetourEffectiveIsNull();

    List<Emprunt> findByLivreIdAndDateRetourEffectiveIsNull(Long livreId);
}
