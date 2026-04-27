package com.bibliotheque.rest.comparaison;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comparaison/requests")
public class ComparisonRequestsController {

    @GetMapping(value = "/rest", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> restRequests() throws IOException {
        // app lancée depuis le module rest -> request.json est à la racine de ce module
        Path p = Path.of("request.json");
        return ResponseEntity.ok(Files.readString(p, StandardCharsets.UTF_8));
    }

    @GetMapping(value = "/graphql", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> graphqlRequests() throws IOException {
        // fichier à la racine du module graphql
        Path p = Path.of("..", "graphql", "request.graphql");
        return ResponseEntity.ok(Files.readString(p, StandardCharsets.UTF_8));
    }
}

