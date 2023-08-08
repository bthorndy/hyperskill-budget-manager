package budget;

public enum PurchaseType {
    NONE,
    FOOD,
    CLOTHES,
    ENTERTAINMENT,
    OTHER,
    ALL;

    @Override
    public String toString() {
        String name = super.toString();
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
