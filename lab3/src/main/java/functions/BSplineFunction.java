package functions;

public class BSplineFunction implements MathFunction {
    private final double[] knots;
    private final double[] coefficients;
    private final int degree;

    public BSplineFunction(double[] knots, double[] coefficients, int degree) {
        if (knots == null || coefficients == null) {
            throw new IllegalArgumentException("Knots and coefficients cannot be null");
        }
        if (degree < 0) {
            throw new IllegalArgumentException("Degree must be non-negative");
        }
        if (knots.length < degree + 2) {
            throw new IllegalArgumentException("Not enough knots for the given degree");
        }
        if (coefficients.length != knots.length - degree - 1) {
            throw new IllegalArgumentException("Number of coefficients must equal knots.length - degree - 1");
        }

        this.knots = knots.clone();
        this.coefficients = coefficients.clone();
        this.degree = degree;
    }

    @Override
    public double apply(double x) {
        if (x < knots[degree] || x > knots[knots.length - degree - 1]) {
            return 0.0;
        }

        double result = 0.0;

        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * DeBoor(x, degree, i);
        }

        return result;
    }

    private double DeBoor(double x, int k, int i) {
        if (k == 0) {
            if (x >= knots[i] && x < knots[i + 1]) {
                return 1.0;
            } else {
                return 0.0;
            }
        }

        double firstTerm = 0.0;
        double secondTerm = 0.0;

        double denominator1 = knots[i + k] - knots[i];
        if (denominator1 != 0) {
            firstTerm = ((x - knots[i]) / denominator1) * DeBoor(x, k - 1, i);
        }

        double denominator2 = knots[i + k + 1] - knots[i + 1];
        if (denominator2 != 0) {
            secondTerm = ((knots[i + k + 1] - x) / denominator2) * DeBoor(x, k - 1, i + 1);
        }

        return firstTerm + secondTerm;
    }
}