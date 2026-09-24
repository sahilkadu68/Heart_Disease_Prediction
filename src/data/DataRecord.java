package data;

public class DataRecord {

    // Used when CA or THAL is missing
    public static final int MISSING_INT = Integer.MIN_VALUE;

    // Numerical attributes
    private int age;
    private double trestbps;
    private double chol;
    private double thalach;
    private double oldpeak;

    // Categorical attributes
    private int sex;
    private int cp;
    private int fbs;
    private int restecg;
    private int exang;
    private int slope;
    private int ca;
    private int thal;

    // Target
    // 0 = No Heart Disease
    // 1 = Heart Disease
    private int target;


    // Constructor
    public DataRecord(int age, int sex, int cp,
                      double trestbps, double chol,
                      int fbs, int restecg, double thalach,
                      int exang, double oldpeak,
                      int slope, int ca, int thal,
                      int target) {

        this.age = age;
        this.sex = sex;
        this.cp = cp;
        this.trestbps = trestbps;
        this.chol = chol;
        this.fbs = fbs;
        this.restecg = restecg;
        this.thalach = thalach;
        this.exang = exang;
        this.oldpeak = oldpeak;
        this.slope = slope;
        this.ca = ca;
        this.thal = thal;
        this.target = target;
    }


    // Getters

    public int getAge() {
        return age;
    }

    public int getSex() {
        return sex;
    }

    public int getCp() {
        return cp;
    }

    public double getTrestbps() {
        return trestbps;
    }

    public double getChol() {
        return chol;
    }

    public int getFbs() {
        return fbs;
    }

    public int getRestecg() {
        return restecg;
    }

    public double getThalach() {
        return thalach;
    }

    public int getExang() {
        return exang;
    }

    public double getOldpeak() {
        return oldpeak;
    }

    public int getSlope() {
        return slope;
    }

    public int getCa() {
        return ca;
    }

    public int getThal() {
        return thal;
    }

    public int getTarget() {
        return target;
    }


    // Setters needed for preprocessing

    public void setCa(int ca) {
        this.ca = ca;
    }

    public void setThal(int thal) {
        this.thal = thal;
    }

    public void setTarget(int target) {
        this.target = target;
    }


    // Check whether CA or THAL is missing

    public boolean hasMissingValues() {
        return ca == MISSING_INT || thal == MISSING_INT;
    }
}