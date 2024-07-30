package fr.umontpellier.iut.graphes;

import fr.umontpellier.iut.trains.Jeu;
import fr.umontpellier.iut.trains.plateau.Tuile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Classe modélisant les sommets. Le numéro du sommet correspond à la numérotation du plateau en partant
 * d'en haut à gauche et en allant vers le bas à droite.
 */
public class Sommet {

    private final int i; //Numéro du sommet.
    private final int surcout; //Coût de pose d'un rail sur la tuile correspondante.
    private int nbPointsVictoire; //Nombre de points de victoire que rapporte la tuile si un joueur a un rail dessus
    private Set<Integer> joueurs; //Ensemble des joueurs ayant un rail sur la tuile.
    private Set<Sommet> voisins; //Ensemble des sommets voisins.
    private Tuile tuileAttribuee; //Tuile correspondante dans le jeu, qui est représenté par this sommet.

    /**
     * Constructeur privé pour forcer l'utilisation du builder.
     */
    Sommet(int i, int surcout, Set<Integer> joueurs, int nbPointsVictoire) {
        this.i = i;
        this.surcout = surcout;
        this.joueurs = joueurs;
        this.nbPointsVictoire = nbPointsVictoire;
        this.voisins = new HashSet<>();
    }


    public Sommet(Tuile tuile, Jeu jeu) {
        HashSet<Integer> joueurs = new HashSet<>();
        for (int i = 0; i < jeu.getJoueurs().size(); i++)
            if (tuile.hasRail(jeu.getJoueurs().get(i)))
                joueurs.add(i + 1);

        this.i = jeu.getIndexTuile(tuile);
        this.surcout = tuile.getSurcout();
        this.nbPointsVictoire = tuile.getNbPointsVictoire();
        this.joueurs = joueurs;
        this.tuileAttribuee = tuile;
        //MANQUE LES VOISINS
    }

    /**
     * Constructeur par recopie.
     *
     * @param s
     */
    public Sommet(Sommet s) {
        this.i = s.i;
        this.surcout = s.surcout;
        this.nbPointsVictoire = s.nbPointsVictoire;
        this.joueurs = new HashSet<>(s.joueurs);
        this.voisins = new HashSet<>(s.voisins);
        this.tuileAttribuee = s.tuileAttribuee;
    }

    public int getIndice() {
        return i;
    }

    public Tuile getTuileAttribuee() {
        return tuileAttribuee;
    }

    public Set<Integer> getJoueurs() {
        return joueurs;
    }

    public int getNbPointsVictoire() {
        return nbPointsVictoire;
    }

    /**
     * @return le coût de pose d'un rail sur la tuile correspondante.
     * Les effets des cartes du jeu ne sont pas à prendre en compte.
     */
    public int getSurcout() {
        return surcout;
    }

    public Set<Sommet> getVoisins() {
        return voisins;
    }

    public void ajouterVoisin(Sommet voisin) {
        voisins.add(voisin);
    }

    public void supprimerVoisin(Sommet voisin) {
        voisins.remove(voisin);
    }

    public boolean estVoisin(Sommet sommet) {
        return voisins.contains(sommet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sommet sommet)) return false;
        return i == sommet.i;
    }

    @Override //Pour le débogage
    public String toString() {
        StringBuilder str = new StringBuilder("Sommet " + i + "\nVoisins :");
        for(Sommet voisin : voisins)
            str.append(" ").append(voisin.i);

        return str.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(i);
    }

    public static class SommetBuilder {
        private int i;
        private int surcout = 0;
        private int nbPointsVictoire = 0;
        private Set<Integer> joueurs = new HashSet<>();

        public SommetBuilder setIndice(int i) {
            this.i = i;
            return this;
        }

        public SommetBuilder setJoueurs(Set<Integer> joueurs) {
            this.joueurs = joueurs;
            return this;
        }

        public SommetBuilder setSurcout(int surcout) {
            this.surcout = surcout;
            return this;
        }

        public SommetBuilder setNbPointsVictoire(int nbPointsVictoire) {
            this.nbPointsVictoire = nbPointsVictoire;
            return this;
        }

        public Sommet createSommet() {
            return new Sommet(i, surcout, joueurs, nbPointsVictoire);
        }
    }

/*    public Sommet sommetCorrespondant(Set<Sommet> ensemble) {
        for (Sommet s : ensemble)
            if (s.equals(this))
                return s;

        return null;
    }*/
}
