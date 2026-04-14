package com.bibliotheque.rest.livre.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bibliotheque.rest.emprunt.entities.Emprunt;
import com.bibliotheque.rest.emprunt.services.EmpruntService;
import com.bibliotheque.rest.livre.dto.Genre;
import com.bibliotheque.rest.livre.entities.Livre;
import com.bibliotheque.rest.livre.services.LivreService;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    @Autowired
    private LivreService livreService;

    @Autowired
    private EmpruntService empruntService;

    @GetMapping
    public List<Livre> getLivres(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Boolean disponible) {

        return livreService.rechercher(titre, genre, disponible);
    }
    
    @GetMapping("/{id}")
    public Optional<Livre> getLivre(@PathVariable Long id) {
        return livreService.findById(id);
    }

    @PostMapping("/{livreId}/emprunter")
    public Emprunt emprunterLivre(
            @PathVariable Long livreId,
            @RequestParam Long adherentId) {

        return empruntService.emprunter(livreId, adherentId);
    }

    @PutMapping("/emprunts/{empruntId}/retourner")
    public Emprunt retournerLivre(@PathVariable Long empruntId) {
        return empruntService.retourner(empruntId);
    }
}
