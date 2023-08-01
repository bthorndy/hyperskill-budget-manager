package budget;

public class Main {
    public static void main(String[] args) {
        boolean repeat = true;
        BudgetManager manager = new BudgetManager();

        while (repeat) {
            manager.displayMenu();
            repeat = manager.selectAction();
        }

        System.out.println("Bye!");
    }
}
