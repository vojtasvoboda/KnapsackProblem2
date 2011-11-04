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
        // do pameti ulozime trivialni stav
        System.out.println("Vkladam do cache trivialni stav (0,0)");
        vypocitane[0][0] = new Batoh(this.batoh.getNosnost());
        // vytvorime plny batoh
        Batoh startovni = new Batoh(this.batoh.getNosnost());
        startovni.setPolozky(barak.getPolozky());
        // spustime rekurzivni vypocet
        Batoh cilovy = solveInstance(startovni, barak.polozky.size());
        /* konec algoritmu, takze musime do batohu dat nejlepsi vysledek */
        this.batoh.setPolozky(cilovy.getPolozky());
        this.batoh.setAktualniCena(cilovy.getAktualniCena());
        this.batoh.setAktualniZatizeni(cilovy.getAktualniZatizeni());
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
     * (V, C, M) = V mnozina veci, C cena batohu, M zatizeni batohu
     *
     * @param state
     * @return
     */
    private Batoh solveInstance(Batoh state, int cisloPolozky) {

        System.out.println("Vstupuji do instance (" + state.getPolozky().size() + "," + state.getNosnost() + "), pro polozku " + cisloPolozky);

        // pokud je to trivialni reseni, return
        if ( isTrivialInstance(state, cisloPolozky) ) {
            System.out.println("Je to trivialni, takze vracim (0,0)");
            return new Batoh(this.batoh.getNosnost());
        }

        // pokud stav uz zname, vratime z tabulky
        /*
        if ( vypocitane[state.getPolozky().size()][state.getNosnost()] != null ) {
            return vypocitane[state.getAktualniZatizeni()][state.getAktualniCena()];
        }
        */

        // aktualne resene polozky
        List<BatohItem> aktualniPolozky = new ArrayList<BatohItem>(state.getPolozky());
        int cenaOdebiranePolozky = aktualniPolozky.get(cisloPolozky).getHodnota();
        int vahaOdebiranePolozky = aktualniPolozky.get(cisloPolozky).getVaha();

        // spustime jednu vetev rekurze, kde n-ta polozka je (nechame ji tam), ale snizime mozne zatizeni batohu
        Batoh novyBatoh = new Batoh(this.batoh.getNosnost() - vahaOdebiranePolozky);
        novyBatoh.setPolozky(aktualniPolozky);
        Batoh stavKdePolozkaJe = solveInstance(state, cisloPolozky - 1);
        vypocitane[state.getAktualniZatizeni()][state.getAktualniCena()] = stavKdePolozkaJe;

        // zjistime aktualni polozky a jednu odebereme kvuli druhe vetvi rekurze
        aktualniPolozky.remove(cisloPolozky);
        Batoh novyBatoh2 = new Batoh(this.batoh.getNosnost());
        novyBatoh2.setPolozky(aktualniPolozky);

        // spustime druhou vetev rekurze, kde n-ta polozka neni
        Batoh stavKdePolozkaNeni = solveInstance(novyBatoh2, cisloPolozky - 1);
        vypocitane[state.getAktualniZatizeni() - vahaOdebiranePolozky][state.getAktualniCena() - cenaOdebiranePolozky] = stavKdePolozkaNeni;

        // porovname oba stavy
        if (stavKdePolozkaJe.getAktualniCena() >
            stavKdePolozkaNeni.getAktualniCena() ) {
            // zaroven ale nesmi byt prekrocena kapacita batohu!!!
            return stavKdePolozkaJe;
        } else {
            return stavKdePolozkaNeni;
        }
    }

    /**
     * Je to trivialni reseni?
     * @param state
     * @return boolean
     */
    private boolean isTrivialInstance(Batoh state, int cisloPolozky) {
        return ( (state.getAktualniCena() < 1) ||
                ( state.polozky.isEmpty() ) ||
                ( cisloPolozky == 0) );
    }

}
