package batoh2;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementace pomoci dynamickeho programovani
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class DynamicAlgorithm implements IAlgorithm {

    // batoh a barak pro uchovani polozek
    private Batoh batoh;
    private Barak barak;
    // tabulka pro uchovani vypocitanych instanci [nosnost][cena]
    private Batoh[][] vypocitane;

    public DynamicAlgorithm(Batoh batoh, Barak barak) {
        this.batoh = batoh;
        this.barak = barak;
        this.vypocitane = new Batoh[barak.polozky.size() + 1][batoh.getNosnost() + 1];
    }

    public void computeStolenItems() {
        // vytvorime plny batoh, kde budou vsechny polozky z baraku
        Batoh startovni = new Batoh(this.batoh.getNosnost());
        startovni.setPolozky(barak.getPolozky());
        // spustime rekurzivni vypocet
        Batoh cilovy = solveInstance(startovni);
        /* konec algoritmu, takze musime do batohu dat nejlepsi vysledek */
        System.out.println("Rekurze skoncila.");
        /*
        this.batoh.setPolozky(cilovy.getPolozky());
        this.batoh.setAktualniCena(cilovy.getAktualniCena());
        this.batoh.setAktualniZatizeni(cilovy.getAktualniZatizeni());
        */
    }

    /**
     * Rekurzivni funkce, ktera vypocita jednu instanci batohu, pseudokod:
     *
     * if isTrivial(V, C, M) return trivialKNAP(V, C, M); // ukončení rekurze, je-li instance triviální
     * (X0, C0, m0) = KNAP(V-{vn}, C-{cn}, M);            // vyřeš batoh, ve kterém n-tá věc není
     * (X1, C1, m1) = KNAP(V-{vn}, C-{cn}, M-vn);         // vyřeš batoh, ve kterém n-tá věc je
     * if (C1+cn) > C0 return(X1.1,  C1+cn, m1+vn);       // jaká varianta je lepší?
     *      else return(X0.0, C0, m0);
     *
     * (V, C, M) = V vahy veci, C ceny veci, M zatizeni batohu
     *
     * @param state
     * @return
     */
    private Batoh solveInstance(Batoh state) {

        // 18/114 - 42/136 - 88/192 - 3/223 (v, c), suma 151/665
        System.out.println("Vstupuji do instance (M=" + state.getNosnost() + 
                            ", n=" + state.getPolozky().size() +
                            ", sumaV=" + state.getAktualniZatizeni() +
                            ", sumaC=" + state.getAktualniCena() + ")");

        // pokud je to trivialni reseni, tak vrat trivialni
        if ( isTrivialInstance(state) ) {
            System.out.println("Je to trivialni, takze vracim aktualni stav (M=" + state.getNosnost() +
                                ", n=" + state.getPolozky().size() +
                                ", sumaV=" + state.getAktualniZatizeni() +
                                ", sumaC=" + state.getAktualniCena() +
                                "), ale polozky nuluji a nosnost take.");
            // TODO return vypocitane[0][0];
            if ( state.getNosnost() < 0 ) state.setNosnost(0);
            state.setPolozky();
            return state;
        }

        // pokud stav uz zname, vratime z tabulky
        /*
        if ( vypocitane[state.getPolozky().size()][state.getNosnost()] != null ) {
            return vypocitane[state.getAktualniZatizeni()][state.getAktualniCena()];
        }
        */

        // aktualne resene polozky (jejich kopie) a definice polozky
        List<BatohItem> aktualniPolozky1 = new ArrayList<BatohItem>(state.getPolozky());
        List<BatohItem> aktualniPolozky2 = new ArrayList<BatohItem>(state.getPolozky());
        int vahaOdebiranePolozky = aktualniPolozky1.get(aktualniPolozky1.size() - 1).getVaha();
        int cenaOdebiranePolozky = aktualniPolozky1.get(aktualniPolozky1.size() - 1).getHodnota();
        BatohItem odebiranaPolozka1 = aktualniPolozky1.get(aktualniPolozky1.size() - 1); // asi by sli sjednotit
        BatohItem odebiranaPolozka2 = aktualniPolozky2.get(aktualniPolozky2.size() - 1); // s touto polozkou


        // spustime jednu vetev rekurze, kde n-ta polozka *JE* a snizime mozne zatizeni batohu
        // (X1, C1, m1) = KNAP(V-{vn}, C-{cn}, M-vn)
        Batoh novyBatoh1 = new Batoh(state.getNosnost() - vahaOdebiranePolozky);
        aktualniPolozky1.remove(odebiranaPolozka1);
        novyBatoh1.setPolozky(aktualniPolozky1);
        Batoh stavKdePolozkaJe = solveInstance(novyBatoh1);


        // spustime druhou vetev rekurze, kde n-ta polozka *NENI*
        // (X0, C0, m0) = KNAP(V-{vn}, C-{cn}, M)
        Batoh novyBatoh2 = new Batoh(state.getNosnost());
        aktualniPolozky2.remove(odebiranaPolozka2);
        novyBatoh2.setPolozky(aktualniPolozky2);
        Batoh stavKdePolozkaNeni = solveInstance(novyBatoh2);
        // vypocitane[state.getAktualniZatizeni() - vahaOdebiranePolozky][state.getAktualniCena() - cenaOdebiranePolozky] = stavKdePolozkaNeni;


        // porovname oba stavy
        // if (C1+cn) > C0 return(X1.1,  C1+cn, m1+vn)
        //            else return(X0.0, C0, m0)
        if ( ((stavKdePolozkaJe.getAktualniCena() + cenaOdebiranePolozky) >
            stavKdePolozkaNeni.getAktualniCena()) ) {

            // upravime polozku pro vraceni
            stavKdePolozkaJe.getPolozky().add(odebiranaPolozka1);
            stavKdePolozkaJe.setAktualniCena(stavKdePolozkaJe.getAktualniCena() + odebiranaPolozka1.getHodnota());
            // vypocitane[state.getAktualniZatizeni()][state.getAktualniCena()] = stavKdePolozkaJe;
            System.out.println("Vracim polozku (M=" + state.getNosnost() +
                            ", n=" + state.getPolozky().size() +
                            ", sumaV=" + state.getAktualniZatizeni() +
                            ", sumaC=" + state.getAktualniCena() + ")");
            return stavKdePolozkaJe;

        } else {
            System.out.println("Vracim polozku (M=" + state.getNosnost() +
                            ", n=" + state.getPolozky().size() +
                            ", sumaV=" + state.getAktualniZatizeni() +
                            ", sumaC=" + state.getAktualniCena() + ")");
            return stavKdePolozkaNeni;
        }
    }

    /**
     * Je to trivialni reseni?
     * Pseudokod:
     * - return(isEmpty(V) or M=0 or M<0)
     *
     * @param state
     * @return boolean
     */
    private boolean isTrivialInstance(Batoh state) {
        return ( (state.getNosnost() < 1) ||
                ( state.polozky.isEmpty() ) );
    }

    /**
     * Vypise vypocitane stavy
     */
    private void printVypocitane() {
        System.out.println("Vypisuji vypocitane stavy:");
        for (int i = 0; i < vypocitane.length; i++) {
            for (int j = 0; j < vypocitane[i].length; j++) {
                if ( vypocitane[i][j] != null ) {
                    Batoh batohs = vypocitane[i][j];
                    System.out.print("{" + batohs.getAktualniCena() + "," + batohs.getAktualniZatizeni() + "}");
                }
            }
        }
    }

}
