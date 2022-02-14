package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AddNewFactoryController implements Initializable {

    private static List <Item> itemsInDatabase = new ArrayList<>();
    private static List<Item> selectedItems = new ArrayList<>();
    private static List <String> itemsName = new ArrayList<>();
    private static List <String> citiesInCB = new ArrayList<>();
    String selection;

    @FXML
    private TextField name;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private ListView<String> items;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetItemsThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        itemsInDatabase=Database.getItemsFromDatabase();
        itemsName = itemsInDatabase.stream().map(NamedEntity::getName)
                .collect(Collectors.toList());

        items.getItems().addAll(itemsName);

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new GetAddressesThread());
        executorService.shutdown();

        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Address> addresses = Database.getAddressesFromDatabase();

        citiesInCB = addresses.stream().map(c->c.getCityName())
                .collect(Collectors.toList());

        choiceBox.getItems().addAll(citiesInCB);
        choiceBox.setOnAction(this::choiceBoxAction);
        items.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
            selection = items.getSelectionModel().getSelectedItem();
            for (int i=0;i<itemsInDatabase.size();i++){
                if (selection.equals(itemsInDatabase.get(i).getName())){
                    selectedItems.add(itemsInDatabase.get(i));
                }
            }
            }
        });
    }

    public void save(){
        Long id = 0L;

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new GetFactoriesThread());
        executorService.shutdown();

        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Factory> factories = Database.getFactoriesFromDatabase();
        id = (long) (factories.size() + 1);

        StringBuilder errorMessages = new StringBuilder();
        StringBuilder pickedItems = new StringBuilder();
        String factoryName = name.getText();
        if (factoryName.isEmpty()) {
            errorMessages.append("Factory must have a name!\n");
        }

        String cityName="";
        try{
            cityName=choiceBox.getValue().toString();
        }catch (NullPointerException ex) {
            errorMessages.append("Factory must have a city!\n");
        }

        Address address = new Address();

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetAddressesThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Address> addresses = Database.getAddressesFromDatabase();
        for (int i = 0; i < addresses.size(); i++) {
            if (cityName.equalsIgnoreCase(addresses.get(i).getCityName())) {
                address.setId(addresses.get(i).getId());
                address.setCityName(cityName);
                address.setPostalCode(addresses.get(i).getPostalCode());
                address.setStreet(addresses.get(i).getStreet());
                address.setPostalCode(addresses.get(i).getPostalCode());
            }
        }

            Set<Item> itemsSet = new HashSet<>();
            pickedItems.append("You have picked following items from the list:\n");
            for (int i = 0; i < selectedItems.size(); i++) {
                itemsSet.add(selectedItems.get(i));
                pickedItems.append(selectedItems.get(i).getName()).append("\n");
            }

            if (itemsSet.isEmpty()) {
                errorMessages.append("You must pick at least one item from the list!");
            }

            if (errorMessages.isEmpty()) {
                Factory factory = new Factory(factoryName, id, address, itemsSet);

                ExecutorService exs = Executors.newCachedThreadPool();
                exs.execute(new SaveFactoryThread(factory));
                exs.shutdown();

                try {
                    exs.awaitTermination(2000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<Item> itemList = new ArrayList<>(itemsSet);
                for (int i = 0; i < itemsSet.size(); i++) {
                    ExecutorService executorService1 = Executors.newCachedThreadPool();
                    executorService1.execute(new SaveItemIntoFactoryThread(factory,itemList.get(i)));
                    executorService1.shutdown();

                    try {
                        executorService1.awaitTermination(2000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save action succeeded!");
                alert.setHeaderText("Factory data saved!");
                alert.setContentText(pickedItems.toString());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save action failed!");
                alert.setHeaderText("Factory data not saved!");
                alert.setContentText(errorMessages.toString());
                alert.showAndWait();
            }
        }


    public void choiceBoxAction(ActionEvent event){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});
    }

}
