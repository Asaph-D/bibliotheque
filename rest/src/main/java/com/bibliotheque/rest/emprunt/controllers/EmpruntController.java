package com.bibliotheque.rest.emprunt.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotheque.rest.emprunt.entities.Emprunt;
import com.bibliotheque.rest.emprunt.repositories.EmpruntRepository;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    private final EmpruntRepository empruntRepository;

    public EmpruntController(EmpruntRepository empruntRepository) {
        this.empruntRepository = empruntRepository;
    }

    /**
     * Endpoint de lecture pour la comparaison REST vs GraphQL.
     *
     * Exemples:
     * - /api/emprunts?actifs=true
     * - /api/emprunts?livreId=4&actifs=true
     * - /api/emprunts?adherentId=1
     */
    @GetMapping
    public List<Emprunt> lister(
            @RequestParam(required = false) Long livreId,
            @RequestParam(required = false) Long adherentId,
            @RequestParam(required = false, defaultValue = "false") boolean actifs) {

        if (actifs) {
            List<Emprunt> actifsList = empruntRepository.findByDateRetourEffectiveIsNull();
            return actifsList.stream()
                    .filter(e -> livreId == null || (e.getLivre() != null && livreId.equals(e.getLivre().getId())))
                    .filter(e -> adherentId == null || (e.getAdherent() != null && adherentId.equals(e.getAdherent().getId())))
                    .toList();
        }

        if (livreId != null) {
            return empruntRepository.findByLivreId(livreId);
        }
        if (adherentId != null) {
            return empruntRepository.findByAdherentId(adherentId);
        }
        return empruntRepository.findAll();
    }
}

