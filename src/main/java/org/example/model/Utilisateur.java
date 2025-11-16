package org.example.model;


    public abstract class Utilisateur {
        protected int idUser;
        protected String pseudo;
        protected String prenom;
        protected String nom;
        protected String email;
        protected String motDePasse;
        protected String typeCompte; // CLIENT ou ORGANISATEUR

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
        public int getIdUser() { return idUser; }
        public String getPseudo() { return pseudo; }
        public String getPrenom() { return prenom; }
        public String getNom() { return nom; }
        public String getEmail() { return email; }
        public String getMotDePasse() { return motDePasse; }
        public String getTypeCompte() { return typeCompte; }

        public void setPseudo(String pseudo) { this.pseudo = pseudo; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public void setNom(String nom) { this.nom = nom; }
        public void setEmail(String email) { this.email = email; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
        public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }

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




