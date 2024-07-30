**Date de rendu (Phase 2) : 7 juin 2024 à 23h00**

### Consignes pour le fork
Les étapes à suivre par chaque équipe pour organiser correctement votre projet :

  1. L'équipe devra être constituée de 2 étudiants issus du même groupe de TD (de préférence du même sous-groupe). Exceptionnellement et avec l'accord du chargé de TD (du cours de Graphes), il est possible d'avoir des équipes de 1 ou 3 étudiants. Communiquez les noms à votre chargé de TD (du cours de Graphes).
  2. Le fork de votre équipe sera créé par les enseignants dans le groupe GitLab [Graphes](https://gitlabinfo.iutmontp.univ-montp2.fr/graphes/). Tous les autres forks que vous allez créer seront ignorés !
    
### Consignes générales
* Pour que le projet soit fini, vous devez implémenter correctement l'ensemble de méthodes levant une exception avec l'instruction `throw new RuntimeException("Méthode pas encore implémentée !")`.
* **Les signatures des méthodes qui vous sont fournies doivent rester inchangées.**
* N'hésitez pas à _ajouter_ des fonctions utilitaires qui vous paraissent nécessaires. 
* Vous respecterez les bonnes pratiques en programmation objet vues en cours.
* Le respect des conventions de nommage du langage Java est **impératif**.
* Votre base de code **doit être en permanence testée** et donc toujours être dans un état sain (le code compile et les tests passent). **Un programme qui ne compile pas ne doit en aucun cas être soumis avec `git commit`!**
* Comment tester votre programme ? Voici quelques consignes :

    1. En exécutant les tests unitaires qui vous ont été fournis dans le repertoire `src/test/java`. Leurs noms contiennent le terme `ProfTest`.
        * La plupart de ces tests sont annotés `@Disabled` et donc pour l'instant, ils sont ignorés.
        * Au fur et à mesure de l'avancement de votre projet vous activerez chaque test (en supprimant l'annotation `@Disabled`) et vérifierez que ce test passe.
        * **Attention** : n'essayez pas de passer tous les tests en même temps (principe _BabySteps_).
        * Ne faites pas de `git add/commit` tant que des tests non annotés `@Disabled` ne passent pas.
       
    2. En écrivant vos propres tests unitaires. Pour cela vous pouvez vous inspirer des tests déjà fournis.
  
     **Remarque** : soyez vigilants même si votre programme passe tous vos tests, car en règle générale un programme n'est **jamais** suffisamment testé...

     **Rappel :** les classes de tests unitaires contenant le terme `ProfTest` ne doivent pas être modifiées.


* Certaines précisions ou consignes pourront être ajoutées ultérieurement dans la [FAQ](https://gitlabinfo.iutmontp.univ-montp2.fr/dev-objets/projets/trains/-/blob/master/FAQ.md) et vous en serez informés.
* Surveillez l'activité sur [le forum](https://piazza.com/class/lrahb0patze3u4), les nouvelles informations y seront mentionnées. N'hésitez pas à y poser des questions. Ça vous permettra d'obtenir une réponse de la part des enseignants ou étudiants rapidement, mais aussi d'aider les autres équipes, potentiellement intéressées par la réponse.

### Conseils concernant la gestion de version
* Chaque commit devrait être accompagné d'un message permettant de comprendre l'objet de la modification.
* Vos _commits_ doivent être les plus petits possibles. Un commit qui fait 10 modifications de code sans lien entre elles, devra être découpé en 10 _commits_.
* On vous conseille d'utiliser des _branches Git_ différentes lors du développement d'une fonctionnalité importante. Le nom de la branche doit être au maximum porteur de sens. Une fois que vous pensez que le code de la fonctionnalité est fini (tous les tests associés à celle-ci passent), vous fusionnerez le code de sa branche avec la branche `master`.
