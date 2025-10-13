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
        System.out.println("Reading function from file");

        try (
                FileInputStream fileStream = new FileInputStream("input/binary function.bin");
                BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)
        ) {
            TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedStream, arrayFactory);

            System.out.println("Function read from file:");
            System.out.println(function.toString());

        } catch (IOException e) {
            System.err.println("Error reading file:");
            e.printStackTrace();
        }
    }

    private static void readFromConsole() {
        System.out.println("\nReading function from console");

        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            System.out.println("Enter function size and values:");
            System.out.println("Format: first number of points, then pairs of x y separated by space");
            System.out.println("Example:");
            System.out.println("3");
            System.out.println("1.0 2.0");
            System.out.println("2.0 4.0");
            System.out.println("3.0 6.0");
            System.out.println("Your input:");

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedReader, linkedListFactory);

            System.out.println("\nOriginal function:");
            System.out.println(function.toString());

            TabulatedFunction derivative = differentialOperator.derive(function);
            System.out.println("Function derivative:");
            System.out.println(derivative.toString());

        } catch (Exception e) {
            System.err.println("Error reading from console:");
            e.printStackTrace();
        }
    }
}