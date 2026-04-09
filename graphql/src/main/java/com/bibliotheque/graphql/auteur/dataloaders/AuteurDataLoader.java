package com.bibliotheque.graphql.auteur.dataloaders;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bibliotheque.graphql.auteur.entities.Auteur;
import com.bibliotheque.graphql.auteur.repositories.AuteurRepository;

@Component
public class AuteurDataLoader implements BatchLoaderWithContext<Long, Auteur> {
    
    @Autowired
    private AuteurRepository auteurRepository;
    
    @Override
    public CompletionStage<List<Auteur>> load(List<Long> auteurIds, BatchLoaderEnvironment env) {
        // UNE SEULE requête SQL pour tous les auteurs demandés
        // au lieu de N requêtes individuelles
        List<Auteur> auteurs = auteurRepository.findAllById(auteurIds);
        
        // Retourner dans le même ordre que les IDs demandés
        Map<Long, Auteur> map = auteurs.stream()
                .collect(Collectors.toMap(Auteur::getId, a -> a));
        
        return CompletableFuture.completedFuture(
                auteurIds.stream().map(map::get).toList()
        );
    }
}
