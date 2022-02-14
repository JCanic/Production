package hr.java.production.threads;

import hr.java.production.model.Item;
import hr.java.production.sort.ProductionSorter;

import java.util.List;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

public class SortingItemsThread implements Runnable{

    private static List<Item> items;

    public SortingItemsThread(List<Item> itemsList) {
        this.items=itemsList;
    }

    @Override
    public void run() {
        items.sort(new ProductionSorter());
    }
}
