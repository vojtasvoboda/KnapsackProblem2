package batoh2;

import java.util.List;

/**
 * Batoh - potomek ItemsContainer, akorat navic ma nosnost a aktualni zatizeni
 * @author Bc. Vojtěch Svoboda <svobovo3@fit.cvut.cz>
 */
public class Batoh extends ItemsContainer {

    /**
     * Batoh ma navic nosnost a aktualni zatizeni
     */
    public int nosnost;
    public int aktualniZatizeni;
    public int aktualniCena;

    /**
     * Konstruktor, nastavuje nosnost
     * @param nosnost
     */
    public Batoh(int nosnost) {
        super();
        this.nosnost = nosnost;
    }

    /**
     * Prida polozku do batohu - wrapper pro predani pouze parametru
     * @param hodnota
     * @param vaha
     * @return boolean - povedlo se?
     */
    public boolean addItem(int hodnota, int vaha) {
        return addItemExec(new BatohItem(hodnota,vaha));
    }

    /**
     * Prida polozku do batohu - wrapper pro pretizeni funkce addItem
     * @param item
     * @return boolean - povedlo se?
     */
    public boolean addItem(BatohItem item) {
        return addItemExec(item);
    }

    /**
     * Prida polozku do batohu
     * @param item
     * @return boolean - povedlo se?
     */
    private boolean addItemExec(BatohItem item) {
        /* pokud je batoh plny, tak nejde */
        if ( this.isFull() ) return false;
        /* pokud je polozka moc velka, tak nejde */
        if ( this.zbyvaKapacita() < item.getVaha() ) return false;
        this.polozky.add(item);
        this.aktualniZatizeni += item.getVaha();
        this.aktualniCena += item.getHodnota();
        return true;
    }

    /**
     * Vrati jestli je batoh plny, nebo ne
     * @return
     */
    public boolean isFull() {
        if ( this.nosnost < this.aktualniZatizeni ) {
            System.err.println("Batoh je plny!");
            return true;
        }
        return false;
    }

    /**
     * Vrati kolik jeste zbyva kapacita batohu
     * @return
     */
    public int zbyvaKapacita() {
        return this.nosnost - this.aktualniZatizeni;
    }

    /**
     * Vrati pole polozek - u batohu staci polozky seradit az pri vyberu
     * @return
     */
    @Override
    public List<BatohItem> getPolozky() {
        this.orderItems();
        return polozky;
    }

    /**
     * Vycistime batoh
     */
    @Override
    public void clear() {
        this.polozky.clear();
        this.aktualniCena = 0;
        this.aktualniZatizeni = 0;
    }

    public int getAktualniZatizeni() {
        return aktualniZatizeni;
    }

    public void setAktualniZatizeni(int aktualniZatizeni) {
        this.aktualniZatizeni = aktualniZatizeni;
    }

    public int getAktualniCena() {
        return this.aktualniCena;
    }

    public void setNosnost(int nosnost) {
        this.nosnost = nosnost;
    }

    public int getNosnost() {
        return this.nosnost;
    }

    public void setAktualniCena(int aktualniCena) {
        this.aktualniCena = aktualniCena;
    }

}
