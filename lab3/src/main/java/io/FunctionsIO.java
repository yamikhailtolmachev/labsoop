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

}