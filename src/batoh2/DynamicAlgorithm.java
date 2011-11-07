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
    private boolean DEBUG = false;

    public DynamicAlgorithm(Batoh batoh, Barak barak) {
        this.batoh = batoh;
        this.barak = barak;
        this.vypocitane = new Batoh[barak.polozky.size() + 2][batoh.getNosnost() + 1];
    }

    public void computeStolenItems() {
        // vytvorime plny batoh, kde budou vsechny polozky z baraku
        Batoh startovni = new Batoh(this.batoh.getNosnost());
        startovni.setPolozky(barak.getPolozky());
        // spustime rekurzivni vypocet
        Batoh cilovy = solveInstance(startovni);
        /* konec algoritmu, takze musime do batohu dat nejlepsi vysledek */
        if ( DEBUG ) {
            System.out.println("Rekurze skoncila.");
            System.out.println("Cilovy stav je (M=" + cilovy.getNosnost() +
                                ", n=" + cilovy.getPolozky().size() +
                                ", sumaV=" + cilovy.getAktualniZatizeni() +
                                ", sumaC=" + cilovy.getAktualniCena() + ")");
        }
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
     * (V, C, M) = V vahy veci, C ceny veci, M zatizeni batohu
     *
     * @param state
     * @return
     */
    private Batoh solveInstance(Batoh state) {

        if ( DEBUG ) {
            System.out.println("Vstupuji do instance (M=" + state.getNosnost() +
                                ", n=" + state.getPolozky().size() +
                                ", sumaV=" + state.getAktualniZatizeni() +
                                ", sumaC=" + state.getAktualniCena() + ")");
        }


        // pokud je to trivialni reseni, tak vrat trivialni
        if ( isTrivialInstance(state) ) return getTrivialInstance(state);


        // pokud stav uz zname, vratime z tabulky kopii
        if ( vypocitane[state.getPolozky().size()][state.getNosnost()] != null ) {
            if ( DEBUG ) { System.out.println("Nasel jsem stav (" + state.getPolozky().size() +
                                "," + state.getNosnost() + ") v tabulce, vracim kopii."); }
            return vypocitane[state.getPolozky().size()][state.getNosnost()].clone();
        }


        // aktualne resene polozky (jejich kopie) a definice polozky pro odebrani
        List<BatohItem> aktualniPolozky1 = new ArrayList<BatohItem>(state.getPolozky());
        List<BatohItem> aktualniPolozky2 = new ArrayList<BatohItem>(state.getPolozky());
        BatohItem odebiranaPolozka1 = aktualniPolozky1.get(aktualniPolozky1.size() - 1); // TODO asi by sli sjednotit
        BatohItem odebiranaPolozka2 = aktualniPolozky2.get(aktualniPolozky2.size() - 1); // TODO s touto polozkou
        int vahaOdebiranePolozky = odebiranaPolozka1.getVaha();
        int cenaOdebiranePolozky = odebiranaPolozka1.getHodnota();


        // spustime jednu vetev rekurze, kde n-ta polozka *JE* a snizime mozne zatizeni batohu
        // (X1, C1, m1) = KNAP(V-{vn}, C-{cn}, M-vn)
        Batoh novyBatoh1 = new Batoh(state.getNosnost() - vahaOdebiranePolozky);
        aktualniPolozky1.remove(odebiranaPolozka1);
        novyBatoh1.setPolozky(aktualniPolozky1);
        Batoh stavKdePolozkaJe = solveInstance(novyBatoh1);
        // pokud neni takovy stav ulozeny v tabulce, tak udelame jeho kopii a ulozime ho
        if ( (stavKdePolozkaJe.getPolozky().size() <= barak.polozky.size()) &
             (vypocitane[novyBatoh1.getPolozky().size()][novyBatoh1.getNosnost()] == null ) ) {
            // ulozime kopii do tabulky vypocitanych
            Batoh stavKdePolozkaJeCopy = stavKdePolozkaJe.clone();
            if ( DEBUG ) { System.out.println("Ukladam stav (" + stavKdePolozkaJe.getPolozky().size() +
                                    "," + stavKdePolozkaJe.getNosnost() + ") do tabulky."); }
            vypocitane[novyBatoh1.getPolozky().size()][novyBatoh1.getNosnost()] = stavKdePolozkaJeCopy;
        }


        // spustime druhou vetev rekurze, kde n-ta polozka *NENI*
        // (X0, C0, m0) = KNAP(V-{vn}, C-{cn}, M)
        Batoh novyBatoh2 = new Batoh(state.getNosnost());
        aktualniPolozky2.remove(odebiranaPolozka2);
        novyBatoh2.setPolozky(aktualniPolozky2);
        Batoh stavKdePolozkaNeni = solveInstance(novyBatoh2);
        // pokud neni takovy stav ulozeny v tabulce, tak udelame jeho kopii a ulozime ho
        if ( (stavKdePolozkaNeni.getPolozky().size() <= barak.polozky.size()) &
             (vypocitane[novyBatoh2.getPolozky().size()][novyBatoh2.getNosnost()] == null ) ) {
            // ulozime kopii do tabulky vypocitanych
            Batoh stavKdePolozkaNeniCopy = stavKdePolozkaNeni.clone();
            if ( DEBUG ) { System.out.println("Ukladam stav (" + stavKdePolozkaNeni.getPolozky().size() +
                                    "," + stavKdePolozkaNeni.getNosnost() + ") do tabulky."); }
            vypocitane[novyBatoh2.getPolozky().size()][novyBatoh2.getNosnost()] = stavKdePolozkaNeniCopy;
        }


        // porovname oba vracene stavy - pseudokod:
        // if (C1+cn) > C0 return(X1.1, C1+cn, m1+vn)
        //            else return(X0.0, C0, m0)
        if ( ((stavKdePolozkaJe.getAktualniCena() + cenaOdebiranePolozky) >
               stavKdePolozkaNeni.getAktualniCena()) ) {

            // zkusime tam vratit polozku zpet a kdy to projde, tak vratime novy stav
            stavKdePolozkaJe.setNosnost(state.getNosnost());
            if ( stavKdePolozkaJe.addItem(odebiranaPolozka1) ) {
                if ( DEBUG ) {
                    System.out.println("Vracim polozku kde JE (M=" + stavKdePolozkaJe.getNosnost() +
                                    ", n=" + stavKdePolozkaJe.getPolozky().size() +
                                    ", sumaV=" + stavKdePolozkaJe.getAktualniZatizeni() +
                                    ", sumaC=" + stavKdePolozkaJe.getAktualniCena() + ")");
                }
                return stavKdePolozkaJe;

            } else {
                if ( DEBUG ) {
                    System.out.println("Nepovedlo se pridat, vracim polozku kde NENI (M=" + stavKdePolozkaNeni.getNosnost() +
                                    ", n=" + stavKdePolozkaNeni.getPolozky().size() +
                                    ", sumaV=" + stavKdePolozkaNeni.getAktualniZatizeni() +
                                    ", sumaC=" + stavKdePolozkaNeni.getAktualniCena() + ")");
                }
                return stavKdePolozkaNeni;
            }

        } else {
            if ( DEBUG ) {
                System.out.println("Vracim polozku kde NENI (M=" + stavKdePolozkaNeni.getNosnost() +
                                ", n=" + stavKdePolozkaNeni.getPolozky().size() +
                                ", sumaV=" + stavKdePolozkaNeni.getAktualniZatizeni() +
                                ", sumaC=" + stavKdePolozkaNeni.getAktualniCena() + ")");
            }
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
     * Vrati trivialni reseni
     * @param state
     * @return
     */
    private Batoh getTrivialInstance(Batoh state) {
        if ( DEBUG ) {
            System.out.println("Vracim polozku TRIV (M=" + state.getNosnost() +
                                ", n=" + state.getPolozky().size() +
                                ", sumaV=" + state.getAktualniZatizeni() +
                                ", sumaC=" + state.getAktualniCena() +
                                "), polozky nuluji a nosnost take pokud byla mensi jak 0.");
        }
        // TODO return vypocitane[0][0];
        if ( state.getNosnost() <= 0 ) state.setNosnost(0);
        state.setPolozky();
        return state;
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
