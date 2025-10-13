package io;

import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import java.io.*;

public class TabulatedFunctionFileOutputStream {

    public static void main(String[] args) {
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        try (
                FileOutputStream arrayFileStream = new FileOutputStream("output/array function.bin");
                BufferedOutputStream arrayBufferedStream = new BufferedOutputStream(arrayFileStream);

                FileOutputStream linkedListFileStream = new FileOutputStream("output/linked list function.bin");
                BufferedOutputStream linkedListBufferedStream = new BufferedOutputStream(linkedListFileStream)
        ) {
            FunctionsIO.writeTabulatedFunction(arrayBufferedStream, arrayFunction);
            System.out.println("ArrayTabulatedFunction successfully recorded in output/array function.bin");

            FunctionsIO.writeTabulatedFunction(linkedListBufferedStream, linkedListFunction);
            System.out.println("LinkedListTabulatedFunction successfully recorded in output/linked list function.bin");

            System.out.println("File size array function.bin: " +
                    new File("output/array function.bin").length() + " bytes");
            System.out.println("File size linked list function.bin: " +
                    new File("output/linked list function.bin").length() + " bytes");

        } catch (IOException e) {
            System.err.println("Error when writing files:");
            e.printStackTrace();
        }

        checkFilesExistence();
    }

    private static void checkFilesExistence() {
        File arrayFile = new File("output/array function.bin");
        File linkedListFile = new File("output/linked list function.bin");

        System.out.println("\nChecking file creation:");
        System.out.println("array function.bin exists: " + arrayFile.exists());
        System.out.println("linked list function.bin exists: " + linkedListFile.exists());

        if (arrayFile.exists() && linkedListFile.exists()) {
            System.out.println("Both files have been successfully created in the folder output!");
            System.out.println("Try opening them in a hex editor to view binary data.");
        }
    }
}