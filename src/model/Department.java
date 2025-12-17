package model;

public class Department {
    public int id;
    public int cost;
    public int value;

    public Department(int id, int cost, int value) {
        this.id = id;
        this.cost = cost;
        this.value = value;
    }

    public double getRatio() {

        return (double) value / cost;
    }
}
