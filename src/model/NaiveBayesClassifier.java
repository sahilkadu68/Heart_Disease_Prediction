package model;

import data.DataRecord;
import java.util.*;

public class NaiveBayesClassifier {

    private int count0;
    private int count1;

    private double[] mean0 = new double[5];
    private double[] mean1 = new double[5];

    private double[] variance0 = new double[5];
    private double[] variance1 = new double[5];

    private Map<Integer, Integer>[] categorical0;
    private Map<Integer, Integer>[] categorical1;

    private int total0;
    private int total1;

    public void train(List<DataRecord> records) {

        count0 = 0;
        count1 = 0;

        List<DataRecord> class0 =
                new ArrayList<>();

        List<DataRecord> class1 =
                new ArrayList<>();

        for (DataRecord record : records) {

            if (record.getTarget() == 0) {
                class0.add(record);
                count0++;
            }
            else {
                class1.add(record);
                count1++;
            }
        }

        total0 = class0.size();
        total1 = class1.size();

        calculateMeanVariance(
                class0, mean0, variance0
        );

        calculateMeanVariance(
                class1, mean1, variance1
        );

        categorical0 = createCategoricalMaps();
        categorical1 = createCategoricalMaps();

        calculateCategoricalFrequencies(
                class0, categorical0
        );

        calculateCategoricalFrequencies(
                class1, categorical1
        );
    }

    private void calculateMeanVariance(
            List<DataRecord> records,
            double[] means,
            double[] variances) {

        for (DataRecord record : records) {

            double[] values = getNumericalValues(record);

            for (int i = 0; i < 5; i++) {
                means[i] += values[i];
            }
        }

        for (int i = 0; i < 5; i++) {

            if (!records.isEmpty()) {
                means[i] /= records.size();
            }
        }

        for (DataRecord record : records) {

            double[] values = getNumericalValues(record);

            for (int i = 0; i < 5; i++) {

                double difference =
                        values[i] - means[i];

                variances[i] +=
                        difference * difference;
            }
        }

        for (int i = 0; i < 5; i++) {

            if (!records.isEmpty()) {
                variances[i] /= records.size();
            }

            if (variances[i] == 0) {
                variances[i] = 0.000001;
            }
        }
    }

    private double[] getNumericalValues(
            DataRecord record) {

        return new double[] {
                record.getAge(),
                record.getTrestbps(),
                record.getChol(),
                record.getThalach(),
                record.getOldpeak()
        };
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer>[] createCategoricalMaps() {

        Map<Integer, Integer>[] maps =
                new HashMap[8];

        for (int i = 0; i < 8; i++) {
            maps[i] = new HashMap<>();
        }

        return maps;
    }

    private void calculateCategoricalFrequencies(
            List<DataRecord> records,
            Map<Integer, Integer>[] maps) {

        for (DataRecord record : records) {

            int[] values = {
                    record.getSex(),
                    record.getCp(),
                    record.getFbs(),
                    record.getRestecg(),
                    record.getExang(),
                    record.getSlope(),
                    record.getCa(),
                    record.getThal()
            };

            for (int i = 0; i < 8; i++) {

                int value = values[i];

                maps[i].put(
                        value,
                        maps[i].getOrDefault(value, 0) + 1
                );
            }
        }
    }

    public int predict(DataRecord record) {

        double score0 =
                calculateLogPosterior(record, 0);

        double score1 =
                calculateLogPosterior(record, 1);

        if (score1 > score0) {
            return 1;
        }

        return 0;
    }

    public double[] getProbabilities(
            DataRecord record) {

        double score0 =
                calculateLogPosterior(record, 0);

        double score1 =
                calculateLogPosterior(record, 1);

        double max =
                Math.max(score0, score1);

        double probability0 =
                Math.exp(score0 - max);

        double probability1 =
                Math.exp(score1 - max);

        double total =
                probability0 + probability1;

        return new double[] {
                probability0 / total,
                probability1 / total
        };
    }

    private double calculateLogPosterior(
            DataRecord record,
            int classValue) {

        double prior;

        if (classValue == 0) {
            prior =
                    (double) total0 /
                    (total0 + total1);
        }
        else {
            prior =
                    (double) total1 /
                    (total0 + total1);
        }

        double logProbability =
                Math.log(prior);

        double[] values =
                getNumericalValues(record);

        double[] means;
        double[] variances;

        Map<Integer, Integer>[] categoricalMaps;
        int classTotal;

        if (classValue == 0) {

            means = mean0;
            variances = variance0;
            categoricalMaps = categorical0;
            classTotal = total0;

        }
        else {

            means = mean1;
            variances = variance1;
            categoricalMaps = categorical1;
            classTotal = total1;
        }

        // Numerical features
        for (int i = 0; i < 5; i++) {

            logProbability +=
                    gaussianLogProbability(
                            values[i],
                            means[i],
                            variances[i]
                    );
        }

        // Categorical features
        int[] categoricalValues = {
                record.getSex(),
                record.getCp(),
                record.getFbs(),
                record.getRestecg(),
                record.getExang(),
                record.getSlope(),
                record.getCa(),
                record.getThal()
        };

        for (int i = 0; i < 8; i++) {

            int frequency =
                    categoricalMaps[i].getOrDefault(
                            categoricalValues[i],
                            0
                    );

            // Laplace smoothing
            double probability =
                    (frequency + 1.0) /
                    (classTotal + 2.0);

            logProbability +=
                    Math.log(probability);
        }

        return logProbability;
    }

    private double gaussianLogProbability(
            double value,
            double mean,
            double variance) {

        return -0.5 *
                Math.log(2 * Math.PI * variance)
                -
                ((value - mean) *
                 (value - mean))
                /
                (2 * variance);
    }
}