package hr.java.production.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * presents laptop which contains Item values and warranty duration
 */
public final class Laptop extends Item implements Technical{

    Integer warrantyPeriodMonths;

    /**
     * initializes a new Laptop and takes name, category, width, height, length, production cost,
     * selling price and warranty duration in months as values
     * @param name presents Laptop's name
     * @param category presents Laptop's category
     * @param width presents Laptop's width
     * @param height presents Laptop's height
     * @param length presents Laptop's length
     * @param productionCost presents Laptop's prodution cost
     * @param sellingPrice presents Laptop's selling price
     * @param warrantyPeriodMonths presents Laptop's warranty duration in months
     */
    public Laptop(long id, String name, Category category, BigDecimal width, BigDecimal height, BigDecimal length, BigDecimal productionCost, BigDecimal sellingPrice, BigDecimal discountP, Integer warrantyPeriodMonths) {
        super(id,name, category, width, height, length, productionCost, sellingPrice, discountP);
        this.warrantyPeriodMonths = warrantyPeriodMonths;
    }

    public Laptop(BigDecimal discount, Integer warrantyPeriodMonths) {
        super(discount);
        this.warrantyPeriodMonths = warrantyPeriodMonths;
    }

    /**
     * default
     */
    public Laptop (){}

    public Integer getWarrantyPeriodMonths() {
        return warrantyPeriodMonths;
    }

    public void setWarrantyPeriodMonths(Integer warrantyPeriodMonths) {
        this.warrantyPeriodMonths = warrantyPeriodMonths;
    }

    @Override
    /**
     * implemented method that sets warranty period (duration) in months and returns it as a numeric value
     */
    public Integer warrantyPeriod() {
        return warrantyPeriodMonths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Laptop)) return false;
        if (!super.equals(o)) return false;
        Laptop laptop = (Laptop) o;
        return Objects.equals(getWarrantyPeriodMonths(), laptop.getWarrantyPeriodMonths());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWarrantyPeriodMonths());
    }
}
