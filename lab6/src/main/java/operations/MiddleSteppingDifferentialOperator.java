package operations;

import functions.MathFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiddleSteppingDifferentialOperator extends SteppingDifferentialOperator {
    private static final Logger logger = LoggerFactory.getLogger(MiddleSteppingDifferentialOperator.class);

    public MiddleSteppingDifferentialOperator(double step) {
        super(step);
        logger.info("Создан MiddleSteppingDifferentialOperator с шагом " + step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        logger.info("Вычисление центральной производной с шагом " + step);
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (function.apply(x + step) - function.apply(x - step)) / (2 * step);
            }
        };
    }
}