package hr.java.production.main;

import hr.java.production.enumeration.Cities;
import hr.java.production.exception.SameItem;
import hr.java.production.generics.FoodStore;
import hr.java.production.generics.TechnicalStore;
import hr.java.production.model.*;
import hr.java.production.sort.ProductionSorter;
import javafx.scene.chart.PieChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.*;

import static java.util.stream.Collectors.*;

/**
 * Main class where the program is executed
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String CATEGORIES_FILE_NAME = "dat/categories.txt";
    private static final String ITEMS_FILE_NAME="dat/items.txt";
    private static final String ADDRESSES_FILE_NAME="dat/addresses.txt";
    private static final String FACTORIES_FILE_NAME="dat/factories.txt";
    private static final String STORES_FILE_NAME="dat/stores.txt";
    private static final String SERIALIZED_FACTORIES_FILE_NAME="dat/serializedFactories.dat";
    private static final String SERIALIZED_STORES_FILE_NAME="dat/serializedStores.dat";


    /**
     * executes main method of the program
     * @param args presents command line arguments
     */
    public static void main(String[] args) {

        try {
            Connection connection = Database.connectToDatabase();

            List <Category> categories = Database.loadCategories(connection);
            List <Item> items = Database.loadItems(connection);
            List<Address> addresses = Database.loadAddresses(connection);
            List<Factory> factories = Database.loadFactories(connection);
            List<Store> stores = Database.loadStores(connection);

            Category categoryTest = new Category();
            categoryTest.setName("Video games");
            categoryTest.setDescription("All kinds of video games");
            Database.saveNewCategory(categoryTest);

            System.out.println("Categories: \n");
            for (int i=0;i<categories.size();i++){
                System.out.println(categories.get(i).getName());
            }

            System.out.println("\nItems:");
            for (int i=0;i<items.size();i++){
                System.out.println(items.get(i).getName());
            }

            System.out.println("\nAddresses:");
            for (int i=0;i<addresses.size();i++){
                System.out.println(addresses.get(i).getStreet());
            }

            System.out.println("\nFactories:");
            for (int i=0;i<factories.size();i++){
                System.out.println(factories.get(i).getName());
            }

            System.out.println("\nStores:");
            for (int i=0;i<stores.size();i++){
                System.out.println(stores.get(i).getName());
            }

            Database.loadAndSaveItemsToFactories(connection,factories,items);
            Database.loadAndSaveItemsToStores(connection,stores,items);

            System.out.println("\nFactories:");
            for (int i=0;i<factories.size();i++){
                System.out.println(factories.get(i).getName() + " "+factories.get(i).getId());
                System.out.println("itemi:");
                System.out.println(factories.get(i).getItemSet());
            }

            System.out.println("\nStores:");
            for (int i=0;i<stores.size();i++){
                System.out.println(stores.get(i).getName() + " " + stores.get(i).getId());
                System.out.println("itemi: ");
                System.out.println(stores.get(i).getItemSet());
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nDobrodošli u našu trgovinu!");
        logger.info("Početak programa");

        List<Item> itemi = new ArrayList<>();
        itemi = ReadingDataFromFiles.readItemsFromFile();

        System.out.println(itemi.get(2).getName());

        System.out.println("\nReading categories from file...");
        List<Category> categoriesFromFile = new ArrayList<>();
        readCategoriesFromFile(categoriesFromFile);
        printCategoriesFromFile(categoriesFromFile);

        System.out.println("\nReading items from file...");
        List <Item> itemsFromFile = new ArrayList<>();
        readItemsFromFile(itemsFromFile,categoriesFromFile);
        printItemsFromFile(itemsFromFile);

        System.out.println("\nReading addresses from file: ");
        List<Address> addressesFromFile = new ArrayList<>();
        readAddressesFromFile(addressesFromFile);
        printAddressesFromFile(addressesFromFile);

        System.out.println("\nReading factories from file...");
        List <Factory> factoriesFromFile = new ArrayList<>();
        readFactoriesFromFile(factoriesFromFile,itemsFromFile, addressesFromFile);
        printFactoriesFromFile(factoriesFromFile);

        System.out.println("\nReading stores from file...");
        List <Store> storesFromFile = new ArrayList<>();
        readStoresFromFile(storesFromFile,itemsFromFile);
        printStoresFromFile(storesFromFile);

        serializeFactories(factoriesFromFile);
        serializeStores(storesFromFile);

        deserializeFactories(SERIALIZED_FACTORIES_FILE_NAME);
        deserializeStores(SERIALIZED_STORES_FILE_NAME);

        System.out.println();

        Map<Category, List<Item>> mapOfItems = itemsFromFile
                .stream()
                .collect(groupingBy(Item::getCategory,
                        collectingAndThen(toList(), e -> e.stream().sorted(new ProductionSorter()).collect(toList()))));

        printItemsInCategories(mapOfItems);

        System.out.println(mapOfItems.keySet());

        List <Integer> numbers = new ArrayList<>();
        numbers.add(5);
        numbers.add(3);
        numbers.add(4);
        OptionalInt biggestNumber = numbers.stream().mapToInt(a-> a).max();

        System.out.println();

        Instant start1 = Instant.now();

        List <Item> storedItems = storesFromFile
                .stream()
                .flatMap(s-> Arrays.stream(s.getItems()))
                .collect(toList());

        List<Item> storedItemsSorted = storedItems.stream()
                .sorted(Comparator.comparing(Item::getVolume))
                .collect(toList());

        OptionalDouble averageVolume = itemsFromFile.stream()
                .mapToInt (item -> item.getVolume().intValue())
                .average();

        List <Item> itemsHighVolumes = new ArrayList<>(
                itemsFromFile.stream()
                        .filter(s-> s.getVolume().intValue() > averageVolume.getAsDouble())
                        .collect(toList()));

        OptionalDouble averagePriceItemsHighVolumes = itemsHighVolumes
                .stream()
                .mapToInt(item -> item.getSellingPrice().intValue())
                .average();

        OptionalDouble averageNumberOfItemsInStores = storesFromFile.stream()
                .mapToInt(number -> number.getItems().length)
                .average();

        List <Store> storesWithMoreItems = storesFromFile.stream()
                        .filter(s-> s.getItems().length > averageNumberOfItemsInStores.getAsDouble() )
                .collect(toList());

        Instant end1 = Instant.now();
        Long duration1 = Duration.between(start1, end1).toMillis();
        if (averagePriceItemsHighVolumes.isPresent()){
            double a= averagePriceItemsHighVolumes.getAsDouble();
            System.out.println("Prosječna cijena artikala s nadprosječnim volumenom je " + a +" kn");
        }

        biggestVolumeItem(factoriesFromFile);
        cheapestItem(storesFromFile);
        highestAmountOfKiloCaloriesAndHighestPrice(itemsFromFile);
        shortestWarranty(itemsFromFile);
        cheapestMostExpansiveTechnicalOrEdibleItem(itemsFromFile);

        Instant start2 = Instant.now();

        sortVolumes(itemsFromFile);
        averageSellingPrice(itemsFromFile);
        storesWithMoreItems(storesFromFile);

        System.out.println();
        Instant end2 = Instant.now();
        Long duration2 = Duration.between(start1, end1).toMillis();
        System.out.println("Trajanje sortiranja lambda izrazima je "+duration1);
        System.out.println("Trajanje sortiranja bez lambda izraza traje "+duration2);
        if (duration2>duration1){
            System.out.println("Sortiranje s lambda izrazima traje kraće");
        }
        else if (duration2==duration1)
            System.out.println("Trajanje sortiranja je jednako");
        else System.out.println("Sortiranje bez lambdi traje kraće");


        filterItemsBasedOnDiscount(itemsFromFile);

        System.out.println("\nBroj artikala u trgovinama: ");
        storesFromFile.stream()
                .mapToInt(s->s.getItems().length)
                .forEach(System.out::println);
    }

    /**
     * used for entering data (String)
     * @param scanner takes user input
     * @param message presents message as explanation what's being asked of user
     * @return entered value (String) as data
     */
    private static String dataInput (Scanner scanner, String message){
        System.out.print(message);
        return scanner.nextLine();
    }

    /**
     * used for entering data (numeric value (BigDecimal))
     * @param scanner takes user input
     * @param message presents message as explanation what's being asked of user
     * @return entered value (BigDecimal) as data
     */
    private static BigDecimal bigDecimalInput (Scanner scanner, String message){
        boolean wrongValue;
        BigDecimal number=BigDecimal.ZERO;
        do {
            try {
                System.out.print(message);
                number = scanner.nextBigDecimal();
                wrongValue = false;
            } catch (InputMismatchException exception) {
                System.out.println("Pogrešan unos. Molimo ponoviti.");
                logger.error("Pogrešan format podatka");
                wrongValue = true;
            } finally {
                scanner.nextLine();
            }
        }
            while (wrongValue) ;
        logger.info("Unesen je broj " + number);
        return number;
    }

    /**
     * used for entering data (numeric value (Integer))
     * @param scanner takes user input
     * @return entered value (Integer) as data
     */
    private static Integer integerInput (Scanner scanner){
        boolean wrongValue;
        Integer number=0;
        do{
            try{
                number=scanner.nextInt();
                wrongValue=false;
            } catch (InputMismatchException exception){
                System.out.println("Pogrešan unos. Molimo ponoviti.");
                logger.error("Pogrešan format podatka");
                wrongValue=true;
            } finally {
                scanner.nextLine();
            }
        } while (wrongValue);

        logger.info("Unesen je broj " + number);
        return number;

    }

    /**
     * used for calculating biggest volume among items in factories
     * @param factories presents factories whose items will be taken into account and the name of the one with the
     *                  biggest volume will be shown
     */
    private static void biggestVolumeItem (List<Factory> factories){

        BigDecimal biggestVolume = factories.get(0).getItems()[0].getVolume();
        Integer factoryBiggestVolumeIndex=0;

        for (int i=0;i<factories.size();i++){
            for (int j=0; j<factories.get(i).getItems().length;j++){
                if (factories.get(i).getItems()[j].getVolume().compareTo(biggestVolume)>0){
                    biggestVolume=factories.get(i).getItems()[j].getVolume();
                    factoryBiggestVolumeIndex=i;
                }
            }

        }

        System.out.println("Tvornica koja proizvod artikl s najvećim volumenom je " +
                factories.get(factoryBiggestVolumeIndex).getName());

    }

    /**
     * used for calculating which item is the cheapest
     * @param stores presents stores whose items will be taken into account and the name of the cheapest one will be
     *               shown
     */
    private static void cheapestItem(List<Store> stores){
        BigDecimal lowestPrice = stores.get(0).getItems()[0].getSellingPrice();
        Integer storeCheapestItemIndex=0;

        for (int i=0; i<stores.size();i++){
            for (int j=0; j<stores.get(i).getItems().length;j++){
                if (stores.get(i).getItems()[j].getVolume().compareTo(lowestPrice)<0){
                    lowestPrice=stores.get(i).getItems()[j].getSellingPrice();
                    storeCheapestItemIndex=i;
                }
            }
        }

        System.out.println("Trgovina koja prodaje najjeftiniji artikl je "
                + stores.get(storeCheapestItemIndex).getName());

    }

    /**
     * used for calculating which edible item has the highest amount of calories and which is the most expansive
     * @param items presents items among which name of two items will be shown (one with the highest amount of calories
     *              and one that is the most expansive)
     */
    private static void highestAmountOfKiloCaloriesAndHighestPrice (List<Item> items){
        Integer highestAmountOfKiloCalories=0;
        Integer highestAmountIndex=0;
        Integer highestPrice=0;
        Integer highestPriceIndex=0;
        for (int i=0;i<items.size();i++)
        {
            if (items.get(i) instanceof Salmon || items.get(i) instanceof Peanut){
                if (((Edible) items.get(i)).calculateKilocalories()>highestAmountOfKiloCalories)
                {
                    highestAmountOfKiloCalories=((Edible) items.get(i)).calculateKilocalories();
                    highestAmountIndex=i;
                }

                if (((Edible) items.get(i)).calculatePrice().intValue()>highestPrice){
                    highestPrice=((Edible) items.get(i)).calculatePrice().intValue();
                    highestPriceIndex=i;
                }

            }
        }

        System.out.println("Namirnica s najviše kilokalorija je "+ items.get(highestAmountIndex).getName());
        System.out.println("Najskuplja namirnica je "+ items.get(highestPriceIndex).getName());
    }

    /**
     * used for calculating which technical item has the shortest warranty
     * @param items presents items among which the name of the one with the shortest warranty will be shown
     */
    private static void shortestWarranty (List<Item> items){
        Integer shortestWarrantyDuration=9999;
        Integer shortestWarrantyIndex=0;

        for (int i=0;i<items.size();i++){
            if (items.get(i) instanceof Laptop){
                if (((Laptop) items.get(i)).getWarrantyPeriodMonths()<shortestWarrantyDuration){
                    shortestWarrantyDuration= ((Laptop) items.get(i)).getWarrantyPeriodMonths();
                    shortestWarrantyIndex=i;
                }
            }
        }

        System.out.println("Laptop s najkraćom garancijom je "+ items.get(shortestWarrantyIndex).getName());
    }


    /**
     * used for checking if items are the same, if they are the exception will be thrown
     * @param articleIndexes presents indexes of items taken into account
     * @param size presents number of items taken into account
     * @param userInput presents the index of the item that user has entered
     * @throws SameItem exception that is thrown if items are the same
     */
    private static void checkSameItems(Integer[] articleIndexes, Integer size, Integer userInput) throws SameItem{
        for (int i=0;i<size;i++){
            if (articleIndexes[i]==userInput)
                throw new SameItem(("Navedeni artikl je već odabran."));
        }
    }

    /**
     * prints out the most expensive item and the cheapest item in a category
     * @param items presents items in categories
     */
    private static void printItemsInCategories (Map <Category, List<Item>> items){
        for (Map.Entry<Category,List <Item>> entry:items.entrySet()){
            Category category=entry.getKey();
            List<Item> items1=entry.getValue();
                System.out.println("Najskuplji artikl u kategoriji " + category.getName() + ": " +
                        items1.get(items1.size()-1));
                System.out.println("Najjeftiniji artikl u kategoriji " + category.getName() + ": "
                        + items1.get(0));

        }

    }

    /**
     * prints out teh cheapest and the most expansive item that implements technical or edible interface
     * @param items presents list of items that is being searched
     */
    private static void cheapestMostExpansiveTechnicalOrEdibleItem (List <Item> items){


        Integer cheapestItemIndex=0;
        Integer mostExpansiveItemIndex=0;
        List <Item> items1 = new ArrayList<>();

        for (int j=0;j<items.size();j++) {
            if (items.get(j) instanceof Technical || items.get(j) instanceof Edible) {
                items1.add(items.get(j));
            }
        }

        Integer mostExpansive= items1.get(0).getSellingPrice().intValue();
        Integer cheapest = items1.get(0).getSellingPrice().intValue();

        for (int i=0;i<items.size();i++){
            if (items.get(i) instanceof Technical || items.get(i) instanceof Edible){
                if (items.get(i).getSellingPrice().intValue()>mostExpansive){
                    mostExpansive=items.get(i).getSellingPrice().intValue();
                    mostExpansiveItemIndex=i;
                }
                if (items.get(i).getSellingPrice().intValue()<cheapest){
                    cheapest=items.get(i).getSellingPrice().intValue();
                    cheapestItemIndex=i;
                }
            }
        }

        System.out.println("Najskuplji artikl koji je jestiv ili tehnološki je: "
                +items.get(mostExpansiveItemIndex).getName());
        System.out.println("Najjeftiniji artikl koji je jestiv ili tehnološki je: "+
                items.get(cheapestItemIndex).getName());

    }


    private static void sortVolumes (List <Item> items) {
        BigDecimal temp;
        for (int i = 0; i < items.size(); i++) {
            for (int j = 1; j < items.size() - 1; j++) {
                if (items.get(j-1).getVolume().intValue()>items.get(j).getVolume().intValue()){
                    temp=items.get(j-1).getVolume();
                    items.get(j-1).setVolume((items.get(j).getVolume()));
                    items.get(j).setVolume(temp);
                }
            }
        }

    }

    private static Integer averageSellingPrice(List<Item> items) {
        Integer averageVolume = 0;
        Integer sum = 0;
        Integer sum2 = 0;
        List<Item> newItems = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            sum += items.get(i).getVolume().intValue();
        }
        averageVolume = sum / items.size();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getVolume().intValue() > averageVolume) {
                newItems.add(items.get(i));
            }
        }

        for (int i = 0; i < newItems.size(); i++) {
            sum2 += newItems.get(i).getSellingPrice().intValue();
        }
        return sum2 / newItems.size();


    }

    private static List<Store> storesWithMoreItems (List <Store> stores){
        Integer sum=0;
        Integer averageNumberOfItems;
        List <Store> newStores = new ArrayList<>();

        for (int i=0;i<stores.size();i++){
            sum+=stores.get(i).getItems().length;
        }

        averageNumberOfItems=sum/stores.size();

        for (int i=0;i< stores.size();i++){
            if (stores.get(i).getItems().length>averageNumberOfItems){
                newStores.add(stores.get(i));
            }
        }

        return newStores;

    }

    private static Item filterItemsBasedOnDiscount(List<Item> items) {
        Item itemWithoutDiscount = new Item();
        Optional<Item> itemWithDiscount = items
                .stream()
                .filter(s -> s.getDiscount().intValue() > 0)
                .findFirst();

        return itemWithDiscount.orElse(itemWithoutDiscount);
    }


    private static void readCategoriesFromFile(List<Category> categories) {

        try (FileReader fileReader = new FileReader(Main.CATEGORIES_FILE_NAME);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;

            while ((line = reader.readLine()) != null) {
                Long id = Long.parseLong(line);
                String name = reader.readLine();
                String description = reader.readLine();

                Category category = new Category(id, name, description);
                categories.add(category);
            }
        } catch (IOException e) {
            System.out.println("File " + Main.CATEGORIES_FILE_NAME + " not found.");
            logger.error(e.getMessage(), e);
        }


    }

    private static void readItemsFromFile (List <Item> items, List<Category> categories){

        try (FileReader fileReader = new FileReader(Main.ITEMS_FILE_NAME);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;

            while ((line = reader.readLine()) != null) {
                Long id = Long.parseLong(line);
                String name = reader.readLine();
                String categoryLine=reader.readLine();
                Category category= new Category();
                int idCounter = 0;
                for (int i=0;i<categories.size();i++){
                    if (categoryLine.equals("Food")){
                        category=categories.get(0);
                        idCounter=3;
                        if (name.equals("Salmon")){
                            idCounter=4;
                        }
                    }
                    else if (categoryLine.equals("Technical equipment")){
                        category=categories.get(1);
                        idCounter=2;
                    }
                    else if (categoryLine.equals("Literature")){
                        category=categories.get(2);
                        idCounter=1;
                    }
                    else if (categoryLine.equals("Bicycles")){
                        category=categories.get(3);
                        idCounter=1;
                    }
                    else if (categoryLine.equals("Video games")){
                        category=categories.get(4);
                        idCounter=1;
                    }
                }
                BigDecimal width = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                BigDecimal height = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                BigDecimal length = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                BigDecimal productionCost = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                BigDecimal sellingPrice = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                BigDecimal discount = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                BigDecimal weight = BigDecimal.valueOf(0);
                Integer warranty = 0;
                if (categoryLine.equals("Food") || categoryLine.equals("Technical equipment") ) {
                    if (categoryLine.equals("Food")) {
                        weight = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                    }
                    else {
                        warranty = Integer.parseInt(reader.readLine());
                    }
                }
                else
                    reader.readLine();

                Item item = switch (idCounter){
                    case 2 -> new Laptop(id,name, category, width, height, length, productionCost, sellingPrice,
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
            System.out.println("File " + Main.ITEMS_FILE_NAME + " not found.");
            logger.error(e.getMessage(), e);
        }

    }

    private static void readAddressesFromFile(List<Address> addresses) {

        ArrayList<Cities> cityList = new ArrayList<>();
        for (Cities cities : Cities.values()) {
            cityList.add(cities);
        }
        Optional<Cities> city = Optional.empty();

        try (FileReader fileReader = new FileReader(Main.ADDRESSES_FILE_NAME);
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
            System.out.println("File " + Main.ADDRESSES_FILE_NAME + " not found.");
            logger.error(e.getMessage(), e);
        }

    }

    private static void readFactoriesFromFile (List<Factory> factories, List <Item> items, List <Address> addresses){

        Optional<Address> address = Optional.empty();

        try (FileReader fileReader = new FileReader(Main.FACTORIES_FILE_NAME);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            String idLine;
            String addressIDLine;
            while ((line = reader.readLine()) != null) {
                idLine = reader.readLine();
                Long id = Long.parseLong(idLine);
                String name = reader.readLine();
                addressIDLine= reader.readLine();
                Integer addressID = Integer.parseInt(addressIDLine);
                for (int i=0;i<addresses.size();i++){
                    if ((addressID-1)==i){
                        address= Optional.ofNullable(addresses.get(i));
                    }
                }
                List <Long> itemsIDs= Arrays.stream(reader.readLine()
                        .split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                Set<Item> factoryItems = new HashSet<>();

                for (int i=0;i<itemsIDs.size();i++){
                    for (int j=0;j<items.size();j++){
                        if (itemsIDs.get(i).equals(items.get(j).getId())){
                            factoryItems.add(items.get(j));
                        }
                    }
                }

                Factory factory = new Factory(name,id,address.get(),factoryItems);
                factories.add(factory);
            }


        } catch (IOException e) {
            System.out.println("File " + Main.FACTORIES_FILE_NAME + " not found.");
            logger.error(e.getMessage(), e);
        }

    }

    private static void readStoresFromFile (List<Store> stores, List <Item> items){

        try (FileReader fileReader = new FileReader(Main.STORES_FILE_NAME);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            String idLine;
            String addressIDLine;
            while ((line = reader.readLine()) != null) {
                idLine = reader.readLine();
                Long id = Long.parseLong(idLine);
                String name = reader.readLine();
                String webAddress=reader.readLine();

                List <Long> itemsIDs= Arrays.stream(reader.readLine()
                                .split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                Set<Item> storeItems = new HashSet<>();

                for (int i=0;i<itemsIDs.size();i++){
                    for (int j=0;j<items.size();j++){
                        if (itemsIDs.get(i).equals(items.get(j).getId())){
                            storeItems.add(items.get(j));
                        }
                    }
                }

                int storeType;
                int foodCounter = 0;
                int technicalCounter=0;

                List<Item> storedItems= new ArrayList<>(storeItems);
                Set<Item> foodAndTechnicalItems = new HashSet<>();
                List <Edible> foodItems = new ArrayList<>();
                List <Laptop> technicalItems = new ArrayList<>();

                for (int i=0;i<storedItems.size();i++){
                    if (storedItems.get(i) instanceof Salmon || storedItems.get(i) instanceof Peanut){
                        foodCounter+=1;
                        foodAndTechnicalItems.add(storedItems.get(i));
                        foodItems.add((Edible) storedItems.get(i));
                    }
                    else if (storedItems.get(i) instanceof Laptop){
                        technicalCounter+=1;
                        foodAndTechnicalItems.add(storedItems.get(i));
                        technicalItems.add((Laptop) storedItems.get(i));
                    }
                }

                if (storedItems.size()==foodCounter){
                    storeType=2;
                }
                else if (storedItems.size()==technicalCounter){
                    storeType=3;
                }
                else
                    storeType=1;

                Store store = switch (storeType){
                    case 2 -> new FoodStore<>(name,id,webAddress,foodAndTechnicalItems,foodItems);
                    case 3 -> new TechnicalStore<>(name,id,webAddress,foodAndTechnicalItems,technicalItems);
                    default -> new Store (name,id,webAddress,storeItems);
                };
                stores.add(store);
            }


        } catch (IOException e) {
            System.out.println("File " + Main.STORES_FILE_NAME + " not found.");
            logger.error(e.getMessage(), e);
        }

    }

    private static void serializeFactories(List<Factory> factories){

        File file = new File(Main.SERIALIZED_FACTORIES_FILE_NAME);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {

            List<Factory> serializedFactories = factories.stream()
                    .filter(s -> s.getItems().length>=5)
                    .collect(Collectors.toList());

            out.writeObject(serializedFactories);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void deserializeFactories(String fileName){

        File file = new File(fileName);

        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {

            List<Factory> readFactories = (List<Factory>) in.readObject();

            System.out.println("\nDeserijalizirane tvornice:");

            for (int i=0;i<readFactories.size();i++){
                System.out.println(readFactories.get(i).getName());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void serializeStores(List<Store> stores){

        File file = new File(Main.SERIALIZED_STORES_FILE_NAME);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {

            List<Store> serializedStores = stores.stream()
                    .filter(s -> s.getItems().length>=5)
                    .collect(Collectors.toList());

            out.writeObject(serializedStores);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void deserializeStores (String fileName){

        File file = new File(fileName);

        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {

            List<Store> readStores = (List<Store>) in.readObject();

            System.out.println("\nDeserijalizirane trgovine:");

            for (int i=0;i<readStores.size();i++){
                System.out.println(readStores.get(i).getName());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printCategoriesFromFile (List<Category> categoriesFromFile){
        for (int i=0;i<categoriesFromFile.size();i++){
            System.out.println("ID: "+categoriesFromFile.get(i).getId() + ", Name: "+categoriesFromFile.get(i).getName()
                    +", Description: "+categoriesFromFile.get(i).getDescription());
        }
    }

    private static void printItemsFromFile (List<Item> itemsFromFile){
        for (int i=0;i<itemsFromFile.size();i++){
            System.out.println("ID: "+itemsFromFile.get(i).getId() + ", Name: "+itemsFromFile.get(i).getName()
                    +", Category: "+itemsFromFile.get(i).getCategory()+", Width: "+itemsFromFile.get(i).getWidth()+
                    ", Height: "+itemsFromFile.get(i).getHeight()+ ", Length: "+itemsFromFile.get(i).getLength()+
                    ", Production cost: "+itemsFromFile.get(i).getProductionCost()+", Selling price: "+
                    itemsFromFile.get(i).getSellingPrice()+", discount: "+itemsFromFile.get(i).getDiscount());
            if (itemsFromFile.get(i) instanceof Salmon){
                System.out.println(", Weight: "+((Salmon) itemsFromFile.get(i)).getWeight());
            }
            else if (itemsFromFile.get(i) instanceof Peanut){
                System.out.println(", Weight: "+((Peanut) itemsFromFile.get(i)).getWeight());
            }
            else if (itemsFromFile.get(i) instanceof Laptop){
                System.out.println(", Warranty: "+(((Laptop) itemsFromFile.get(i)).getWarrantyPeriodMonths()));
            }
        }
    }

    private static void printAddressesFromFile (List<Address> addressesFromFile){
        for (int i=0;i< addressesFromFile.size();i++){
            System.out.println("City: " + addressesFromFile.get(i).getCity().getName()+", Postal code: "
                    +addressesFromFile.get(i).getCity().getPostalCode()+", Street: "+addressesFromFile.get(i).getStreet()
                    +", House number: "+addressesFromFile.get(i).getHouseNumber());
        }
    }

    private static void printFactoriesFromFile(List<Factory> factoriesFromFile){
        for (int i=0;i<factoriesFromFile.size();i++){
            System.out.println("Factory name: "+factoriesFromFile.get(i).getName()+", ID: "+factoriesFromFile.get(i)
                    .getId() + ", Address: "+
                    factoriesFromFile.get(i).getAddress().getCity().getName());
            for (int j=0;j<factoriesFromFile.get(i).getItems().length;j++){
                System.out.println("Item number "+(j+1)+": "+factoriesFromFile.get(i).getItems()[j].getName());
            }
        }
    }

    private static void printStoresFromFile (List<Store> storesFromFile){
        for (int i=0;i<storesFromFile.size();i++){
            System.out.println("Store name: "+storesFromFile.get(i).getName()+", ID: "+storesFromFile.get(i).getId()+
                    ", web address: "+storesFromFile.get(i).getWebAddress());
            for (int j=0;j<storesFromFile.get(i).getItems().length;j++){
                System.out.println("Item number "+(j+1)+": "+storesFromFile.get(i).getItems()[j].getName());
            }
        }
    }

}


