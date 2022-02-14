package com.example.canic8;

import hr.java.production.main.Main;
import hr.java.production.model.Category;
import hr.java.production.model.Database;
import hr.java.production.model.Item;
import hr.java.production.model.ReadingDataFromFiles;
import hr.java.production.sort.ProductionSorter;
import hr.java.production.threads.GetCategoriesThread;
import hr.java.production.threads.GetItemsThread;
import hr.java.production.threads.SortingItemsThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchItemsController implements Initializable {

    private static List<Item> items = new ArrayList<>();

    private static ObservableList<Item> observableListItems;
    private static Set<String> categoriesInCB = addToSet(items);

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField name;

    @FXML
    private TableView <Item> itemsTable;

    @FXML
    private TableColumn <Item,String> itemNameColumn;

    @FXML
    private TableColumn <Item, String> itemCategoryColumn;

    @FXML
    private TableColumn <Item, String> itemWidthColumn;

    @FXML
    private TableColumn <Item,String> itemHeightColumn;

    @FXML
    private TableColumn <Item,String> itemLengthColumn;

    @FXML
    private TableColumn <Item,String> itemProductionCostColumn;

    @FXML
    private TableColumn<Item,String> itemSellingPriceColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        itemNameColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue().getName());
        });
        itemCategoryColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getCategory().getName());});
        itemWidthColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getWidth().toString());});
        itemHeightColumn.setCellValueFactory(cellData->{return new SimpleStringProperty(cellData.getValue()
                .getHeight().toString());});
        itemLengthColumn.setCellValueFactory(cellData ->{return new SimpleStringProperty(cellData.getValue()
                .getLength().toString());});
        itemProductionCostColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getProductionCost().toString());});
        itemSellingPriceColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getSellingPrice().toString());});

        choiceBox.getItems().addAll(categoriesInCB);
        choiceBox.setOnAction(this::choiceBoxAction);

        if (observableListItems==null){
            observableListItems = FXCollections.observableArrayList();
        }

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetItemsThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        items = Database.getItemsFromDatabase();

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new SortingItemsThread(items));
        executorService.shutdown();

        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        observableListItems.clear();
        observableListItems.addAll(items);
        itemsTable.setItems(observableListItems);
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            Item item = getTheMostExpansiveItem();
            HelloApplication.getStage().setTitle(item.getName());
        }), new KeyFrame(Duration.seconds(10)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }

    private Item getTheMostExpansiveItem() {
        return items.stream()
                .min((i1, i2) -> i2.getSellingPrice().compareTo(i1.getSellingPrice()))
                .get();
    }

    public void search(){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});

        String nameSearch = name.getText();

        Predicate <Item> predName = item -> item.getName().toLowerCase().contains(nameSearch.toLowerCase());

        List<Item> filteredItems = items.stream().filter(predName).collect(Collectors.toList());
        observableListItems.clear();
        observableListItems.addAll(filteredItems);

    }

    public void choiceBoxAction(ActionEvent event){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});
        String choice = choiceBox.getValue();
        Predicate <Item> predCategory = item -> item.getCategory().getName().toLowerCase()
                .contains(choice.toLowerCase());

        List<Item> filtereItemsWithCheckBox = items.stream().filter(predCategory).collect(Collectors.toList());
        observableListItems.clear();
        observableListItems.addAll(filtereItemsWithCheckBox);
    }

    public static Set <String> addToSet (List<Item> items){
        Set<String> setOfStrings = new HashSet<>();
        for (int i=0;i<items.size();i++){
            setOfStrings.add(items.get(i).getCategory().getName());
        }
        return setOfStrings;

    }

}


