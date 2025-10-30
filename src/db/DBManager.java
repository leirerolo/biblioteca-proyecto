package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class DBManager {
	private static final String DB_URL = "jdbc:sqlite:resources db/biblioteca.db";
	
	public static Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URL);
			return conn;
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
		// tabla para los libros
		// el id se va a generar automaticamente
		String sqlLibros= "CREATE TABLE IF NOT EXISTS Libros ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "titulo TEXT NOT NULL,"
				+ "autor TEXT NOT NULL,"
				+ "valoracion REAL NOT NULL,"
				+ "portada_path TEXT,"
				+ "exemplares_disponibles INTEGER DEFAULT 1"
				+ ");";
		
		// tabla para los usuarios
		
		String sqlUsuarios = "CREATE TABLE IF NOT EXISTS Usuarios ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "nombre TEXT NOT NULL,"
				+ "apellido TEXT NOT NULL"
				+ ");";
		
		// tabla para los prestamos (relacionar libro con usuario)
		
		String sqlLoans = "CREATE TABLE IF NOT EXISTS Loans ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "libro_id INTEGER NOT NULL,"
				+ "usuario_id INTEGER NOT NULL,"
				+ "fecha_prestamo TEXT NOT NULL,"
				+ "FOREIGN KEY(libro_id) REFERENCES Libros(id),"
				+ "FOREIGN KEY(usuario_id) REFERENCES Usuarios(id)"
				+ ");";
		
		try (Connection conn = connect();
				Statement stmt= (conn!= null)? conn.createStatement(): null){
			
			if (stmt != null) {
				stmt.execute(sqlLibros);
				stmt.execute(sqlUsuarios);
				stmt.execute(sqlLoans);
				System.out.println("Las tablas han sido creadas con exito");
			}
		} catch (SQLException e) {
			System.err.println("ERROR al crear las tablas: " + e.getMessage());
		}
	}
}
