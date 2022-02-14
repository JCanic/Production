package hr.java.production.model;

import java.math.BigDecimal;

/**
 * has methods which need to be implemented in edible items
 */
public interface Edible {

    /**
     * needs to be implemented with a goal to calculate kilocalories
     * @return number of kilocalories
     */
    Integer calculateKilocalories();

    /**
     * needs to be implemented with a goal to calculate price
     * @return price
     */
    BigDecimal calculatePrice();

}
