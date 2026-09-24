package data;

import java.io.*;
import java.util.*;

public class CSVReader {

    public List<DataRecord> read(String filePath)
            throws IOException {

        List<DataRecord> records = new ArrayList<>();

        BufferedReader br =
                new BufferedReader(
                        new FileReader(filePath));

        // Skip CSV header
        br.readLine();

        String line;

        while ((line = br.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] values = line.split(",");

            if (values.length != 14) {
                continue;
            }

            try {

                int age =
                        (int) Double.parseDouble(values[0]);

                int sex =
                        (int) Double.parseDouble(values[1]);

                int cp =
                        (int) Double.parseDouble(values[2]);

                double trestbps =
                        Double.parseDouble(values[3]);

                double chol =
                        Double.parseDouble(values[4]);

                int fbs =
                        (int) Double.parseDouble(values[5]);

                int restecg =
                        (int) Double.parseDouble(values[6]);

                double thalach =
                        Double.parseDouble(values[7]);

                int exang =
                        (int) Double.parseDouble(values[8]);

                double oldpeak =
                        Double.parseDouble(values[9]);

                int slope =
                        (int) Double.parseDouble(values[10]);


                // CA can contain ?
                int ca;

                if (values[11].equals("?")) {
                    ca = DataRecord.MISSING_INT;
                }
                else {
                    ca =
                        (int) Double.parseDouble(values[11]);
                }


                // THAL can contain ?
                int thal;

                if (values[12].equals("?")) {
                    thal = DataRecord.MISSING_INT;
                }
                else {
                    thal =
                        (int) Double.parseDouble(values[12]);
                }


                // Original target is 0,1,2,3,4
                int target =
                        (int) Double.parseDouble(values[13]);

                // Convert target to binary
                if (target == 0) {
                    target = 0;
                }
                else {
                    target = 1;
                }


                // Create one patient record
                DataRecord record =
                        new DataRecord(
                                age,
                                sex,
                                cp,
                                trestbps,
                                chol,
                                fbs,
                                restecg,
                                thalach,
                                exang,
                                oldpeak,
                                slope,
                                ca,
                                thal,
                                target
                        );

                records.add(record);

            }
            catch (NumberFormatException e) {

                System.out.println(
                        "Skipping invalid row.");
            }
        }

        br.close();

        return records;
    }
}