package io;

import functions.TabulatedFunction;
import functions.LinkedListTabulatedFunction;
import operations.TabulatedDifferentialOperator;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedListTabulatedFunctionSerialization {
    private static final Logger logger = LoggerFactory.getLogger(LinkedListTabulatedFunctionSerialization.class);

    public static void main(String[] args) {
        logger.info("Запуск LinkedListTabulatedFunctionSerialization");
        serializeFunctions();
        deserializeAndPrintFunctions();
        logger.info("Завершение LinkedListTabulatedFunctionSerialization");
    }

    private static void serializeFunctions() {
        logger.info("Сериализация LinkedListTabulatedFunction");

        try (
                FileOutputStream fileStream = new FileOutputStream("output/serialized linked list functions.bin");
                BufferedOutputStream bufferedStream = new BufferedOutputStream(fileStream)
        ) {
            double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
            double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
            TabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);

            TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

            TabulatedFunction firstDerivative = differentialOperator.derive(originalFunction);
            TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);

            FunctionsIO.serialize(bufferedStream, originalFunction);
            FunctionsIO.serialize(bufferedStream, firstDerivative);
            FunctionsIO.serialize(bufferedStream, secondDerivative);

            logger.info("Сериализация LinkedListTabulatedFunction завершена");

        } catch (IOException e) {
            logger.error("Ошибка сериализации LinkedListTabulatedFunction", e);
            e.printStackTrace();
        }
    }

    private static void deserializeAndPrintFunctions() {
        logger.info("Десериализация LinkedListTabulatedFunction");

        try (
                FileInputStream fileStream = new FileInputStream("output/serialized linked list functions.bin");
                BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)
        ) {
            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedStream);

            logger.info("Десериализация LinkedListTabulatedFunction завершена");

            System.out.println("Исходная функция:");
            System.out.println(deserializedOriginal.toString());
            System.out.println("Первая производная:");
            System.out.println(deserializedFirstDerivative.toString());
            System.out.println("Вторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка десериализации LinkedListTabulatedFunction", e);
            e.printStackTrace();
        }
    }
}