package util;

import model.Department;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CSVReader {

    public static List<Department> loadDepartments(String path) {
        // ID kontrolü için Map (Tekrarları onler)
        Map<Integer, Department> uniqueMap = new HashMap<>();

        Random fixedRandom = new Random(12345); // Her calıstıgında aynı degerler

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // Baslıgı atla

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                try {
                    int id = Integer.parseInt(parts[0]);

                    // Tekrar kontrolu
                    if (uniqueMap.containsKey(id)) {
                        continue;
                    }

                    int cost = Integer.parseInt(parts[1]);
                    int value = Integer.parseInt(parts[2]);

                    Department d = new Department(id, cost, value);

                    d.currentStock = fixedRandom.nextInt(4);

                    uniqueMap.put(id, d);

                } catch (NumberFormatException e) {
                    System.err.println("Satır hatası: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Department> resultList = new ArrayList<>(uniqueMap.values());

        // ID'ye gore sırala
        resultList.sort((d1, d2) -> Integer.compare(d1.id, d2.id));

        System.out.println("Veri Yüklendi. Benzersiz Kayıt: " + resultList.size());

        return resultList;
    }
}