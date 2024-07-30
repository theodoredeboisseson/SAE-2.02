package fr.umontpellier.iut.trains;

import fr.umontpellier.iut.graphes.Sommet;
import org.junit.Test;
import org.junit.jupiter.api.Timeout;

import fr.umontpellier.iut.graphes.Graphe;
import fr.umontpellier.iut.trains.plateau.Plateau;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 1, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class GrapheTest {
    @Test
    public void test_graphe_tokyo() {
        Jeu jeu = new Jeu(new String[]{"Batman", "Robin"}, new String[]{}, Plateau.TOKYO);
        Graphe graphe = jeu.getGraphe();

        assertEquals(66, graphe.getNbSommets());
        assertEquals(156, graphe.getNbAretes());
        assertTrue(graphe.estConnexe());
        assertTrue(graphe.possedeUnCycle());
        assertEquals(6, graphe.degreMax());
    }

    @Test
    public void test_distances_tokyo() {
        Jeu jeu = new Jeu(new String[]{"Rick", "Morty"}, new String[]{}, Plateau.TOKYO);
        Graphe graphe = jeu.getGraphe();

        assertEquals(4, graphe.getDistance(graphe.getSommet(0), graphe.getSommet(54)));
        assertEquals(0, graphe.getDistance(graphe.getSommet(13), graphe.getSommet(54)));
        assertEquals(0, graphe.getDistance(graphe.getSommet(3), graphe.getSommet(54)));
        assertEquals(11, graphe.getDistance(graphe.getSommet(67), graphe.getSommet(9)));
        assertEquals(2, graphe.getDistance(graphe.getSommet(34), graphe.getSommet(35)));
    }

    @Test
    public void test_graphe_osaka() {
        Jeu jeu = new Jeu(new String[]{"Lois", "Clark"}, new String[]{}, Plateau.OSAKA);
        Graphe graphe = jeu.getGraphe();

        assertEquals(66, graphe.getNbSommets());
        assertEquals(151, graphe.getNbAretes());
        assertTrue(graphe.estConnexe());
        assertTrue(graphe.possedeUnCycle());
        assertEquals(6, graphe.degreMax());
    }

    @Test
    public void test_graphe_vide() {
        Graphe g = new Graphe();

        assertTrue(g.estChaine());
        assertFalse(g.estConnexe());
        assertFalse(g.estCycle());
        assertFalse(g.possedeUnCycle());
        assertTrue(g.estForet());
    }

    public Graphe creerCycle(int ordre) {
        Graphe g = new Graphe(ordre);
        ArrayList<Sommet> sommets = new ArrayList<>(g.getSommets());
        for (int i = 0; i < sommets.size(); i++) //Faire un cycle simple
            g.ajouterArete(sommets.get(i), sommets.get((i + 1) % sommets.size()));

        return g;
    }

    public Graphe creerGrapheComplet(int ordre) {
        Graphe g = new Graphe(ordre);
        Set<Sommet> sommets = g.getSommets();
        for (Sommet s : sommets)
            for (Sommet v : sommets)
                g.ajouterArete(s, v);

        return g;
    }

    @Test //Question 3
    public void test_graphe_est_complet() {
        Graphe g = creerGrapheComplet(6);

        assertTrue(g.estComplet());

        g.supprimerArete(g.getSommet(0), g.getSommet(1));

        assertFalse(g.estComplet());
    }

    @Test //Question 4
    public void test_est_chaine() {
        Graphe g = creerCycle(4);

        assertFalse(g.estChaine());

        g.supprimerArete(g.getSommet(0), g.getSommet(1));
        assertTrue(g.estChaine());
    }

    @Test //Question 5
    public void test_graphe_est_cycle() {
        Graphe g = creerCycle(4);
        assertTrue(g.estCycle());

        g.ajouterArete(g.getSommet(1), g.getSommet(3));
        assertFalse(g.estCycle());
        g.supprimerArete(g.getSommet(1), g.getSommet(3));

        g.ajouterSommet(4);
        assertFalse(g.estCycle());
    }

    @Test //Question 6
    public void test_graphe_possede_un_cycle() {
        Graphe g = creerCycle(4);
        assertTrue(g.possedeUnCycle());

        g.supprimerArete(g.getSommet(2), g.getSommet(3));
        g.supprimerArete(g.getSommet(3), g.getSommet(0));

        assertFalse(g.possedeUnCycle());

        g.ajouterArete(g.getSommet(0), g.getSommet(2));
        assertTrue(g.possedeUnCycle());
    }

    @Test //True si : On copie g1 dans g2 et que l'on supprime une arête dans g1, l'arrête est toujours dans g2
    public void test_voisinage_en_fonction_des_constructeurs() {
        Graphe g1 = new Graphe(2);

        g1.ajouterArete(g1.getSommet(0), g1.getSommet(1));
        assertTrue(g1.getSommet(0).estVoisin(g1.getSommet(1)));

        Graphe g2 = new Graphe(g1.getSommets());

        g1.supprimerArete(g1.getSommet(0), g1.getSommet(1));
        assertTrue(g2.getSommet(0).estVoisin(g2.getSommet(1)));
    }

    @Test //Question 7
    public void test_sequence_est_un_graphe() {
        ArrayList<Integer> sequence = new ArrayList<>();
        sequence.add(2);
        sequence.add(2);
        sequence.add(2);
        sequence.add(3);
        sequence.add(4);
        sequence.add(5);

        assertTrue(Graphe.sequenceEstGraphe(sequence)); //graphe normal

        sequence.add(8);
        assertFalse(Graphe.sequenceEstGraphe(sequence)); //graphe avec sommet degrés supérieur a sa taille

        sequence = new ArrayList<>();
        sequence.add(2);

        assertFalse(Graphe.sequenceEstGraphe(sequence)); //graphe non connectable

        sequence = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            sequence.add(5);

        assertTrue(Graphe.sequenceEstGraphe(sequence));
    }

    @Test //Question 8 & 9, même si le code nous est donné
    public void test_classe_connexite_sommet() {
        Graphe g = creerCycle(4);
        Set<Sommet> classe1 = new HashSet<>(g.getSommets());

        assertEquals(g.getClasseConnexite(g.getSommet(0)), classe1);

        g.ajouterSommet(4);
        Set<Sommet> classe2 = new HashSet<>();
        classe2.add(g.getSommet(4));

        assertEquals(g.getClasseConnexite(g.getSommet(0)), classe1);
        assertEquals(g.getClasseConnexite(g.getSommet(4)), classe2);

        Set<Set<Sommet>> ensembleConnexite = new HashSet<>();
        ensembleConnexite.add(classe1);
        ensembleConnexite.add(classe2);
        assertEquals(g.getEnsembleClassesConnexite(), ensembleConnexite);
    }

    @Test //Question 10
    public void test_possede_un_isthme(){

    }

    @Test //Question 11
    public void test_est_un_arbre(){

    }

    @Test //Question 12
    public void test_est_foret(){

    }

    @Test //Question 13
    public void test_fusionnerEnsembleSommets(){

    }

    @Test //Question 14
    //Graphe utilisé pour le test : https://cdn.discordapp.com/attachments/1241506673256824973/1247466305414627350/image.png?ex=66602108&is=665ecf88&hm=c7af59979212ac823fba9c5b314b5a2b46a4dcc26e6cfc08e15437a9bdf3f177&
    public void test_coloration_gloutonne(){
        Graphe g = new Graphe(5);
        ArrayList<Sommet> sommets = new ArrayList<>(g.getSommets());
        Sommet a = sommets.get(0);
        Sommet b = sommets.get(1);
        Sommet c = sommets.get(2);
        Sommet d = sommets.get(3);
        Sommet e = sommets.get(4);

        g.ajouterArete(a,b);
        g.ajouterArete(b,d);
        g.ajouterArete(d,e);
        g.ajouterArete(e,c);
        g.ajouterArete(c,a);
        g.ajouterArete(b,e);

        HashMap<Integer, Set<Sommet>> predictionColorationGloutonne = new HashMap<>();
        Set<Sommet> coul1 = new HashSet<>();
        Set<Sommet> coul2 = new HashSet<>();
        Set<Sommet> coul3 = new HashSet<>();

        coul1.add(b);
        coul1.add(c);

        coul2.add(e);
        coul2.add(a);

        coul3.add(d);

        predictionColorationGloutonne.put(0, coul1);
        predictionColorationGloutonne.put(1, coul2);
        predictionColorationGloutonne.put(2, coul3);

        assertEquals(predictionColorationGloutonne, g.getColorationGloutonne());
    }

    @Test //Question 15
    //Graphe utilié pour ce test : https://cdn.discordapp.com/attachments/1241506673256824973/1247903974766415912/image.png?ex=6661b8a4&is=66606724&hm=8882715c9b1be9e6647faf587ad08a875b44b04989030e4c1661c121febb286f&
    public void test_coloration_propre_optimale(){
        Graphe G = new Graphe(10);
        ArrayList<Sommet> sommets = new ArrayList<>(G.getSommets());
        Sommet a = sommets.get(0);
        Sommet b = sommets.get(1);
        Sommet c = sommets.get(2);
        Sommet d = sommets.get(3);
        Sommet e = sommets.get(4);
        Sommet f = sommets.get(5);
        Sommet g = sommets.get(6);
        Sommet h = sommets.get(7);
        Sommet i = sommets.get(8);
        Sommet j = sommets.get(9);

        G.ajouterArete(a,b);
        G.ajouterArete(b,c);
        G.ajouterArete(c,d);
        G.ajouterArete(d,e);
        G.ajouterArete(e,a);
        G.ajouterArete(f,g);
        G.ajouterArete(g,h);
        G.ajouterArete(h,i);
        G.ajouterArete(i,j);
        G.ajouterArete(j,f);
        G.ajouterArete(a,i);
        G.ajouterArete(b,g);
        G.ajouterArete(c,j);
        G.ajouterArete(d,h);
        G.ajouterArete(e,f);

        assertEquals(G.getColoration(sommets).keySet().size(),3);
    }

}