package com.bibliotheque.graphql.livre.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import com.bibliotheque.graphql.emprunt.entities.Emprunt;
import com.bibliotheque.graphql.emprunt.services.EmpruntService;
import com.bibliotheque.graphql.livre.dto.Genre;
import com.bibliotheque.graphql.livre.entities.Livre;
import com.bibliotheque.graphql.livre.services.LivreService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Controller
public class LivreController {

    @Autowired
    private LivreService livreService;

    @Autowired
    private EmpruntService empruntService;

    // === QUERIES ===
    @QueryMapping
    public List<Livre> livres(
            @Argument String titre,
            @Argument Genre genre,
            @Argument Boolean disponible) {

        return livreService.rechercher(titre, genre, disponible);
    }

    @QueryMapping
    public Optional<Livre> livre(@Argument Long id) {
        return livreService.findById(id);
    }

    // === MUTATION ===
    @MutationMapping
    public Emprunt emprunterLivre(
            @Argument Long livreId,
            @Argument Long adherentId) {

        return empruntService.emprunter(livreId, adherentId);
    }

    @MutationMapping
    public Emprunt retournerLivre(@Argument Long empruntId) {
        Emprunt retour = empruntService.retourner(empruntId);

        // Notifier les subscribers que le livre est disponible
        livreDisponibleSink.tryEmitNext(retour.getLivre());

        return retour;
    }

    // === SUBSCRIPTION ===
    private final Sinks.Many<Livre> livreDisponibleSink =
            Sinks.many().multicast().onBackpressureBuffer();

    @SubscriptionMapping
    public Flux<Livre> livreDisponible(@Argument Genre genre) {
        return livreDisponibleSink.asFlux()
                .filter(l -> genre == null || l.getGenre() == genre);
    }
}