package com.bibliotheque.rest.emprunt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotheque.rest.api.ConflictException;
import com.bibliotheque.rest.api.NotFoundException;
import com.bibliotheque.rest.adherent.entities.Adherent;
import com.bibliotheque.rest.adherent.repositories.AdherentRepository;
import com.bibliotheque.rest.emprunt.entities.Emprunt;
import com.bibliotheque.rest.emprunt.repositories.EmpruntRepository;
import com.bibliotheque.rest.livre.entities.Livre;
import com.bibliotheque.rest.livre.repositories.LivreRepository;
import com.bibliotheque.rest.livre.services.LivreService;

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
                .orElseThrow(() -> new NotFoundException("Livre non trouvé"));
        
        Adherent adherent = adherentRepository.findById(adherentId)
                .orElseThrow(() -> new NotFoundException("Adhérent non trouvé"));
        
        if (!livre.getDisponible()) {
            throw new ConflictException("Le livre n'est pas disponible");
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
                .orElseThrow(() -> new NotFoundException("Emprunt non trouvé"));
        
        emprunt.setDateRetourEffective(LocalDate.now());
        livreService.markAsAvailable(emprunt.getLivre().getId());
        
        return empruntRepository.save(emprunt);
    }
}
