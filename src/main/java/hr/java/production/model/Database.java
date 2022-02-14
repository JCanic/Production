package hr.java.production.model;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Database {

    public static Boolean activeConnectionWithDatabase = false;

    public static List<Category> categories = new ArrayList<>();

    public static List<Category> getCategoriesFromDatabase() {
        return categories;
    }

    public static List<Item> items = new ArrayList<>();

    public static List<Item> getItemsFromDatabase() {
        return items;
    }

    public static List<Address> addresses = new ArrayList<>();

    public static List<Address> getAddressesFromDatabase() {
        return addresses;
    }

    public static List<Factory> factories = new ArrayList<>();

    public static List<Factory> getFactoriesFromDatabase() {
        return factories;
    }

    public static List<Store> stores = new ArrayList<>();

    public static List<Store> getStoresFromDatabase() {
        return stores;
    }

    public static List<Client> clients = new ArrayList<>();

    public static List<Client> getClientsFromDatabase() {
        return clients;
    }

    public static synchronized Connection connectToDatabase() throws SQLException, IOException {
        Properties configuration = new Properties();
        configuration.load(new FileReader("dat/database.properties"));

        String databaseURL = configuration.getProperty("databaseURL");
        String databaseUserName = configuration.getProperty("databaseUserName");
        String databasePassword = configuration.getProperty("databasePassword");

        Connection connection = DriverManager.getConnection(databaseURL, databaseUserName, databasePassword);

        Database.activeConnectionWithDatabase = true;

        return connection;
    }

    public static synchronized void disconnectFromDatabase(Connection connection) throws SQLException {
        connection.close();
        Database.activeConnectionWithDatabase = false;
    }

    public static List<Category> loadCategories(Connection connection) throws SQLException {

        categories.clear();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM CATEGORY");

        while (rs.next()) {
            Long id = rs.getLong("ID");
            String name = rs.getString("NAME");
            String description = rs.getString("DESCRIPTION");

            Category category = new Category(id, name, description);
            categories.add(category);
        }

        return categories;

    }

    public static List<Item> loadItems(Connection connection) throws SQLException {

        items.clear();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM ITEM");

        while (rs.next()) {
            Long id = rs.getLong("ID");
            Long categoryID = rs.getLong("CATEGORY_ID");
            List<Category> categories = loadCategories(connection);
            Category category = new Category();
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == categoryID) {
                    category.setId(categories.get(i).getId());
                    category.setDescription(categories.get(i).getDescription());
                    category.setName(categories.get(i).getName());
                }
            }
            String name = rs.getString("NAME");
            BigDecimal width = rs.getBigDecimal("WIDTH");
            BigDecimal height = rs.getBigDecimal("HEIGHT");
            BigDecimal length = rs.getBigDecimal("LENGTH");
            BigDecimal productionCost = rs.getBigDecimal("PRODUCTION_COST");
            BigDecimal sellingPrice = rs.getBigDecimal("SELLING_PRICE");
            BigDecimal discount = BigDecimal.ZERO;


            Item item = new Item(id, name, category, width, height, length, productionCost, sellingPrice,
                    discount);
            items.add(item);
        }

        return items;

    }

    public static List<Address> loadAddresses(Connection connection) throws SQLException {

        addresses.clear();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM ADDRESS");

        while (rs.next()) {
            Long id = rs.getLong("ID");
            String street = rs.getString("STREET");
            String houseNumber = rs.getString("HOUSE_NUMBER");
            String city = rs.getString("CITY");
            Integer postalCode = rs.getInt("POSTAL_CODE");

            Address address = new Address(id, street, houseNumber, city, postalCode);
            addresses.add(address);
        }

        return addresses;

    }

    public static List<Factory> loadFactories(Connection connection) throws SQLException {

        factories.clear();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM FACTORY");

        while (rs.next()) {
            Long id = rs.getLong("ID");
            String name = rs.getString("NAME");
            Long addressId = rs.getLong("ADDRESS_ID");
            Address address = new Address();

            List<Address> addresses = loadAddresses(connection);
            for (int i = 0; i < addresses.size(); i++) {
                if (addresses.get(i).getId().equals(addressId)) {
                    address.setCityName(addresses.get(i).getCityName());
                    address.setId(addresses.get(i).getId());
                    address.setStreet(addresses.get(i).getStreet());
                    address.setHouseNumber(addresses.get(i).getHouseNumber());
                    address.setPostalCode(addresses.get(i).getPostalCode());
                }
            }


            Factory factory = new Factory(name, id, address);
            factories.add(factory);
        }

        return factories;

    }

    public static List<Store> loadStores(Connection connection) throws SQLException {

        stores.clear();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM STORE");

        while (rs.next()) {
            Long id = rs.getLong("ID");
            String name = rs.getString("NAME");
            String webAddress = rs.getString("WEB_ADDRESS");

            Store store = new Store(name, id, webAddress);
            stores.add(store);
        }

        return stores;

    }

    public static void loadAndSaveItemsToFactories(Connection connection, List<Factory> factories, List<Item> items) throws SQLException {

        Statement statement = connection.createStatement();
        for (int i = 0; i < factories.size(); i++) {
            Set<Item> itemsInFactories = new HashSet<>();
            ResultSet rs = statement.executeQuery("SELECT * FROM FACTORY_ITEM FI, ITEM I WHERE FI.FACTORY_ID = " + (i + 1) + " AND FI.ITEM_ID = I.ID;");

            while (rs.next()) {
                Long iId = rs.getLong("ITEM_ID");
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getId() == iId) {
                        itemsInFactories.add(items.get(j));
                    }
                }
            }
            factories.get(i).setItemSet(itemsInFactories);
        }

    }

    public static void loadAndSaveItemsToStores(Connection connection, List<Store> stores, List<Item> items) throws SQLException {

        Statement statement = connection.createStatement();
        for (int i = 0; i < stores.size(); i++) {
            Set<Item> itemsInStores = new HashSet<>();
            ResultSet rs = statement.executeQuery("SELECT * FROM STORE_ITEM SI, ITEM I WHERE SI.STORE_ID = " + (i + 1) + " AND SI.ITEM_ID = I.ID;");

            while (rs.next()) {
                Long iId = rs.getLong("ITEM_ID");
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getId() == iId) {
                        itemsInStores.add(items.get(j));
                    }
                }
            }
            stores.get(i).setItemSet(itemsInStores);
        }

    }

    public static Category getCategoryFromDatabase(Connection connection, Long id) throws SQLException {
        Category category = new Category();

        String sql = "SELECT * FROM CATEGORY WHERE ID = " + id;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            Long categoryID = rs.getLong("ID");
            String name = rs.getString("NAME");
            String description = rs.getString("DESCRIPTION");
            category.setId(categoryID);
            category.setName(name);
            category.setDescription(description);
        }

        return category;
    }

    public static Item getItemFromDatabase(Connection connection, Long id) throws SQLException {
        Item item = new Item();

        String sql = "SELECT * FROM ITEM WHERE ID = " + id;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            Long itemId = rs.getLong("ID");
            Long categoryID = rs.getLong("CATEGORY_ID");
            List<Category> categories = loadCategories(connection);
            Category category = new Category();
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == categoryID) {
                    category.setId(categories.get(i).getId());
                    category.setDescription(categories.get(i).getDescription());
                    category.setName(categories.get(i).getName());
                }
            }
            String name = rs.getString("NAME");
            BigDecimal width = rs.getBigDecimal("WIDTH");
            BigDecimal height = rs.getBigDecimal("HEIGHT");
            BigDecimal length = rs.getBigDecimal("LENGTH");
            BigDecimal productionCost = rs.getBigDecimal("PRODUCTION_COST");
            BigDecimal sellingPrice = rs.getBigDecimal("SELLING_PRICE");
            BigDecimal discount = BigDecimal.ZERO;
            Discount discountItem = new Discount(discount);

            item.setId(itemId);
            item.setName(name);
            item.setCategory(category);
            item.setWidth(width);
            item.setHeight(height);
            item.setLength(length);
            item.setProductionCost(productionCost);
            item.setSellingPrice(sellingPrice);
            item.setDiscount(discountItem);
        }

        return item;
    }

    public static Address getAddressFromDatabase(Connection connection, Long id) throws SQLException {
        Address address = new Address();

        String sql = "SELECT * FROM ADDRESS WHERE ID = " + id;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            Long addressId = rs.getLong("ID");
            String street = rs.getString("STREET");
            String houseNumber = rs.getString("HOUSE_NUMBER");
            String city = rs.getString("CITY");
            Integer postalCode = rs.getInt("POSTAL_CODE");

            address.setId(addressId);
            address.setPostalCode(postalCode);
            address.setCityName(city);
            address.setStreet(street);
            address.setHouseNumber(houseNumber);
        }

        return address;
    }

    public static Factory getFactoryFromDatabase(Connection connection, Long id, List<Item> items) throws SQLException {
        Factory factory = null;

        String sql = "SELECT * FROM FACTORY WHERE ID = " + id;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            Long factoryID = rs.getLong("ID");
            String name = rs.getString("NAME");
            Long addressId = rs.getLong("ADDRESS_ID");
            Address address = new Address();

            List<Address> addresses = loadAddresses(connection);
            for (int i = 0; i < addresses.size(); i++) {
                if (addresses.get(i).getId().equals(addressId)) {
                    address.setCityName(addresses.get(i).getCityName());
                    address.setId(addresses.get(i).getId());
                    address.setStreet(addresses.get(i).getStreet());
                    address.setHouseNumber(addresses.get(i).getHouseNumber());
                    address.setPostalCode(addresses.get(i).getPostalCode());
                }
            }
            factory.setId(factoryID);
            factory.setAddress(address);
            factory.setName(name);

            Statement statement = connection.createStatement();
            Set<Item> itemsInFactories = new HashSet<>();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM FACTORY_ITEM FI, ITEM I WHERE FI.FACTORY_ID = " + id + " AND FI.ITEM_ID = I.ID;");

            while (resultSet.next()) {
                Long iId = rs.getLong("ITEM_ID");
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getId() == iId) {
                        itemsInFactories.add(items.get(j));
                    }
                }
            }
            factory.setItemSet(itemsInFactories);
        }

        return factory;

    }

    public static Store getStoreFromDatabase(Connection connection, Long id, List<Item> items) throws SQLException {
        Store store = new Store();

        String sql = "SELECT * FROM STORE WHERE ID = " + id;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            Long storeID = rs.getLong("ID");
            String name = rs.getString("NAME");
            String webAddress = rs.getString("WEB_ADDRESS");

            store.setId(storeID);
            store.setName(name);
            store.setWebAddress(webAddress);

        }

        Statement statement = connection.createStatement();
        Set<Item> itemsInStores = new HashSet<>();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM STORE_ITEM SI, ITEM I WHERE SI.STORE_ID = " + id + " AND SI.ITEM_ID = I.ID;");
        while (resultSet.next()) {
            Long iId = rs.getLong("ITEM_ID");
            for (int j = 0; j < items.size(); j++) {
                if (items.get(j).getId() == iId) {
                    itemsInStores.add(items.get(j));
                }
            }
            store.setItemSet(itemsInStores);
        }
        return store;
    }

    public static void saveNewCategory(Category newCategory) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO CATEGORY(NAME, DESCRIPTION) VALUES(?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, newCategory.getName());
            ps.setString(2, newCategory.getDescription());

            ps.executeUpdate();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveNewItem(Item newItem) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO ITEM(CATEGORY_ID, NAME, WIDTH, HEIGHT, LENGTH, PRODUCTION_COST, SELLING_PRICE) VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, newItem.getCategory().getId());
            ps.setString(2, newItem.getName());
            ps.setBigDecimal(3, newItem.getWidth());
            ps.setBigDecimal(4, newItem.getHeight());
            ps.setBigDecimal(5, newItem.getLength());
            ps.setBigDecimal(6, newItem.getProductionCost());
            ps.setBigDecimal(7, newItem.getSellingPrice());

            ps.executeUpdate();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveNewAddress(Address newAddress) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO ADDRESS(STREET, HOUSE_NUMBER, CITY, POSTAL_CODE) VALUES(?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, newAddress.getStreet());
            ps.setString(2, newAddress.getHouseNumber());
            ps.setString(3, newAddress.getCityName());
            ps.setInt(4, newAddress.getPostalCode());

            ps.executeUpdate();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveNewFactory(Factory newFactory) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO FACTORY(NAME, ADDRESS_ID) VALUES(?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, newFactory.getName());
            ps.setLong(2, newFactory.getAddress().getId());

            ps.executeUpdate();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveNewStore(Store newStore) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO STORE(NAME, WEB_ADDRESS) VALUES(?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, newStore.getName());
            ps.setString(2, newStore.getWebAddress());

            ps.executeUpdate();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveItemIntoFactory(Factory factory, Item item) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO FACTORY_ITEM(FACTORY_ID, ITEM_ID) VALUES(?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, factory.getId());
            ps.setLong(2, item.getId());

            ps.executeUpdate();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveItemIntoStore(Store store, Item item) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO STORE_ITEM(STORE_ID, ITEM_ID) VALUES(?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, store.getId());
            ps.setLong(2, item.getId());

            ps.executeUpdate();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * loads client from the database
     * @param connection connects to the database
     * @return returns list of clients
     * @throws SQLException
     */

    public static List<Client> loadClients(Connection connection) throws SQLException {

        clients.clear();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM CLIENT");

        while (rs.next()) {
            Long id = rs.getLong("ID");
            String name = rs.getString("FIRST_NAME");
            String lastName = rs.getString("LAST_NAME");
            Date dateOfBirth = rs.getDate("DATE_OF_BIRTH");
            Long addressId = rs.getLong("ADDRESS_ID");
            Address address = new Address();

            List<Address> addresses = loadAddresses(connection);
            for (int i = 0; i < addresses.size(); i++) {
                if (addresses.get(i).getId().equals(addressId)) {
                    address.setCityName(addresses.get(i).getCityName());
                    address.setId(addresses.get(i).getId());
                    address.setStreet(addresses.get(i).getStreet());
                    address.setHouseNumber(addresses.get(i).getHouseNumber());
                    address.setPostalCode(addresses.get(i).getPostalCode());
                }
            }

            Client client = new Client(id, name, lastName, dateOfBirth, address);
            clients.add(client);
        }

        return clients;

    }

    /**
     * adds new client to the database
     * @param client
     */

    public static void saveNewClient(Client client) {
        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO CLIENT(FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ADDRESS_ID) VALUES(?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, client.getName());
            ps.setString(2, client.getLastName());
            ps.setDate(3, client.getDateOfBirth());
            ps.setLong(4, client.getAddress().getId());

            ps.executeUpdate();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * updates specific client
     * @param client presents client that is being updated
     * @throws SQLException
     * @throws IOException
     */
    public static void updateClient(Client client) throws SQLException, IOException {
        try (Connection connection = connectToDatabase()) {

            try {
                PreparedStatement st = connection.prepareStatement("UPDATE CLIENT SET FIRST_NAME = ?, LAST_NAME = ?, DATE_OF_BIRTH = ?, ADDRESS_ID = ? WHERE ID = ?");
                st.setString(1, client.getName());
                st.setString(2, client.getLastName());
                st.setDate(3, client.getDateOfBirth());
                st.setLong(4, client.getAddress().getId());
                st.setLong(5,client.getId());
                st.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateAddress(Address address) throws SQLException, IOException {
        try (Connection connection = connectToDatabase()) {

            try {
                PreparedStatement st = connection.prepareStatement("UPDATE ADDRESS SET STREET = ?, HOUSE_NUMBER = ?, CITY = ?, POSTAL_CODE = ? WHERE ID = ?");
                st.setString(1, address.getStreet());
                st.setString(2, address.getHouseNumber());
                st.setString(3, address.getCityName());
                st.setInt(4, address.getPostalCode());
                st.setLong(5,address.getId());
                st.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
