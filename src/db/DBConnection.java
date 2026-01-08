package db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class DBConnection {
	
	private static final String DB_FILE = "biblioteca.db";
	private static final Path DB_DIR = Paths.get("resources_db");
	private static final String URL = "jdbc:sqlite:" + DB_DIR.resolve(DB_FILE).toString().replace('\\', '/');
	

	
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("Error: Driver SQLite JDBC no encontrado");
			throw new SQLException("Falta Driver JDBC: " + e.getMessage());
		}
		try {
			Files.createDirectories(DB_DIR);
		} catch (Exception e) {
			throw new SQLException("No se ha podido crear el directorio de la base de datos: " + DB_DIR, e);
		}
		Connection con = DriverManager.getConnection(URL);
		try (Statement st = con.createStatement()) {
			st.execute("PRAGMA foreign_keys = ON;");
		}
		return con;
	}
	
	public static void createTables() {
		
		//tabla libro
		String sqlLibro = "CREATE TABLE IF NOT EXISTS Libro (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " titulo TEXT NOT NULL,\n"
                + " autor TEXT NOT NULL,\n"
                + " valoracion_original REAL DEFAULT 0.0,\n" 
                + " valoracion_media REAL DEFAULT 0.0,\n"     
                + " num_valoraciones INTEGER DEFAULT 0,\n"    
                + " portada_path TEXT,\n"                     
                + " genero TEXT\n"
                + ");";
		
		//tabla user
		String sqlUser = "CREATE TABLE IF NOT EXISTS User (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " nombre TEXT NOT NULL,\n"
                + " apellido TEXT,\n"
                + " email TEXT UNIQUE NOT NULL,\n"
                + " password TEXT NOT NULL,\n"
                + " avatar_path TEXT,\n"
                + " usuario TEXT UNIQUE NOT NULL,\n"
                + " penalizacion_hasta TEXT,\n"  
                + " rol TEXT\n"
                + ");";
		
		//tabla reserva
		String sqlReserva = "CREATE TABLE IF NOT EXISTS Reserva (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " id_libro INTEGER NOT NULL,\n"
                + " id_user INTEGER NOT NULL,\n"
                + " fecha_reserva TEXT NOT NULL,\n"           // TEXT para LocalDate
                + " duracion INTEGER NOT NULL,\n"             
                + " prolongaciones INTEGER NOT NULL,\n"
                + " valoracion_usuario REAL DEFAULT 0.0,\n"  
                + "devuelto INTEGER DEFAULT 0,\n"
                + " FOREIGN KEY (id_libro) REFERENCES Libro(id),\n"
                + " FOREIGN KEY (id_user) REFERENCES User(id)\n"
                + ");";
		
		
		//tabla valoración
		String sqlValoracion = "CREATE TABLE IF NOT EXISTS Valoracion (\n"
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
				+ "codigoLibro INTEGER NOT NULL,\n"
			    + "puntuacion REAL NOT NULL,\n"
			    + "usuario TEXT,\n"
			    + "FOREIGN KEY (codigoLibro) REFERENCES Libro(id)\n"
			    + ");";

		//tabla favoritos
		String sqlFavorito = "CREATE TABLE IF NOT EXISTS Favorito (\n"
				+ " id_user INTEGER NOT NULL,\n"
				+ " id_libro INTEGER NOT NULL,\n"
				+ " fecha TEXT NOT NULL,\n"
				+ " PRIMARY KEY (id_user, id_libro),\n"
				+ " FOREIGN KEY (id_user) REFERENCES User(id) ON DELETE CASCADE,\n"
				+ " FOREIGN KEY (id_libro) REFERENCES Libro(id) ON DELETE CASCADE\n"
				+ ");";
		
		
		
		try (Connection con = getConnection();
	             Statement stmt = con.createStatement()) { 
	            
	            stmt.execute(sqlLibro); 
	            stmt.execute(sqlUser);
	            stmt.execute(sqlReserva);
	            stmt.execute(sqlValoracion);
	            stmt.execute(sqlFavorito);
	            // Índices (mejoran rendimiento en consultas frecuentes)
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_reserva_libro_devuelto ON Reserva(id_libro, devuelto);");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_reserva_user_devuelto ON Reserva(id_user, devuelto);");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_valoracion_libro ON Valoracion(codigoLibro);");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_favorito_user ON Favorito(id_user);");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_favorito_libro ON Favorito(id_libro);");
	            
	            // Migraciones suaves: si la BD ya existía, añadimos columnas que falten
	            ensureUserColumns(stmt);
	            
	            System.out.println("Tablas de la base de datos verificadas/creadas con éxito.");

	        } catch (SQLException e) {
	            System.err.println("Error al crear las tablas: " + e.getMessage());
	        }
	    
		
		
			    
			
	}

	/**
	 * En SQLite, CREATE TABLE IF NOT EXISTS no añade columnas a una tabla existente.
	 * Esta migración ligera añade las columnas nuevas si no están presentes.
	 */
	private static void ensureUserColumns(Statement stmt) throws SQLException {
		Set<String> cols = new HashSet<>();
		try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(User);") ) {
			while (rs.next()) {
				cols.add(rs.getString("name").toLowerCase());
			}
		}
		if (!cols.contains("rol")) {
			stmt.execute("ALTER TABLE User ADD COLUMN rol TEXT DEFAULT 'USER';");
		}
		if (!cols.contains("penalizacion_hasta")) {
			stmt.execute("ALTER TABLE User ADD COLUMN penalizacion_hasta TEXT;");
		}
		if (!cols.contains("usuario")) {
			// Campo obligatorio: añadimos primero, luego (si hay datos) se deberá rellenar.
			stmt.execute("ALTER TABLE User ADD COLUMN usuario TEXT;");
		}
		if (!cols.contains("avatar_path")) {
			stmt.execute("ALTER TABLE User ADD COLUMN avatar_path TEXT;");
		}
	}
}
