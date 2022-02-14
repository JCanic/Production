package hr.java.production.enumeration;

/**
 * used as a list of available cities to choose from when setting up address
 */
public enum Cities {

    ZAGREB("Zagreb",10000),
    RIJEKA("Rijeka",51000),
    OSIJEK("Osijek",31000),
    SPLIT("Split",21000),
    VARAZDIN("Varazdin",42000);

    private final String name;
    private final Integer postalCode;

    /**
     * Initializes a new Cities object
     * @param name city's name
     * @param postalCode city's postal code
     */
    Cities(String name, Integer postalCode) {
        this.name=name;
        this.postalCode=postalCode;
    }

    public String getName() {
        return name;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return name;

    }
}
