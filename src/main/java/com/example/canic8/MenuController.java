package com.example.canic8;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {

    @FXML
    public void showItemSearchScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("itemSearch.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),651,451);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Item search");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void showCategorySearchScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("categorySearch.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),600,400);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Category search");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void showFactorySearchScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("factorySearch.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),631,413);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Factory search");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void showStoreSearchScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("storeSearch.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),631,413);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Store search");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void addNewItemScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addNewItemScreen.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),541,613);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Add new Item");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void addNewCategoryScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addNewCategoryScreen.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),541,613);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Add new Category");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void addNewFactoryScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addNewFactoryScreen.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),541,613);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Add new Factory");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void addNewStoreScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addNewStoreScreen.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),541,613);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Add new Store");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void searchClientsScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("clientsScreen.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),641,513);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Search Clients");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

    @FXML
    public void addNewClientScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addNewClientScreen.fxml"));
        Scene scene=null;

        try{
            scene = new Scene(fxmlLoader.load(),541,613);
        }catch (IOException e){
            e.printStackTrace();
        }
        HelloApplication.getStage().setTitle("Add new Client");
        HelloApplication.getStage().setScene(scene);
        HelloApplication.getStage().show();
    }

}
