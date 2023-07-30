package budget;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> purchases = new ArrayList<>();
        double total = 0;

        try (Scanner scanner = new Scanner(System.in)) {

            while (scanner.hasNext()) {
                String input = scanner.nextLine();
                purchases.add(input);
                double cost = Double.parseDouble(input.split("\\$")[1]);
                System.out.println(cost);
                total += cost;
                System.out.println(total);
            }

            for (String purchase : purchases) {
                System.out.println(purchase);
            }

            System.out.println();
            System.out.printf("Total: $%1$.2f", total);

        } catch (Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }
}
