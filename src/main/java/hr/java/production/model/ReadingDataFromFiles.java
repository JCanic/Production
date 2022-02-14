package hr.java.production.model;

import hr.java.production.enumeration.Cities;
import hr.java.production.generics.FoodStore;
import hr.java.production.generics.TechnicalStore;
import hr.java.production.main.Main;
import org.slf4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public interface ReadingDataFromFiles {

    String CATEGORIES_FILE_NAME = "dat/categories.txt";
    String ITEMS_FILE_NAME = "dat/items.txt";
    String ADDRESSES_FILE_NAME = "dat/addresses.txt";
    String FACTORIES_FILE_NAME = "dat/factories.txt";
    String STORES_FILE_NAME = "dat/stores.txt";

    static List<Category> readCategoriesFromFile() {

        List<Category> categories = new ArrayList<>();
        File categoriesFile = new File(CATEGORIES_FILE_NAME);

        Logger logger = null;
        if (categoriesFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(categoriesFile))) {

                String line;

                while ((line = br.readLine()) != null) {
                    Long id = Long.parseLong(line);
                    String name = br.readLine();
                    String description = br.readLine();
                    br.readLine();

                    Category category = new Category(id, name, description);
                    categories.add(category);
                }
            } catch (IOException e) {
                System.out.println("File " + CATEGORIES_FILE_NAME + " not found.");
                logger.error(e.getMessage(), e);
            }


        }
        return categories;
    }

    static List<Item> readItemsFromFile() {

        File itemsFile = new File(ITEMS_FILE_NAME);
        List<Item> items = new ArrayList<>();
        List<Category> categories1 = readCategoriesFromFile();

        if (itemsFile.exists()) {

            try (BufferedReader br = new BufferedReader(new FileReader(itemsFile))) {

                String line;

                while ((line = br.readLine()) != null) {
                    Long id = Long.parseLong(line);
                    String name = br.readLine();
                    String categoryLine = br.readLine();
                    Category category = new Category();
                    int idCounter = 0;
                    for (int i = 0; i < categories1.size(); i++) {
                        if (categoryLine.equals("Food")) {
                            category = categories1.get(0);
                            idCounter = 3;
                            if (name.equals("Salmon")) {
                                idCounter = 4;
                            }
                        } else if (categoryLine.equals("Technical equipment")) {
                            category = categories1.get(1);
                            idCounter = 2;
                        } else if (categoryLine.equals("Literature")) {
                            category = categories1.get(2);
                            idCounter = 1;
                        } else if (categoryLine.equals("Bicycles")) {
                            category = categories1.get(3);
                            idCounter = 1;
                        } else if (categoryLine.equals("Video games")) {
                            category = categories1.get(4);
                            idCounter = 1;
                        }
                    }
                    BigDecimal width = BigDecimal.valueOf(Double.parseDouble(br.readLine()));
                    BigDecimal height = BigDecimal.valueOf(Double.parseDouble(br.readLine()));
                    BigDecimal length = BigDecimal.valueOf(Double.parseDouble(br.readLine()));
                    BigDecimal productionCost = BigDecimal.valueOf(Double.parseDouble(br.readLine()));
                    BigDecimal sellingPrice = BigDecimal.valueOf(Double.parseDouble(br.readLine()));
                    BigDecimal discount = BigDecimal.valueOf(Double.parseDouble(br.readLine()));
                    BigDecimal weight = BigDecimal.valueOf(0);
                    Integer warranty = 0;
                    if (categoryLine.equals("Food") || categoryLine.equals("Technical equipment")) {
                        if (categoryLine.equals("Food")) {
                            weight = BigDecimal.valueOf(Double.parseDouble(br.readLine()));
                        } else {
                            warranty = Integer.parseInt(br.readLine());
                        }
                    } else
                        br.readLine();

                    Item item = switch (idCounter) {
                        case 2 -> new Laptop(id, name, category, width, height, length, productionCost, sellingPrice,
                                discount, warranty);
                        case 4 -> new Salmon(id, name, category, width, height, length, productionCost, sellingPrice, weight,
                                discount);
                        case 3 -> new Peanut(id, name, category, width, height, length, productionCost, sellingPrice, weight,
                                discount);
                        default -> new Item(id, name, category, width, height, length, productionCost, sellingPrice,
                                discount);
                    };
                    items.add(item);
                }
            } catch (IOException e) {
                System.out.println("File " + ITEMS_FILE_NAME + " not found.");
                Logger logger = null;
                logger.error(e.getMessage(), e);
            }

        }
        return items;
    }

    static List<Address> readAddressesFromFile() {

        List<Address> addresses = new ArrayList<>();
        ArrayList<Cities> cityList = new ArrayList<>();
        for (Cities cities : Cities.values()) {
            cityList.add(cities);
        }
        Optional<Cities> city = Optional.empty();

        try (FileReader fileReader = new FileReader(ADDRESSES_FILE_NAME);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String cityLine = reader.readLine();
                String street = reader.readLine();
                String houseNumber = reader.readLine();

                for (int j = 0; j < cityList.size(); j++) {
                    if (cityLine.equalsIgnoreCase(cityList.get(j).getName())) {
                        city = Optional.ofNullable(cityList.get(j));
                    }
                }

                Address address = new Address(city.get(), street, houseNumber);
                addresses.add(address);
            }


        } catch (IOException e) {
            System.out.println("File " + ADDRESSES_FILE_NAME + " not found.");
        }

        return addresses;
    }

    static List<Factory> readFactoriesFromFile() {

        File factoriesFile = new File(FACTORIES_FILE_NAME);
        Optional<Address> address = Optional.empty();
        List<Address> addresses = readAddressesFromFile();
        List<Item> items = readItemsFromFile();
        List<Factory> factories = new ArrayList<>();

        if (factoriesFile.exists()) {

            try (BufferedReader br = new BufferedReader(new FileReader(factoriesFile))) {

                String line;
                String idLine;
                String addressIDLine;
                while ((line = br.readLine()) != null) {
                    idLine = br.readLine();
                    Long id = Long.parseLong(idLine);
                    String name = br.readLine();
                    addressIDLine = br.readLine();
                    Integer addressID = Integer.parseInt(addressIDLine);
                    for (int i = 0; i < addresses.size(); i++) {
                        if ((addressID - 1) == i) {
                            address = Optional.ofNullable(addresses.get(i));
                        }
                    }
                    List<Long> itemsIDs = Arrays.stream(br.readLine()
                                    .split(","))
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                    Set<Item> factoryItems = new HashSet<>();

                    for (int i = 0; i < itemsIDs.size(); i++) {
                        for (int j = 0; j < items.size(); j++) {
                            if (itemsIDs.get(i).equals(items.get(j).getId())) {
                                factoryItems.add(items.get(j));
                            }
                        }
                    }

                    Factory factory = new Factory(name, id, address.get(), factoryItems);
                    factories.add(factory);
                }


            } catch (IOException e) {
                System.out.println("File " + FACTORIES_FILE_NAME + " not found.");
            }

        }
        return factories;
    }

    static List<Store> readStoresFromFile() {

        File factoriesFile = new File(STORES_FILE_NAME);
        List<Item> items = readItemsFromFile();
        List<Store> stores = new ArrayList<>();

        if (factoriesFile.exists()) {

            try (BufferedReader br = new BufferedReader(new FileReader(factoriesFile))) {

                String line;
                String idLine;
                String addressIDLine;
                while ((line = br.readLine()) != null) {
                    idLine = br.readLine();
                    Long id = Long.parseLong(idLine);
                    String name = br.readLine();
                    String webAddress = br.readLine();

                    List<Long> itemsIDs = Arrays.stream(br.readLine()
                                    .split(","))
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                    Set<Item> storeItems = new HashSet<>();

                    for (int i = 0; i < itemsIDs.size(); i++) {
                        for (int j = 0; j < items.size(); j++) {
                            if (itemsIDs.get(i).equals(items.get(j).getId())) {
                                storeItems.add(items.get(j));
                            }
                        }
                    }

                    int storeType;
                    int foodCounter = 0;
                    int technicalCounter = 0;

                    List<Item> storedItems = new ArrayList<>(storeItems);
                    Set<Item> foodAndTechnicalItems = new HashSet<>();
                    List<Edible> foodItems = new ArrayList<>();
                    List<Laptop> technicalItems = new ArrayList<>();

                    for (int i = 0; i < storedItems.size(); i++) {
                        if (storedItems.get(i) instanceof Salmon || storedItems.get(i) instanceof Peanut) {
                            foodCounter += 1;
                            foodAndTechnicalItems.add(storedItems.get(i));
                            foodItems.add((Edible) storedItems.get(i));
                        } else if (storedItems.get(i) instanceof Laptop) {
                            technicalCounter += 1;
                            foodAndTechnicalItems.add(storedItems.get(i));
                            technicalItems.add((Laptop) storedItems.get(i));
                        }
                    }

                    if (storedItems.size() == foodCounter) {
                        storeType = 2;
                    } else if (storedItems.size() == technicalCounter) {
                        storeType = 3;
                    } else
                        storeType = 1;

                    Store store = switch (storeType) {
                        case 2 -> new FoodStore<>(name, id, webAddress, foodAndTechnicalItems, foodItems);
                        case 3 -> new TechnicalStore<>(name, id, webAddress, foodAndTechnicalItems, technicalItems);
                        default -> new Store(name, id, webAddress, storeItems);
                    };
                    stores.add(store);
                }


            } catch (IOException e) {
                System.out.println("File " + STORES_FILE_NAME + " not found.");
            }

        }
        return stores;
    }

    static void writeItem (Item item){
        try (FileWriter fileWriter = new FileWriter(ITEMS_FILE_NAME,true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter))
             {
            printWriter.println();
            printWriter.println(item.getId());
            printWriter.println(item.getName());
            printWriter.println(item.getCategory());
            printWriter.println(item.getWidth());
            printWriter.println(item.getHeight());
            printWriter.println(item.getLength());
            printWriter.println(item.getProductionCost());
            printWriter.println(item.getSellingPrice());
            printWriter.println(item.getDiscount());
        }
        catch (IOException e){
            System.out.println("File "+ ITEMS_FILE_NAME +" not found");

        }
    }

    static void writeCategory (Category category){
        try (FileWriter fileWriter = new FileWriter(CATEGORIES_FILE_NAME,true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter))
        {
            printWriter.println();
            printWriter.println(category.getId());
            printWriter.println(category.getName());
            printWriter.println(category.getDescription());
        }
        catch (IOException e){
            System.out.println("File "+ CATEGORIES_FILE_NAME +" not found");

        }
    }

    static void writeFactory (Factory factory, Long addressID, List<Item> items){
        try (FileWriter fileWriter = new FileWriter(FACTORIES_FILE_NAME,true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter))
        {
            printWriter.println();
            printWriter.println();
            printWriter.println(factory.getId());
            printWriter.println(factory.getName());
            printWriter.println(addressID);
            for (int i=0;i<items.size();i++){
                printWriter.print(items.get(i).getId()+",");
            }
        }
        catch (IOException e){
            System.out.println("File "+ FACTORIES_FILE_NAME +" not found");

        }
    }

    static void writeStore (Store store, List<Item> items){
        try (FileWriter fileWriter = new FileWriter(STORES_FILE_NAME,true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter))
        {
            printWriter.println();
            printWriter.println();
            printWriter.println(store.getId());
            printWriter.println(store.getName());
            printWriter.println(store.getWebAddress());
            for (int i=0;i<items.size();i++){
                printWriter.print(items.get(i).getId()+",");
            }
        }
        catch (IOException e){
            System.out.println("File "+ STORES_FILE_NAME +" not found");

        }
    }
}
