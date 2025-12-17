package algorithm;

import model.Department;
import java.util.*;

public class GreedySolver {

    public static List<Department> solve(List<Department> depts, int budget) {
        // 1. Listeyi bozmamak için yeni bir kopyasını Array'e çevirelim
        // (Arrayler üzerinde algoritma yazmak daha performanslı ve "low-level"dır)
        Department[] deptArray = new Department[depts.size()];
        for(int i = 0; i < depts.size(); i++) {
            deptArray[i] = depts.get(i);
        }

        // 2. Kendi yazdığımız Merge Sort ile sıralayalım
        mergeSort(deptArray, 0, deptArray.length - 1);

        // 3. Klasik Greedy Seçimi
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

    // --- KENDİ YAZDIĞIMIZ MERGE SORT ALGORİTMASI ---

    // Bölme (Divide) Kısmı
    private static void mergeSort(Department[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid);      // Solu sırala
            mergeSort(arr, mid + 1, right); // Sağı sırala

            merge(arr, left, mid, right);   // Birleştir
        }
    }

    // Birleştirme (Conquer/Merge) Kısmı
    private static void merge(Department[] arr, int left, int mid, int right) {
        // Alt dizilerin boyutlarını bul
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Geçici diziler oluştur
        Department[] L = new Department[n1];
        Department[] R = new Department[n2];

        // Verileri kopyala
        for (int i = 0; i < n1; ++i)
            L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[mid + 1 + j];

        // Birleştirme mantığı (Burada Ratio'ya göre BÜYÜKTEN KÜÇÜĞE sıralıyoruz)
        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            // Oranları karşılaştır (Ratio)
            if (L[i].getRatio() >= R[j].getRatio()) { // Büyük olanı başa al
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        // Kalan elemanları ekle
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