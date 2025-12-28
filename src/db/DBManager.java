package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class DBManager {
	
	private static final String DB_URL = "jdbc:sqlite:resources_db/biblioteca.db";
	
	public static Connection connect() {
		try {
			// Preferimos la conexión centralizada (foreign_keys ON, creación de directorio, etc.)
			return DBConnection.getConnection();
		} catch (SQLException e) {
			System.err.println("Error de conexión a la BD: " + e.getMessage());
			JOptionPane.showMessageDialog(null, 
					"ERROR: La conexión a la BD ha fallado. Verifica el driver JDBC y el DB path. Detalles: " + e.getMessage(),
					"ERROR BD",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	public static void createTables() {
		// Delegamos en la clase nueva
		DBConnection.createTables();
	}
}
