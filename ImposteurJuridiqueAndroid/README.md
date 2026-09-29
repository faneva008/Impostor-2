# Projet Android — Imposteur Juridique 1.1

Consultez le [guide complet à la racine du dépôt](../README.md) pour GitHub Actions, l’installation et les limites de cette version de test.

## Compilation

Java 17, Android SDK 35, Build Tools 35.0.0. Gradle 8.7 est fourni par le wrapper.

Windows : `gradlew.bat assembleDebug lintDebug`

Linux/macOS : `bash ./gradlew assembleDebug lintDebug`

APK : `app/build/outputs/apk/debug/app-debug.apk`

CSS déjà compilés et ressources embarquées : le jeu fonctionne sans permission Internet.
