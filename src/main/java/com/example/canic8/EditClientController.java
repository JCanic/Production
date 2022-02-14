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
 * updates client by giving the option to edit its fields
 */
public class EditClientController implements Initializable {

    private static List <Client> clients = new ArrayList<>();
    private static List <String> citiesInCB = new ArrayList<>();
    String selection;

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

        name.setText(ClientsController.getSelectedClient().getName());
        lastName.setText(ClientsController.getSelectedClient().getLastName());
        datePicker.setValue(ClientsController.getSelectedClient().getDateOfBirth().toLocalDate());
        cityNameField.setText(ClientsController.getSelectedClient().getAddress().getCityName());
        streetNameField.setText(ClientsController.getSelectedClient().getAddress().getStreet());
        houseNumberField.setText(ClientsController.getSelectedClient().getAddress().getHouseNumber());
        postalCodeField.setText(ClientsController.getSelectedClient().getAddress().getPostalCode().toString());

    }

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
        address.setId(ClientsController.getSelectedClient().getAddress().getId());

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
            executorService1.execute(new UpdateAddressThread(address));
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
     * updates client by giving option to change its name or last name or address or date of birth
     */
    public void save(){
        Long id = 0L;

        id = ClientsController.getSelectedClient().getId();

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

        Address address = ClientsController.getSelectedClient().getAddress();

        if (errorMessages.isEmpty()) {
            Client client = new Client(id, clientName,clientLastName, dateOfBirth, address);

            ExecutorService exs = Executors.newCachedThreadPool();
            exs.execute(new UpdateClientTask(client));
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
