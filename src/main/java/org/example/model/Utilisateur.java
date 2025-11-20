package org.example.model;

/**
 * Classe abstraite représentant un utilisateur de la plateforme.
 * Elle regroupe les informations communes aux différents types d'utilisateurs
 * (Client et Organisateur).
 */
    public abstract class Utilisateur {
        protected int idUser;
        protected String pseudo;
        protected String prenom;
        protected String nom;
        protected String email;
        protected String motDePasse;
        protected String typeCompte; // CLIENT ou ORGANISATEUR

    /**
     * Construit un nouvel utilisateur avec les informations fournies.
     *
     * @param idUser     l'identifiant unique de l'utilisateur.
     * @param pseudo     le nom d'utilisateur utilisé pour la connexion.
     * @param prenom     le prénom de l'utilisateur.
     * @param nom        le nom de famille de l'utilisateur.
     * @param email      l'adresse email de l'utilisateur.
     * @param motDePasse le mot de passe utilisé pour la connexion.
     * @param typeCompte le type de compte (Client ou Organisateur).
     */
        public Utilisateur(int idUser, String pseudo,String prenom,String nom, String email, String motDePasse, String typeCompte) {
            this.idUser = idUser;
            this.pseudo = pseudo;
            this.prenom = prenom;
            this.nom = nom;
            this.email = email;
            this.motDePasse = motDePasse;
            this.typeCompte = typeCompte;
        }

        // Getters & setters
    /**
     * Retourne l'identifiant de l'utilisateur.
     *
     * @return l'id de l'utilisateur.
     */
        public int getIdUser() { return idUser; }
    /**
     * Retourne le pseudo de l'utilisateur.
     *
     * @return le pseudo.
     */
        public String getPseudo() { return pseudo; }
        public String getPrenom() { return prenom; }
        public String getNom() { return nom; }
        public String getEmail() { return email; }
        public String getMotDePasse() { return motDePasse; }
        public String getTypeCompte() { return typeCompte; }



    /**
     * Modifie le pseudo de l'utilisateur.
     *
     * @param pseudo le nouveau pseudo.
     */
        public void setPseudo(String pseudo) { this.pseudo = pseudo; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public void setNom(String nom) { this.nom = nom; }
        public void setEmail(String email) { this.email = email; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
        public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }


    /**
     * Retourne une représentation textuelle de l'utilisateur,
     * incluant les informations principales telles que l'id,
     * le pseudo, le nom, le prénom et le type de compte.
     *
     * @return une chaîne décrivant l'utilisateur.
     */
        @Override
        public String toString() {
            return "Utilisateur{" +
                    "idUser=" + idUser +
                    ", pseudo='" + pseudo + '\'' +
                    ", prenom='" + prenom + '\'' +
                    ", nom='" + nom + '\'' +
                    ", email='" + email + '\'' +
                    ", typeCompte='" + typeCompte + '\'' +
                    '}';
        }
    }




