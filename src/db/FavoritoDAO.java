package db;

import domain.Libro;
import domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la funcionalidad de "Favoritos".
 * Guarda qué libros ha marcado como favorito cada usuario.
 */
public class FavoritoDAO {

    private final LibroDAO libroDAO = new LibroDAO();

    public boolean esFavorito(User user, Libro libro) throws SQLException {
        if (user == null || libro == null) return false;
        String sql = "SELECT 1 FROM Favorito WHERE id_user = ? AND id_libro = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void addFavorito(User user, Libro libro) throws SQLException {
        if (user == null || libro == null) return;
        String sql = "INSERT OR IGNORE INTO Favorito(id_user, id_libro, fecha) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ps.setInt(2, libro.getId());
            ps.setString(3, LocalDate.now().toString());
            ps.executeUpdate();
        }
    }

    public void removeFavorito(User user, Libro libro) throws SQLException {
        if (user == null || libro == null) return;
        String sql = "DELETE FROM Favorito WHERE id_user = ? AND id_libro = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ps.setInt(2, libro.getId());
            ps.executeUpdate();
        }
    }

    public List<Libro> getFavoritosByUser(User user) throws SQLException {
        List<Libro> favoritos = new ArrayList<>();
        if (user == null) return favoritos;

        String sql = "SELECT id_libro FROM Favorito WHERE id_user = ? ORDER BY fecha DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idLibro = rs.getInt("id_libro");
                    Libro libro = libroDAO.getLibroById(idLibro);
                    if (libro != null) favoritos.add(libro);
                }
            }
        }
        return favoritos;
    }

    public int countFavoritos(User user) throws SQLException {
        if (user == null) return 0;
        String sql = "SELECT COUNT(*) AS c FROM Favorito WHERE id_user = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        }
    }
}
