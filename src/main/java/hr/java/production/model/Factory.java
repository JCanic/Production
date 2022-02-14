package hr.java.production.model;

import java.io.Serializable;
import java.util.*;

/**
 * presents factory which contains name, address and items
 */
public class Factory extends NamedEntity implements Serializable {

    Address address;
    Item[] items;
    Set<Item> itemSet;

    /**
     * takes name, address and items as values and initializes a new factory
     * @param name presents factory name
     * @param address presents factory address
     * @param items presents items in the factory
     */
    public Factory(String name, long id, Address address, Set<Item> items) {
        super(name, id);
        this.address = address;
        this.itemSet= items;
    }

    public Factory(String name, long id, Address address) {
        super(name, id);
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public String printItems (){
        String itemsString="";
        List<Item>itemArrayList = new ArrayList<>(itemSet);
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
        if (!(o instanceof Factory)) return false;
        if (!super.equals(o)) return false;
        Factory factory = (Factory) o;
        return Objects.equals(getAddress(), factory.getAddress()) && Arrays.equals(getItems(), factory.getItems());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), getAddress());
        result = 31 * result + Arrays.hashCode(getItems());
        return result;
    }
}
