import java.sql.*;
import java.util.Scanner;

public class Alpha {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter database name (e.g., oop_assignment): ");
        String dbName = scanner.nextLine().trim();

        System.out.print("Enter table name (e.g., Quiz): ");
        String tableName = scanner.nextLine().trim();


        String url = "jdbc:postgresql://localhost:5432/" + dbName;
        String username = "postgres";
        String password = "658708";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database: " + dbName);


            String sql = "SELECT * FROM " + tableName;

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            // 5. Display results nicely
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();


            System.out.println("\nTable: " + tableName);
            System.out.println("=".repeat(80));
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                System.out.printf("%-30s", columnName);
            }
            System.out.println();
            System.out.println("-".repeat(80));


            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    System.out.printf("%-30s", value != null ? value : "NULL");
                }
                System.out.println();
            }

            System.out.println("=".repeat(80));
            System.out.println("Total rows: " + rowCount);

        } catch (SQLException e) {
            System.out.println("Error occurred:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {

            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("\nConnection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            scanner.close();
        }
    }
}