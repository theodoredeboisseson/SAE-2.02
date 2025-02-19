package fr.umontpellier.iut.trains.cartes;

import java.util.List;

import fr.umontpellier.iut.trains.Joueur;

public class Decharge extends Carte {

    public Decharge() {
        super("Décharge", 2, 0, TypeCarte.ACTION);
    }

    public void jouer(Joueur joueur) {
        super.jouer(joueur);

        List<Carte> cartesFerraille = joueur.getMain().stream().filter(c -> c.getTypes().contains(TypeCarte.FERRAILLE))
                .toList();
        for (Carte c : cartesFerraille) {
            joueur.getMain().remove(c);
            joueur.remettreCarteDansLaReserve(c);
        }
    }

}
