import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Bravo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter database name (e.g., oop_assignment): ");
        String dbName = scanner.nextLine().trim();


        String url = "jdbc:postgresql://localhost:5432/" + dbName;
        String username = "postgres";
        String password = "658708";

        System.out.print("Enter path to the Aiken-format questions file: ");
        String filePath = scanner.nextLine().trim();

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database: " + dbName);


            createQuizTable(conn);


            loadQuestionsFromFile(conn, filePath);

            System.out.println("All questions have been successfully loaded into the Quiz table.");

        } catch (SQLException e) {
            System.out.println("Database error:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File reading error:");
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

    private static void createQuizTable(Connection conn) throws SQLException {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS Quiz (
                    questionId INTEGER,
                    question VARCHAR(4000),
                    choicea VARCHAR(1000),
                    choiceb VARCHAR(1000),
                    choicec VARCHAR(1000),
                    choiced VARCHAR(1000),
                    answer VARCHAR(5)
                );
                """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Quiz table is ready (created or already exists).");
        }
    }

    private static void loadQuestionsFromFile(Connection conn, String filePath)
            throws SQLException, IOException {

        String insertSQL = """
                INSERT INTO Quiz (questionId, question, choicea, choiceb, choicec, choiced, answer)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        int questionId = 0;
        String question = null;
        String a = null, b = null, c = null, d = null;
        String answer = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }


                if (Character.isDigit(line.charAt(0)) && line.contains(".")) {

                    if (question != null) {
                        insertQuestion(pstmt, questionId, question, a, b, c, d, answer);
                        questionId++;
                    }


                    int dotIndex = line.indexOf('.');
                    question = line.substring(dotIndex + 1).trim();
                    a = b = c = d = answer = null;
                }

                else if (line.startsWith("A.")) {
                    a = line.substring(2).trim();
                } else if (line.startsWith("B.")) {
                    b = line.substring(2).trim();
                } else if (line.startsWith("C.")) {
                    c = line.substring(2).trim();
                } else if (line.startsWith("D.")) {
                    d = line.substring(2).trim();
                }

                else if (line.toUpperCase().startsWith("ANSWER:")) {
                    answer = line.substring(7).trim().toUpperCase();
                }
            }


            if (question != null) {
                insertQuestion(pstmt, questionId, question, a, b, c, d, answer);
                questionId++;
            }

            System.out.println("Total questions inserted: " + questionId);
        }
    }

    private static void insertQuestion(PreparedStatement pstmt, int id, String question,
                                       String a, String b, String c, String d, String answer)
            throws SQLException {

        pstmt.setInt(1, id);
        pstmt.setString(2, question);
        pstmt.setString(3, a);
        pstmt.setString(4, b);
        pstmt.setString(5, c);
        pstmt.setString(6, d);
        pstmt.setString(7, answer);

        pstmt.executeUpdate();
    }
}