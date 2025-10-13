package io;

import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import java.io.*;

public class TabulatedFunctionFileWriter {

    public static void main(String[] args) {
        try (
                FileWriter arrayWriter = new FileWriter("output/array function.txt");
                FileWriter linkedListWriter = new FileWriter("output/linked list function.txt");
                BufferedWriter bufferedArrayWriter = new BufferedWriter(arrayWriter);
                BufferedWriter bufferedLinkedListWriter = new BufferedWriter(linkedListWriter)
        ) {
            double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
            double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

            TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

            FunctionsIO.writeTabulatedFunction(bufferedArrayWriter, arrayFunction);
            FunctionsIO.writeTabulatedFunction(bufferedLinkedListWriter, linkedListFunction);

            System.out.println("Файлы успешно созданы в папке output/");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}