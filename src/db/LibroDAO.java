package db;

import domain.Libro;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    
    private final UserDAO userDAO = new UserDAO(); 

   
    public void insertaLibro(Libro libro) throws SQLException {
        
        String sql = "INSERT INTO Libro(titulo, autor, valoracion_original, valoracion_media, num_valoraciones, portada_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setDouble(3, libro.getValoracionOriginal());
            pstmt.setDouble(4, libro.getValoracion()); 
            pstmt.setInt(5, libro.getNumValoraciones());
            pstmt.setString(6, libro.getPortadaPath());
            
            
            
            int filasAfectadas = pstmt.executeUpdate(); 

            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        libro.setId(rs.getInt(1));
                    }
                }
            }
        } 
    }
    

  
    public List<Libro> getTodosLosLibros() throws SQLException {
        String sql = "SELECT id, titulo, autor, valoracion_original, valoracion_media, num_valoraciones, portada_path FROM Libro";
        List<Libro> listaLibros = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                double valOriginal = rs.getDouble("valoracion_original");
                double valMedia = rs.getDouble("valoracion_media");
                int numVal = rs.getInt("num_valoraciones");
                String path = rs.getString("portada_path");
                

                Libro libro = new Libro(id, titulo, autor, valOriginal, path);
                libro.setValoracion(valMedia);
                libro.setNumValoraciones(numVal);
                
                listaLibros.add(libro);
            }
        }
        return listaLibros;
    }
    
    public Libro getLibroById(int id) throws SQLException {
        String sql = "SELECT id, titulo, autor, valoracion_original, valoracion_media, num_valoraciones, portada_path FROM Libro WHERE id = ?";
        Libro libro = null;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) { 

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String titulo = rs.getString("titulo");
                    String autor = rs.getString("autor");
                    double valOriginal = rs.getDouble("valoracion_original");
                    double valMedia = rs.getDouble("valoracion_media");
                    int numVal = rs.getInt("num_valoraciones");
                    String path = rs.getString("portada_path");
                    
                   
                    libro = new Libro(id, titulo, autor, valOriginal, path);
                    libro.setValoracion(valMedia);
                    libro.setNumValoraciones(numVal);
                }
            }
        }
        return libro;
    }
    public void updateRating(Libro libro) throws SQLException {
        String sql = "UPDATE Libro SET valoracion_media = ?, num_valoraciones = ? WHERE id = ?";
        
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement pstmt = con.prepareStatement(sql)) { 
            
            pstmt.setDouble(1, libro.getValoracion()); 
            pstmt.setInt(2, libro.getNumValoraciones());
            pstmt.setInt(3, libro.getId());
            
            pstmt.executeUpdate();
            
        } 
    }
    //para el admin
    public void deleteLibro(Libro libro) throws SQLException {
        String sql = "DELETE FROM Libro WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, libro.getId());
            pstmt.executeUpdate();
        }
    }

    
}