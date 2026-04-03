# Research Center

Une application web de gestion pour un centre de recherche, développée avec Spring Boot, Java 21 et Thymeleaf.

## 📝 Description

**Research Center** est une plateforme centralisée conçue pour faciliter la gestion et le suivi des activités d'un centre de recherche. L'application permet de gérer des projets de recherche, de classer ces projets par domaines spécifiques, et de gérer les différents acteurs impliqués en fonction de leurs rôles (Administrateurs, Gestionnaires, Participants).

## 🚀 Fonctionnalités principales

* **Gestion des Utilisateurs** : Système d'authentification robuste avec gestion des rôles (Administrateur, Gestionnaire, Participant).
* **Authentification unifiée** : Support pour l'authentification standard (formulaire) et via OAuth2 (intégration de la connexion Google).
* **Gestion des Projets** : Création, suivi des statuts (en cours, terminé, etc.) et gestion globale des projets de recherche.
* **Domaines de Recherche** : Catégorisation et organisation des projets par domaine de recherche spécifique.
* **Tableaux de bord (Dashboards)** : Interfaces personnalisées selon le type d'utilisateur, affichant des statistiques et les activités clés.

## 🛠️ Stack Technique

* **Langage** : Java 21
* **Framework Backend** : Spring Boot (Web, Data JPA, Security, OAuth2 Client)
* **Moteur de Templates (Frontend)** : Thymeleaf (avec intégration de Spring Security)
* **Bases de données** :
  * H2 Database (Environnement de Développement - en mémoire)
  * PostgreSQL (Environnement de Production)
* **Documentation API** : SpringDoc OpenAPI (Swagger UI)
* **Utilitaires** : Lombok, Maven

## ⚙️ Prérequis

* **Java Development Kit (JDK) 21**
* **Maven 3.8+** (ou utiliser le wrapper `mvnw` inclus dans le projet)
* Variables d'environnement pour l'authentification Google (si OAuth2 est utilisé).

## 🏗️ Installation & Lancement

1. **Cloner le dépôt**
   ```bash
   git clone <URL_DU_DEPOT>
   cd researchCenter
   ```

2. **Configuration des variables d'environnement (OAuth2)**
   
   Pour que l'authentification Google fonctionne, l'application s'attend à trouver les variables d'environnement correspondantes protégées (non codées en dur). Pour tester en local, définissez :
   * `GOOGLE_CLIENT_ID`
   * `GOOGLE_CLIENT_SECRET`

3. **Exécuter l'application (Environnement de Développement)**
   
   Les paramètres pour l'environnement de base (H2 en mémoire) sont présents dans `application-dev.properties`.
   
   ```bash
   # Utilisation du wrapper Maven inclus
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Accès et URLs importantes**
   * **Application (Accueil)** : `http://localhost:9500`
   * **Base de données H2 (Console)** : `http://localhost:9500/h2-console` (URL jdbc : `jdbc:h2:mem:researchCenter`)
   * **Documentation de l'API (Swagger UI)** : `http://localhost:9500/api-docs`

## 📦 Structure du Projet (Vue d'ensemble)

* `src/main/java/com/esmt/researchcenter`
  * `controller/` : Gestion des requêtes HTTP (`AdminController`, `ProjectController`, etc.)
  * `model/` : Entités et domaines persistants (`User`, `Project`, `Participant`, etc.)
  * `repository/` : Interfaces Spring Data JPA pour les interactions en base de données.
  * `service/` : Contient la logique métier (business logic) de l'application.
  * `security/` : Configuration globale de Spring Security et OAuth2.
  * `config/` : Configurations spécifiques (par exemple, pour Swagger OpenAPI).
* `src/main/resources`
  * `application.properties` : Bascule de profil principal (`dev` vs `prod`).
  * `application-dev.properties` / `application-prod.properties` : Configurations spécifiques aux environnements.
  * `templates/` : Vues HTML générées avec Thymeleaf.
