-- Insertion d'utilisateurs de test
-- Mot de passe: password (encodé en BCrypt)
INSERT INTO users (id, username, password, created_at) VALUES
(1, 'admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', CURRENT_TIMESTAMP),
(2, 'user1', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', CURRENT_TIMESTAMP),
(3, 'user2', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', CURRENT_TIMESTAMP);
-- Si on fixe les clés auto-générées, il faut réinitialiser le compteur
-- Attention, cette commande n'est pas standard SQL
-- ici la syntaxe pour H2
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;

-- Insertion des rôles
INSERT INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

-- Association des rôles aux utilisateurs
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin a le rôle ADMIN
(1, 2), -- admin a aussi le rôle USER
(2, 2), -- user1 a le rôle USER
(3, 2); -- user2 a le rôle USER

-- Insertion de notes de test
INSERT INTO notes (id, title, content, created_at, updated_at, user_id) VALUES
(1, 'Ma première note', '<p>Ceci est ma <strong>première note</strong> avec du texte riche!</p>', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
(2, 'Liste de courses', '<ul><li>Pain</li><li>Lait</li><li>Oeufs</li></ul>', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
(3, 'Réunion importante', '<p>Points à discuter:</p><ol><li>Budget</li><li>Planning</li><li>Ressources</li></ol>', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3);
-- Si on fixe les clés auto-générées, il faut réinitialiser le compteur
-- Attention, cette commande n'est pas standard SQL
-- ici la syntaxe pour H2
ALTER TABLE notes ALTER COLUMN id RESTART WITH 4;