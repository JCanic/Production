package hr.java.production.generics;

import hr.java.production.model.Item;
import hr.java.production.model.Laptop;
import hr.java.production.model.Store;

import java.util.List;
import java.util.Set;

public class TechnicalStore <T extends Laptop> extends Store {

    List<T> listOfItems;

    public TechnicalStore(String name, long id, String webAddress, Set<Item> items, List<T> listOfItems) {
        super(name, id, webAddress, items);
        this.listOfItems = listOfItems;
    }

    public TechnicalStore (List<T> listOfItems){
        this.listOfItems = listOfItems;
    }

    public TechnicalStore (){};

    //public TechnicalStore(){};

    public List<T> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(List<T> listOfItems) {
        this.listOfItems = listOfItems;
    }

}
