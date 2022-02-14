package hr.java.production.threads;

import hr.java.production.model.Database;
import hr.java.production.model.Factory;
import hr.java.production.model.Item;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoadItemsIntoFactoriesThread implements Runnable{

    private static Connection connectToDatabase;
    private static List<Factory> factories = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();

    public LoadItemsIntoFactoriesThread(List<Factory> factoriesC, List<Item> itemsC) {
        factories=factoriesC;
        items=itemsC;
    }

    @Override
    public void run() {
        try {
            openConnectionWithDatabase();

            Database.loadAndSaveItemsToFactories(connectToDatabase,factories,items);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnectionWithDatabase();
        }
    }


    public synchronized void openConnectionWithDatabase() throws IOException, SQLException {
        if (Database.activeConnectionWithDatabase) {
            try {
                wait();
                System.out.println("Connection is busy");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        connectToDatabase = Database.connectToDatabase();
    }

    public synchronized void closeConnectionWithDatabase() {
        try {
            Database.disconnectFromDatabase(connectToDatabase);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        notifyAll();
    }
}
