package hr.java.production.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * presents a category which contains name and description
 */
public class Category extends NamedEntity implements Serializable {

    private String description;
    private String name;
    private long id;

    /**
     * Used for initializing a new Category with name and description
     * @param name presents category name
     * @param description presents category description
     */
    public Category(long id, String name, String description) {
        super(name, id);
        this.description = description;
    }

    public Category(String name, String description) {
        super(name);
        this.description = description;
    }


    /**
     * default
     */
    public Category(){}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        if (!super.equals(o)) return false;
        Category category = (Category) o;
        return Objects.equals(getDescription(), category.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDescription());
    }

    @Override
    public String toString() {
        return getName();
    }
}
