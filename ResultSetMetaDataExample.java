import java.sql.*;

public class ResultSetMetaDataExample {

    public static void main(String[] args) {

        try {
            // 1. Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Create Connection
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/4bcab",  // your DB
                    "root",                               // username
                    "admin"                            // password
            );

            // 3. Execute Query
            String sql = "SELECT * FROM registration";  // your table
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // 4. Get Metadata
            ResultSetMetaData meta = rs.getMetaData();

            int columnCount = meta.getColumnCount();
            System.out.println("Total Columns = " + columnCount);

            // 5. Print Column Details
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("\nColumn " + i + ":");
                System.out.println("Name  : " + meta.getColumnName(i));
                System.out.println("Type  : " + meta.getColumnTypeName(i));
                System.out.println("Size  : " + meta.getColumnDisplaySize(i));
                System.out.println("Table : " + meta.getTableName(i));
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}
