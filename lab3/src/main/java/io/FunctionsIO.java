package io;

import functions.TabulatedFunction;
import functions.Point;
import java.io.*;
import java.util.Locale;

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
}