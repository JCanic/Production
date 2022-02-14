package hr.java.production.sort;

import hr.java.production.model.Item;

import java.util.Comparator;

/**
 * used for sorting ascending or descending
 */

public class ProductionSorter implements Comparator<Item> {

    public ProductionSorter(){};

    /**
     * compares items selling prices
     * @param a first item in comparison
     * @param b second item in comparison
     * @return higher or lower value based on comparison
     */
    @Override
    public int compare (Item a, Item b){
        return b.getSellingPrice().compareTo(a.getSellingPrice());
    }

    @Override
    public Comparator<Item> reversed() {
        return Comparator.super.reversed();
    }
}
