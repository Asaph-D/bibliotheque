package com.bibliotheque.rest.emprunt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.rest.emprunt.entities.Emprunt;

import java.util.List;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByAdherentId(Long adherentId);
    
    List<Emprunt> findByLivreId(Long livreId);
    
    List<Emprunt> findByDateRetourEffectiveIsNull();
}
