package hr.java.production.threads;

import hr.java.production.model.Address;
import hr.java.production.model.Category;
import hr.java.production.model.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SaveNewAddressThread implements Runnable{

    private static Connection connectToDatabase;
    private Address address;

    public SaveNewAddressThread(Address address) {
        this.address=address;
    }

    @Override
    public void run() {
        try {
            openConnectionWithDatabase();

            Database.saveNewAddress(address);

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
