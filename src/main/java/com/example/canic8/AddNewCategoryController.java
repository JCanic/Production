package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.GetCategoriesThread;
import hr.java.production.threads.GetFactoriesThread;
import hr.java.production.threads.SaveCategoryThread;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class AddNewCategoryController implements Initializable {

    @FXML
    private TextField name;

    @FXML
    private TextField description;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void save(){

        StringBuilder errorMessages = new StringBuilder();
        Long id = 0L;

        ExecutorService ex = Executors.newCachedThreadPool();
        ex.execute(new GetCategoriesThread());
        ex.shutdown();

        try {
            ex.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Category> categories = Database.getCategoriesFromDatabase();

        id = (long) (categories.size() + 1);

        String categoryName = name.getText();

        if (categoryName.isEmpty()){
            errorMessages.append("Category must have a name\n");
        }

        String categoryDescription = description.getText();

        if (categoryDescription.isEmpty()){
            errorMessages.append("Category must have a description\n");
        }

        if (errorMessages.isEmpty()) {
            Category category = new Category(id,categoryName, categoryDescription);

            ExecutorService es = Executors.newCachedThreadPool();
            es.execute(new SaveCategoryThread(category));
            es.shutdown();

            try {
                es.awaitTermination(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Database.saveNewCategory(category);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save action succeeded!");
            alert.setHeaderText("Category data saved!");
            alert.setContentText("Category " + categoryName + " saved to file");
            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save action failed!");
            alert.setHeaderText("Category data not saved!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }

    }
}
