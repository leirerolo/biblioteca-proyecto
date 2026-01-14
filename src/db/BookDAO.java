package db;

import domain.Genero;
import domain.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class BookDAO {

	public static boolean insertBook(Libro libro) {
		String sql= "INSERT INTO Libros(titulo, autor, valoracion, portada_path, exemplares_disponibles, genero) VALUES(?, ?, ?, ?, ?, ?)";
		
		
		try (Connection conn = DBManager.connect();
				PreparedStatement pstmt = (conn != null) ? conn.prepareStatement(sql) : null) {
				
			
				if(pstmt == null) return false;
				
				
				pstmt.setString(1, libro.getTitulo());
				pstmt.setString(2, libro.getAutor());
				pstmt.setDouble(3, libro.getValoracion());
				pstmt.setString(4, libro.getPortadaPath());
				pstmt.setInt(5,1); //suponemos que al inicializar solo hay un exemplar disponible
				pstmt.setString(6, libro.getGenero().toString());
				
				
				int rowsAffected= pstmt.executeUpdate();
				return rowsAffected > 0;
				
		} catch (SQLException e) {
			
			System.err.println("ERROR al insertar el libro " + libro.getTitulo() + " -> " + e.getMessage());
			JOptionPane.showMessageDialog(null,
					"Error SQL al insertar el libro " + libro.getTitulo() + ". Detalles: " + e.getMessage(),
					"Error insertar BD",
					JOptionPane.ERROR_MESSAGE);
			return false;
			
		}		
	}
	
	
	
	// consultar y devolver todos los libros de la BD
	
	
	
	public static List <Libro> getAllBooks(){
		String sql = "SELECT id, titulo, autor, valoracion, portada_path, genero FROM Libros";
		List<Libro> listaLibros = new ArrayList<>();
		
		
		try (Connection conn = DBManager.connect();
				Statement stmt = (conn != null) ? conn.createStatement(): null;
				ResultSet rs = (stmt != null) ? stmt.executeQuery(sql) : null){
				
			
				if (rs == null) return listaLibros;
				
				
				while (rs.next()) {
					int id = rs.getInt("id");
					String titulo = rs.getString("titulo");
					String autor = rs.getString("autor");
					double valoracion = rs.getDouble("valoracion");
					String portadaPath = rs.getString("portada_path");
					String genero = rs.getString("genero");
					
					
					// crear el objeto libro
					
					
					Libro libro = new Libro(id, titulo, autor, valoracion, portadaPath, Genero.fromString(genero));
					listaLibros.add(libro);
					//
					
				}
				} catch (SQLException e) {
					System.err.println("Error al consultar los libros: " + e.getMessage());
					JOptionPane.showMessageDialog(null,
							"ERROR SQL al leer los libros. Detalles: " + e.getMessage(),
							"ERROR al consultar la BD",
							JOptionPane.ERROR_MESSAGE);
				}
				return listaLibros;
				
	}
				
	
 // otras operaciones que podriamos hacer
				
// modificar el rating de un libro (PreparedStatement)
				
	
		public static boolean updateBookRating(int id, double newRating) {
			String sql = "UPDATE Libros SET valoracion = ? WHERE id = ?";
			try (Connection conn = DBManager.connect();
					PreparedStatement pstmt = (conn != null) ? conn.prepareStatement(sql): null){
						if (pstmt == null) return false;
						
						pstmt.setDouble(1, newRating);
						pstmt.setInt(2, id);
						
						return pstmt.executeUpdate() > 0;
					
		} catch (SQLException e) {
			System.err.println("ERROR al actualizar el libro " + e.getMessage());
			return false;
		}
			
	}
 
		
}
