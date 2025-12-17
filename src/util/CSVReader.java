package util;

import model.Department;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    // Python ile hazırlanan temiz veriyi okur
    public static List<Department> loadDepartments(String path) {
        List<Department> departments = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            // 1. Satırı (Başlıkları: Dept,Cost,Value) okuyup atlıyoruz.
            String line = br.readLine();

            // Diğer satırları okumaya başla
            while ((line = br.readLine()) != null) {
                // Virgülle ayır
                String[] parts = line.split(",");

                // Veri formatımız: DeptID, Cost, Value
                // Python çıktısında sıralama böyleydi.
                try {
                    int id = Integer.parseInt(parts[0]);    // Dept
                    int cost = Integer.parseInt(parts[1]);  // Cost
                    int value = Integer.parseInt(parts[2]); // Value

                    // Listeye ekle
                    departments.add(new Department(id, cost, value));

                } catch (NumberFormatException e) {
                    System.err.println("Satır okuma hatası (Sayı formatı bozuk): " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("CSV Okuma Tamamlandı. Toplam Departman: " + departments.size());
        return departments;
    }
}