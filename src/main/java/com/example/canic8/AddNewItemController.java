package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.GetCategoriesThread;
import hr.java.production.threads.GetItemsThread;
import hr.java.production.threads.SaveCategoryThread;
import hr.java.production.threads.SaveItemThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
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

public class AddNewItemController implements Initializable {

    private static List<String> categoriesInCB = new ArrayList<>();
    private  static List<Category> categories = new ArrayList<>();

    @FXML
    private TextField name;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField width;

    @FXML
    private TextField height;

    @FXML
    private TextField length;

    @FXML
    private TextField productionCost;

    @FXML
    private TextField sellingPrice;

    @FXML
    private TextField discount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetCategoriesThread());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        categoriesInCB = Database.getCategoriesFromDatabase().stream().map(c->c.getName())
                .collect(Collectors.toList());
        choiceBox.getItems().addAll(categoriesInCB);
        choiceBox.setOnAction(this::choiceBoxAction);
    }

    public void save(){
        StringBuilder errorMessages = new StringBuilder();
        Long id = 0L;

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new GetItemsThread());
        executorService.shutdown();

        try {
            executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Item> items = Database.getItemsFromDatabase();
        id = (long) (items.size() + 1);

        String itemName = name.getText();
        if (itemName.isEmpty()){
            errorMessages.append("Item must have a name!\n");
        }

        String categoryName="";
        try{
            categoryName=choiceBox.getValue().toString();
        }catch (NullPointerException ex) {
            errorMessages.append("Item must have a category!\n");
        }

        String  categoryDescription = "";
        Long categoryID = Long.valueOf(0);

        List<Category> categories = new ArrayList<>();

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetCategoriesThread());
        es.shutdown();

        categories=Database.getCategoriesFromDatabase();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i=0;i<categories.size();i++){
            if (categories.get(i).getName().equals(categoryName)){
                categoryDescription=categories.get(i).getDescription();
                categoryID=categories.get(i).getId();
            }
        }
        Category itemCategory = new Category(categoryID,categoryName,categoryDescription);

        BigDecimal itemWidth = BigDecimal.valueOf(0);
        if (width.getText().isEmpty()){
            errorMessages.append("Item width can't be empty!\n");
        }
        else
            try{
                itemWidth = BigDecimal.valueOf(Long.parseLong(width.getText()));
            }catch (NumberFormatException ex){
                errorMessages.append("Width is not in valid format!\n");
            }

        BigDecimal itemHeight = BigDecimal.valueOf(0);
        if (height.getText().isEmpty()){
            errorMessages.append("Item height can't be empty!\n");
        }
        else
            try{
                itemHeight = BigDecimal.valueOf(Long.parseLong(height.getText()));
            }catch (NumberFormatException ex){
                errorMessages.append("Height is not in valid format!\n");
            }

        BigDecimal itemLength = BigDecimal.valueOf(0);
        if (length.getText().isEmpty()){
            errorMessages.append("Item length can't be empty!\n");
        }
        else
            try{
                itemLength = BigDecimal.valueOf(Long.parseLong(length.getText()));
            }catch (NumberFormatException ex){
                errorMessages.append("Length is not in valid format!\n");
            }

        BigDecimal itemProductionCost = BigDecimal.valueOf(0);
        if (productionCost.getText().isEmpty()){
            errorMessages.append("Item production cost can't be empty!\n");
        }
        else
            try{
                itemProductionCost = BigDecimal.valueOf(Long.parseLong(productionCost.getText()));
            }catch (NumberFormatException ex){
                errorMessages.append("Production cost is not in valid format!\n");
            }

        BigDecimal itemSellingPrice = BigDecimal.valueOf(0);
        if (sellingPrice.getText().isEmpty()){
            errorMessages.append("Item selling price can't be empty!\n");
        }
        else
            try{
                itemSellingPrice = BigDecimal.valueOf(Long.parseLong(sellingPrice.getText()));
            }catch (NumberFormatException ex){
                errorMessages.append("Selling price is not in valid format!\n");
            }

        BigDecimal itemDiscount = BigDecimal.valueOf(0);
        if (discount.getText().isEmpty()){
            errorMessages.append("Item discount can't be empty!");
        }
        else
            try{
                itemDiscount = BigDecimal.valueOf(Long.parseLong(discount.getText()));
            }catch (NumberFormatException ex){
                errorMessages.append("Discount is not in valid format!");
            }

        if (errorMessages.isEmpty()) {
            Item item = new Item (id, itemName, itemCategory, itemWidth, itemHeight, itemLength, itemProductionCost,
                    itemSellingPrice, itemDiscount);

            ExecutorService exs = Executors.newCachedThreadPool();
            exs.execute(new SaveItemThread(item));
            exs.shutdown();

            try {
                exs.awaitTermination(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save action succeeded!");
            alert.setHeaderText("Item data saved!");
            alert.setContentText("Item " + itemName + " saved to file");
            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save action failed!");
            alert.setHeaderText("Item data not saved!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }
    }

    public void choiceBoxAction(ActionEvent event){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});
    }
}
