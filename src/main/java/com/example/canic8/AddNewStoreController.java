package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AddNewStoreController implements Initializable {

    private static List<Item> itemsInFile = new ArrayList<>();
    private static List<Item> selectedItems = new ArrayList<>();
    private static List <String> itemsName = new ArrayList<>();
    private static List <Store> stores = new ArrayList<>();
    private static List <Item> itemsInDB = new ArrayList<>();
    String selection;

    @FXML
    private TextField name;

    @FXML
    private TextField webAddress;

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

        itemsInDB=Database.getItemsFromDatabase();
        itemsName = itemsInDB.stream().map(NamedEntity::getName)
                .collect(Collectors.toList());

        items.getItems().addAll(itemsName);
        items.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                selection = items.getSelectionModel().getSelectedItem();
                for (int i=0;i<itemsInDB.size();i++){
                    if (selection.equals(itemsInDB.get(i).getName())){
                        selectedItems.add(itemsInDB.get(i));
                    }
                }
            }
        });
    }

    public void save(){
        Long id = 0L;

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new GetStoresThread());
        executorService.shutdown();

        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Store> stores = Database.getStoresFromDatabase();
        id = (long) (stores.size() + 1);

        StringBuilder errorMessages = new StringBuilder();
        StringBuilder pickedItems = new StringBuilder();

        String storeName = name.getText();
        if (storeName.isEmpty()){
            errorMessages.append("Store must have a name!\n");
        }
        String storeWebAddress = webAddress.getText();
        if (storeWebAddress.isEmpty()){
            errorMessages.append("Store must have a web address!\n");
        }

        Set<Item> itemsSet = new HashSet<>();
        pickedItems.append("You have picked following items from the list:\n");
        for (int i=0;i<selectedItems.size();i++){
            itemsSet.add(selectedItems.get(i));
            pickedItems.append(selectedItems.get(i).getName()).append("\n");
        }
        if (itemsSet.isEmpty()){
            errorMessages.append("You must pick at least one item from the list!");
        }

        if (errorMessages.isEmpty()) {
            Store store = new Store(storeName, id, storeWebAddress, itemsSet);

            ExecutorService exs = Executors.newCachedThreadPool();
            exs.execute(new SaveStoreThread(store));
            exs.shutdown();

            try {
                exs.awaitTermination(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<Item> itemList = new ArrayList<>(itemsSet);
            for (int i = 0; i < itemsSet.size(); i++) {
                ExecutorService executorService1 = Executors.newCachedThreadPool();
                executorService1.execute(new SaveItemIntoStoreThread(store,itemList.get(i)));
                executorService1.shutdown();

                try {
                    executorService1.awaitTermination(2000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save action succeeded!");
            alert.setHeaderText("Store data saved!");
            alert.setContentText(pickedItems.toString());
            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save action failed!");
            alert.setHeaderText("Store data not saved!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }

    }

}
