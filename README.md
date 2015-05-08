# Clt_Freebox
Client FREEBOX V6 version 3.0.3
Conception d’un programme en java pour :
- Création d’une app sur la Freebox V6 vesion 3.0.3
- Ouverture d’une session
- Exemple de programme pour :

> État de la connexion ADSL
> Obtenir les log des appels

Dialogue entre l’API Freebox et la boite à outils Json.

Pour le bon fonctionnement besoin du fichier : JSON-simple-1.1.1.jar
à recopier dans le répertoire de java :
...\jre1.8.0_40\lib\ext

Fonctionnement:
1- Utiliser bouton "FreeBox" pour faire une demande de création d'app sur la freebox.
2- Valider sur la freebox cette demande.
3- La clé token est alors sauvegarder dans un fichier texte sur la racine C:
4- Utiliser le bouton ">> Freebox" pour se connecter à l'app.
5- Dans les tests effectués, récupération l'état de la ligne ADSL et les Log des appels.

Lien vers la doc API FREEBOX : http://dev.freebox.fr/sdk/os/
