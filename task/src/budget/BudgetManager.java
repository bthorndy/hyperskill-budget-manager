package budget;

import java.io.*;
import java.util.*;

public class BudgetManager {

    private double balance;
    private Scanner scanner;
    ArrayList<Purchase> purchases;
    File purchaseFile;

    public BudgetManager() {
        this.balance = 0;
        this.purchases = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.purchaseFile = new File("purchases.txt");
    }

    /** @return Returns boolean indicating whether user wants to terminate the program. */
    public boolean run() {
        displayActionMenu();

        int action = selectMenuItem();

        switch (action) {
            case 1 -> addIncome();
            case 2 -> addPurchase();
            case 3 -> selectPurchaseList();
            case 4 -> printBalance();
            case 5 -> savePurchases();
            case 6 -> loadPurchases();
            case 0 -> {
                return false;
            }
            default -> {
                System.out.println("Not a valid selection. Please select from the menu.");
            }
        }
        return true;
    }

    public static void displayActionMenu() {
        System.out.print("""
                
                Choose your action:
                1) Add income
                2) Add purchase
                3) Show list of purchases
                4) Balance
                5) Save
                6) Load
                0) Exit
                """);
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
                                PurchaseType.valueOf(purchaseStrings[2]));
                        purchases.add(purchase);
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

                for (Purchase purchase : purchases) {
                    writer.printf("%s,%.2f,%s%n", purchase.getName(), purchase.getPrice(), purchase.getPurchaseType().toString());
                }

            } catch (FileNotFoundException e) {
                System.out.println("Unable to load purchases: file not found.");
                e.printStackTrace();
                return;
            }
            purchases.clear();
            System.out.println("Purchases were saved!");
        }


    }

    private void addIncome() {
        double income = 0;

        System.out.println("Enter income:");
        income = readDoubleInput();
        balance += income;
        System.out.println("Income was added!");
    }

    private void addPurchase() {
        boolean addMore = true;
        while (addMore) {
            int selection;
            PurchaseType purchaseType;
            String purchaseName;
            double price;

            System.out.print("""
                    
                    Choose the type of purchase
                    1) Food
                    2) Clothes
                    3) Entertainment
                    4) Other
                    5) Back
                    """);
            selection = selectMenuItem();

            if (selection < 1) {
                System.out.println("Invalid menu selection. Try again.");
                continue;
            } else if (selection < 5) {
                purchaseType = PurchaseType.values()[selection - 1];
            } else {
                return;
            }

            System.out.println("Enter purchase name:");
            purchaseName = scanner.nextLine();
            System.out.println("Enter its price:");
            price = readDoubleInput();
            purchases.add(new Purchase(purchaseName, price, purchaseType));

            if (price > balance) {
                balance = 0;
            } else {
                balance -= price;
            }
            System.out.println("Purchase was added!");
        }
    }

    private void printBalance() {
        System.out.printf("Balance: $%1$.2f%n", balance);
    }

    private void selectPurchaseList() {
        boolean selectAgain = true;
        int selection;

        while(selectAgain) {
            System.out.print("""
                                        
                        Choose the type of purchases
                        1) Food
                        2) Clothes
                        3) Entertainment
                        4) Other
                        5) All
                        6) Back
                        """);
            selection = selectMenuItem();
            if (selection < 6) {
                listPurchases(PurchaseType.values()[selection - 1]);
            } else selectAgain = false;
        }
    }

    private void listPurchases(PurchaseType purchaseType) {
        List<Purchase> purchaseBucket;
        double bucketTotal = 0;

        if (purchaseType == PurchaseType.All) {
            purchaseBucket = purchases;
        } else {
            purchaseBucket = purchases.stream()
                    .filter(purchase -> purchase.getPurchaseType() == purchaseType)
                    .toList();
        }

        System.out.println(purchaseType.toString() + ":");

        if (purchaseBucket.size() == 0) {
            System.out.println("The purchase list is empty.");
        } else {
            for (Purchase p : purchaseBucket) {
                System.out.printf("%s $%.2f%n", p.getName(), p.getPrice());
                bucketTotal += p.getPrice();
            }
            System.out.printf("Total sum: $%.2f%n", bucketTotal);
        }
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
