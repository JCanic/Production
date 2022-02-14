package hr.java.production.generics;

import hr.java.production.model.*;

import java.util.List;
import java.util.Set;

public class FoodStore <T extends Edible> extends Store {

    List<T> listOfItems;

    public FoodStore(String name, long id, String webAddress, Set<Item> items, List<T> listOfItems) {
        super(name, id, webAddress, items);
        this.listOfItems = listOfItems;
    }

    public FoodStore (List <T> listOfItems){
        this.listOfItems=listOfItems;
    }

    public FoodStore(){};

    public List<T> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(List<T> listOfItems) {
        this.listOfItems = listOfItems;
    }

}
