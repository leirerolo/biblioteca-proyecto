package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ValoracionDAO {
	
	public void insertaVal(int codigoLibro, double puntuacion, String usuario) throws SQLException {
        String sql = "INSERT INTO Valoracion (codigoLibro, puntuacion, usuario) VALUES (?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, codigoLibro);
            ps.setDouble(2, puntuacion);
            ps.setString(3, usuario);
            
            ps.executeUpdate();
        }
    }
}
