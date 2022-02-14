package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * adds new client into the list of clients and put it in the database
 */

public class AddNewClientController implements Initializable {

    @FXML
    private TextField name;

    @FXML
    private TextField lastName;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField cityNameField;

    @FXML
    private TextField streetNameField;

    @FXML
    private TextField houseNumberField;

    @FXML
    private TextField postalCodeField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new GetAddressesThread());
        executorService.shutdown();

        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Address> addresses = Database.getAddressesFromDatabase();
    }

    /**
     * saves address to the database
     */

    public void saveAddress() {
        Address address = new Address();
        StringBuilder errorMessages = new StringBuilder();

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetAddressesThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Address> addresses = Database.getAddressesFromDatabase();
        Long AddressID = (long) addresses.size() + 1;
        address.setId(AddressID);

        String cityName = cityNameField.getText();
        if (cityName.isEmpty()) {
            errorMessages.append("Address must have a city name!\n");
        } else
            address.setCityName(cityName);

        String streetName = streetNameField.getText();
        if (streetName.isEmpty()) {
            errorMessages.append("Address must have a street name!\n");
        } else
            address.setStreet(streetName);

        String houseNumber = houseNumberField.getText();
        if (houseNumber.isEmpty()) {
            errorMessages.append("Address must have a house number!\n");
        } else
            address.setHouseNumber(houseNumber);

        Integer postalCode = Integer.valueOf(postalCodeField.getText());
        if (postalCode.toString().isEmpty()) {
            errorMessages.append("Address must have a postal code!\n");
        } else
            address.setPostalCode(postalCode);

        if (errorMessages.isEmpty()) {

            ExecutorService executorService1 = Executors.newCachedThreadPool();
            executorService1.execute(new SaveNewAddressThread(address));
            executorService1.shutdown();

            try {
                executorService1.awaitTermination(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save action succeeded!");
            alert.setHeaderText("Address data saved!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save action failed!");
            alert.setHeaderText("Address data not saved!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }
    }

    /**
     * adds new client to the database, if wrong, the message what should be fixed is shown
     */
    public void save(){
        Long id = 0L;

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new GetClientTask());
        executorService.shutdown();

        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Client> clients = Database.getClientsFromDatabase();
        id = (long) (clients.size() + 1);

        StringBuilder errorMessages = new StringBuilder();
        String clientName = name.getText();
        if (clientName.isEmpty()) {
            errorMessages.append("Client must have a name!\n");
        }

        String clientLastName = lastName.getText();
        if (clientLastName.isEmpty()) {
            errorMessages.append("Client must have a last name!\n");
        }

        Date dateOfBirth = Date.valueOf(datePicker.getValue());
        if (dateOfBirth==null) {
            errorMessages.append("Client must have a date of birth!\n");
        }

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetAddressesThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Address>addresses = Database.getAddressesFromDatabase();
        Address address = addresses.get(addresses.size()-1);

        if (errorMessages.isEmpty()) {
            Client client = new Client(id, clientName,clientLastName, dateOfBirth, address);

            ExecutorService exs = Executors.newCachedThreadPool();
            exs.execute(new SaveNewClientTask(client));
            exs.shutdown();

            try {
                exs.awaitTermination(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save action succeeded!");
            alert.setHeaderText("Client data saved!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save action failed!");
            alert.setHeaderText("Client data not saved!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }
    }


    public void choiceBoxAction(ActionEvent event){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});
    }

}
