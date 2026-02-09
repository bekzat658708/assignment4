import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {

    private static final String URL = "jdbc:postgresql://localhost:5432/oop4";
    private static final String USER = "postgres";
    private static final String PASSWORD = "658708";

    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("PostgreSQL-ге қосылдық! ✅");
            System.out.println("Дерекқор нұсқасы: " + conn.getMetaData().getDatabaseProductVersion());

        } catch (SQLException e) {
            System.out.println("Қосылу қатесі шықты:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Қосылым жабылды.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}