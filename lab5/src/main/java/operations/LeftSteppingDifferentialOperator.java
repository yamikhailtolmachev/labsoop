package operations;

import functions.MathFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator {
    private static final Logger logger = LoggerFactory.getLogger(LeftSteppingDifferentialOperator.class);

    public LeftSteppingDifferentialOperator(double step) {
        super(step);
        logger.info("Создан LeftSteppingDifferentialOperator с шагом " + step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        logger.info("Вычисление левой производной с шагом " + step);
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (function.apply(x) - function.apply(x - step)) / step;
            }
        };
    }
}