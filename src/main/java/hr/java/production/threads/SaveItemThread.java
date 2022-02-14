package hr.java.production.threads;

import hr.java.production.model.Category;
import hr.java.production.model.Database;
import hr.java.production.model.Item;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SaveItemThread implements Runnable{

    private static Connection connectToDatabase;
    private Item item;

    public SaveItemThread(Item itemC) {
        item = itemC;
    }

    @Override
    public void run() {
        try {
            openConnectionWithDatabase();

            Database.saveNewItem(item);

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
