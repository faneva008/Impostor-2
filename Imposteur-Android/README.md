# L’Imposteur Juridique — Android hors ligne

Version 1.1 de test • Identifiant Android : `com.imposteurjuridique.app`

Le jeu HTML original est embarqué dans une application Android. L’interface rose, les cartes, les animations et les dix notions juridiques originales sont conservées. L’icône provient de l’image fournie par le propriétaire du projet (voir `artwork/`).

## Obtenir l’APK avec GitHub, sans installer Android Studio

1. Placez **le contenu de cette archive à la racine du dépôt** `faneva008/Imposteur-Android`, en remplaçant les fichiers de même nom.
2. Vérifiez cette structure dans l’onglet **Code** :
   ```text
   .github/workflows/gradle.yml
   ImposteurJuridiqueAndroid/
   artwork/
   README.md
   ```
   Il ne faut PAS un dossier supplémentaire entre la racine du dépôt et ces dossiers. Il ne faut PAS seulement déposer le ZIP : GitHub Actions doit voir les fichiers décompressés.
3. Validez les changements sur la branche `main` (**Commit changes**).
4. Ouvrez **Actions → APK Android hors ligne**. Si nécessaire, autorisez les workflows. Le lancement est automatique à chaque modification du projet sur `main`. Il est aussi possible de cliquer sur **Run workflow → Run workflow**.
5. Attendez la coche verte. Ouvrez l’exécution et descendez jusqu’à **Artifacts**.
6. Téléchargez **Imposteur-Juridique-APK**, décompressez ce téléchargement, puis récupérez `Imposteur-Juridique-test.apk`.
7. Transférez cet APK sur votre téléphone Android et ouvrez-le depuis le gestionnaire de fichiers. Si Android le demande, autorisez temporairement l’installation depuis cette application. Désactivez ensuite cette autorisation.
8. Lancez le jeu en **mode avion** pour vérifier les écrans, les icônes et une partie complète. Aucun premier lancement connecté n’est requis par l’application.

Il faut être connecté à GitHub pour télécharger les artifacts. Ceux-ci sont conservés 30 jours ; relancez le workflow pour en générer un nouveau.

### Téléverser les modifications depuis Windows

**Méthode navigateur :** dans l’archive extraite, entrez dans le dossier qui contient `README.md`, `.github`, `artwork` et `ImposteurJuridiqueAndroid`. Dans votre dépôt, choisissez **Add file → Upload files**, glissez ces éléments (pas leur dossier parent), puis validez. Incluez impérativement `.github` pour remplacer le workflow existant.

**Méthode GitHub Desktop :** clonez votre dépôt, copiez les mêmes éléments dans le dossier local du dépôt en acceptant les remplacements, puis **Commit to main → Push origin**. Ne remplacez jamais le dossier caché `.git` ; l’archive ne contient pas de dossier `.git`.

## Ce qui a été corrigé

- Police **Plus Jakarta Sans** embarquée (400, 500, 600, 700 et 800).
- Font Awesome 6.4 relié à la page et chemins des polices corrigés.
- Tailwind 3.4.17 compilé localement ; aucune dépendance CDN à l’exécution.
- Interrupteur d’indice corrigé : ses dimensions manquaient dans la configuration originale.
- Icône fournie déclinée en plusieurs résolutions et en icône adaptative Android.
- Gestion de la hauteur disponible, du défilement, du clavier et des barres système.
- Appui tactile : masque immédiatement le rôle au relâchement, à l’annulation ou à la perte de focus. Aucun véritable lecteur biométrique n’est utilisé : l’empreinte est un bouton du jeu.
- Retour Android : ferme une fenêtre ouverte, propose d’abandonner une partie, ou confirme la sortie depuis l’accueil.
- Noms, domaines sélectionnés et réglages conservés sur l’appareil. Les rôles secrets et la partie en cours ne sont PAS enregistrés sur disque.
- Limite de 3 à 10 joueurs appliquée et noms insérés comme texte, sans exécuter de HTML.
- Au moins une catégorie reste sélectionnée visuellement et dans la logique du jeu.
- Deux imposteurs seulement à partir de cinq joueurs, pour garder une majorité de juristes ; sinon un imposteur.
- Mention du verdict adaptée au pluriel.

## Hors ligne et confidentialité

L’application ne demande pas la permission `INTERNET`. Les fichiers sont servis par l’application depuis les assets de l’APK à l’URL interne `https://appassets.androidplatform.net/assets/index.html` : ce n’est **pas** un site web à contacter. Les autres chargements sont refusés. Pas de compte, pas de publicité, pas d’API ni de suivi ajouté.

La mention originale « EN DIRECT » signifie une discussion entre joueurs réunis physiquement, pas une connexion Internet.

Les préférences restent dans le stockage local WebView. La sauvegarde cloud et le transfert Android des données de l’application sont exclus. Un écran neutre masque l’interface lors de la mise en arrière-plan ; la vignette des applications récentes est désactivée sur Android 13+. Cela ne constitue pas une protection contre quelqu’un qui inspecte volontairement les fichiers ou capture l’écran pendant un appui : c’est un jeu de confiance sur un téléphone partagé.

Internet est nécessaire pour télécharger l’APK et les outils de compilation, mais **pas pour jouer après installation**. Android System WebView doit être présent et de préférence à jour sur le téléphone.

## Compatibilité et limites

- Installation prévue à partir d’**Android 6 / API 23**. Compilation/SDK cible : **35**.
- La compilation a réussi avec Gradle 8.7. L’APK fourni est signé avec une **clé de débogage**, pour les essais et l’installation directe ; ce n’est pas une version signée pour publication Play Store.
- Dix tests automatisés Chromium réussis, réseau désactivé : partie complète, ressources locales, noms sûrs, préférences, limites et six tailles de fenêtre (320×568 à 800×1280, dont paysage).
- Analyse Android Lint : **0 erreur, 3 avertissements** (compatibilité du bouton Retour et variantes d’icône) lors de la préparation. Voir `VERIFICATION.md` pour le détail.
- **Pas de test réalisé sur un téléphone Android ou un émulateur Android.** La compilation et les tests navigateur ne remplacent pas une validation sur votre appareil.
- Les emojis, les masques des icônes et certains effets peuvent varier selon Android et son lanceur. La croix et la barre de navigation propres à Gemini/Google ne sont pas reproduites : elles ne faisaient pas partie du jeu.
- En cas d’arrêt du processus par Android, l’application revient à l’accueil ; les réglages restent conservés, mais il faut recommencer la partie.
- Les dix notions du fichier initial sont inchangées ; certaines relèvent du droit français. Elles ne constituent pas une base juridique exhaustive.

## Signature des APK de test

Chaque environnement peut créer sa propre clé de débogage. **L’APK préparé dans Arena et un APK créé par GitHub peuvent donc avoir des signatures différentes.** Si Android refuse la mise à jour (« conflit avec un package », « application non installée »), désinstallez l’ancienne version avant d’installer la nouvelle. Cela efface ses réglages locaux.

Le workflow accepte, de manière facultative, un secret `ANDROID_DEBUG_KEYSTORE_BASE64` contenant une clé de débogage Android standard encodée en base64 (alias `androiddebugkey`, mots de passe standard `android`). Cela permet de conserver la même signature de test entre les compilations GitHub. **Ne mettez jamais de clé de signature dans le dépôt public.** Sans ce secret, Gradle génère une clé temporaire sur la machine GitHub.

Pour une diffusion durable ou le Play Store, configurez une clé de production privée et une build `release` signée. Ce projet n’automatise pas la publication.

## Compiler sur Windows (facultatif)

Ouvrez le dossier `ImposteurJuridiqueAndroid` dans Android Studio. Utilisez Java 17 pour Gradle, installez la plateforme Android 35 et Build Tools 35.0.0, puis lancez dans un terminal :

```powershell
cd ImposteurJuridiqueAndroid
.\gradlew.bat assembleDebug lintDebug
```

Fichier obtenu : `app\build\outputs\apk\debug\app-debug.apk`.

Les CSS sont déjà compilés. Si vous modifiez les classes Tailwind :

```powershell
npm ci
npm run build:css
npx playwright install chromium
npm test
```

Le wrapper Gradle est inclus, avec vérification SHA-256 de sa distribution. Aucune installation globale de Gradle n’est requise.

## Ressources tierces

Les licences de Plus Jakarta Sans (OFL), Font Awesome Free et Tailwind CSS sont incluses dans `ImposteurJuridiqueAndroid/app/src/main/assets/licenses/`. L’icône et le code du jeu fournis par le propriétaire ne sont pas relicenciés ici.
