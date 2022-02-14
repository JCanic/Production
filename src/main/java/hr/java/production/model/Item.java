package hr.java.production.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * presents item which contains it's category, width, height, length, production cost, selling price, volume
 * and discount
 */
public class Item extends NamedEntity implements Serializable {

    Category category;
    BigDecimal width;
    BigDecimal height;
    BigDecimal length;
    BigDecimal productionCost;
    BigDecimal sellingPrice;
    BigDecimal volume;
    Discount discount;

    /**
     * takes name category, width, height, length, production cost and selling price as values
     * @param name presents item name
     * @param category presents item's category
     * @param width presents item's width
     * @param height presents item's height
     * @param length presents item's length
     * @param productionCost presents item's production cost
     * @param sellingPrice presents item's selling price
     */
    public Item(long id,String name, Category category, BigDecimal width, BigDecimal height, BigDecimal length,
                BigDecimal productionCost, BigDecimal sellingPrice, BigDecimal discountP) {
        super(name, id);
        this.category = category;
        this.width = width;
        this.height = height;
        this.length = length;
        this.productionCost = productionCost;
        this.sellingPrice = sellingPrice;
        this.discount = new Discount((discountP));
    }

    public Item(BigDecimal discount){
        this.discount=new Discount(discount);
    }

    /**
     * default
     */
    public Item(){
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getProductionCost() {
        return productionCost;
    }

    public void setProductionCost(BigDecimal productionCost) {
        this.productionCost = productionCost;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public BigDecimal getDiscount() {
        return discount.discountAmount();
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }


    /**
     * calculates selling price by multiplying current selling price by discount amouunt
     * @param sellingPrice presents final selling price
     */
    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice.subtract(sellingPrice.multiply(discount.discountAmount()));
    }

    /**
     * calculates item's volume
     * @return volume (numeric value)
     */
    public BigDecimal getVolume(){
        volume=height.multiply(width).multiply((length));
        return volume;
    }

    public void setVolume(BigDecimal volume){
        this.volume=volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        if (!super.equals(o)) return false;
        Item item = (Item) o;
        return Objects.equals(getCategory(), item.getCategory()) && Objects.equals(getWidth(), item.getWidth()) && Objects.equals(getHeight(), item.getHeight()) && Objects.equals(getLength(), item.getLength()) && Objects.equals(getProductionCost(), item.getProductionCost()) && Objects.equals(getSellingPrice(), item.getSellingPrice()) && Objects.equals(getVolume(), item.getVolume()) && Objects.equals(discount, item.discount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCategory(), getWidth(), getHeight(), getLength(), getProductionCost(), getSellingPrice(), getVolume(), discount);
    }

    @Override
    public String toString() {
        return getName();
    }
}
