package hr.java.production.threads;

import hr.java.production.model.Category;
import hr.java.production.model.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * loads clients from the database
 */
public class GetClientTask implements Runnable{

    private static Connection connectToDatabase;


    @Override
    public void run() {
        try {
            openConnectionWithDatabase();

            Database.loadClients(connectToDatabase);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnectionWithDatabase();
        }
    }

    /**
     * connects to the database in order to save new data to it
     * @throws IOException
     * @throws SQLException
     */
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

    /**
     * disconnects from the database
     */
    public synchronized void closeConnectionWithDatabase() {
        try {
            Database.disconnectFromDatabase(connectToDatabase);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        notifyAll();
    }
}
