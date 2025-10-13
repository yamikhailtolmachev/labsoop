package io;

import functions.TabulatedFunction;
import functions.Point;
import java.io.*;
import java.util.Locale;

import functions.factory.TabulatedFunctionFactory;
import java.text.NumberFormat;
import java.text.ParseException;

public final class FunctionsIO {

    private FunctionsIO() {
        throw new UnsupportedOperationException("Cannot instantiate FunctionsIO class");
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.println(function.getCount());

        for (Point point : function) {
            printWriter.printf(Locale.US, "%f %f\n", point.x, point.y);
        }

        writer.flush();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function)
            throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(function.getCount());

        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
        }

        dataOutputStream.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {
        try {
            String countLine = reader.readLine();
            if (countLine == null) {
                throw new IOException("File is empty");
            }

            int count = Integer.parseInt(countLine);

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Unexpected end of file. Expected " + count + " points, but got only " + i);
                }

                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    throw new IOException("Invalid format in line: " + line + ". Expected two numbers separated by space");
                }

                xValues[i] = format.parse(parts[0]).doubleValue();
                yValues[i] = format.parse(parts[1]).doubleValue();
            }

            return factory.create(xValues, yValues);

        } catch (ParseException e) {
            throw new IOException("Error parsing number", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory)
            throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int count = dataInputStream.readInt();

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            xValues[i] = dataInputStream.readDouble();
            yValues[i] = dataInputStream.readDouble();
        }

        return factory.create(xValues, yValues);
    }
}