package budget;

import java.util.ArrayList;
import java.util.Scanner;

public class BudgetManager {
    private double balance;
    private double totalPurchases;
    private ArrayList<String> purchases;
    private Scanner scanner;

    public BudgetManager() {
        this.balance = 0;
        this.totalPurchases = 0;
        this.purchases = new ArrayList<String>();
        this.scanner = new Scanner(System.in);
    }

    public BudgetManager(Scanner scanner) {
        this.balance = 0;
        this.totalPurchases = 0;
        this.purchases = new ArrayList<String>();
        this.scanner = scanner;
    }

    public static void displayMenu() {
        System.out.print("""
                Choose your action:
                1) Add income
                2) Add purchase
                3) Show list of purchases
                4) Balance
                0) Exit
                """);
    }

    /** @return Returns boolean indicating whether user wants to terminate the program. */
    public boolean selectAction() {
        int selection;

        try {
            selection = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            selection = -1;
        }

        System.out.println();

        switch (selection) {
            case 1 -> addIncome();
            case 2 -> addPurchase();
            case 3 -> listPurchases();
            case 4 -> printBalance();
            case 0 -> {
                return false;
            }
            default -> {
                System.out.println("Not a valid selection. Please select from the menu.");
            }
        }
        System.out.println();
        return true;
    }

    private void addIncome() {
        double income = 0;

        System.out.println("Enter income:");
        income = readDoubleInput();
        balance += income;
        System.out.println("Income was added!");
    }

    private void addPurchase() {
        String purchaseName;
        double price;

        System.out.println("Enter purchase name:");
        purchaseName = scanner.nextLine();
        System.out.println("Enter its price:");
        price = readDoubleInput();
        purchases.add(String.format("%s $%.2f", purchaseName, price));
        totalPurchases += price;
        if (price > balance) {
            balance = 0;
        } else {
            balance -= price;
        }
        System.out.println("Purchase was added!");
    }

    private void printBalance() {
        System.out.printf("Balance: $%1$.2f%n", balance);
    }

    private void listPurchases() {
        if (purchases.size() == 0) {
            System.out.println("The purchase list is empty.");
        } else {
            for (String purchase : purchases) {
                System.out.println(purchase);
            }
            System.out.printf("Total sum: $%1$.2f%n", totalPurchases);
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
}
