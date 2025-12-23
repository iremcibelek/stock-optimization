package model;

public class Department {
    public int id;
    public int cost;
    public int value;
    public int currentStock;

    public Department(int id, int cost, int value) {
        this.id = id;
        this.cost = cost;
        this.value = value;
        this.currentStock = 0;
    }

    // Sıralama için oran hesabı
    public double getRatio() {
        return (double) this.value / (double) this.cost;
    }
}