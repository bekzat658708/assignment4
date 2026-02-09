import java.sql.*;
import java.util.Scanner;

public class Charlie {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter database name (e.g., oop4): ");
        String dbName = scanner.nextLine().trim();

        String url = "jdbc:postgresql://localhost:5432/" + dbName;
        String username = "postgres";
        String password = "658708";

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database: " + dbName);

            createStudent2Table(conn);

            int recordsCopied = copyAndSplitNames(conn);

            System.out.println("Success! Data copied from Student1 to Student2.");
            System.out.println("Total records processed: " + recordsCopied);

        } catch (SQLException e) {
            System.out.println("Database error:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Connection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            scanner.close();
        }
    }

    private static void createStudent2Table(Connection conn) throws SQLException {
        String createSQL = """
                CREATE TABLE IF NOT EXISTS Student2 (
                    username VARCHAR(50) NOT NULL,
                    password VARCHAR(50) NOT NULL,
                    firstname VARCHAR(100),
                    lastname VARCHAR(100),
                    CONSTRAINT pkStudent PRIMARY KEY (username)
                );
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createSQL);
            System.out.println("Student2 table is ready (created or already exists).");
        }
    }

    private static int copyAndSplitNames(Connection conn) throws SQLException {
        String selectSQL = "SELECT username, password, fullname FROM Student1";

        String insertSQL = """
                INSERT INTO Student2 (username, password, firstname, lastname)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (username) DO NOTHING;
                """;

        int count = 0;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullname = rs.getString("fullname");

                if (fullname == null || fullname.trim().isEmpty()) {
                    System.out.println("Skipping: " + username + " (fullname is empty)");
                    continue;
                }

                String[] parts = fullname.trim().split("\\s+");
                if (parts.length < 2) {
                    System.out.println("Skipping: " + username + " (fullname has less than 2 words)");
                    continue;
                }

                String firstname = parts[0];
                String lastname = parts[parts.length - 1];


                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, firstname);
                pstmt.setString(4, lastname);

                pstmt.executeUpdate();
                count++;
            }
        }

        return count;
    }
}