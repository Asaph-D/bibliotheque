package com.bibliotheque.graphql.config;

import org.dataloader.DataLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import com.bibliotheque.graphql.auteur.dataloaders.AuteurDataLoader;
import com.bibliotheque.graphql.livre.entities.Livre;

import graphql.schema.DataFetcher;

@Configuration
public class GraphqlConfiguration {
    
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(AuteurDataLoader auteurDataLoader) {
        return builder -> builder.type("Livre", typeWiring ->
                typeWiring.dataFetcher("auteur", auteurDataFetcher())
        );
    }
    
    private DataFetcher<Object> auteurDataFetcher() {
        return environment -> {
            DataLoader<Long, Object> loader = environment.getDataLoader("auteurs");
            Livre livre = environment.getSource();
            // Fallback: si le DataLoader n'est pas enregistré, utiliser la relation JPA directement.
            if (loader == null) {
                return livre.getAuteur();
            }
            Long auteurId = livre.getAuteurId();
            return loader.load(auteurId); // batch automatique
        };
    }
}
