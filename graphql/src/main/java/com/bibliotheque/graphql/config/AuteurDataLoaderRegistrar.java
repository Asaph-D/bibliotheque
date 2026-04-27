package com.bibliotheque.graphql.config;

import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;
import org.springframework.graphql.execution.DataLoaderRegistrar;
import org.springframework.stereotype.Component;

import com.bibliotheque.graphql.auteur.dataloaders.AuteurDataLoader;

import graphql.GraphQLContext;

@Component
public class AuteurDataLoaderRegistrar implements DataLoaderRegistrar {

    private final AuteurDataLoader auteurDataLoader;

    public AuteurDataLoaderRegistrar(AuteurDataLoader auteurDataLoader) {
        this.auteurDataLoader = auteurDataLoader;
    }

    @Override
    public void registerDataLoaders(DataLoaderRegistry registry, GraphQLContext graphQLContext) {
        registry.register("auteurs", DataLoaderFactory.newDataLoader(auteurDataLoader));
    }
}

