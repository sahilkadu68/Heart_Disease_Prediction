package model;

import data.DataRecord;
import java.util.*;

public class ModelEvaluator {

    public static class EvaluationResult {

        public final int truePositive;
        public final int trueNegative;
        public final int falsePositive;
        public final int falseNegative;

        public final double accuracy;
        public final double sensitivity;
        public final double specificity;
        public final double precision;

        public EvaluationResult(
                int truePositive,
                int trueNegative,
                int falsePositive,
                int falseNegative) {

            this.truePositive = truePositive;
            this.trueNegative = trueNegative;
            this.falsePositive = falsePositive;
            this.falseNegative = falseNegative;

            int total =
                    truePositive +
                    trueNegative +
                    falsePositive +
                    falseNegative;

            if (total == 0) {
                accuracy = 0;
            }
            else {
                accuracy =
                        (double) (truePositive + trueNegative)
                        / total;
            }

            if (truePositive + falseNegative == 0) {
                sensitivity = 0;
            }
            else {
                sensitivity =
                        (double) truePositive
                        / (truePositive + falseNegative);
            }

            if (trueNegative + falsePositive == 0) {
                specificity = 0;
            }
            else {
                specificity =
                        (double) trueNegative
                        / (trueNegative + falsePositive);
            }

            if (truePositive + falsePositive == 0) {
                precision = 0;
            }
            else {
                precision =
                        (double) truePositive
                        / (truePositive + falsePositive);
            }
        }
    }

    public EvaluationResult evaluate(
            NaiveBayesClassifier model,
            List<DataRecord> testData) {

        int truePositive = 0;
        int trueNegative = 0;
        int falsePositive = 0;
        int falseNegative = 0;

        for (DataRecord record : testData) {

            int actual = record.getTarget();

            int predicted =
                    model.predict(record);

            if (actual == 1 && predicted == 1) {
                truePositive++;
            }
            else if (actual == 0 && predicted == 0) {
                trueNegative++;
            }
            else if (actual == 0 && predicted == 1) {
                falsePositive++;
            }
            else if (actual == 1 && predicted == 0) {
                falseNegative++;
            }
        }

        return new EvaluationResult(
                truePositive,
                trueNegative,
                falsePositive,
                falseNegative
        );
    }
}