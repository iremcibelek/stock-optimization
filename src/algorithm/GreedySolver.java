package algorithm;

import model.Department;
import java.util.*;

public class GreedySolver {

    public static List<Department> solve(List<Department> depts, int budget) {

        Department[] deptArray = new Department[depts.size()];
        for(int i = 0; i < depts.size(); i++) {
            deptArray[i] = depts.get(i);
        }

        mergeSort(deptArray, 0, deptArray.length - 1);

        // Greedy secimi
        List<Department> selected = new ArrayList<>();
        int usedBudget = 0;

        for (Department d : deptArray) {
            if (usedBudget + d.cost <= budget) {
                selected.add(d);
                usedBudget += d.cost;
            }
        }

        return selected;
    }

    private static void mergeSort(Department[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            merge(arr, left, mid, right);
        }
    }

    private static void merge(Department[] arr, int left, int mid, int right) {

        int n1 = mid - left + 1;
        int n2 = right - mid;

        Department[] L = new Department[n1];
        Department[] R = new Department[n2];

        // Verileri kopyala
        for (int i = 0; i < n1; ++i)
            L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[mid + 1 + j];

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (L[i].getRatio() >= R[j].getRatio()) { // buyuk olanı basa al
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
}