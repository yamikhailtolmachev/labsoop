package operations;

import functions.MathFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {
    private static final Logger logger = LoggerFactory.getLogger(RightSteppingDifferentialOperator.class);

    public RightSteppingDifferentialOperator(double step) {
        super(step);
        logger.info("Создан RightSteppingDifferentialOperator с шагом " + step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        logger.info("Вычисление правой производной с шагом " + step);
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (function.apply(x + step) - function.apply(x)) / step;
            }
        };
    }
}