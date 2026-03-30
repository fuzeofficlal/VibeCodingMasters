package org.example.vibecodingmaster;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MigrationRunner {

    @Test
    public void runMigration() {
        String url = "jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "Azhe114514";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            System.out.println("Starting Database Migration for Phase 1...");

            
            System.out.println("Altering table portfolio...");
            try {
                stmt.execute("ALTER TABLE portfolio ADD COLUMN cash_balance DECIMAL(15,4) DEFAULT 0");
                stmt.execute("ALTER TABLE portfolio ADD COLUMN version INT DEFAULT 0");
                stmt.execute("ALTER TABLE portfolio ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE");
                stmt.execute("UPDATE portfolio SET cash_balance = 0, version = 0, is_deleted = false");
                System.out.println("Success altering portfolio.");
            } catch (Exception e) {
                System.out.println("Warning altering portfolio (may already exist): " + e.getMessage());
            }

            
            System.out.println("Altering table portfolio_item...");
            try {
                stmt.execute("ALTER TABLE portfolio_item ADD COLUMN version INT DEFAULT 0");
                stmt.execute("ALTER TABLE portfolio_item ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE");
                stmt.execute("UPDATE portfolio_item SET version = 0, is_deleted = false");
                System.out.println("Success altering portfolio_item.");
            } catch (Exception e) {
                System.out.println("Warning altering portfolio_item (may already exist): " + e.getMessage());
            }

            
            System.out.println("Creating table transaction_history...");
            String createTableTxHistory = 
                "CREATE TABLE IF NOT EXISTS transaction_history (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    portfolio_id BIGINT NOT NULL," +
                "    ticker_symbol VARCHAR(20)," +
                "    transaction_type VARCHAR(20) NOT NULL," +
                "    volume INT NOT NULL," +
                "    price DECIMAL(15,4) NOT NULL," +
                "    status VARCHAR(20) NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "    version INT DEFAULT 0," +
                "    is_deleted BOOLEAN DEFAULT FALSE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
            stmt.execute(createTableTxHistory);
            System.out.println("Success creating transaction_history.");

            System.out.println("Migration Completed Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
