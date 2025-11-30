package db;

import domain.Libro;
import domain.Reserva;
import domain.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {
    
    private final LibroDAO libroDAO = new LibroDAO(); 
    private final UserDAO userDAO = new UserDAO();
   
    /**
     * Inserta una nueva reserva en la base de datos.
     */
    public void insertaReserva(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO Reserva(id_libro, id_user, fecha_reserva, duracion, prolongaciones, valoracion_usuario) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            // Asumimos que los IDs de Libro y User ya han sido establecidos.
            pstmt.setInt(1, reserva.getLibro().getId());
            pstmt.setInt(2, reserva.getUser().getId());
            
            pstmt.setString(3, reserva.getFecha().toString()); 
            pstmt.setInt(4, reserva.getDuracion());
            pstmt.setInt(5, reserva.getProlongaciones());
            pstmt.setDouble(6, reserva.getValoracionUsuario());
            
            pstmt.executeUpdate();
        } 
    }
    

    public List<Reserva> getReservasByUser(User user) throws SQLException {
        String sql = "SELECT id, id_libro, id_user, fecha_reserva, duracion, prolongaciones, valoracion_usuario FROM Reserva WHERE id_user = ?";
        List<Reserva> listaReservas = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, user.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idLibro = rs.getInt("id_libro");
                    int idUser = rs.getInt("id_user");
                    LocalDate fecha = LocalDate.parse(rs.getString("fecha_reserva")); 
                    int duracion = rs.getInt("duracion");
                    int prolongaciones = rs.getInt("prolongaciones");
                    double valoracion = rs.getDouble("valoracion_usuario");
                    
                    Libro libro = libroDAO.getLibroById(idLibro); 
                    
                    User reservador = userDAO.getUserById(idUser);
                    
                    if (libro != null && reservador != null) {
                        Reserva reserva = new Reserva(libro, reservador, fecha);
                        reserva.setDuracion(duracion);
                        reserva.setProlongaciones(prolongaciones);
                        reserva.setValoracionUsuario(valoracion);
                        listaReservas.add(reserva);
                    }
                }
            }
        }
        return listaReservas;
    }
    

    public void actualizaReserva(Reserva reserva) throws SQLException {
        String sql = "UPDATE Reserva SET duracion = ?, prolongaciones = ?, valoracion_usuario = ? WHERE id_libro = ? AND id_user = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, reserva.getDuracion());
            pstmt.setInt(2, reserva.getProlongaciones());
            pstmt.setDouble(3, reserva.getValoracionUsuario());
            
            pstmt.setInt(4, reserva.getLibro().getId());
            pstmt.setInt(5, reserva.getUser().getId());
            
            pstmt.executeUpdate();
        }
    }
    public void eliminaReserva(Reserva reserva) throws SQLException {
        String sql = "DELETE FROM Reserva WHERE id_libro = ? AND id_user = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, reserva.getLibro().getId());
            pstmt.setInt(2, reserva.getUser().getId());
            
            pstmt.executeUpdate();
        }
    }
}