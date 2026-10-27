package hu.nye.progtech.rushhour.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private static final String DB_URL = "jdbc:h2:./rushhourdb";

    private static DatabaseService instance;
    private Connection connection;

    private DatabaseService() {
        try {
            connection = DriverManager.getConnection(DB_URL, "sa", "");
            initDatabase();
        } catch (SQLException e) {
            logger.error("Hiba az adatbázis kapcsolódásakor", e);
        }
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    private void initDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS LEADERBOARD (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "PLAYER_NAME VARCHAR(255), " +
                "WINNER VARCHAR(50))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            logger.info("Adatbázis tábla inicializálva.");
        } catch (SQLException e) {
            logger.error("Hiba a tábla létrehozásakor", e);
        }
    }

    public void saveWin(String playerName, String winner) {
        String insertSQL = "INSERT INTO LEADERBOARD (PLAYER_NAME, WINNER) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, playerName);
            pstmt.setString(2, winner);
            pstmt.executeUpdate();
            logger.info("Győzelem elmentve az adatbázisba: {} - {}", playerName, winner);
        } catch (SQLException e) {
            logger.error("Hiba a győzelem mentésekor", e);
        }
    }

    // Új metódus a dicsőségtábla kiolvasására és kiíratására
    public void printLeaderboard() {
        String query = "SELECT PLAYER_NAME, WINNER FROM LEADERBOARD";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== DICSŐSÉGTÁBLA ===");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String player = rs.getString("PLAYER_NAME");
                String winner = rs.getString("WINNER");
                System.out.println("Játékos: " + player + " | Nyertes: " + winner);
            }
            if (!hasData) {
                System.out.println("Még nincs rögzített eredmény.");
            }
            System.out.println("=====================\n");

        } catch (SQLException e) {
            logger.error("Hiba a dicsőségtábla lekérdezésekor", e);
        }
    }
}