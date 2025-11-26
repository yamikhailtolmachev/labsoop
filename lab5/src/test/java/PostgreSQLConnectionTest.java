import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.*;

class PostgreSQLConnectionTest {
    @Test
    void testPostgreSQLConnection() {
        String url = "jdbc:postgresql://localhost:5432/labsoop_db";
        String user = "postgres";
        String password = "093306";

        System.out.println("Тестируем подключение к PostgreSQL...");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            assertNotNull(connection, "Подключение не должно быть null");
            assertTrue(connection.isValid(2), "Подключение должно быть валидным");
            assertFalse(connection.isClosed(), "Подключение не должно быть закрытым");

            System.out.println("Подключение к PostgreSQL успешно!");
            System.out.println("PostgreSQL выбрана как реляционная БД");

        } catch (Exception e) {
            fail("Не удалось подключиться к PostgreSQL: " + e.getMessage());
        }
    }

    @Test
    void testPostgreSQLDriver() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер PostgreSQL загружен");
        } catch (ClassNotFoundException e) {
            fail("Драйвер PostgreSQL не найден: " + e.getMessage());
        }
    }
}