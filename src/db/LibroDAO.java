package db;

import domain.Libro;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public List<Object[]> getLibrosPeorValorados(int limite) throws SQLException {
        String sql =
            "SELECT l.id, l.titulo, l.autor, l.valoracion_original, l.valoracion_media, " +
            "       l.num_valoraciones, l.portada_path, AVG(v.puntuacion) AS media " +
            "FROM Libro l " +
            "JOIN Valoracion v ON l.id = v.codigoLibro " + 
            "GROUP BY l.id " +
            "ORDER BY media ASC " +
            "LIMIT ?";

        List<Object[]> lista = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    // Construir libro igual que en tu DAO
                    int id = rs.getInt("id");
                    String titulo = rs.getString("titulo");
                    String autor = rs.getString("autor");
                    double valOriginal = rs.getDouble("valoracion_original");
                    double valMediaGuardada = rs.getDouble("valoracion_media");
                    int numVal = rs.getInt("num_valoraciones");
                    String path = rs.getString("portada_path");

                    Libro libro = new Libro(id, titulo, autor, valOriginal, path);
                    libro.setValoracion(valMediaGuardada);
                    libro.setNumValoraciones(numVal);

                    double mediaCalculada = rs.getDouble("media");

                    lista.add(new Object[] { libro, mediaCalculada });
                }
            }
        }
        System.out.println(lista);
        return lista;
    }

    
    public void actualizarValoracionMedia(int idLibro) throws SQLException {
        String sql = "SELECT AVG(puntuacion) AS media, COUNT(*) AS num " +
                     "FROM Valoracion WHERE codigoLibro = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double media = rs.getDouble("media");
                    int numVal = rs.getInt("num");

                    String updateSql = "UPDATE Libro SET valoracion_media = ?, num_valoraciones = ? WHERE id = ?";
                    try (PreparedStatement psUpdate = con.prepareStatement(updateSql)) {
                        psUpdate.setDouble(1, media);
                        psUpdate.setInt(2, numVal);
                        psUpdate.setInt(3, idLibro);
                        psUpdate.executeUpdate();
                    }
                }
            }
        }
    }
    
    
    public List<Libro> getLibrosDisponibles(List<Libro> todosLibros) throws SQLException {
        List<Libro> disponibles = new ArrayList<>();
        
        String sql = "SELECT l.id " +
                     "FROM Libro l " +
                     "LEFT JOIN Reserva r ON l.id = r.id_libro AND r.devuelto = 0 " +
                     "WHERE r.id IS NULL"; // solo los libros sin reserva activa

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");

                // Buscar el libro original en la lista para mantener portada y demás datos
                for (Libro l : todosLibros) {
                    if (l.getId() == id) {
                        disponibles.add(l);
                        break;
                    }
                }
            }
        }

        return disponibles;
    }

    //Para que el admin pueda eliminar libros de la aplicación
    public void eliminarLibro(Libro libro) throws SQLException {
        String sql = "DELETE FROM Libro WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, libro.getId());
            pstmt.executeUpdate();
        }
    }
}