package com.bibliotheque.graphql.emprunt.services;

import com.bibliotheque.graphql.emprunt.entities.Emprunt;
import com.bibliotheque.graphql.emprunt.repositories.EmpruntRepository;
import com.bibliotheque.graphql.livre.entities.Livre;
import com.bibliotheque.graphql.livre.repositories.LivreRepository;
import com.bibliotheque.graphql.livre.services.LivreService;
import com.bibliotheque.graphql.adherent.entities.Adherent;
import com.bibliotheque.graphql.adherent.repositories.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmpruntService {
    
    @Autowired
    private EmpruntRepository empruntRepository;
    
    @Autowired
    private LivreRepository livreRepository;
    
    @Autowired
    private LivreService livreService;
    
    @Autowired
    private AdherentRepository adherentRepository;
    
    public Emprunt emprunter(Long livreId, Long adherentId) {
        Livre livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        
        Adherent adherent = adherentRepository.findById(adherentId)
                .orElseThrow(() -> new RuntimeException("Adhérent non trouvé"));
        
        if (!livre.getDisponible()) {
            throw new RuntimeException("Le livre n'est pas disponible");
        }
        
        Emprunt emprunt = new Emprunt();
        emprunt.setLivre(livre);
        emprunt.setAdherent(adherent);
        emprunt.setDateEmprunt(LocalDate.now());
        emprunt.setDateRetourPrevue(LocalDate.now().plusDays(30));
        
        livreService.markAsUnavailable(livreId);
        
        return empruntRepository.save(emprunt);
    }
    
    public Emprunt retourner(Long empruntId) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));
        
        emprunt.setDateRetourEffective(LocalDate.now());
        livreService.markAsAvailable(emprunt.getLivre().getId());
        
        return empruntRepository.save(emprunt);
    }
}
