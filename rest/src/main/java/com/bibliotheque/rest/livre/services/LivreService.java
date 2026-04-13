package com.bibliotheque.rest.livre.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotheque.rest.livre.dto.Genre;
import com.bibliotheque.rest.livre.entities.Livre;
import com.bibliotheque.rest.livre.repositories.LivreRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LivreService {
    
    @Autowired
    private LivreRepository livreRepository;
    
    public List<Livre> rechercher(String titre, Genre genre, Boolean disponible) {
        List<Livre> livres = livreRepository.findAll();
        
        if (titre != null) {
            livres = livres.stream()
                    .filter(l -> l.getTitre().toLowerCase().contains(titre.toLowerCase()))
                    .toList();
        }
        
        if (genre != null) {
            livres = livres.stream()
                    .filter(l -> l.getGenre() == genre)
                    .toList();
        }
        
        if (disponible != null) {
            livres = livres.stream()
                    .filter(l -> l.getDisponible().equals(disponible))
                    .toList();
        }
        
        return livres;
    }
    
    public Optional<Livre> findById(Long id) {
        return livreRepository.findById(id);
    }
    
    public Livre save(Livre livre) {
        return livreRepository.save(livre);
    }
    
    public void markAsUnavailable(Long livreId) {
        livreRepository.findById(livreId).ifPresent(livre -> {
            livre.setDisponible(false);
            livreRepository.save(livre);
        });
    }
    
    public void markAsAvailable(Long livreId) {
        livreRepository.findById(livreId).ifPresent(livre -> {
            livre.setDisponible(true);
            livreRepository.save(livre);
        });
    }
}
