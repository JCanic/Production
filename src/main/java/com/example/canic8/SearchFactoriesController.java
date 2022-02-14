package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.GetFactoriesThread;
import hr.java.production.threads.GetItemsThread;
import hr.java.production.threads.LoadItemsIntoFactoriesThread;
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

public class SearchFactoriesController implements Initializable {

    private static List<Factory> factories = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();

    private static ObservableList<Factory> observableListFactories;

    @FXML
    private TextField name;

    @FXML
    private TableView<Factory> factoryTable;

    @FXML
    private TableColumn <Factory, String> factoryNameColumn;

    @FXML
    private TableColumn<Factory,String> factoryAddressColumn;

    @FXML
    private TableColumn <Factory, String> factoryItemColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        factoryNameColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue().getName());
        });
        factoryAddressColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getAddress().getCityName());});
        factoryItemColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue().printItems());});

        if (observableListFactories==null){
            observableListFactories = FXCollections.observableArrayList();
        }

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetFactoriesThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        factories = Database.getFactoriesFromDatabase();

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
        exs.execute(new LoadItemsIntoFactoriesThread(factories,items));
        exs.shutdown();

        try {
            exs.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        observableListFactories.clear();
        observableListFactories.addAll(factories);
        factoryTable.setItems(observableListFactories);
        factoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    };

    public void search(){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});

        String nameSearch = name.getText();

        Predicate<Factory> predName = item -> item.getName().toLowerCase().contains(nameSearch.toLowerCase());

        List<Factory> filteredFactories = factories.stream().filter(predName).collect(Collectors.toList());
        observableListFactories.clear();
        observableListFactories.addAll(filteredFactories);

    }

}



