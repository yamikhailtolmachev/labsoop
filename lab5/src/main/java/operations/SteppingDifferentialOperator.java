package operations;

import functions.MathFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction> {
    private static final Logger logger = LoggerFactory.getLogger(SteppingDifferentialOperator.class);

    protected double step;

    public SteppingDifferentialOperator(double step) {
        if (step <= 0 || Double.isInfinite(step) || Double.isNaN(step)) {
            logger.error("Недопустимый шаг: " + step);
            throw new IllegalArgumentException("Step must be positive finite number");
        }
        this.step = step;
        logger.info("Создан SteppingDifferentialOperator с шагом " + step);
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        if (step <= 0 || Double.isInfinite(step) || Double.isNaN(step)) {
            logger.error("Недопустимый шаг при установке: " + step);
            throw new IllegalArgumentException("Step must be positive finite number");
        }
        this.step = step;
        logger.info("Шаг изменен на " + step);
    }
}