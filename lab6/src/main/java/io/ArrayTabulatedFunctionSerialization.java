package io;

import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import operations.TabulatedDifferentialOperator;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayTabulatedFunctionSerialization {
    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunctionSerialization.class);

    public static void main(String[] args) {
        logger.info("Начало сериализации ArrayTabulatedFunction");

        try (
                FileOutputStream fileOutputStream = new FileOutputStream("output/serialized array functions.bin");
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
        ) {
            double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
            double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

            TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

            TabulatedFunction firstDerivative = operator.derive(function);
            TabulatedFunction secondDerivative = operator.derive(firstDerivative);

            FunctionsIO.serialize(bufferedOutputStream, function);
            FunctionsIO.serialize(bufferedOutputStream, firstDerivative);
            FunctionsIO.serialize(bufferedOutputStream, secondDerivative);

            logger.info("Сериализация завершена: функция и две производные");

        } catch (IOException e) {
            logger.error("Ошибка сериализации", e);
            e.printStackTrace();
        }

        logger.info("Начало десериализации ArrayTabulatedFunction");

        try (
                FileInputStream fileInputStream = new FileInputStream("output/serialized array functions.bin");
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)
        ) {
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedInputStream);

            logger.info("Десериализация завершена");

            System.out.println("Исходная функция:");
            System.out.println(deserializedFunction.toString());
            System.out.println("Первая производная:");
            System.out.println(deserializedFirstDerivative.toString());
            System.out.println("Вторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка десериализации", e);
            e.printStackTrace();
        }

        logger.info("Завершение ArrayTabulatedFunctionSerialization");
    }
}