package ui;

import data.DataRecord;
import model.NaiveBayesClassifier;
import model.ModelEvaluator.EvaluationResult;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * HeartDiseaseGUI.java
 *
 * Java Swing desktop interface for the Heart Disease Prediction System.
 * Collects 13 patient attributes, validates input, calls the trained
 * Naive Bayes model, and displays the prediction result.
 *
 * Design: Clean, professional, suitable for a college CIA demonstration.
 */
public class HeartDiseaseGUI extends JFrame {

    // ── Colour palette ────────────────────────────────────────────────────
    private static final Color CLR_BG        = new Color(15,  23,  42);   // Dark navy
    private static final Color CLR_PANEL     = new Color(30,  41,  59);   // Slightly lighter
    private static final Color CLR_CARD      = new Color(51,  65,  85);   // Card background
    private static final Color CLR_ACCENT    = new Color(56, 189, 248);   // Sky blue
    private static final Color CLR_SUCCESS   = new Color(16, 185, 129);   // Emerald
    private static final Color CLR_DANGER    = new Color(239, 68,  68);   // Red
    private static final Color CLR_TEXT      = new Color(241, 245, 249);  // Near-white
    private static final Color CLR_SUBTEXT   = new Color(148, 163, 184);  // Muted grey
    private static final Color CLR_BORDER    = new Color(71,  85, 105);   // Border
    private static final Color CLR_INPUT_BG  = new Color(51,  65,  85);   // Input background

    // ── Fonts ─────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADER   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_LABEL    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_INPUT    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font FONT_RESULT   = new Font("Segoe UI", Font.BOLD,  17);
    private static final Font FONT_PROB     = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_STATUS   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_DISCLAIMER = new Font("Segoe UI", Font.ITALIC, 10);

    // ── Model reference ───────────────────────────────────────────────────
    private final NaiveBayesClassifier model;

    // ── Input components ──────────────────────────────────────────────────
    private JTextField tfAge, tfTrestbps, tfChol, tfThalach, tfOldpeak;
    private JComboBox<String> cbSex, cbCp, cbFbs, cbRestecg, cbExang;
    private JComboBox<String> cbSlope, cbCa, cbThal;

    // ── Output components ─────────────────────────────────────────────────
    private JLabel  lblResult, lblProb0, lblProb1;
    private JPanel  pnlResult;
    private JLabel  lblStatus;

    // ── Evaluation result for status bar ─────────────────────────────────
    private final EvaluationResult evalResult;
    private final int trainCount, testCount;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public HeartDiseaseGUI(NaiveBayesClassifier model,
                           EvaluationResult evalResult,
                           int trainCount, int testCount) {
        this.model       = model;
        this.evalResult  = evalResult;
        this.trainCount  = trainCount;
        this.testCount   = testCount;

        setTitle("Heart Disease Prediction System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(CLR_BG);

        buildUI();
        pack();
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        setResizable(true);
    }

    // -----------------------------------------------------------------------
    // UI Construction
    // -----------------------------------------------------------------------

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(CLR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        root.add(buildHeader(),         BorderLayout.NORTH);
        root.add(buildCenter(),         BorderLayout.CENTER);
        root.add(buildStatusBar(),      BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Header ────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_PANEL);
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, CLR_ACCENT),
                new EmptyBorder(14, 24, 14, 24)));

        // Left side: title
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);

        JLabel title = new JLabel("♥  Heart Disease Prediction System");
        title.setFont(FONT_TITLE);
        title.setForeground(CLR_TEXT);

        JLabel subtitle = new JLabel(
                "Based on UCI Cleveland Dataset · Naïve Bayes Classifier · Academic Demonstration");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(CLR_SUBTEXT);

        left.add(title);
        left.add(subtitle);

        // Right side: accuracy badge
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        JLabel badge = new JLabel(String.format("Model Accuracy: %.2f%%  ",
                evalResult.accuracy * 100));
        badge.setFont(FONT_HEADER);
        badge.setForeground(CLR_SUCCESS);
        right.add(badge);

        panel.add(left,  BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // ── Center (form + result side-by-side) ──────────────────────────────

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CLR_BG);
        panel.setBorder(new EmptyBorder(16, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.gridy  = 0;
        gbc.weighty = 1.0;

        // Input form
        gbc.gridx  = 0;
        gbc.weightx = 0.6;
        panel.add(buildInputForm(), gbc);

        // Result panel
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.insets  = new Insets(0, 0, 0, 0);
        panel.add(buildResultPanel(), gbc);

        return panel;
    }

    // ── Input Form ────────────────────────────────────────────────────────

    private JPanel buildInputForm() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        JLabel hdr = new JLabel("  Patient Information");
        hdr.setFont(FONT_HEADER);
        hdr.setForeground(CLR_ACCENT);
        outer.add(hdr, BorderLayout.NORTH);

        // Grid of input fields (2 columns)
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(CLR_PANEL);
        grid.setBorder(new CompoundBorder(
                new LineBorder(CLR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5, 6, 5, 6);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weighty = 0;

        int row = 0;

        // ── Row: Age | Sex ────────────────────────────────────────────────
        tfAge  = makeTextField("e.g. 54");
        cbSex  = makeComboBox("Male", "Female");
        row = addFieldRow(grid, g, row, "Age (years)", tfAge, "Sex", cbSex);

        // ── Row: Chest Pain | Fasting Blood Sugar ─────────────────────────
        cbCp  = makeComboBox("Typical Angina", "Atypical Angina", "Non-Anginal Pain", "Asymptomatic");
        cbFbs = makeComboBox("No (≤120 mg/dl)", "Yes (>120 mg/dl)");
        row = addFieldRow(grid, g, row, "Chest Pain Type", cbCp, "Fasting Blood Sugar", cbFbs);

        // ── Row: Resting BP | Cholesterol ────────────────────────────────
        tfTrestbps = makeTextField("e.g. 130");
        tfChol     = makeTextField("e.g. 250");
        row = addFieldRow(grid, g, row, "Resting Blood Pressure (mmHg)", tfTrestbps,
                "Serum Cholesterol (mg/dl)", tfChol);

        // ── Row: Resting ECG | Max Heart Rate ────────────────────────────
        cbRestecg  = makeComboBox("Normal", "ST-T Wave Abnormality", "Left Ventricular Hypertrophy");
        tfThalach  = makeTextField("e.g. 150");
        row = addFieldRow(grid, g, row, "Resting ECG", cbRestecg, "Maximum Heart Rate", tfThalach);

        // ── Row: Exercise Angina | Oldpeak ───────────────────────────────
        cbExang  = makeComboBox("No", "Yes");
        tfOldpeak = makeTextField("e.g. 1.0");
        row = addFieldRow(grid, g, row, "Exercise Induced Angina", cbExang,
                "ST Depression (Oldpeak)", tfOldpeak);

        // ── Row: Slope | Major Vessels ────────────────────────────────────
        cbSlope = makeComboBox("Up Sloping", "Flat", "Down Sloping");
        cbCa    = makeComboBox("0", "1", "2", "3");
        row = addFieldRow(grid, g, row, "Slope of Peak Exercise ST", cbSlope,
                "Major Vessels (0–3)", cbCa);

        // ── Row: Thal (full width) ────────────────────────────────────────
        cbThal = makeComboBox("Normal", "Fixed Defect", "Reversible Defect");
        addSingleFieldRow(grid, g, row, "Thalassemia (Thal)", cbThal);

        outer.add(grid, BorderLayout.CENTER);

        // Predict button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        btnPanel.setOpaque(false);
        JButton btnPredict = createPredictButton();
        btnPanel.add(btnPredict);
        outer.add(btnPanel, BorderLayout.SOUTH);

        return outer;
    }

    private JButton createPredictButton() {
        JButton btn = new JButton("  PREDICT  ");
        btn.setFont(FONT_BUTTON);
        btn.setBackground(CLR_ACCENT);
        btn.setForeground(CLR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 30, 10, 30));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(14,165,233)); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(CLR_ACCENT); }
        });
        btn.addActionListener(e -> onPredict());
        return btn;
    }

    // ── Result Panel ──────────────────────────────────────────────────────

    private JPanel buildResultPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 10));
        outer.setOpaque(false);

        JLabel hdr = new JLabel("  Prediction Result");
        hdr.setFont(FONT_HEADER);
        hdr.setForeground(CLR_ACCENT);
        outer.add(hdr, BorderLayout.NORTH);

        pnlResult = new JPanel();
        pnlResult.setLayout(new BoxLayout(pnlResult, BoxLayout.Y_AXIS));
        pnlResult.setBackground(CLR_PANEL);
        pnlResult.setBorder(new CompoundBorder(
                new LineBorder(CLR_BORDER, 1, true),
                new EmptyBorder(24, 20, 24, 20)));

        // Idle state: instruction
        lblResult = new JLabel("<html><center>Enter patient data<br>and click PREDICT</center></html>",
                SwingConstants.CENTER);
        lblResult.setFont(FONT_RESULT);
        lblResult.setForeground(CLR_SUBTEXT);
        lblResult.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblProb0 = new JLabel("No Heart Disease: —");
        lblProb0.setFont(FONT_PROB);
        lblProb0.setForeground(CLR_SUBTEXT);
        lblProb0.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblProb1 = new JLabel("Heart Disease:  —");
        lblProb1.setFont(FONT_PROB);
        lblProb1.setForeground(CLR_SUBTEXT);
        lblProb1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sep = new JLabel("─────────────────────");
        sep.setForeground(CLR_BORDER);
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblProbHdr = new JLabel("Model Probabilities:");
        lblProbHdr.setFont(FONT_SUBTITLE);
        lblProbHdr.setForeground(CLR_SUBTEXT);
        lblProbHdr.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Evaluation summary section
        JPanel evalPanel = buildEvalSummary();
        evalPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlResult.add(Box.createVerticalGlue());
        pnlResult.add(lblResult);
        pnlResult.add(Box.createVerticalStrut(20));
        pnlResult.add(sep);
        pnlResult.add(Box.createVerticalStrut(8));
        pnlResult.add(lblProbHdr);
        pnlResult.add(Box.createVerticalStrut(6));
        pnlResult.add(lblProb0);
        pnlResult.add(Box.createVerticalStrut(4));
        pnlResult.add(lblProb1);
        pnlResult.add(Box.createVerticalStrut(20));
        pnlResult.add(evalPanel);
        pnlResult.add(Box.createVerticalGlue());

        // Disclaimer
        JLabel disclaimer = new JLabel(
                "<html><center>⚠ This prediction is for academic demonstration only<br>"
                + "and is NOT a medical diagnosis.</center></html>",
                SwingConstants.CENTER);
        disclaimer.setFont(FONT_DISCLAIMER);
        disclaimer.setForeground(new Color(250, 204, 21));
        disclaimer.setBorder(new EmptyBorder(8, 4, 0, 4));

        outer.add(pnlResult,   BorderLayout.CENTER);
        outer.add(disclaimer,  BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildEvalSummary() {
        JPanel p = new JPanel(new GridLayout(5, 2, 4, 3));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(280, 120));

        addEvalRow(p, "Train / Test Split:",
                trainCount + " / " + testCount + " records");
        addEvalRow(p, "Accuracy:",
                String.format("%.2f%%", evalResult.accuracy    * 100));
        addEvalRow(p, "Sensitivity:",
                String.format("%.2f%%", evalResult.sensitivity * 100));
        addEvalRow(p, "Specificity:",
                String.format("%.2f%%", evalResult.specificity * 100));
        addEvalRow(p, "Precision:",
                String.format("%.2f%%", evalResult.precision   * 100));
        return p;
    }

    private void addEvalRow(JPanel p, String key, String value) {
        JLabel k = new JLabel(key);
        k.setFont(FONT_PROB);
        k.setForeground(CLR_SUBTEXT);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 12));
        v.setForeground(CLR_ACCENT);

        p.add(k);
        p.add(v);
    }

    // ── Status Bar ────────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 23, 42));
        panel.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, CLR_BORDER),
                new EmptyBorder(5, 16, 5, 16)));

        lblStatus = new JLabel("✓  Model trained successfully  |  70% Train / 30% Test Split  |  Naïve Bayes (Gaussian + Categorical with Laplace Smoothing)");
        lblStatus.setFont(FONT_STATUS);
        lblStatus.setForeground(CLR_SUCCESS);

        panel.add(lblStatus, BorderLayout.WEST);

        JLabel citation = new JLabel("UCI Cleveland Heart Disease Dataset  ·  303 records  ");
        citation.setFont(FONT_STATUS);
        citation.setForeground(CLR_SUBTEXT);
        panel.add(citation, BorderLayout.EAST);

        return panel;
    }

    // -----------------------------------------------------------------------
    // Prediction Logic
    // -----------------------------------------------------------------------

    private void onPredict() {
        try {
            DataRecord input = buildInputRecord();
            if (input == null) return; // Validation failed

            int predicted        = model.predict(input);
            double[] probs       = model.getProbabilities(input);

            // Update result label
            if (predicted == 1) {
                lblResult.setText("<html><center>❤  Heart Disease<br>PRESENT</center></html>");
                lblResult.setForeground(CLR_DANGER);
                pnlResult.setBorder(new CompoundBorder(
                        new LineBorder(CLR_DANGER, 2, true),
                        new EmptyBorder(24, 20, 24, 20)));
            } else {
                lblResult.setText("<html><center>✓  No Heart<br>Disease</center></html>");
                lblResult.setForeground(CLR_SUCCESS);
                pnlResult.setBorder(new CompoundBorder(
                        new LineBorder(CLR_SUCCESS, 2, true),
                        new EmptyBorder(24, 20, 24, 20)));
            }

            lblProb0.setText(String.format("No Heart Disease :  %.2f%%", probs[0] * 100));
            lblProb1.setText(String.format("Heart Disease      :  %.2f%%", probs[1] * 100));
            lblProb0.setForeground(CLR_SUCCESS);
            lblProb1.setForeground(CLR_DANGER);

            pnlResult.revalidate();
            pnlResult.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads all input fields, validates them, and builds a DataRecord.
     * Returns null if validation fails (error dialog is shown).
     */
    private DataRecord buildInputRecord() {
        // ── Numerical inputs ──────────────────────────────────────────────
        int age = validateInt(tfAge, "Age", 29, 77);
        if (age == Integer.MIN_VALUE) return null;

        double trestbps = validateDouble(tfTrestbps, "Resting Blood Pressure", 94, 200);
        if (Double.isNaN(trestbps)) return null;

        double chol = validateDouble(tfChol, "Cholesterol", 126, 564);
        if (Double.isNaN(chol)) return null;

        double thalach = validateDouble(tfThalach, "Maximum Heart Rate", 71, 202);
        if (Double.isNaN(thalach)) return null;

        double oldpeak = validateDouble(tfOldpeak, "Oldpeak (ST Depression)", 0, 6.2);
        if (Double.isNaN(oldpeak)) return null;

        // ── Categorical inputs (convert human-readable → dataset encoding) ─
        int sex     = cbSex.getSelectedIndex()     == 0 ? 1 : 0;     // Male=1, Female=0
        int cp      = cbCp.getSelectedIndex()      + 1;               // 1–4
        int fbs     = cbFbs.getSelectedIndex()     == 0 ? 0 : 1;     // No=0, Yes=1
        int restecg = cbRestecg.getSelectedIndex();                    // 0/1/2
        int exang   = cbExang.getSelectedIndex()   == 0 ? 0 : 1;    // No=0, Yes=1
        int slope   = cbSlope.getSelectedIndex()   + 1;               // 1/2/3
        int ca      = cbCa.getSelectedIndex();                         // 0/1/2/3
        int thal;

        switch (cbThal.getSelectedIndex()) {
            case 0:  thal = 3; break;   // Normal
            case 1:  thal = 6; break;   // Fixed Defect
            default: thal = 7; break;   // Reversible Defect
        }

        return new DataRecord(age, sex, cp, trestbps, chol, fbs, restecg,
                              thalach, exang, oldpeak, slope, ca, thal, -1 /* target unknown */);
    }

    // ── Input validation helpers ──────────────────────────────────────────

    /**
     * Validates a JTextField as an integer within [min, max].
     * Returns Integer.MIN_VALUE and shows an error dialog if invalid.
     */
    private int validateInt(JTextField tf, String fieldName, int min, int max) {
        String text = tf.getText().trim();
        if (text.isEmpty()) {
            showError(fieldName + " cannot be blank.");
            return Integer.MIN_VALUE;
        }

        try {
            int val = Integer.parseInt(text);
            if (val < min || val > max) {
                showError(fieldName + " must be between " + min + " and " + max
                        + ".\nEntered: " + val);
                return Integer.MIN_VALUE;
            }
            return val;
        } catch (NumberFormatException e) {
            showError(fieldName + " must be a whole number.\nEntered: '" + text + "'");
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Validates a JTextField as a double within [min, max].
     * Returns Double.NaN and shows an error dialog if invalid.
     */
    private double validateDouble(JTextField tf, String fieldName, double min, double max) {
        String text = tf.getText().trim();
        if (text.isEmpty()) {
            showError(fieldName + " cannot be blank.");
            return Double.NaN;
        }

        try {
            double val = Double.parseDouble(text);
            if (val < min || val > max) {
                showError(fieldName + " must be between " + min + " and " + max
                        + ".\nEntered: " + val);
                return Double.NaN;
            }
            return val;
        } catch (NumberFormatException e) {
            showError(fieldName + " must be a valid number.\nEntered: '" + text + "'");
            return Double.NaN;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    // -----------------------------------------------------------------------
    // Helper widget factories
    // -----------------------------------------------------------------------

    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_INPUT);
        tf.setBackground(CLR_INPUT_BG);
        tf.setForeground(CLR_TEXT);
        tf.setCaretColor(CLR_TEXT);
        tf.setBorder(new CompoundBorder(
                new LineBorder(CLR_BORDER, 1),
                new EmptyBorder(4, 8, 4, 8)));
        tf.setToolTipText(placeholder);
        tf.setPreferredSize(new Dimension(140, 30));
        return tf;
    }

    private JComboBox<String> makeComboBox(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_INPUT);
        cb.setBackground(Color.WHITE);
        cb.setForeground(Color.BLACK);
        cb.setBorder(new LineBorder(CLR_BORDER, 1));
        cb.setPreferredSize(new Dimension(140, 30));

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (isSelected) {
                    label.setBackground(new Color(226, 232, 240));
                    label.setForeground(Color.BLACK);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }

                label.setBorder(new EmptyBorder(4, 8, 4, 8));

                return label;
            }
        });

        return cb;
    }

    private int addFieldRow(
            JPanel grid,
            GridBagConstraints g,
            int row,
            String leftLabel,
            JComponent leftComp,
            String rightLabel,
            JComponent rightComp) {

        g.gridy   = row * 2;
        g.weighty = 0;

        // Left label
        g.gridx = 0;
        g.weightx = 0.1;

        JLabel ll = makeLabel(leftLabel);
        grid.add(ll, g);

        // Left input
        g.gridx = 1;
        g.weightx = 0.4;
        grid.add(leftComp, g);

        // Right label
        g.gridx = 2;
        g.weightx = 0.1;

        JLabel rl = makeLabel(rightLabel);
        grid.add(rl, g);

        // Right input
        g.gridx = 3;
        g.weightx = 0.4;
        grid.add(rightComp, g);

        // Spacer row
        g.gridy  = row * 2 + 1;
        g.gridx  = 0;
        g.gridwidth = 4;
        g.weighty = 0;

        grid.add(Box.createVerticalStrut(2), g);

        g.gridwidth = 1;

        return row + 1;
    }

    private void addSingleFieldRow(
            JPanel grid,
            GridBagConstraints g,
            int row,
            String label,
            JComponent comp) {

        g.gridy   = row * 2;
        g.weighty = 0;

        g.gridx = 0;
        g.weightx = 0.1;

        grid.add(makeLabel(label), g);

        g.gridx = 1;
        g.weightx = 0.9;
        g.gridwidth = 3;

        grid.add(comp, g);

        g.gridwidth = 1;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text + ":");
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(CLR_SUBTEXT);
        lbl.setBorder(new EmptyBorder(0, 0, 0, 8));
        return lbl;
    }
}