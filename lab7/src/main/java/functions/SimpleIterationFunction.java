package functions;

public class SimpleIterationFunction implements MathFunction {

    private final MathFunction phiFunction;
    private final double initialGuess;
    private final double tolerance;
    private final int maxIterations;

    public SimpleIterationFunction(MathFunction phiFunction) {
        this(phiFunction, 0.0, 1e-6, 1000);
    }

    public SimpleIterationFunction(MathFunction phiFunction, double initialGuess, double tolerance, int maxIterations) {
        if (phiFunction == null) {
            throw new IllegalArgumentException("Phi function cannot be null");
        }
        if (tolerance <= 0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("Max iterations must be positive");
        }

        this.phiFunction = phiFunction;
        this.initialGuess = initialGuess;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double apply(double x) {
        return solve();
    }

    public double solve() {
        double current = initialGuess;
        double next;
        int iterations = 0;

        while (iterations < maxIterations) {
            next = phiFunction.apply(current);

            if (Math.abs(next - current) < tolerance) {
                return next;
            }

            current = next;
            iterations++;
        }
        return current;
    }

    public double solve(double initialGuess) {
        double current = initialGuess;
        double next;
        int iterations = 0;

        while (iterations < maxIterations) {
            next = phiFunction.apply(current);

            if (Math.abs(next - current) < tolerance) {
                return next;
            }

            current = next;
            iterations++;
        }

        return current;
    }

    public MathFunction getPhiFunction() {
        return phiFunction;
    }

    public double getInitialGuess() {
        return initialGuess;
    }

    public double getTolerance() {
        return tolerance;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public String toString() {
        return "SimpleIterationFunction {phiFunction = " + phiFunction + ", initialGuess = " + initialGuess + ", tolerance = " + tolerance + ", maxIterations = " + maxIterations + "}";
    }
}