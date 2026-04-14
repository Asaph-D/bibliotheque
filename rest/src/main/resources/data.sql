-- Données d'initialisation pour REST API
-- Base de données: rest_db

-- Insertion des auteurs
INSERT INTO auteurs (id, nom, nationalite) VALUES
(1, 'Victor Hugo', 'Français'),
(2, 'Jules Verne', 'Français'),
(3, 'Agatha Christie', 'Britannique'),
(4, 'Isaac Asimov', 'Russe-Américain'),
(5, 'Albert Einstein', 'Suisse-Allemand'),
(6, 'Yuval Noah Harari', 'Israélien'),
(7, 'J.K. Rowling', 'Britannique'),
(8, 'Stephen King', 'Américain'),
(9, 'George R.R. Martin', 'Américain'),
(10, 'J.R.R. Tolkien', 'Britannique'),
(11, 'Arthur Conan Doyle', 'Britannique'),
(12, 'Émile Zola', 'Français'),
(13, 'Honoré de Balzac', 'Français'),
(14, 'Alexandre Dumas', 'Français'),
(15, 'Dante Alighieri', 'Italien')
ON CONFLICT (id) DO NOTHING;

-- Insertion des livres
INSERT INTO livres (id, titre, isbn, genre, annee_publication, disponible, auteur_id) VALUES
(1, 'Les Misérables', '978-2-07-036491-8', 'ROMAN', 1862, true, 1),
(2, 'Notre-Dame de Paris', '978-2-07-036403-0', 'ROMAN', 1831, true, 1),
(3, 'Voyage au Centre de la Terre', '978-2-07-044742-4', 'SCIENCE_FICTION', 1864, false, 2),
(4, 'Vingt Mille Lieues sous les Mers', '978-2-07-036492-5', 'SCIENCE_FICTION', 1870, true, 2),
(5, 'Meurtre sur l\'Orient Express', '978-2-07-036493-2', 'POLICIER', 1934, true, 3),
(6, 'Le Crime de l\'Orient Express', '978-2-07-036494-9', 'POLICIER', 1934, false, 3),
(7, 'Fondation', '978-0-553-29438-0', 'SCIENCE_FICTION', 1951, true, 4),
(8, 'Les Robots', '978-0-553-29438-1', 'SCIENCE_FICTION', 1950, true, 4),
(9, 'Théorie de la Relativité', '978-0-486-42871-1', 'SCIENCE', 1916, true, 5),
(10, 'Sapiens', '978-2-226-24675-3', 'HISTOIRE', 2011, false, 6),
(11, 'Homo Deus', '978-2-226-39182-8', 'HISTOIRE', 2015, true, 6),
(12, 'Le Seigneur des Anneaux', '978-2-07-044743-1', 'ROMAN', 1954, true, 10),
(13, 'Harry Potter à l\'école des sorciers', '978-0-747-53269-9', 'ROMAN', 1998, true, 7),
(14, 'Ça', '978-0-451-16996-4', 'POLICIER', 1986, true, 8),
(15, 'Le Trône de Fer', '978-0-553-10359-8', 'ROMAN', 1996, false, 9),
(16, 'Sherlock Holmes - Étude en Rouge', '978-0-141-04408-9', 'POLICIER', 1887, true, 11),
(17, 'L\'Assommoir', '978-2-07-036587-8', 'ROMAN', 1877, true, 12),
(18, 'Le Père Goriot', '978-2-07-036588-5', 'ROMAN', 1835, true, 13),
(19, 'Le Comte de Monte Cristo', '978-0-141-04408-9', 'ROMAN', 1844, true, 14),
(20, 'La Comédie Divine', '978-2-07-036589-2', 'HISTOIRE', 1320, false, 15),
(21, 'Les Trois Mousquetaires', '978-0-141-04409-6', 'ROMAN', 1844, true, 14),
(22, 'Neuromancien', '978-0-441-56956-6', 'SCIENCE_FICTION', 1984, true, 8),
(23, '1984', '978-0-451-52493-5', 'SCIENCE_FICTION', 1949, true, 2),
(24, 'Pauvre Bitos', '978-2-07-036590-8', 'POLICIER', 1956, false, 12),
(25, 'Cinq semaines en ballon', '978-0-141-04407-2', 'SCIENCE_FICTION', 1863, true, 2),
(26, 'Les Mystères de Paris', '978-2-07-036591-5', 'POLICIER', 1843, true, 14),
(27, 'L\'Étranger', '978-0-451-52494-2', 'ROMAN', 1942, true, 13),
(28, 'Le Temps Retrouvé', '978-0-141-04410-2', 'ROMAN', 1927, true, 1),
(29, 'La Bête Humaine', '978-2-07-036592-2', 'POLICIER', 1890, true, 12),
(30, 'Les Aventures de Huckleberry Finn', '978-0-226-89457-8', 'ROMAN', 1884, true, 10)
ON CONFLICT (id) DO NOTHING;

-- Insertion des adhérents
INSERT INTO adherents (id, nom, email, telephone) VALUES
(1, 'Alice Dupont', 'alice.dupont@email.com', '06-12-34-56-78'),
(2, 'Bob Martin', 'bob.martin@email.com', '06-23-45-67-89'),
(3, 'Claire Leblanc', 'claire.leblanc@email.com', '06-34-56-78-90'),
(4, 'David Moreau', 'david.moreau@email.com', '06-45-67-89-01'),
(5, 'Eve Rousseau', 'eve.rousseau@email.com', '06-56-78-90-12'),
(6, 'Frank Laurent', 'frank.laurent@email.com', '06-67-89-01-23'),
(7, 'Gisèle Dubois', 'gisele.dubois@email.com', '06-78-90-12-34'),
(8, 'Henri Gaston', 'henri.gaston@email.com', '06-89-01-23-45'),
(9, 'Irène Hubert', 'irene.hubert@email.com', '06-90-12-34-56'),
(10, 'Jacques Leclerc', 'jacques.leclerc@email.com', '06-01-23-45-67')
ON CONFLICT (id) DO NOTHING;

-- Insertion des emprunts
INSERT INTO emprunts (id, date_emprunt, date_retour_prevue, date_retour_effective, livre_id, adherent_id) VALUES
(1, '2024-01-15', '2024-02-15', NULL, 3, 1),
(2, '2024-01-20', '2024-02-20', NULL, 6, 2),
(3, '2024-02-01', '2024-03-01', '2024-02-28', 1, 3),
(4, '2024-02-10', '2024-03-10', NULL, 10, 4),
(5, '2024-02-15', '2024-03-15', NULL, 7, 5),
(6, '2024-02-20', '2024-03-20', '2024-03-18', 12, 6),
(7, '2024-02-25', '2024-03-25', NULL, 13, 7),
(8, '2024-03-01', '2024-04-01', NULL, 14, 8),
(9, '2024-03-05', '2024-04-05', '2024-04-02', 15, 9),
(10, '2024-03-10', '2024-04-10', NULL, 16, 10),
(11, '2024-03-15', '2024-04-15', NULL, 2, 1),
(12, '2024-03-20', '2024-04-20', '2024-04-18', 5, 2),
(13, '2024-03-25', '2024-04-25', NULL, 8, 3),
(14, '2024-03-30', '2024-04-30', NULL, 9, 4),
(15, '2024-04-01', '2024-05-01', '2024-04-28', 11, 5),
(16, '2024-04-05', '2024-05-05', NULL, 17, 6),
(17, '2024-04-10', '2024-05-10', NULL, 18, 7),
(18, '2024-04-15', '2024-05-15', '2024-05-10', 19, 8),
(19, '2024-04-20', '2024-05-20', NULL, 21, 9),
(20, '2024-04-25', '2024-05-25', NULL, 22, 10),
(21, '2024-05-01', '2024-06-01', NULL, 23, 1),
(22, '2024-05-05', '2024-06-05', '2024-06-02', 25, 2),
(23, '2024-05-10', '2024-06-10', NULL, 26, 3),
(24, '2024-05-15', '2024-06-15', NULL, 27, 4),
(25, '2024-05-20', '2024-06-20', NULL, 29, 5)
ON CONFLICT (id) DO NOTHING;

-- IDs sequences reset
SELECT setval('auteurs_id_seq', (SELECT MAX(id) FROM auteurs) + 1);
SELECT setval('livres_id_seq', (SELECT MAX(id) FROM livres) + 1);
SELECT setval('adherents_id_seq', (SELECT MAX(id) FROM adherents) + 1);
SELECT setval('emprunts_id_seq', (SELECT MAX(id) FROM emprunts) + 1);
