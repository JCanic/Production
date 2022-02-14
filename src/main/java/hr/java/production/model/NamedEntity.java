package hr.java.production.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * presents a named entity
 */
public abstract class NamedEntity implements Serializable {

    private String name;
    private long id;

    /**
     * initializes named entity and takes name as value
     * @param name presents name
     */
    public NamedEntity(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public NamedEntity(String name){
        this.name=name;
    }

    /**
     * default
     */
    public NamedEntity (){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedEntity that = (NamedEntity) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
