import algorithm.*;
import model.Department;
import util.CSVReader;

import java.util.*;

public class ConsoleMain {

    public static void main(String[] args) {

        List<Department> departments =
                CSVReader.loadDepartments("data/cleanedWalmartTopStore.csv");

        int totalCost = departments.stream().mapToInt(d -> d.cost).sum();
        int budget = (int) (totalCost * 0.30);

        System.out.println("Total Budget: " + budget);

        List<Department> greedyResult =
                GreedySolver.solve(new ArrayList<>(departments), budget);

        List<Department> dpResult =
                DPSolver.solve(departments, budget);

        System.out.println("\nGreedy Solution:");
        printResult(greedyResult);

        System.out.println("\nDP Solution:");
        printResult(dpResult);
    }

    private static void printResult(List<Department> result) {
        int cost = 0, value = 0;

        for (Department d : result) {
            cost += d.cost;
            value += d.value;
        }

        System.out.println("Selected departments: " + result.size());
        System.out.println("Total Cost: " + cost);
        System.out.println("Total Value: " + value);
    }
}
