package hr.java.production.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * presents discount which sets discount amount
 */
public record Discount(BigDecimal discountAmount) implements Serializable {}

