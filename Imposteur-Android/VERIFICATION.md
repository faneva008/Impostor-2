# Vérifications effectuées — 29 septembre 2026

## Résultat

APK de test version 1.1 (versionCode 2) compilé dans l’environnement de travail Arena à partir de la copie adaptée du dépôt `faneva008/Imposteur-Android`. Aucun commit ni lancement de workflow n’a été effectué sur le compte GitHub de l’utilisateur.

## Android

- Wrapper Gradle 8.7 et Android Gradle Plugin 8.6.1.
- SDK / targetSdk 35, Build Tools 35.0.0, minSdk 23.
- Commande : `./gradlew --no-daemon assembleDebug lintDebug`.
- Résultat : `BUILD SUCCESSFUL`.
- Analyse Lint : 0 erreur, 3 avertissements :
  - `enableOnBackInvokedCallback` ignoré avant API 33 : le code garde `onBackPressed` pour ces versions.
  - Deux avertissements d’icône monochrome sur les variantes `v26` : les versions monochromes sont fournies séparément dans `mipmap-anydpi-v33`, pour Android 13+.
- `apksigner verify --verbose --print-certs` : signature valide (schémas v1 et v2), certificat de débogage Android.
- `aapt dump permissions` : aucune permission déclarée, notamment pas `INTERNET`.
- `aapt dump badging` : package `com.imposteurjuridique.app`, version 1.1, activité de lancement présente.

## Interface et logique web

`npm test` : 10 tests Playwright/Chromium réussis, avec réseau désactivé avant chargement des fichiers locaux.

1. Police Plus Jakarta Sans et Font Awesome chargées ; interrupteur de 22 px.
2. Partie complète : configuration, quatre distributions, discussion, verdict et retour à l’accueil ; rôle invisible dès le relâchement.
3. Noms contenant du HTML traités comme texte ; limites de 3 à 10 ; préférences restaurées au rechargement.
4. Dernière catégorie impossible à décocher ; deux imposteurs avec cinq joueurs ; indice désactivable ; masquage sur perte de focus et annulation tactile ; fermeture d’une fenêtre avec la fonction Retour.
5–10. Parcours des quatre écrans et bouton principal accessible sans débordement horizontal à 320×568, 360×720, 393×852, 412×915, 640×360 et 800×1280.

Aucune requête HTTP/HTTPS et aucune erreur JavaScript détectées dans les tests de chargement et de partie complète. Les captures jointes proviennent de cette version locale (fenêtre 360×720), pas d’un téléphone Android.

## À vérifier sur le téléphone

L’application n’a PAS été exécutée sur un téléphone ou un émulateur Android dans cette session.

- Installer puis faire un premier lancement en mode avion.
- Vérifier la police, les icônes et l’icône de lancement.
- Ajouter des noms au clavier, fermer la fenêtre puis changer d’orientation.
- Maintenir l’empreinte, glisser le doigt hors du bouton, relâcher : le rôle doit se masquer.
- Pendant un appui, mettre l’application en arrière-plan, puis revenir : le rôle doit rester masqué.
- Tester Retour dans une fenêtre, pendant une partie et à l’accueil.
- Fermer puis rouvrir l’application : noms et réglages conservés ; aucune ancienne partie restaurée.
- Jouer à une partie entière hors ligne.

La validation dans Chromium ne garantit pas l’identité pixel à pixel sur chaque version de WebView. Les emojis et le découpage de l’icône dépendent aussi du système et du lanceur Android.

Commit du dépôt utilisé comme base : `a8adcaef0916a5ff1fbeeef589f75f40f3396e3e`.
