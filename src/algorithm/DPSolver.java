package algorithm;

import model.Department;
import java.util.*;

public class DPSolver {

    public static List<Department> solve(List<Department> depts, int budget) {
        int n = depts.size();

        int[][] dp = new int[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            Department d = depts.get(i - 1);
            for (int b = 0; b <= budget; b++) {
                if (d.cost <= b) {
                    dp[i][b] = Math.max(
                            dp[i - 1][b], // Ürünü almazsak
                            dp[i - 1][b - d.cost] + d.value // Ürünü alırsak
                    );
                } else {
                    dp[i][b] = dp[i - 1][b];
                }
            }
        }

        // Geriye Dönük İz Sürme (Backtracking)
        List<Department> selected = new ArrayList<>();
        int b = budget;

        for (int i = n; i > 0; i--) {
            if (dp[i][b] != dp[i - 1][b]) {
                Department d = depts.get(i - 1);
                selected.add(d);
                b -= d.cost;
            }
        }

        return selected;
    }
}