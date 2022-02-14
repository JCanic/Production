package hr.java.production.model;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Presents client that contains first and last name, date of birth and address
 */

public class Client extends NamedEntity {

    private String lastName;
    private java.sql.Date dateOfBirth;
    Address address;

    /**
     * takes id, first and last name, date of birth and address and initializes a new client
     * @param id
     * @param name
     * @param lastName
     * @param dateOfBirth
     * @param address
     */
    public Client(long id, String name, String lastName, java.sql.Date dateOfBirth, Address address) {
        super(name, id);
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public java.sql.Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Client{" +
                "lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address=" + address +
                '}';
    }
}
