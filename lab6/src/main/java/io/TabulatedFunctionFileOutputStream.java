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
            FunctionsIO.writeTabulatedFunction(linkedListBufferedStream, linkedListFunction);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}