package hr.java.production.threads;

import hr.java.production.model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SaveItemIntoStoreThread implements Runnable{

    private static Connection connectToDatabase;
    private Store store;
    private Item item;

    public SaveItemIntoStoreThread(Store store, Item item) {
        this.store = store;
        this.item = item;
    }

    @Override
    public void run() {
        try {
            openConnectionWithDatabase();

            Database.saveItemIntoStore(store,item);

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
