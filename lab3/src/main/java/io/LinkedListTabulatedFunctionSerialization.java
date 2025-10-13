package io;

import functions.TabulatedFunction;
import functions.LinkedListTabulatedFunction;
import operations.TabulatedDifferentialOperator;
import java.io.*;

public class LinkedListTabulatedFunctionSerialization {

    public static void main(String[] args) {
        serializeFunctions();

        deserializeAndPrintFunctions();
    }

    private static void serializeFunctions() {
        System.out.println("Serializing LinkedListTabulatedFunction and its derivatives");

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

            System.out.println("Successfully serialized 3 functions:");
            System.out.println("1. Original function");
            System.out.println("2. First derivative");
            System.out.println("3. Second derivative");
            System.out.println("File: output/serialized linked list functions.bin");

        } catch (IOException e) {
            System.err.println("Error during serialization:");
            e.printStackTrace();
        }
    }

    private static void deserializeAndPrintFunctions() {
        System.out.println("\nDeserializing and printing functions");

        new File("output").mkdirs();

        try (
                FileInputStream fileStream = new FileInputStream("output/serialized linked list functions.bin");
                BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)
        ) {
            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedStream);

            System.out.println("\n1. Original function (deserialized):");
            System.out.println(deserializedOriginal.toString());

            System.out.println("\n2. First derivative (deserialized):");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\n3. Second derivative (deserialized):");
            System.out.println(deserializedSecondDerivative.toString());

            System.out.println("\nAll functions successfully deserialized and printed!");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during deserialization:");
            e.printStackTrace();
        }
    }
}