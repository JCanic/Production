package com.example.canic8;

import hr.java.production.model.Category;
import hr.java.production.model.Database;
import hr.java.production.model.ReadingDataFromFiles;
import hr.java.production.threads.GetCategoriesThread;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
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

public class SearchCategoriesController implements Initializable {
    private static List<Category> categories = new ArrayList<>();

    private static ObservableList<Category> observableListCategory;

    @FXML
    private TextField name;

    @FXML
    private TableView <Category> categoryTable;

    @FXML
    private TableColumn <Category,String> categoryNameColumn;

    @FXML
    private TableColumn <Category, String> categoryDescriptionColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        categoryNameColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue().getName());
        });
        categoryDescriptionColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getDescription());});

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetCategoriesThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        categories=Database.getCategoriesFromDatabase();

        if (observableListCategory==null){
            observableListCategory = FXCollections.observableArrayList();
        }

        observableListCategory.clear();
        observableListCategory.addAll(categories);
        categoryTable.setItems(observableListCategory);
        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    };

    public void search(){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});

        String nameSearch = name.getText();

        Predicate <Category> predName = category -> category.getName().toLowerCase().contains(nameSearch.toLowerCase());

        List<Category> filteredCategories = categories.stream().filter(predName).collect(Collectors.toList());
        observableListCategory.clear();
        observableListCategory.addAll(filteredCategories);

    }


}


