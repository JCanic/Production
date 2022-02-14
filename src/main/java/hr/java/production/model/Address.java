package hr.java.production.model;

import hr.java.production.enumeration.Cities;

import java.io.Serializable;
import java.util.Objects;

/**
 * presents Address which contains street name, house number, city name and postal code
 */

public class Address implements Serializable {

    private String street;
    private String houseNumber;
    private Cities city;
    private String cityName;
    private Integer postalCode;
    private Long id;

    /**
     * takes street name, house number, city name and postal code as String values
     * @param street presents street name
     * @param houseNumber presents house number
     * @param city is an enumeration and presents city's name and postal code
     */
    public Address(Cities city,String street, String houseNumber) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.city=city;
    }

    public Address(Long id,String street, String houseNumber, String cityName, Integer postalCode) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.cityName = cityName;
        this.postalCode = postalCode;
        this.id = id;
    }

    /**
     * Builder pattern used for initializing a new Address
     */
    public static class Builder {
        Cities city;
        String street;
        String houseNumber;

        /**
         * Used for initializing Address with city name and postal code
         * @param city is enumeration (Cities) used for initializing city's name and postal code
         */
        public Builder (Cities city){
            this.city=city;
        }

        /**
         * Used for initializing Address with street name
         * @param street is a String that presents street name
         * @return a Builder object
         */
        public Builder atStreet (String street){
            this.street=street;
            return this;
        }

        /**
         * Used for initializing Address with house number
         * @param houseNumber is a String that presents house number
         * @return a Builder object
         */
        public Builder atHouseNumber (String houseNumber){
            this.houseNumber=houseNumber;
            return this;
        }

        /**
         * Used for initializing Address with postal code
         * @param postalCode is a String that presents postal code
         * @return a Builder object
         */

        /**
         * Used for initializing a new address and setting city name, house number, street and address
         * @return instance of Address
         */
        public Address build (){
            Address address = new Address();
            address.city=city;
            address.houseNumber=houseNumber;
            address.street=street;

            return address;
        }

    }

    /**
     * default
     */
    public Address (){}

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Cities getCity() {
        return city;
    }

    public void setCity(Cities city) {
        this.city = city;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getHouseNumber(), address.getHouseNumber()) && getCity() == address.getCity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStreet(), getHouseNumber(), getCity());
    }

    @Override
    public String toString() {
        return cityName + ", "+street+", "+houseNumber+", "+postalCode;
    }
}
