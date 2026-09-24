package data;

import java.util.*;

public class DataPreprocessor {

    public void preprocess(List<DataRecord> records) {

        // Find mode of CA
        int caMode = findMode(records, true);

        // Find mode of THAL
        int thalMode = findMode(records, false);

        // Replace missing values
        for (DataRecord record : records) {

            if (record.getCa() == DataRecord.MISSING_INT) {
                record.setCa(caMode);
            }

            if (record.getThal() == DataRecord.MISSING_INT) {
                record.setThal(thalMode);
            }
        }
    }

    private int findMode(List<DataRecord> records, boolean caColumn) {

        Map<Integer, Integer> frequency =
                new HashMap<>();

        for (DataRecord record : records) {

            int value;

            if (caColumn) {
                value = record.getCa();
            }
            else {
                value = record.getThal();
            }

            if (value == DataRecord.MISSING_INT) {
                continue;
            }

            frequency.put(
                    value,
                    frequency.getOrDefault(value, 0) + 1
            );
        }

        int mode = -1;
        int highestFrequency = 0;

        for (Map.Entry<Integer, Integer> entry
                : frequency.entrySet()) {

            if (entry.getValue() > highestFrequency) {

                highestFrequency =
                        entry.getValue();

                mode = entry.getKey();
            }
        }

        return mode;
    }
}