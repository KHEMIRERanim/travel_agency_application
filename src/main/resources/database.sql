-- Add role column to client table
ALTER TABLE client ADD COLUMN role VARCHAR(10) DEFAULT 'USER';

-- Add gender column to client table
ALTER TABLE client ADD COLUMN gender VARCHAR(10);

-- Insert admin user
INSERT INTO client (nom, prenom, email, numero_telephone, date_de_naissance, mot_de_passe, profile_picture, role, gender)
VALUES ('Admin', 'Admin', 'admin@gmail.com', 12345678, '01/01/2000', 'admin', '/images/default_profile.png', 'ADMIN', 'Homme'); 