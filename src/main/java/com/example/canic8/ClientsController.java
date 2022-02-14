package com.example.canic8;

import hr.java.production.model.*;
import hr.java.production.threads.GetCategoriesThread;
import hr.java.production.threads.GetClientTask;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * loads clients from the list from the database, and they are displayed in the table
 */

public class ClientsController implements Initializable {
    private static List<Client> clients = new ArrayList<>();
    private static Client selectedClient = null;
    private static ObservableList<Client> observableListClients;

    @FXML
    private TextField searchWord;

    @FXML
    private TableView <Client> clientTable;

    @FXML
    private TableColumn <Client,String> clientNameColumn;

    @FXML
    private TableColumn <Client, String> clientLastNameColumn;

    @FXML
    private TableColumn <Client, String> clientDateOfBirthColumn;

    @FXML
    private TableColumn <Client, String> clientAddressColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        clientNameColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue().getName());
        });
        clientLastNameColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getLastName());});
        clientDateOfBirthColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getDateOfBirth().toString());});
        clientAddressColumn.setCellValueFactory(cellData -> {return new SimpleStringProperty(cellData.getValue()
                .getAddress().toString());});

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(new GetClientTask());
        es.shutdown();

        try {
            es.awaitTermination(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clients=Database.getClientsFromDatabase();

        if (observableListClients==null){
            observableListClients = FXCollections.observableArrayList();
        }

        observableListClients.clear();
        observableListClients.addAll(clients);
        clientTable.setItems(observableListClients);
        clientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("Edit client");
        cm.getItems().add(mi1);
        mi1.setOnAction(t -> {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("editClientScreen.fxml"));
            Scene scene=null;

            try{
                scene = new Scene(fxmlLoader.load(),651,551);
            }catch (IOException e){
                e.printStackTrace();
            }
            HelloApplication.getStage().setTitle("Edit Client");
            HelloApplication.getStage().setScene(scene);
            HelloApplication.getStage().show();
        });

        clientTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    cm.show(clientTable, t.getScreenX(), t.getScreenY());
                    selectedClient=clientTable.getSelectionModel().getSelectedItem();
                }
            }
        });

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

            HelloApplication.getStage().setTitle("The youngest client: " +getYoungestClient().getName()+" "+getYoungestClient().getLastName());

            Timeline tl = new Timeline (new KeyFrame(Duration.seconds(5),f->
            HelloApplication.getStage().setTitle("The oldest client: "+getOldestClient().getName()+" "+getOldestClient().getLastName())));
            tl.setCycleCount(Animation.INDEFINITE);
            tl.play();

        }), new KeyFrame(Duration.seconds(5)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }

    /**
     *
     * @return returns the oldest client from the list
     */

    private Client getYoungestClient() {
        return clients.stream().max(Comparator.comparing(Client::getDateOfBirth)).get();
    }

    /**
     *
     * @return returns the youngest client from the list
     */
    private Client getOldestClient() {
        return clients.stream().min(Comparator.comparing(Client::getDateOfBirth)).get();
    }

    /**
     * search after specific client based on it's first name or last name, or date of  birth or address
     */
    public void search(){
        HelloApplication.getStage().getScene().setOnKeyPressed(e-> {if (e.getCode()!= KeyCode.ENTER) return;});

        String searchKeyWord = searchWord.getText();

        Predicate <Client> predName = client -> client
                .getName().toLowerCase().contains(searchKeyWord.toLowerCase()) || client.getLastName().toLowerCase()
                .contains(searchKeyWord.toLowerCase()) || client.getAddress().getCityName().toLowerCase()
                .contains(searchKeyWord.toLowerCase()) || client.getDateOfBirth().toString().toLowerCase()
                .contains(searchKeyWord.toLowerCase());

            List<Client> clientsByName = clients.stream().filter(predName).collect(Collectors.toList());
            observableListClients.clear();
            observableListClients.addAll(clientsByName);
    }

    /**
     * returns the client that is selected by the mouse click
     * @return
     */
    static Client getSelectedClient(){
        return selectedClient;
    }



}


