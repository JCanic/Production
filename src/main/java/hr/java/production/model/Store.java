package hr.java.production.model;

import java.io.Serializable;
import java.util.*;

/**
 * presents store which contains name, web address and items
 */
public class Store extends NamedEntity implements Serializable {

    String webAddress;
    Item[] items;
    Set<Item> itemSet;

    /**
     * takes name, web address and items as values and initializes a new store
     * @param name presents factory name
     * @param webAddress presents store web address
     * @param items presents items
     */
    public Store(String name, long id, String webAddress, Set<Item> items) {
        super(name, id);
        this.webAddress = webAddress;
        this.items = items.toArray(new Item[0]);

    }


    public Store(String name, long id, String webAddress) {
        super(name, id);
        this.webAddress = webAddress;

    }

    public Store(String name,String webAddress) {
        super(name);
        this.webAddress = webAddress;

    }

    public Store() {

    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public String printItems (){
        String itemsString="";
        List<Item> itemArrayList = new ArrayList<>(itemSet);
        for (int i=0;i<itemArrayList.size();i++){
            itemsString+=itemArrayList.get(i).getName()+", ";
        }
        return itemsString;
    }

    public Set<Item> getItemSet() {
        return itemSet;
    }

    public void setItemSet(Set<Item> itemSet) {
        this.itemSet = itemSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store)) return false;
        if (!super.equals(o)) return false;
        Store store = (Store) o;
        return Objects.equals(getWebAddress(), store.getWebAddress()) && Arrays.equals(getItems(), store.getItems());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), getWebAddress());
        result = 31 * result + Arrays.hashCode(getItems());
        return result;
    }

    @Override
    public String toString() {
        Integer itemLength= items.length;
        return itemLength.toString();
    }
}
