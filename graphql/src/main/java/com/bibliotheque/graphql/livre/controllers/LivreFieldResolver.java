package com.bibliotheque.graphql.livre.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.bibliotheque.graphql.emprunt.entities.Emprunt;
import com.bibliotheque.graphql.emprunt.repositories.EmpruntRepository;
import com.bibliotheque.graphql.livre.entities.Livre;

@Controller
public class LivreFieldResolver {

    @Autowired
    private EmpruntRepository empruntRepository;

    // Résoudre le champ `empruntsActifs` de Livre
    @SchemaMapping(typeName = "Livre", field = "empruntsActifs")
    public List<Emprunt> empruntsActifs(Livre livre) {
        return empruntRepository.findByLivreIdAndDateRetourEffectiveIsNull(livre.getId());
    }
}
