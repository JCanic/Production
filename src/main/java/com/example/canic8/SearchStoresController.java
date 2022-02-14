package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchStoresController implements Initializable {

    private static List<Store> stores = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();

    private static ObservableList<Store> observableListStores;

    @FXML
    private TextField name;

    @FXML
    private TableView<Store> storeTable;

    @FXML
    private TableColumn <Store, String> storeNameColumn;

    @FXML
    private TableColumn<Store,String> storeWebAddressColumn;

    @FXML
    private TableColumn <Store, String> storeItemColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        storeNameColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue().getName());
        });
        storeWebAddressColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getWebAddress());});
        storeItemColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue().printItems());});

        if (observableListStores==null){
            observableListStores = FXCollections.observableArrayList();
        }

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetStoresThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stores = Database.getStoresFromDatabase();

        ExecutorService ex = Executors.newCachedThreadPool();
        ex.execute(new GetItemsThread());
        ex.shutdown();

        try {
            ex.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        items=Database.getItemsFromDatabase();

        ExecutorService exs = Executors.newCachedThreadPool();
        exs.execute(new LoadItemsIntoStoresThread(stores,items));
        exs.shutdown();

        try {
            exs.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        observableListStores.clear();
        observableListStores.addAll(stores);
        storeTable.setItems(observableListStores);
        storeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    };

    public void search(){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});

        String nameSearch = name.getText();

        Predicate<Store> predName = item -> item.getName().toLowerCase().contains(nameSearch.toLowerCase());

        List<Store> filteredStores = stores.stream().filter(predName).collect(Collectors.toList());
        observableListStores.clear();
        observableListStores.addAll(filteredStores);

    }
    
}



