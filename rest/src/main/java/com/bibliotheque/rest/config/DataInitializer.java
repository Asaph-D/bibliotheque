package com.bibliotheque.rest.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bibliotheque.rest.adherent.entities.Adherent;
import com.bibliotheque.rest.adherent.repositories.AdherentRepository;
import com.bibliotheque.rest.author.entities.Auteur;
import com.bibliotheque.rest.author.repositories.AuteurRepository;
import com.bibliotheque.rest.emprunt.entities.Emprunt;
import com.bibliotheque.rest.emprunt.repositories.EmpruntRepository;
import com.bibliotheque.rest.livre.dto.Genre;
import com.bibliotheque.rest.livre.entities.Livre;
import com.bibliotheque.rest.livre.repositories.LivreRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AuteurRepository auteurRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private EmpruntRepository empruntRepository;

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si les données existent déjà
        if (auteurRepository.count() == 0) {
            initializeAuthors();
            initializeBooks();
            initializeMembers();
            initializeLoans();
            System.out.println("✅ Données d'initialisation créées avec succès!");
        } else {
            System.out.println("ℹ️ Les données existent déjà, pas de réinitialisation.");
        }
    }

    private void initializeAuthors() {
        List<Auteur> auteurs = List.of(
            new Auteur(null, "Victor Hugo", "Français", null),
            new Auteur(null, "Jules Verne", "Français", null),
            new Auteur(null, "Agatha Christie", "Britannique", null),
            new Auteur(null, "Isaac Asimov", "Russe-Américain", null),
            new Auteur(null, "Albert Einstein", "Suisse-Allemand", null),
            new Auteur(null, "Yuval Noah Harari", "Israélien", null),
            new Auteur(null, "J.K. Rowling", "Britannique", null),
            new Auteur(null, "Stephen King", "Américain", null),
            new Auteur(null, "George R.R. Martin", "Américain", null),
            new Auteur(null, "J.R.R. Tolkien", "Britannique", null),
            new Auteur(null, "Arthur Conan Doyle", "Britannique", null),
            new Auteur(null, "Émile Zola", "Français", null),
            new Auteur(null, "Honoré de Balzac", "Français", null),
            new Auteur(null, "Alexandre Dumas", "Français", null),
            new Auteur(null, "Dante Alighieri", "Italien", null)
        );
        auteurRepository.saveAll(auteurs);
    }

    private void initializeBooks() {
        List<Auteur> auteurs = auteurRepository.findAll();
        
        List<Livre> livres = List.of(
            new Livre(null, "Les Misérables", "978-2-07-036491-8", Genre.FICTION, 1862, true, auteurs.get(0)),
            new Livre(null, "Notre-Dame de Paris", "978-2-07-036403-0", Genre.FICTION, 1831, true, auteurs.get(0)),
            new Livre(null, "Voyage au Centre de la Terre", "978-2-07-044742-4", Genre.FANTASY, 1864, true, auteurs.get(1)),
            new Livre(null, "Vingt Mille Lieues sous les Mers", "978-2-07-036492-5", Genre.FANTASY, 1870, true, auteurs.get(1)),
            new Livre(null, "Meurtre sur l'Orient Express", "978-2-07-036493-2", Genre.MYSTERY, 1934, true, auteurs.get(2)),
            new Livre(null, "Le Crime de l'Orient Express", "978-2-07-036494-9", Genre.MYSTERY, 1934, true, auteurs.get(2)),
            new Livre(null, "Fondation", "978-0-553-29438-0", Genre.FANTASY, 1951, true, auteurs.get(3)),
            new Livre(null, "Les Robots", "978-0-553-29438-1", Genre.FANTASY, 1950, true, auteurs.get(3)),
            new Livre(null, "Théorie de la Relativité", "978-0-486-42871-1", Genre.SCIENCE, 1916, true, auteurs.get(4)),
            new Livre(null, "Sapiens", "978-2-226-24675-3", Genre.HISTORY, 2011, true, auteurs.get(5)),
            new Livre(null, "Homo Deus", "978-2-226-39182-8", Genre.HISTORY, 2015, true, auteurs.get(5)),
            new Livre(null, "Le Seigneur des Anneaux", "978-2-07-044743-1", Genre.FICTION, 1954, true, auteurs.get(9)),
            new Livre(null, "Harry Potter à l'école des sorciers", "978-0-747-53269-9", Genre.FICTION, 1998, true, auteurs.get(6)),
            new Livre(null, "Ça", "978-0-451-16996-4", Genre.MYSTERY, 1986, true, auteurs.get(7)),
            new Livre(null, "Le Trône de Fer", "978-0-553-10359-8", Genre.FICTION, 1996, true, auteurs.get(8)),
            new Livre(null, "Sherlock Holmes - Étude en Rouge", "978-0-141-04408-9", Genre.MYSTERY, 1887, true, auteurs.get(10)),
            new Livre(null, "L'Assommoir", "978-2-07-036587-8", Genre.FICTION, 1877, true, auteurs.get(11)),
            new Livre(null, "Le Père Goriot", "978-2-07-036588-5", Genre.FICTION, 1835, true, auteurs.get(12)),
            new Livre(null, "Le Comte de Monte Cristo", "978-2-253-04567-0", Genre.FICTION, 1844, true, auteurs.get(13)),
            new Livre(null, "La Comédie Divine", "978-2-07-036589-2", Genre.HISTORY, 1320, true, auteurs.get(14)),
            new Livre(null, "Les Trois Mousquetaires", "978-2-017-74019-5", Genre.FICTION, 1844, true, auteurs.get(13)),
            new Livre(null, "Neuromancien", "978-0-441-56956-6", Genre.FANTASY, 1984, true, auteurs.get(7)),
            new Livre(null, "1984", "978-0-451-52493-5", Genre.FANTASY, 1949, true, auteurs.get(1)),
            new Livre(null, "Pauvre Bitos", "978-2-07-036590-8", Genre.MYSTERY, 1956, true, auteurs.get(11)),
            new Livre(null, "Cinq semaines en ballon", "978-2-253-06573-9", Genre.FANTASY, 1863, true, auteurs.get(1)),
            new Livre(null, "Les Mystères de Paris", "978-2-07-036591-5", Genre.MYSTERY, 1843, true, auteurs.get(13)),
            new Livre(null, "L'Étranger", "978-0-451-52494-2", Genre.FICTION, 1942, true, auteurs.get(12)),
            new Livre(null, "Le Temps Retrouvé", "978-0-141-04410-2", Genre.FICTION, 1927, true, auteurs.get(0)),
            new Livre(null, "La Bête Humaine", "978-2-07-036592-2", Genre.MYSTERY, 1890, true, auteurs.get(11)),
            new Livre(null, "Les Aventures de Huckleberry Finn", "978-0-226-89457-8", Genre.FICTION, 1884, true, auteurs.get(9))
        );
        livreRepository.saveAll(livres);
    }

    private void initializeMembers() {
        List<Adherent> adherents = List.of(
            new Adherent(null, "Alice Dupont", "alice.dupont@email.com", "06-12-34-56-78"),
            new Adherent(null, "Bob Martin", "bob.martin@email.com", "06-23-45-67-89"),
            new Adherent(null, "Claire Leblanc", "claire.leblanc@email.com", "06-34-56-78-90"),
            new Adherent(null, "David Moreau", "david.moreau@email.com", "06-45-67-89-01"),
            new Adherent(null, "Eve Rousseau", "eve.rousseau@email.com", "06-56-78-90-12"),
            new Adherent(null, "Frank Laurent", "frank.laurent@email.com", "06-67-89-01-23"),
            new Adherent(null, "Gisèle Dubois", "gisele.dubois@email.com", "06-78-90-12-34"),
            new Adherent(null, "Henri Gaston", "henri.gaston@email.com", "06-89-01-23-45"),
            new Adherent(null, "Irène Hubert", "irene.hubert@email.com", "06-90-12-34-56"),
            new Adherent(null, "Jacques Leclerc", "jacques.leclerc@email.com", "06-01-23-45-67")
        );
        adherentRepository.saveAll(adherents);
    }

    private void initializeLoans() {
        List<Livre> livres = livreRepository.findAll();
        List<Adherent> adherents = adherentRepository.findAll();

        List<Emprunt> emprunts = List.of(
            new Emprunt(null, livres.get(2), adherents.get(0), LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 15), null),
            new Emprunt(null, livres.get(5), adherents.get(1), LocalDate.of(2024, 1, 20), LocalDate.of(2024, 2, 20), null),
            new Emprunt(null, livres.get(0), adherents.get(2), LocalDate.of(2024, 2, 1), LocalDate.of(2024, 3, 1), LocalDate.of(2024, 2, 28)),
            new Emprunt(null, livres.get(9), adherents.get(3), LocalDate.of(2024, 2, 10), LocalDate.of(2024, 3, 10), null),
            new Emprunt(null, livres.get(6), adherents.get(4), LocalDate.of(2024, 2, 15), LocalDate.of(2024, 3, 15), null),
            new Emprunt(null, livres.get(11), adherents.get(5), LocalDate.of(2024, 2, 20), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 18)),
            new Emprunt(null, livres.get(12), adherents.get(6), LocalDate.of(2024, 2, 25), LocalDate.of(2024, 3, 25), null),
            new Emprunt(null, livres.get(13), adherents.get(7), LocalDate.of(2024, 3, 1), LocalDate.of(2024, 4, 1), null),
            new Emprunt(null, livres.get(14), adherents.get(8), LocalDate.of(2024, 3, 5), LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 2)),
            new Emprunt(null, livres.get(15), adherents.get(9), LocalDate.of(2024, 3, 10), LocalDate.of(2024, 4, 10), null),
            new Emprunt(null, livres.get(1), adherents.get(0), LocalDate.of(2024, 3, 15), LocalDate.of(2024, 4, 15), null),
            new Emprunt(null, livres.get(4), adherents.get(1), LocalDate.of(2024, 3, 20), LocalDate.of(2024, 4, 20), LocalDate.of(2024, 4, 18)),
            new Emprunt(null, livres.get(7), adherents.get(2), LocalDate.of(2024, 3, 25), LocalDate.of(2024, 4, 25), null),
            new Emprunt(null, livres.get(8), adherents.get(3), LocalDate.of(2024, 3, 30), LocalDate.of(2024, 4, 30), null),
            new Emprunt(null, livres.get(10), adherents.get(4), LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 4, 28)),
            new Emprunt(null, livres.get(16), adherents.get(5), LocalDate.of(2024, 4, 5), LocalDate.of(2024, 5, 5), null),
            new Emprunt(null, livres.get(17), adherents.get(6), LocalDate.of(2024, 4, 10), LocalDate.of(2024, 5, 10), null),
            new Emprunt(null, livres.get(18), adherents.get(7), LocalDate.of(2024, 4, 15), LocalDate.of(2024, 5, 15), LocalDate.of(2024, 5, 10)),
            new Emprunt(null, livres.get(20), adherents.get(8), LocalDate.of(2024, 4, 20), LocalDate.of(2024, 5, 20), null),
            new Emprunt(null, livres.get(21), adherents.get(9), LocalDate.of(2024, 4, 25), LocalDate.of(2024, 5, 25), null),
            new Emprunt(null, livres.get(22), adherents.get(0), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 6, 1), null),
            new Emprunt(null, livres.get(24), adherents.get(1), LocalDate.of(2024, 5, 5), LocalDate.of(2024, 6, 5), LocalDate.of(2024, 6, 2)),
            new Emprunt(null, livres.get(25), adherents.get(2), LocalDate.of(2024, 5, 10), LocalDate.of(2024, 6, 10), null),
            new Emprunt(null, livres.get(26), adherents.get(3), LocalDate.of(2024, 5, 15), LocalDate.of(2024, 6, 15), null),
            new Emprunt(null, livres.get(28), adherents.get(4), LocalDate.of(2024, 5, 20), LocalDate.of(2024, 6, 20), null)
        );
        empruntRepository.saveAll(emprunts);
    }
}
