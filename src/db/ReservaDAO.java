package db;

import domain.Libro;
import domain.Reserva;
import domain.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    
    public List<Reserva> getReservasActivas() throws SQLException {
        String sql = "SELECT id, id_libro, id_user, fecha_reserva, duracion, prolongaciones, valoracion_usuario FROM Reserva";
        List<Reserva> listaReservas = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idLibro = rs.getInt("id_libro");
                int idUser = rs.getInt("id_user");
                LocalDate fecha = LocalDate.parse(rs.getString("fecha_reserva"));
                int duracion = rs.getInt("duracion");
                int prolongaciones = rs.getInt("prolongaciones");
                double valoracion = rs.getDouble("valoracion_usuario");

                Libro libro = libroDAO.getLibroById(idLibro);
                User user = userDAO.getUserById(idUser);

                if (libro != null && user != null) {
                    Reserva reserva = new Reserva(libro, user, fecha);
                    reserva.setDuracion(duracion);
                    reserva.setProlongaciones(prolongaciones);
                    reserva.setValoracionUsuario(valoracion);
                    listaReservas.add(reserva);
                }
            }
        }
        return listaReservas;
    }

    public List<Reserva> getTodasLasReservasActivas() throws SQLException {
    	String sql = "SELECT id_libro, id_user, fecha_reserva, duracion, prolongaciones, valoracion_usuario " +
                "FROM Reserva " +
                "WHERE date('now') <= date(fecha_reserva, '+' || duracion || ' days')";

    	List<Reserva> listaReservas = new ArrayList<>();
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int idLibro = rs.getInt("id_libro");
                int idUser = rs.getInt("id_user");
                LocalDate fecha = LocalDate.parse(rs.getString("fecha_reserva"));
                int duracion = rs.getInt("duracion");
                int prolongaciones = rs.getInt("prolongaciones");
                double valoracion = rs.getDouble("valoracion_usuario");
                
                Libro libro = libroDAO.getLibroById(idLibro);
                User user = userDAO.getUserById(idUser);
                
                if (libro != null && user != null) {
                    Reserva r = new Reserva(libro, user, fecha);
                    r.setDuracion(duracion);
                    r.setProlongaciones(prolongaciones);
                    r.setValoracionUsuario(valoracion);
                    listaReservas.add(r);
                }
            }
        }
        return listaReservas;
    }

    public List<Object[]> getLibrosMasReservados(int limite) throws SQLException {
        String sql = 
            "SELECT l.id, COUNT(r.id) AS reservas " +
            "FROM Libro l " +
            "JOIN Reserva r ON l.id = r.id_libro " +
            "GROUP BY l.id " +
            "HAVING reservas > 0 " +
            "ORDER BY reservas DESC " +
            "LIMIT ?";
        
        //hará las veces de mapa con la estructura: 
        //libro reservado, veces que ha sido reservado
        List<Object[]> lista = new ArrayList<>();
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idLibro = rs.getInt("id");
                    int numReservas = rs.getInt("reservas");

                    Libro libro = new LibroDAO().getLibroById(idLibro);  
                    if (libro != null) {
                        lista.add(new Object[] {
                        		libro, numReservas
                        });
                    }
                }
            }
        }
        return lista;
    }



}