package functions;

public class BSplineFunction implements MathFunction {
    private final double[] knots;
    private final double[] coefficients;
    private final int degree;

    public BSplineFunction(double[] knots, double[] coefficients, int degree) {
        this.knots = knots.clone();
        this.coefficients = coefficients.clone();
        this.degree = degree;
    }

    @Override
    public double apply(double x) {
        return evaluateBSpline(x, degree, knots, coefficients);
    }

    private double evaluateBSpline(double x, int k, double[] knots, double[] coefs) {
        if (k == 0) {
            for (int i = 0; i < knots.length - 1; i++) {
                if (x >= knots[i] && x < knots[i + 1]) {
                    return (i < coefs.length) ? coefs[i] : 0;
                }
            }
            return 0;
        }

        int i = findSpan(x, knots, coefs.length + k);
        if (i < 0) return 0;

        double[] temp = new double[k + 1];
        for (int j = 0; j <= k; j++) {
            int idx = i - k + j;
            temp[j] = (idx >= 0 && idx < coefs.length) ? coefs[idx] : 0;
        }

        for (int r = 1; r <= k; r++) {
            for (int j = k; j >= r; j--) {
                int idx = i - k + j;
                double alpha = (x - knots[idx]) / (knots[idx + k - r + 1] - knots[idx]);
                temp[j] = (1 - alpha) * temp[j - 1] + alpha * temp[j];
            }
        }
        return temp[k];
    }

    private int findSpan(double x, double[] knots, int n) {
        if (x >= knots[n]) return n - 1;
        if (x <= knots[0]) return 0;

        int low = 0;
        int high = n;
        int mid = (low + high) / 2;

        while (x < knots[mid] || x >= knots[mid + 1]) {
            if (x < knots[mid]) high = mid;
            else low = mid;
            mid = (low + high) / 2;
        }
        return mid;
    }
}
