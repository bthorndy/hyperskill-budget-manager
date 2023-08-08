package budget;

import jdk.jshell.spi.ExecutionControl;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetManager {

    private double balance;
    private final Scanner scanner;
    ArrayList<Purchase> allPurchases;
    File purchaseFile;

    public BudgetManager() {
        this.balance = 0;
        this.allPurchases = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.purchaseFile = new File("purchases.txt");
    }

   public void run() {

        boolean repeat = true;

        while (repeat) {
            BudgetPrinter.actionMenu();

            int action = selectMenuItem();

            switch (action) {
                case 1 -> addIncome();
                case 2 -> addPurchase();
                case 3 -> listPurchases();
                case 4 -> BudgetPrinter.printBalance(balance);
                case 5 -> savePurchases();
                case 6 -> loadPurchases();
                case 7 -> analyze();
                case 0 -> repeat = false;
                default -> System.out.println("Not a valid selection. Please select from the menu.");
            }
        }
        System.out.println("Bye!");
    }

    private void loadPurchases() {
        if (purchaseFileReady()) {

            try (Scanner fileReader = new Scanner(purchaseFile)) {
                if (fileReader.hasNext()) {
                    balance = Double.parseDouble(fileReader.nextLine());
                }

                while (fileReader.hasNext()) {
                    String currentLine = fileReader.nextLine();

                    try {
                        String[] purchaseStrings = currentLine.split(",");
                        Purchase purchase = new Purchase(purchaseStrings[0],
                                Double.parseDouble(purchaseStrings[1]),
                                PurchaseType.values()[Integer.parseInt(purchaseStrings[2])]);
                        allPurchases.add(purchase);
                    } catch (Exception e) {
                        System.out.printf("Error processing purchase. Skipping line: %s%n", currentLine);
                        e.printStackTrace();
                    }
                }

                //Clear contents after loading
                new FileWriter(purchaseFile).close();

            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        System.out.println("Purchases were loaded!");
    }

    private void savePurchases() {

        if (purchaseFileReady()) {

            try (PrintWriter writer = new PrintWriter(purchaseFile)) {
                writer.printf("%.2f%n", balance);

                for (Purchase purchase : allPurchases) {
                    writer.printf("%s,%.2f,%s%n", purchase.getName(), purchase.getPrice(), purchase.getPurchaseType().ordinal());
                }

            } catch (FileNotFoundException e) {
                System.out.println("Unable to load purchases: file not found.");
                e.printStackTrace();
                return;
            }
            allPurchases.clear();
            System.out.println("Purchases were saved!");
        }


    }

    private void addIncome() {
        double income = 0;

        System.out.println("Enter income:");
        income = readDoubleInput();
        balance += income;
        System.out.printf("Income was added!%n");
    }

    private void addPurchase() {
        boolean addMore = true;
        while (addMore) {
            int selection;
            PurchaseType purchaseType;
            String purchaseName;
            double price;

            purchaseType = selectPurchaseType(false);
            if (purchaseType.equals(PurchaseType.NONE)) {
                return;
            }

            System.out.println("Enter purchase name:");
            purchaseName = scanner.nextLine();
            System.out.println("Enter its price:");
            price = readDoubleInput();
            allPurchases.add(new Purchase(purchaseName, price, purchaseType));

            if (price > balance) {
                balance = 0;
            } else {
                balance -= price;
            }
            System.out.println("Purchase was added!");
        }
    }

    private void listPurchases() {
        PurchaseType purchaseType = null;
        List<Purchase> purchases;

        do {
            purchaseType = selectPurchaseType(true);
            if (purchaseType.equals(PurchaseType.NONE)) {
                return;
            } else {
                purchases = generatePurchaseList(purchaseType);
                BudgetPrinter.printPurchaseList(purchaseType.toString(), purchases);
            }
        } while (!purchaseType.equals(PurchaseType.NONE));
    }

    private void analyze() {
        boolean analyzeAgain = true;
        while(analyzeAgain) {
            BudgetPrinter.sortOptions();
            int sortType = selectMenuItem();
            switch (sortType) {
                case 1 -> BudgetPrinter.printPurchaseList(PurchaseType.ALL.toString(), sortByAmount(allPurchases));
                case 2 -> sortByType();
                case 3 -> sortCertainType();
                case 4 -> {
                    analyzeAgain = false;
                }
                default -> System.out.println("Invalid selection.");
            }
        }
    }

    private void sortByType() {
        HashMap<PurchaseType, Double> purchaseTotalsMap = new HashMap<>();
        ArrayList<Purchase> purchaseTotalsArr = new ArrayList<>();

        for (PurchaseType pt : PurchaseType.values()) {
            if (pt != PurchaseType.NONE && pt != PurchaseType.ALL) {
                purchaseTotalsMap.put(pt, 0d);
            }
        }

        for (Purchase p : allPurchases) {
            purchaseTotalsMap.put(p.getPurchaseType(), purchaseTotalsMap.get(p.getPurchaseType()) + p.getPrice());
        }

        for (Map.Entry<PurchaseType, Double> entry: purchaseTotalsMap.entrySet()) {
            purchaseTotalsArr.add(new Purchase(entry.getKey().toString(), entry.getValue(), entry.getKey()));
        }

        BudgetPrinter.printCategoryTotals(sortByAmount(purchaseTotalsArr));
    }

    private void sortCertainType() {
        PurchaseType purchaseType = selectPurchaseType(false);
        if (purchaseType.equals(PurchaseType.NONE)) {
            return;
        }

        BudgetPrinter.printPurchaseList(purchaseType.toString(), sortByAmount(generatePurchaseList(purchaseType)));
    }

    private List<Purchase> sortByAmount(List<Purchase> purchases) {
        return purchases.stream()
                .sorted(Comparator.comparing(Purchase::getPrice).reversed())
                .toList();
    }

    private List<Purchase> bubbleSortByAmount(ArrayList<Purchase> purchases) {
        return sortByAmount(purchases);
    }

    private ArrayList<Purchase> generatePurchaseList(PurchaseType purchaseType) {
        ArrayList<Purchase> purchases = new ArrayList<>();

        if (purchaseType == PurchaseType.ALL) {
            purchases = (ArrayList<Purchase>) allPurchases.clone();
        } else {
            for (Purchase purchase : allPurchases) {
                if (purchase.getPurchaseType() == purchaseType) {
                    purchases.add(purchase);
                }
            }
        }
        return purchases;
    }

    private PurchaseType selectPurchaseType(boolean includeAllOption) {
        PurchaseType purchaseType = null;

        while (purchaseType == null) {
            BudgetPrinter.purchaseCategories(includeAllOption);
            int selected = selectMenuItem();

            if (selected < 5 || (includeAllOption && selected == 5)) {
                purchaseType = PurchaseType.values()[selected];
            } else if (selected == 5 || (includeAllOption && selected == 6)) {
                purchaseType = PurchaseType.NONE;
            } else {
                System.out.println("Invalid selection. Please select an option from the menu.");
            }
        }
        return purchaseType;
    }

    private double readDoubleInput() {
        double input = -1;
        while (input < 0) {
            try {
                input = Double.parseDouble(scanner.nextLine());
                if (input < 0) {
                    throw new IllegalArgumentException("Entry must be positive.");
                }
            } catch (Exception e) {
                System.out.println("Invalid entry. Enter a positive decimal value.");
            }
        }
        return input;
    }

    private int selectMenuItem() {
        int selection = -1;

        while(selection < 0) {
            try {
                selection = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                selection = -1;
                System.out.println("Invalid entry. Select a menu item by number.");
            }
        }
        System.out.println();
        return selection;
    }

    private boolean purchaseFileReady() {
        try {
            purchaseFile.createNewFile();
        } catch (Exception e) {
            System.out.println("An error occurred in finding or creating purchases file.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
