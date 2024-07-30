package fr.umontpellier.iut.graphes;

import fr.umontpellier.iut.trains.Jeu;
import fr.umontpellier.iut.trains.plateau.Tuile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Graphe simple non-orienté pondéré représentant le plateau du jeu.
 * Pour simplifier, on supposera que le graphe sans sommets est le graphe vide.
 * Le poids de chaque sommet correspond au coût de pose d'un rail sur la tuile correspondante.
 * Les sommets sont indexés par des entiers (pas nécessairement consécutifs).
 */

public class Graphe {
    private final Set<Sommet> sommets;

    public Graphe(Set<Sommet> sommets) {
        this.sommets = new HashSet<>();
        for (Sommet s : sommets)
            this.sommets.add(new Sommet(s));

/*        addVoisinsCorrespondants(sommets);
    }

    public void addVoisinsCorrespondants(Set<Sommet> sommets){
        for(Sommet s : this.sommets) //Pour chaque sommet du Graphe
            for(Sommet v : s.sommetCorrespondant(sommets).getVoisins()) //On récupère les voisins correspondants par rapport au paramètre sommets
                s.ajouterVoisin(v.sommetCorrespondant(this.sommets)); //Et pour chacun de ses voisins, on ajoute celui sa correspondance dans this*/
    }


    /**
     * Construit un graphe à n sommets 0..n-1 sans arêtes
     */
    public Graphe(int n) {
        sommets = new HashSet<>();
        for (int i = 0; i < n; i++)
            ajouterSommet(i);
    }

    /**
     * Construit un graphe vide
     */
    public Graphe() {
        sommets = new HashSet<>();
    }

    /**
     * Construit un sous-graphe induit par un ensemble de sommets
     * sans modifier le graphe donné
     *
     * @param g le graphe à partir duquel on construit le sous-graphe
     * @param X les sommets à considérer (on peut supposer que X est inclus dans l'ensemble des sommets de g,
     *          même si en principe ce n'est pas obligatoire)
     */
    public Graphe(Graphe g, Set<Sommet> X) {
        this.sommets = new HashSet<>();
        for (Sommet s : g.sommets)
            this.sommets.add(new Sommet(s));

        sommets.removeIf(s -> !X.contains(s));
        for (Sommet s : sommets)
            s.getVoisins().removeIf(voisin -> !X.contains(voisin));
    }

    public Graphe(Jeu jeu) {
        sommets = new HashSet<>();
        for (Tuile t : jeu.getTuiles()) {
            sommets.add(new Sommet(t, jeu));
        }
        Sommet s;
        Tuile t;
        for (int i = 0; i < jeu.getTuiles().toArray().length; i++) {
            s = getSommet(i);
            t = jeu.getTuile(i);
            for (Tuile tVoisine : t.getVoisines()) {
                s.ajouterVoisin(getSommet(tVoisine));
            }
        }
    }

    /**
     * @return true si et seulement si la séquence d'entiers passée en paramètre
     * correspond à un graphe simple valide dont les degrés correspondent aux éléments de la liste.
     * Pré-requis : on peut supposer que la séquence est triée dans l'ordre croissant.
     */
    public static boolean sequenceEstGraphe(List<Integer> sequence) {
        for (Integer i : sequence)
            if (i < 0 || i >= sequence.size())
                return false;

        int[] seq = sequence.stream().mapToInt(Integer::intValue).toArray();
        int sum = Arrays.stream(seq).sum();
        if (sum % 2 != 0) //Si somme est impaire
            return false;

        for (int i = seq.length - 1; i > 0; i--) { //pour chacun des sommets
            int j = i - 1;
            while (seq[i] != 0 && j > -1) { //tant que toutes ses arêtes n'ont pas été reliés et qu'on n'a pas regardé tous les sommets
                if (seq[j] != 0) { //Si l'autre sommet a des degrés, alors on le connecte
                    seq[j]--;
                    seq[i]--;
                }
                j--;
            }
            if (seq[i] != 0) //Si après avoir regardé tous les sommets il nous reste des degrés alors ce n'est pas un graphe
                return false;
        }

        return true;
    }

    /**
     * @param g        le graphe source, qui ne doit pas être modifié
     * @param ensemble un ensemble de sommets
     *                 pré-requis : l'ensemble donné est inclus dans l'ensemble des sommets de {@code g}
     * @return un nouveau graph obtenu en fusionnant les sommets de l'ensemble donné.
     * On remplacera l'ensemble de sommets par un seul sommet qui aura comme indice
     * le minimum des indices des sommets de l'ensemble. Le surcout du nouveau sommet sera
     * la somme des surcouts des sommets fusionnés. Le nombre de points de victoire du nouveau sommet
     * sera la somme des nombres de points de victoire des sommets fusionnés.
     * L'ensemble de joueurs du nouveau sommet sera l'union des ensembles de joueurs des sommets fusionnés.
     */
    public static Graphe fusionnerEnsembleSommets(Graphe g, Set<Sommet> ensemble) {
        if (ensemble.isEmpty())
            // Si l'ensemble des sommets à fusionner est vide, retourne le graphe original
            return g;

        // Trouver l'indice minimum et calculer les propriétés combinées des sommets
        int minIndice = Integer.MAX_VALUE;
        int surcoutTotal = 0;
        int nbPointsVictoireTotal = 0;
        Set<Integer> joueursTotal = new HashSet<>();
        Set<Sommet> voisinsTotal = new HashSet<>();

        for (Sommet sommet : ensemble) {
            if (sommet.getIndice() < minIndice)
                minIndice = sommet.getIndice();
            surcoutTotal += sommet.getSurcout();
            nbPointsVictoireTotal += sommet.getNbPointsVictoire();
            joueursTotal.addAll(sommet.getJoueurs());
            voisinsTotal.addAll(sommet.getVoisins());
        }

        // Supprimer les sommets de l'ensemble original et les ajouter au nouvel ensemble des sommets
        Set<Sommet> nouveauxSommets = new HashSet<>(g.getSommets());
        nouveauxSommets.removeAll(ensemble);

        // Créer le nouveau sommet fusionné
        Sommet nouveauSommet = new Sommet.SommetBuilder()
                .setIndice(minIndice)
                .setSurcout(surcoutTotal)
                .setNbPointsVictoire(nbPointsVictoireTotal)
                .setJoueurs(joueursTotal)
                .createSommet();

        // Ajouter les voisins du nouveau sommet, en évitant les sommets fusionnés
        voisinsTotal.removeAll(ensemble);
        for (Sommet voisin : voisinsTotal) {
            nouveauSommet.ajouterVoisin(voisin);
            voisin.supprimerVoisin(nouveauSommet);
            voisin.ajouterVoisin(nouveauSommet);
        }

        // Ajouter le nouveau sommet à l'ensemble des sommets
        nouveauxSommets.add(nouveauSommet);

        // Créer et retourner le nouveau graphe
        return new Graphe(nouveauxSommets);
    }


    /**
     * @param i un entier
     * @return le sommet d'indice {@code i} dans le graphe ou null si le sommet d'indice {@code i} n'existe pas dans this
     */
    public Sommet getSommet(int i) {
        for (Sommet s : sommets) {
            if (s.getIndice() == i) {
                return s;
            }
        }
        return null;
    }

    public Sommet getSommet(Tuile t) {
        for (Sommet s : sommets) {
            if (s.getTuileAttribuee().equals(t)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return l'ensemble des sommets du graphe
     */
    public Set<Sommet> getSommets() {
        return sommets;
    }

    /**
     * @return l'ordre du graphe, c'est-à-dire le nombre de sommets
     */
    public int getNbSommets() {
        return sommets.size();
    }

    /**
     * @return l'ensemble d'arêtes du graphe sous forme d'ensemble de paires de sommets
     */
    public Set<Set<Sommet>> getAretes() {
        Set<Set<Sommet>> aretes = new HashSet<>();
        for (Sommet s : sommets) {
            for (Sommet voisin : s.getVoisins()) {
                Set<Sommet> arete = new HashSet<>(Arrays.asList(s, voisin));
                aretes.add(arete);
            }
        }
        return aretes;
    }

    /**
     * @return le nombre d'arêtes du graphe
     */
    public int getNbAretes() {
        int sommeDesDegres = 0;
        for (Sommet s : sommets)
            sommeDesDegres += degre(s);

        if (sommeDesDegres % 2 == 1)
            throw new RuntimeException("Graphe impossible rencontré: Somme des degrés est impaire");

        return sommeDesDegres / 2;
    }

    /**
     * Ajoute un sommet d'indice i au graphe s'il n'est pas déjà présent
     *
     * @param i l'entier correspondant à l'indice du sommet à ajouter dans le graphe
     */
    public boolean ajouterSommet(int i) {
        if (getSommet(i) != null)
            return false;

        sommets.add(new Sommet.SommetBuilder()
                .setIndice(i)
                .createSommet());
        return true;
    }

    /**
     * Ajoute un sommet au graphe s'il n'est pas déjà présent
     *
     * @param s le sommet à ajouter
     * @return true si le sommet a été ajouté, false sinon
     */
    public boolean ajouterSommet(Sommet s) {
        return sommets.add(s);
    }

    /**
     * @param s le sommet dont on veut connaître le degré
     *          pré-requis : {@code s} est un sommet de this
     * @return le degré du sommet {@code s}
     */
    public int degre(Sommet s) {
        return s.getVoisins().size();
    }

    /**
     * @return la sequence de degrés de this
     */
    public List<Integer> getSequence(){
        List<Integer> sequence = new ArrayList<>();
        for(Sommet s: sommets)
            sequence.add(s.getIndice());

        sequence.sort(Integer::compareTo);

        return sequence;
    }

    /**
     * @return true si et seulement si this est complet.
     */
    public boolean estComplet() {
        int ordre = sommets.size();
        int taille = getNbAretes();

        return taille == (ordre * (ordre - 1) / 2);
    }

    /**
     * @return true si et seulement si this est une chaîne. On considère que le graphe vide est une chaîne.
     */
    public boolean estChaine() {
        if (sommets.isEmpty())
            return true;

        int nbSommetsDeDegre1 = 0;
        for (Sommet s : sommets) {
            int degre = this.degre(s);
            if (degre > 2 || degre == 0) return false;
            if (degre == 1)
                nbSommetsDeDegre1++;
        }
        return nbSommetsDeDegre1 == 2;
    }

    /**
     * @return true si et seulement si this est un cycle. On considère que le graphe vide n'est pas un cycle.
     */
    public boolean estCycle() {
        for (Sommet s : sommets)
            if (degre(s) != 2) return false;

        return estConnexe();
    }

    /**
     * @return true si et seulement si this est un arbre. Un arbre est un graphe connexe
     * qui ne possède pas de cycle
     */
    public boolean estUnArbre() {
        return estConnexe() && !possedeUnCycle();
    }

    /**
     * @return true si et seulement si this est une forêt. On considère qu'un arbre est une forêt
     * et que le graphe vide est un arbre.
     */
    public boolean estForet() {
        if (sommets.isEmpty()) return true;

        for (Set<Sommet> classe : getEnsembleClassesConnexite())
            if (!new Graphe(classe).estUnArbre()) return false;

        return true;
    }

    /**
     * @return true si et seulement si this a au moins un cycle. On considère que le graphe vide n'est pas un cycle.
     */
    public boolean possedeUnCycle() {

        for (Set<Sommet> classeConnexite : getEnsembleClassesConnexite()) {
            Graphe g = new Graphe(classeConnexite);
            int ordre = g.getSommets().size();
            int taille = g.getNbAretes();
            if (taille >= 3 && taille > ordre - 1) return true;
        }

        return false;
    }

    /**
     * @return true si et seulement si this a un isthme
     */
    public boolean possedeUnIsthme() {
        for (Sommet s : sommets)
            for (Sommet voisin : s.getVoisins())
                if (estIsthme(s, voisin))
                    return true;

        return false;
    }

    private boolean estIsthme(Sommet s, Sommet t) {
        supprimerArete(s, t);
        boolean estConnexeApresSuppression = estConnexe();
        ajouterArete(s, t); // Restaurer l'arête après vérification
        return !estConnexeApresSuppression;
    }

    private boolean possedeArticulation(){
        if (sommets.isEmpty()) return false;
        for (Sommet s : sommets)
            if (estPointDArticulation(s))
                return true;
        return false;
    }

    private boolean estPointDArticulation(Sommet s){
        Graphe copie = new Graphe(this, sommets);
        Sommet aEnlever = new Sommet(s);
        for (Sommet s2 : copie.sommets)
            if (s2.getIndice() == s.getIndice())
                aEnlever = s2;
        copie.supprimerSommet(aEnlever);
        return copie.getEnsembleClassesConnexite().size() > getEnsembleClassesConnexite().size();
    }

    public void ajouterArete(Sommet s, Sommet t) {
        if (s.equals(t))
            return;
        // On ne peut pas ajouter une arête entre un sommet et lui-même ou si l'arête existe déjà

        s.ajouterVoisin(t);
        t.ajouterVoisin(s);
    }

    public void supprimerArete(Sommet s, Sommet t) {
        s.supprimerVoisin(t);
        t.supprimerVoisin(s);
    }

    public void supprimerSommet(Sommet sommet){
        if (!sommets.contains(sommet)) return;
        for (Sommet s : sommets){
            s.supprimerVoisin(sommet);
        }
        sommet.getVoisins().clear();
        sommets.remove(sommet);
    }

    /**
     * @param depart  - ensemble non-vide de sommets
     * @param arrivee
     * @return le surcout total minimal du parcours entre l'ensemble de depart et le sommet d'arrivée
     * pré-requis : l'ensemble de départ et le sommet d'arrivée sont inclus dans l'ensemble des sommets de this
     */
    public int getDistance(Set<Sommet> depart, Sommet arrivee) {
        if (depart == null || arrivee == null || depart.isEmpty() || !sommets.containsAll(depart) || !sommets.contains(arrivee))
            throw new IllegalArgumentException("L'ensemble de départ et le sommet d'arrivée ne font pas partie du graphe...");

        Map<Sommet, Integer> distances = new HashMap<>();
        List<Sommet> aVisiter = new ArrayList<>();
        for (Sommet s : sommets) distances.put(s, Integer.MAX_VALUE);
        for (Sommet s : depart) { distances.put(s, 0); aVisiter.add(s); }

        while (!aVisiter.isEmpty()) {
            Sommet actuel = null;
            int minDist = Integer.MAX_VALUE;
            for (Sommet s : aVisiter) {
                if (distances.get(s) < minDist) {
                    minDist = distances.get(s);
                    actuel = s;
                }
            }
            aVisiter.remove(actuel);
            if (actuel.equals(arrivee)) return distances.get(actuel);
            for (Sommet voisin : actuel.getVoisins()) {
                int nouvelleDistance = distances.get(actuel) + voisin.getSurcout();
                if (nouvelleDistance < distances.get(voisin)) {
                    distances.put(voisin, nouvelleDistance);
                    if (!aVisiter.contains(voisin)) aVisiter.add(voisin);
                }
            }
        }
        return distances.get(arrivee);
    }

    /**
     * @return le surcout total minimal du parcours entre le sommet de depart et le sommet d'arrivée
     */
    public int getDistance(Sommet depart, Sommet arrivee) {
        if (depart == null || arrivee == null || !sommets.contains(depart) || !sommets.contains(arrivee)) {
            throw new IllegalArgumentException("Les sommets de départ et d'arrivée doivent faire partie du graphe");
        }

        // Dijkstra
        Map<Sommet, Integer> distances = new HashMap<>();
        PriorityQueue<Sommet> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<Sommet> visites = new HashSet<>();

        for (Sommet s : sommets) {
            distances.put(s, Integer.MAX_VALUE);
        }
        distances.put(depart, 0);
        queue.add(depart);

        while (!queue.isEmpty()) {
            Sommet actuel = queue.poll();
            if (actuel.equals(arrivee)) {
                return distances.get(actuel);
            }
            if (visites.contains(actuel)) {
                continue;
            }
            visites.add(actuel);

            for (Sommet voisin : actuel.getVoisins()) {
                if (!visites.contains(voisin)) {
                    int nouvelleDistance = distances.get(actuel) + voisin.getSurcout();
                    if (nouvelleDistance < distances.get(voisin)) {
                        distances.put(voisin, nouvelleDistance);
                        queue.add(voisin);
                    }
                }
            }
        }
        return distances.get(arrivee);
    }

    /**
     * @return l'ensemble des classes de connexité du graphe sous forme d'un ensemble d'ensembles de sommets.
     */
    public Set<Set<Sommet>> getEnsembleClassesConnexite() {
        Set<Set<Sommet>> ensembleClassesConnexite = new HashSet<>();
        if (sommets.isEmpty())
            return ensembleClassesConnexite;

        Set<Sommet> sommets = new HashSet<>(this.sommets);
        while (!sommets.isEmpty()) {
            Sommet v = sommets.iterator().next();
            Set<Sommet> classe = getClasseConnexite(v);
            sommets.removeAll(classe);
            ensembleClassesConnexite.add(classe);
        }
        return ensembleClassesConnexite;
    }

    /**
     * @param v un sommet du graphe this
     * @return la classe de connexité du sommet {@code v} sous forme d'un ensemble de sommets.
     */
    public Set<Sommet> getClasseConnexite(Sommet v) {
        if (!sommets.contains(v))
            return new HashSet<>();
        Set<Sommet> classe = new HashSet<>();
        calculerClasseConnexite(v, classe);
        return classe;
    }

    private void calculerClasseConnexite(Sommet v, Set<Sommet> dejaVus) {
        dejaVus.add(v);
        Set<Sommet> voisins = v.getVoisins();

        for (Sommet voisin : voisins) {
            if (dejaVus.add(voisin))
                calculerClasseConnexite(voisin, dejaVus);
        }
    }

    /**
     * @return true si et seulement si this est connexe.
     */
    public boolean estConnexe() {
        return getEnsembleClassesConnexite().size() == 1;
    }

    /**
     * @return si this is isomorphe à g
     */
    public boolean estIsomorphe(Graphe g1, Graphe g2){
        if (g2 == null || g1 == null || g1.getNbSommets() != g2.getNbSommets() || g1.getNbAretes() != g2.getNbAretes()) return false;
        Set<Set<Sommet>> problemes = new HashSet<>(); //les couples de sommets qui ne permettent pas l'isomorphisme
        Set<Sommet> probleme = new HashSet<>(); //la variable qui va prendre les couples problématiques
        Map<Sommet, Sommet> liaisons = new HashMap<>(); //liste de couple pour vérifier l'isomorphisme
        liaisons = creerLiaisons(problemes, liaisons, g1.sommets, g2.sommets);
        while (liaisons != null && probleme != null){ /* Tant que :
            'liaisons' forme une liste possible (null sinon)
            'probleme' continue d'être un objet (sinon ça veut dire qu'il n'y a plus de probleme et que liaisons est
                vérifié en tant qu'isomorphisme)
            */
            probleme = verifierIsomorphisme(liaisons);
            problemes.add(probleme);
            liaisons = creerLiaisons(problemes, liaisons, g1.sommets, g2.sommets);
        }
        return probleme != null;
    }

    /**
     * @param problemes : les couples de sommet qui forment des erreurs.
     * @param liaisons : les couples de sommets liés (pour vérifier l'isomorphisme),
     *                 normalement aucun couple n'est retrouvable dans problemes.
     * @param ensemble1 : premier ensemble de sommets.
     * @param ensemble2 : second ensemble de sommets, de même taille que le premier.
     * @return la Map de sommets 'liaisons' complétée.
     */
    public Map<Sommet, Sommet> creerLiaisons(
            Set<Set<Sommet>> problemes,
            Map<Sommet, Sommet> liaisons,
            Set<Sommet> ensemble1,
            Set<Sommet> ensemble2){
        if (liaisons.size() == ensemble1.size())
            return liaisons;

        for (Sommet sommet1 : ensemble1)
            if (!liaisons.containsKey(sommet1))
                for (Sommet sommet2 : ensemble2)
                    if (!liaisons.containsValue(sommet2)) {
                        Set<Sommet> coupleTest = new HashSet<>();
                        coupleTest.add(sommet1);
                        coupleTest.add(sommet2);
                        if (!problemes.contains(coupleTest)) {
                            liaisons.put(sommet1, sommet2);
                            return creerLiaisons(problemes, liaisons, ensemble1, ensemble2);
                        }
                    }
        return null;
    }

    /**
     * @param liaisons : deux ensembles de sommets, les uns liés aux autres
     * @return le couple de sommets qui ne vérifie pas l'isomorphisme
     * et renvoie null si l'isomorphisme est vérifié.
     */
    public Set<Sommet> verifierIsomorphisme(Map<Sommet, Sommet> liaisons){
        if (liaisons.isEmpty()) {
            return null;
        }
        for (Sommet sommet1 : liaisons.keySet())
            for (Sommet voisin : sommet1.getVoisins())
                if (!liaisons.get(sommet1).getVoisins().contains(liaisons.get(voisin))) {
                    //"si le sommet lié à sommet1 n'a pas pour voisin le sommet lié au voisin de sommet1 : "
                    Set<Sommet> probleme = new HashSet<>();
                    probleme.add(sommet1);
                    probleme.add(liaisons.get(sommet1));
                    return probleme;
                }
        return null;
    }

    /**
     * @return le degré maximum des sommets du graphe
     */
    public int degreMax() {
        int maxDegre = 0;
        for (Sommet s : sommets)
            maxDegre = Math.max(maxDegre, degre(s));

        return maxDegre;
    }

    /**
     * @return une coloration gloutonne du graphe sous forme d'une Map d'ensemble indépendants de sommets.
     * L'ordre de coloration des sommets est suivant l'ordre décroissant des degrés des sommets
     * (si deux sommets ont le même degré, alors on les ordonne par indice croissant).
     */
    public Map<Integer, Set<Sommet>> getColorationGloutonne() {
        List<Sommet> sommetsTries = trierSommets();
        return getColoration(sommetsTries);
    }

    /**
     * @return Une nouvelle liste de sommets qui est une copie de this.sommets mais les organisent dans l'ordre des degrés décroissants
     * puis dans l'ordre des degrés croissants s'il y a une égalité de degrés.
     */
    private List<Sommet> trierSommets() {
        List<Sommet> sommetsTries = new ArrayList<>(sommets);

        sommetsTries.sort((a, b) -> {
            int compareDegre = Integer.compare(degre(b), degre(a));
            if (compareDegre != 0)
                return compareDegre;
            return Integer.compare(a.getIndice(), b.getIndice());
        });

        return sommetsTries;
    }

    /**
     * @return une coloration propre optimale du graphe sous forme d'une Map d'ensemble indépendants de sommets.
     * Chaque classe de couleur est représentée par un entier (la clé de la Map).
     * Pré-requis : le graphe est issu du plateau du jeu Train (entre autres, il est planaire).
     */
    public Map<Integer, Set<Sommet>> getColorationPropreOptimale() {
        Set<Sommet> sommetsRacine = new HashSet<>(this.sommets);
        Set<Sommet> sommetsChoisis = new HashSet<>();
        Map<Integer, Set<Sommet>> coloration = new HashMap<>();

        return getColorationRecursive(sommetsRacine, sommetsChoisis,coloration);
    }


    public Map<Integer, Set<Sommet>> getColorationRecursive(Set<Sommet> sommetsRestants, Set<Sommet> sommetsChoisis,
                                                            Map<Integer, Set<Sommet>> colorationMinimale) {
        Map<Integer, Set<Sommet>> colorationActuelle;
        if (sommetsRestants.size() > 1)
            for(Sommet s: sommetsRestants){
                sommetsRestants.remove(s);
                sommetsChoisis.add(s);
                colorationActuelle = getColorationRecursive(sommetsRestants,sommetsChoisis,colorationMinimale);
                sommetsChoisis.remove(s);
                sommetsRestants.add(s);

                if(!colorationActuelle.equals(colorationMinimale))
                    colorationMinimale = colorationActuelle;
            }
        else{
            sommetsChoisis.addAll(sommetsRestants); //Plus que 1 sommet dans sommetActuel alors, ajoute le dernier sommet par évidence
            colorationActuelle = getColoration(new ArrayList<>(sommetsChoisis));
            if(colorationActuelle.keySet().size() < colorationMinimale.keySet().size() || colorationMinimale.isEmpty())
                return colorationActuelle;
        }

        return colorationMinimale;
    }

    public Map<Integer, Set<Sommet>> getColoration(List<Sommet> sommets) {
        Map<Integer, Set<Sommet>> coloration = new HashMap<>();
        int couleurActuelle;

        for (Sommet sommet : sommets) {
            couleurActuelle = 0;
            Set<Sommet> sommetsAvecCouleurActuelle = coloration.get(couleurActuelle);

            while (sommetsAvecCouleurActuelle != null && sommetsAvecCouleurActuelle.stream().anyMatch(s -> s.estVoisin(sommet))) {
                couleurActuelle++;
                sommetsAvecCouleurActuelle = coloration.get(couleurActuelle);
            }

            if (sommetsAvecCouleurActuelle == null) {
                sommetsAvecCouleurActuelle = new HashSet<>();
                coloration.put(couleurActuelle, sommetsAvecCouleurActuelle);
            }
            sommetsAvecCouleurActuelle.add(sommet);
        }

        return coloration;
    }

    /**
     * @return true si et seulement si this possède un sous-graphe complet d'ordre {@code k}
     */
    public boolean possedeSousGrapheComplet(int k) {
        Graphe complet = new Graphe(k);
        for (Sommet s : complet.sommets)
            for (Sommet voisin : complet.sommets)
                if (!s.equals(voisin)) s.ajouterVoisin(voisin);
        return possedeSousGrapheIsomorphe(complet);
    }

    /**
     * @param g un graphe
     * @return true si et seulement si this possède un sous-graphe isomorphe à {@code g}
     */
    public boolean possedeSousGrapheIsomorphe(Graphe g) {
        return false;
    }

    /**
     * @param s
     * @param t
     * @return un ensemble de sommets qui forme un ensemble critique de plus petite taille entre {@code s} et {@code t}
     */
    public Set<Sommet> getEnsembleCritique(Sommet s, Sommet t) {
        throw new RuntimeException("Méthode à implémenter");
    }
}
