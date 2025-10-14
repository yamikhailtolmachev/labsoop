package io;

import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;
import java.io.*;

public class TabulatedFunctionFileInputStream {

    public static void main(String[] args) {
        readFromFile();

        readFromConsole();
    }

    private static void readFromFile() {
        System.out.println("Чтение функции из файла");

        try (
                FileInputStream fileStream = new FileInputStream("input/binary function.bin");
                BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)
        ) {
            TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedStream, arrayFactory);

            System.out.println("Функция из файла:");
            System.out.println(function.toString());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла:");
            e.printStackTrace();
        }
    }

    private static void readFromConsole() {
        System.out.println("Чтение функции из консоли");

        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            System.out.println("Введите размер и значения функции:");

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedReader, linkedListFactory);

            TabulatedFunction derivative = differentialOperator.derive(function);
            System.out.println("Производная функции:");
            System.out.println(derivative.toString());

        } catch (Exception e) {
            System.err.println("Ошибка при чтении из консоли:");
            e.printStackTrace();
        }
    }
}