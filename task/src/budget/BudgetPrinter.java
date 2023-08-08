package budget;

import java.util.List;

public class BudgetPrinter {

    public static void actionMenu() {
        System.out.print("""
                                
                Choose your action:
                1) Add income
                2) Add purchase
                3) Show list of purchases
                4) Balance
                5) Save
                6) Load
                7) Analyze (Sort)
                0) Exit
                """);
    }

    public static void purchaseCategories(boolean includeAll) {
        System.out.print("""                               
                
                Choose the type of purchases
                1) Food
                2) Clothes
                3) Entertainment
                4) Other
                """);
        if (includeAll) {
            System.out.print("""
                    5) All
                    6) Back
                    """);
        } else {
            System.out.println("""
                    5) Back""");
        }
    }

    public static void sortOptions() {
        System.out.print("""
                
                How do you want to sort?
                1) Sort all purchases
                2) Sort by type
                3) Sort certain type
                4) Back
                """);
    }

    public static void printPurchaseList(String listHeader, List<Purchase> purchases) {
        double purchaseTotal = 0d;
        System.out.println(listHeader + ":");

        if (purchases.size() == 0) {
            System.out.println("The purchase list is empty.");
        } else {
            for (Purchase p : purchases) {
                System.out.printf("%s $%.2f%n", p.getName(), p.getPrice());
                purchaseTotal += p.getPrice();
            }
            System.out.printf("Total sum: $%.2f%n", purchaseTotal);
        }
    }

    public static void printBalance(double balance) {
        System.out.printf("Balance: $%1$.2f%n", balance);
    }

    public static void printCategoryTotals(List<Purchase> purchases) {
        double purchaseTotal = 0d;
        System.out.println("Types:");

        for (Purchase p : purchases) {
            System.out.printf("%s - $%.2f%n", p.getPurchaseType().toString(), p.getPrice());
            purchaseTotal += p.getPrice();
        }
            System.out.printf("Total sum: $%.2f%n", purchaseTotal);
    }
}