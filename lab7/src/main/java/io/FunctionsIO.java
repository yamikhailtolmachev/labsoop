package io;

import functions.TabulatedFunction;
import functions.Point;
import java.io.*;
import java.util.Locale;
import functions.factory.TabulatedFunctionFactory;
import java.text.NumberFormat;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FunctionsIO {
    private static final Logger logger = LoggerFactory.getLogger(FunctionsIO.class);

    private FunctionsIO() {
        throw new UnsupportedOperationException("Cannot instantiate FunctionsIO class");
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        logger.info("Десериализация табличной функции");
        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        return (TabulatedFunction) objectInputStream.readObject();
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        logger.info("Запись табличной функции в writer с " + function.getCount() + " точками");
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.println(function.getCount());

        for (Point point : function) {
            printWriter.printf("%f %f%n", point.x, point.y);
        }

        writer.flush();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function)
            throws IOException {
        logger.info("Запись табличной функции в output stream с " + function.getCount() + " точками");
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
                logger.error("Пустой файл при чтении");
                throw new IOException("File is empty");
            }

            int count = Integer.parseInt(countLine);
            logger.info("Чтение табличной функции с " + count + " точками");

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line == null) {
                    logger.error("Неожиданный конец файла на точке " + i);
                    throw new IOException("Unexpected end of file. Expected " + count + " points, but got only " + i);
                }

                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    logger.error("Неверный формат в строке: " + line);
                    throw new IOException("Invalid format in line: " + line + ". Expected two numbers separated by space");
                }

                xValues[i] = format.parse(parts[0]).doubleValue();
                yValues[i] = format.parse(parts[1]).doubleValue();
            }

            return factory.create(xValues, yValues);

        } catch (ParseException e) {
            logger.error("Ошибка парсинга числа", e);
            throw new IOException("Error parsing number", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory)
            throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int count = dataInputStream.readInt();
        logger.info("Чтение табличной функции из input stream с " + count + " точками");

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            xValues[i] = dataInputStream.readDouble();
            yValues[i] = dataInputStream.readDouble();
        }

        return factory.create(xValues, yValues);
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        logger.info("Сериализация табличной функции с " + function.getCount() + " точками");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
        objectOutputStream.writeObject(function);
        objectOutputStream.flush();
    }
}