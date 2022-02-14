package hr.java.production.threads;

import hr.java.production.model.Client;
import hr.java.production.model.Database;
import hr.java.production.model.Factory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * updates client by editing its data and saving it to the database
 */
public class UpdateClientTask implements Runnable {

    private static Connection connectToDatabase;
    private Client client;
    public UpdateClientTask(Client client) {
        this.client=client;
    }

    @Override
    public void run() {
        try {
            openConnectionWithDatabase();

            Database.updateClient(client);

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
