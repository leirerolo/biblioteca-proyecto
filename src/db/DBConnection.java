package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	
	private static final String DB_FILE = "biblioteca.db";
	private static final String URL = "jdbc:sqlite:resources_db/" + DB_FILE;
	

	
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("Error: Driver SQLite JDBC no encontrado");
			throw new SQLException("Falta Driver JDBC: " + e.getMessage());
		}
		return DriverManager.getConnection(URL);
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
                + " portada_path TEXT\n"                     
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
		
		
		try (Connection con = getConnection();
	             Statement stmt = con.createStatement()) { 
	            
	            stmt.execute(sqlLibro); 
	            stmt.execute(sqlUser);
	            stmt.execute(sqlReserva);
	            stmt.execute(sqlValoracion);
	            
	            /*//como hemos creado roles después de haber creado la tabla,
	            //no se ha actualizado la nueva columna
	            
	            //comprobar si existe
	            boolean rolExiste = false;
	            //AYUDA DE IA
	            try (ResultSet rs = stmt.executeQuery("PRAGMA tabla_info(User);")) {
	            	while (rs.next()) {
	            		if ("rol".equalsIgnoreCase(rs.getString("name"))) {
	            			rolExiste = true;
	            			break;
	            		}
	            	}
	            }
	            
	            //si no existe, añadimos la columna
	            if (!rolExiste) {
	            	stmt.execute("ALTER TABLE User ADD COLUMN rol TEXT DEFAULT 'USER';");
	            	System.out.println("Columna 'rol' añadida a la tabla User.");

	            }*/
	            
	            System.out.println("Tablas de la base de datos verificadas/creadas con éxito.");

	        } catch (SQLException e) {
	            System.err.println("Error al crear las tablas: " + e.getMessage());
	        }
	    
		
		
			    
			
	}
}
