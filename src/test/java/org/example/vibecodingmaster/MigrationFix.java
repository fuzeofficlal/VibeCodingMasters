package org.example.vibecodingmaster;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MigrationFix {
    @Test
    public void runFix() {
        String url = "jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "Azhe114514";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            System.out.println("Applying fix...");
            stmt.execute("ALTER TABLE portfolio_item ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
            System.out.println("Fix applied.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
