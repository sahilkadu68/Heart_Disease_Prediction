import data.CSVReader;
import data.DataPreprocessor;
import data.DataRecord;
import model.NaiveBayesClassifier;
import model.ModelEvaluator;
import model.ModelEvaluator.EvaluationResult;
import ui.HeartDiseaseGUI;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        try {

            // 1. Read dataset
            CSVReader reader = new CSVReader();

            List<DataRecord> records =
                    reader.read("data/heart_disease.csv");

            System.out.println(
                    "Total records: " + records.size()
            );

            // 2. Preprocess data
            DataPreprocessor preprocessor =
                    new DataPreprocessor();

            preprocessor.preprocess(records);

            System.out.println(
                    "Preprocessing completed."
            );

            // 3. Shuffle dataset
            Collections.shuffle(
                    records,
                    new Random(42)
            );

            // 4. Split dataset into 70% training and 30% testing
            int trainSize =
                    (int) (records.size() * 0.70);

            List<DataRecord> trainingData =
                    new ArrayList<>(
                            records.subList(
                                    0,
                                    trainSize
                            )
                    );

            List<DataRecord> testingData =
                    new ArrayList<>(
                            records.subList(
                                    trainSize,
                                    records.size()
                            )
                    );

            System.out.println(
                    "Training records: "
                    + trainingData.size()
            );

            System.out.println(
                    "Testing records: "
                    + testingData.size()
            );

            // 5. Train Naive Bayes model
            NaiveBayesClassifier model =
                    new NaiveBayesClassifier();

            model.train(trainingData);

            System.out.println(
                    "Naive Bayes training completed."
            );

            // 6. Evaluate model
            ModelEvaluator evaluator =
                    new ModelEvaluator();

            EvaluationResult result =
                    evaluator.evaluate(
                            model,
                            testingData
                    );

            // 7. Display evaluation results
            System.out.println();
            System.out.println(
                    "===== MODEL EVALUATION ====="
            );

            System.out.println(
                    "True Positive: "
                    + result.truePositive
            );

            System.out.println(
                    "True Negative: "
                    + result.trueNegative
            );

            System.out.println(
                    "False Positive: "
                    + result.falsePositive
            );

            System.out.println(
                    "False Negative: "
                    + result.falseNegative
            );

            System.out.printf(
                    "Accuracy: %.2f%%%n",
                    result.accuracy * 100
            );

            System.out.printf(
                    "Sensitivity: %.2f%%%n",
                    result.sensitivity * 100
            );

            System.out.printf(
                    "Specificity: %.2f%%%n",
                    result.specificity * 100
            );

            System.out.printf(
                    "Precision: %.2f%%%n",
                    result.precision * 100
            );

            // 8. Open GUI
            javax.swing.SwingUtilities.invokeLater(() -> {

                HeartDiseaseGUI gui =
                        new HeartDiseaseGUI(
                                model,
                                result,
                                trainingData.size(),
                                testingData.size()
                        );

                gui.setVisible(true);
            });

        }
        catch (Exception e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}