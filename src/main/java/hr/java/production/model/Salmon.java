package hr.java.production.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * presents salmon which contains it's name, category, width, height, length, production cost, selling price
 * and weight
 */
public class Salmon extends Item implements Edible{

    private static final Integer KILOCALORIES_PER_KILOGRAM=1790;
    BigDecimal weight;

    /** used for initializing a new Salmon and takes name, category, width, height, length, production cost,
     * selling price and weight as values
     * @param name presents name
     * @param category presents category
     * @param width presents salmon's width
     * @param height presents salmon's height
     * @param length presents salmon's length
     * @param productionCost presents salmon's production cost
     * @param sellingPrice presents salmon's selling price
     * @param weight presents salmon's weight
     */
    public Salmon(long id, String name, Category category, BigDecimal width, BigDecimal height, BigDecimal length, BigDecimal productionCost, BigDecimal sellingPrice, BigDecimal discountP, BigDecimal weight) {
        super(id,name, category, width, height, length, productionCost, sellingPrice, discountP);
        this.weight = weight;
    }

    public Salmon(BigDecimal discount, BigDecimal weight) {
        super(discount);
        this.weight = weight;
    }

    /**
     * default
     */
    public Salmon (){}

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    /**
     * sets selling price by multiplying weight by calculated price
     * @param sellingPrice which is calculated by multiplying weight by
     *                     calculated price
     */
    @Override
    public void setSellingPrice(BigDecimal sellingPrice) {
        super.setSellingPrice(this.weight.multiply(calculatePrice()));
    }

    /**
     * calculates number of kilocalories by multiplying kilocalories by weight
     * @return a number of kilocalories as a numeric value
     */
    @Override
    public Integer calculateKilocalories() {
        return KILOCALORIES_PER_KILOGRAM*this.weight.intValue();
    }

    /**
     * calculates prices by multiplying weight by selling price
     * @return price as numeric value
     */
    @Override
    public BigDecimal calculatePrice() {
        return this.weight.multiply((this.sellingPrice));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salmon)) return false;
        if (!super.equals(o)) return false;
        Salmon salmon = (Salmon) o;
        return Objects.equals(getWeight(), salmon.getWeight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWeight());
    }
}
