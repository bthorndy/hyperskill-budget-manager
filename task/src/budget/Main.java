package budget;

public class Main {
    public static void main(String[] args) {
        boolean repeat = true;
        BudgetManager manager = new BudgetManager();

        while (repeat) {
            repeat = manager.run();
        }

        System.out.println("Bye!");
    }
}
